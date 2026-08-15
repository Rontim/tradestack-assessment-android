# AI Log

## What I used it for

### 1. Claude (claude.ai)

I used Claude throughout the task in two phases: before the timed build and during the timed build.

**Before the timer**, I used it to analyse the brief and supplied code, inspect the stub server to understand its idempotency and `207` response semantics, and reason through the sync state model and failure handling. The discussion led to the four-state sync model and the rule that `SYNCING` must be reset on application restart. I made the final decisions on the model and behaviour.

I also created a small Hilt–WorkManager spike in a scratch project the evening before the timed build. This exposed two scaffolding issues: an AppCompat theme was referenced without the required dependency, and the `Application` class did not implement `Configuration.Provider`. I fixed both issues and recorded them in `DECISIONS`.

**During the timed build**, Claude helped me navigate the repository structure and reviewed parts of my implementation. I wrote and adapted the sync repository myself; in particular, the batched `markSyncing` implementation was mine. We also discussed whether to catch every exception or only `IOException`, and I chose the narrower `IOException` handling.

Claude reviewed my entity, DAO, and list-screen code and identified several issues, including a missing package declaration, a modifier that needed to be passed through to a child chip, and a semantically inappropriate Sync icon for unsent receipts.

At my request, Claude provided some of the wiring boilerplate, the `Application` class, scheduler, worker, and device ID provider, as well as the record screen. I deliberately used that assistance in the final half hour so that I could spend my remaining time on the core synchronisation logic.

### 2. Google AI Mode (Gemini 3)

I used Gemini as a second reviewer for the UI I had written. I supplied my list screen and collection row and asked it to review the implementation. I adopted some of its suggestions, including the card layout, currency and date formatting, and empty-state presentation.

I chose Gemini for this UI review because I assumed a Google tool would have particularly strong Android/Material context. That was a judgement based on the tool's association with Google rather than something I independently tested or established.

## Something it got wrong

Before we inspected the stub server's implementation, Claude argued that a permanent-failure state arguably should not exist because receipts are expected eventually to reach the server, making a "failed forever" state seem contradictory.

Reading `server.py` disproved that assumption. The server permanently rejects receipts containing unknown outlet codes, and retrying such a receipt cannot resolve the underlying problem.

I identified the contradiction while working through the `207` response handling and rejected the earlier recommendation. `REJECTED` therefore became a terminal state that is never automatically retried, with the server's rejection reason stored alongside the receipt.

This was an important example of treating the AI's output as a hypothesis to verify rather than as an authority.

## What I verified myself

I personally ran and exercised all of the code before committing it, regardless of whether a particular piece had been drafted by me or generated with AI assistance.

On the emulator, using the default chaos settings, I verified that:

* Recording a receipt in airplane mode survives a force-stop.
* The receipt synchronises when network connectivity returns.
* Killing the application during a push does not create a duplicate server record. The resend uses the same `client_uuid`, the server responds as a duplicate, and `GET /collections/` still shows the receipt only once.
* A `503` response causes WorkManager to apply exponential backoff. I observed the retry behaviour directly; initially I mistakenly thought the worker was broken because the retry was delayed by a persisted backoff schedule from three earlier failed runs.

The final implementation was therefore not accepted on the basis of AI output alone. I ran the application, exercised the failure and recovery paths, investigated unexpected behaviour, and verified the resulting system behaviour against the requirements before committing it.
