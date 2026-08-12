.PHONY: run chaos-off reset

run:
	python3 server.py

chaos-off:
	CHAOS=0 python3 server.py

reset:
	curl -s -X POST http://127.0.0.1:8080/api/v1/_reset/ && echo
