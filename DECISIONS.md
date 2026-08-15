# Decisions

Graded, and where the live session starts.

## How I decided to represent state
I used a `SyncState` enum with four states:
*   `NOT_SYNCED`: The record is on the handset but hasn't reached the server yet.
*   `SYNCING`: A sync operation is currently in flight for this record.
*   `SYNCED`: The server has confirmed receipt (either `accepted` or `duplicate`).
*   `REJECTED`: The server explicitly rejected the data (e.g., validation error).

This separates the "Is it safe?" (it's in Room) from "Is it done?" (it's on the server).

## Anything in the brief or the design note I pushed back on
The design note suggested a binary "Saved" (green tick) vs "Failed" (red). I felt this was too optimistic for a field app. I updated the `SyncStatusChip` to use `AssistChip` with more nuanced semantics:
*   **Saved**: (Outlined check) Indicates the data is locally safe but pending sync. I used the `outline` color instead of green to avoid false confidence.
*   **Sent**: (Filled check, primary color) Positive confirmation that the server has the data.
*   **Needs attention**: (Error icon, error color) Replaces "Failed" to indicate a permanent rejection that requires user action, rather than a transient network error.

## What happens when the push fails
*   **Fails once/repeatedly**: I'm relying on WorkManager for reliability. In the "backoff-after-three-failures incident," we saw how the system handles transient network drops—it just keeps the records in `NOT_SYNCED` and retries with exponential backoff.
*   **Server got it, response died**: The "murder test" scenario. If the process is killed after the server receives the data but before the handset records the success, the next sync attempt sends the same `clientUuid`. The server responds with `duplicate`, which `SyncRepository` treats as success, ensuring idempotency and preventing duplicate entries on the backend.

## What I did not build, and why
*   **Outlet Picker**: Currently uses a hardcoded outlet. This would be the "first thing I'd add" in a real app to make it functional for a rep covering multiple stores.
*   **Comprehensive Unit Tests**: I focused on the "testability" of the architecture (Room, Dagger/Hilt, Repository pattern) rather than 100% coverage, to demonstrate the structure within the time limit.
*   **Retry Cap**: Currently, `NOT_SYNCED` items will retry indefinitely via WorkManager. A real app might need a "dead letter" state or a cap to prevent battery drain on corrupted records.
*   **Database Migrations**: Used `fallbackToDestructiveMigration()` for speed during the assessment phase.

## What I would test on a real handset that I could not test on an emulator
*   **Network Transitions**: Moving from Wi-Fi to edge cases (2G/3G) or complete dead zones while a sync is in progress.
*   **Screen Visibility**: Validating the `SyncStatusChip` colors and icons in direct sunlight (5-inch screen).
*   **Battery Optimization**: Ensuring WorkManager tasks aren't being overly aggressive or deferred too long by Doze mode.

## Scaffolding Warts
*   **Theme**: The Material3 theme is a basic scaffold; a production app would need a full brand-aligned design system.
*   **Configuration.Provider**: The Hilt setup for WorkManager is functional but adds some boilerplate to the `Application` class that could be further cleaned up.
