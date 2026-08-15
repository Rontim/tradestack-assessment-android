# Submission: Ron Timothy

## Run

1. `cd stub-server && python3 server.py` (leave default chaos settings)
2. Open in Android Studio, run on an emulator. Base URL already points at `10.0.2.2:8080`.

## Demo

**[Demo video (Google Drive)](https://drive.google.com/file/d/1cetsfFado5ZZPVXG55j3jz-EbWiMYDBX/view?usp=sharing)** - 5 minutes, one uncut take, with on-screen captions.

Shows: record in airplane mode → force-stop → reopen → restore network → receipt reaches the server exactly once.

## Docs

* **DECISIONS.md**: state model, design-note decisions, failure handling, and what I cut.
* **AI-LOG.md**: AI usage, one incorrect recommendation, and what I verified myself.

Tests committed after the three-hour time limit are labelled as such in the commit history.

## Timeline

- **Wed 12 Aug:** `ad4a953` starting point as supplied; reading and setup done as prep.
- **Sat 08:44 → 11:45:** the three-hour timed block: `72751f0` (first fix, 08:44) through `90a24c3` (record screen, 11:45), closed with end-to-end verification on the emulator.
- **Sat afternoon:** `3299d88` docs and demo, `39c42d9` cleanup, `489c5e5` unit tests (labelled post-timebox).

## Feedback

The chaos server and design note made this a useful exercise in reasoning about failure modes rather than just implementation speed.

One note on the supplied scaffolding: there were two wiring gaps, an AppCompat theme was referenced without the dependency, and the `Application` class did not implement `Configuration.Provider`, which prevented `@HiltWorker`s from being instantiated. I found and fixed both, and documented them in `DECISIONS.md`.
