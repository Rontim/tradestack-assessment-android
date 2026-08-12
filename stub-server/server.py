#!/usr/bin/env python3
"""Deliberately unpleasant stub server for the Android assessment.

Standard library only. Python 3.9 or later.

    python3 server.py                 # 0.0.0.0:8080, chaos on
    CHAOS=0 python3 server.py         # well behaved, for your own debugging
    PORT=9000 python3 server.py

Endpoints
---------
GET  /api/v1/outlets/
POST /api/v1/collections/batch/
GET  /api/v1/collections/            what the server currently holds
POST /api/v1/_reset/                 wipe server state

Behaviour
---------
Every request sleeps 500-1500 ms. Roughly one POST in six returns 503. Roughly
one POST in twelve answers after four seconds, which is longer than a careless
client's timeout. A receipt is identified by client_uuid: send the same one
twice and the second is reported as a duplicate and not stored again.

State is written to state.json so it survives a restart, which is how you can
tell whether your client double-posted after you killed it.
"""
import json
import os
import random
import threading
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import urlparse

HERE = Path(__file__).resolve().parent
STATE = HERE / "state.json"
OUTLETS = json.loads((HERE / "outlets.json").read_text())
CHAOS = os.environ.get("CHAOS", "1") != "0"
PORT = int(os.environ.get("PORT", "8080"))

_lock = threading.Lock()
_received = {}
if STATE.exists():
    try:
        _received = json.loads(STATE.read_text())
    except Exception:
        _received = {}


def persist():
    STATE.write_text(json.dumps(_received, indent=2))


def chaos_delay(is_post):
    if not CHAOS:
        time.sleep(0.05)
        return None
    time.sleep(random.uniform(0.5, 1.5))
    if not is_post:
        return None
    roll = random.random()
    if roll < 0.165:
        return 503
    if roll < 0.25:
        time.sleep(4.0)      # slower than a careless timeout
    return None


class Handler(BaseHTTPRequestHandler):
    protocol_version = "HTTP/1.1"

    def log_message(self, fmt, *args):
        print(f"  {self.command:<5} {self.path:<32} {fmt % args}")

    def _send(self, code, payload):
        body = json.dumps(payload).encode()
        self.send_response(code)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        path = urlparse(self.path).path
        chaos_delay(False)
        if path == "/api/v1/outlets/":
            return self._send(200, {"results": OUTLETS})
        if path == "/api/v1/collections/":
            with _lock:
                return self._send(200, {"count": len(_received),
                                        "results": list(_received.values())})
        return self._send(404, {"detail": "not found"})

    def do_POST(self):
        path = urlparse(self.path).path
        length = int(self.headers.get("Content-Length") or 0)
        raw = self.rfile.read(length) if length else b"{}"

        if path == "/api/v1/_reset/":
            with _lock:
                _received.clear()
                persist()
            return self._send(200, {"detail": "reset"})

        if path != "/api/v1/collections/batch/":
            return self._send(404, {"detail": "not found"})

        fail = chaos_delay(True)
        if fail:
            return self._send(fail, {"detail": "upstream unavailable, retry"})

        try:
            payload = json.loads(raw)
        except json.JSONDecodeError:
            return self._send(400, {"detail": "malformed json"})

        receipts = payload.get("receipts")
        if not isinstance(receipts, list):
            return self._send(400, {"detail": "receipts must be a list"})
        if len(receipts) > 200:
            return self._send(413, {"detail": "batch limit is 200"})

        codes = {o["outlet_code"] for o in OUTLETS}
        results = []
        with _lock:
            for r in receipts:
                uuid = r.get("client_uuid")
                if not uuid:
                    results.append({"client_uuid": None, "status": "rejected",
                                    "reason": "client_uuid is required"})
                    continue
                if uuid in _received:
                    results.append({"client_uuid": uuid, "status": "duplicate"})
                    continue
                if r.get("outlet_code") not in codes:
                    results.append({"client_uuid": uuid, "status": "rejected",
                                    "reason": "unknown outlet_code"})
                    continue
                stored = dict(r)
                stored["server_received_at"] = time.strftime(
                    "%Y-%m-%dT%H:%M:%SZ", time.gmtime())
                _received[uuid] = stored
                results.append({"client_uuid": uuid, "status": "accepted"})
            persist()
        return self._send(207, {"results": results})


if __name__ == "__main__":
    print(f"stub server on 0.0.0.0:{PORT}   chaos={'on' if CHAOS else 'off'}")
    print(f"  emulator host: http://10.0.2.2:{PORT}")
    print(f"  holding {len(_received)} receipt(s) from a previous run")
    ThreadingHTTPServer(("0.0.0.0", PORT), Handler).serve_forever()
