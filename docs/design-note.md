# Design note - collections screen

From: Wanjiru, product design
For: the collections screen on the rep handset

The rep is standing in a doorway with a customer waiting. Every second of
uncertainty is a second the customer spends wondering whether the payment
went through. Speed of feedback is the whole game here.

So: **when the rep taps Save, show the green tick immediately.** Do not make
them wait, do not show a spinner, do not show anything ambiguous. Green tick,
straight away, and move on to the next customer.

I have specced the chip as:

| state | look |
|---|---|
| saved | green tick, "Saved" |
| failed | red, "Failed" |

Two states is enough. Anything more is clutter on a 5-inch screen in
sunlight.

`SyncStatusChip.kt` is already built to this spec.
