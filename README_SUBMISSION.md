# Submission: Ron Timothy

## Demo

**[Demo video(Google Drive):](https://drive.google.com/file/d/1cetsfFado5ZZPVXG55j3jz-EbWiMYDBX/view?usp=sharing)** 5 minutes, one uncut take, with on-screen captions.

Shows: record in airplane mode → force-stop → reopen → restore network → receipt reaches the server exactly once.

## Docs

* **DECISIONS.md**: state model, design-note decisions, failure handling, and what I cut.
* **AI-LOG.md**: AI usage, one incorrect recommendation, and what I verified myself.

Tests committed after the three-hour time limit are labelled as such in the commit history.

## Feedback

The chaos server and design note made this a useful exercise in reasoning about failure modes rather than just implementation speed.

One note on the supplied scaffolding: there were two wiring gaps, an AppCompat theme was referenced without the dependency, and the `Application` class did not implement `Configuration.Provider`, which prevented `@HiltWorker`s from being instantiated. I found and fixed both, and documented them in `DECISIONS.md`.
