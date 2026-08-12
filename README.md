# Android assessment - collections on a bad network

**Time box: three hours.** Stop at three hours. What you have at three hours is
what we look at.

## Requirements

Android Studio. This is not optional - it is how we run your submission.
Open the project, let Gradle sync, run on an emulator or a device.

## Getting started

Unzip this folder, open it in Android Studio, and let Gradle sync. The wrapper
properties are in place, so Android Studio will fetch Gradle 8.9 on first open.

Make it a git repository before you start work. We read the commit history.

```bash
git init && git add -A && git commit -m "Starting point as supplied"
```

## Sending it back

Create a repository on your own GitHub or GitLab account, make it **public**,
push, and send us the link.

```bash
git remote add origin <your repo url>
git push -u origin main
```

That is the whole submission. Nothing to email, no access to grant.

Run `./gradlew clean` first, or Build > Clean Project, so build output stays
out of the repository. `.gitignore` already covers the rest.

**Put the screen recording somewhere we can watch it** - Drive, unlisted
YouTube, anywhere - and link it in `README_SUBMISSION.md`. Do not commit the
video.

## The situation

Kirinyaga Distributors runs six vans out of a depot in Nakuru. A rep stands in
a shop doorway, takes cash, and records the receipt on a handset. Sometimes
there is 4G. Often there is nothing. Sometimes there is one bar, which is
worse than nothing, because requests hang instead of failing.

The rep cannot wait for the network. The receipt has to be recorded the moment
the money changes hands, and it has to get to the server eventually.

## Your task

Build the collections screen.

1. **Record a collection offline.** Outlet, amount, method (cash, M-Pesa,
   cheque). It must save with the device in airplane mode and it must survive
   the app being killed.
2. **List what has been recorded**, most recent first, with its state.
3. **Sync.** Push what has not reached the server. Handle the server being
   slow, the server failing, and the app being killed mid-push. Do not let the
   same receipt land twice.

## What is already wired

You should not spend your three hours on scaffolding. Already in place:

- Kotlin, Jetpack Compose, Material 3
- Room: `CollectionEntity`, `CollectionDao`, `AppDatabase`
- Hilt: `AppModule` provides the database, the DAO and Retrofit
- Retrofit: `CollectionsApi` with the two endpoints already declared
- Navigation between a list screen and a record screen, both stubbed
- WorkManager dependency, no worker written

Search the project for `TODO(candidate)` to find your seams.

## The stub server

A real server, deliberately unpleasant. No dependencies beyond Python 3.

```bash
cd stub-server
python3 server.py            # listens on 0.0.0.0:8080
```

From an emulator, the host is `http://10.0.2.2:8080`. That is already set in
`local.properties.example` and read by `app/build.gradle.kts`. On a physical
device, use your machine's LAN address.

It is slow, it fails about one request in six, and it sometimes takes four
seconds to answer. That is on purpose. It is a fair imitation of a 3G route in
Subukia. Do not work around it by turning the flakiness off - `make chaos-off`
exists for your own debugging, but your submission is judged against the
default settings.

## Deliver

- Working app, running from a clean checkout in Android Studio.
- **A screen recording** at `docs/demo.mp4` or a link in `README_SUBMISSION.md`.
  It must show: recording a collection in airplane mode, killing the app,
  reopening it, turning the network back on, and the receipt reaching the
  server. That recording is the single most important artefact you submit.
- `DECISIONS.md` and `AI-LOG.md`, filled in.
- Unit tests where they earn their keep. We are not counting them.

## Read the design note before you build

`docs/design-note.md` has the instruction that came with the ticket, and the
status chip has already been built to match it. Treat it the way you would
treat a real design note from a real colleague - which is to say, read it
properly and say something if it is wrong.

## Using AI

Use whatever helps. You own everything you submit. In the live session you
will change this app in front of us on your own machine, so do not submit code
you cannot navigate.
