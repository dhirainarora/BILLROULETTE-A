# BillRoulette Android UI parity

The existing web app in `dhirainarora/BILLROULETTE` is the visual and UX source of truth.

Do NOT redesign or simplify the UI.

Required parity:
- Same dark #09090D background, violet accent system, typography hierarchy, spacing, rounded corners, borders, shadows/glows.
- Same header: SplitRoulette logo on home, back button on inner screens, sound/history/settings controls.
- Same Home screen layout and copy.
- Same 3-step Setup flow: progress segments, participant counter and quick pills, numbered name inputs, currency cards, large bill input, equal-share preview, four randomness cards, rounding choices, review, errors.
- Same Fate Animation: cinematic violet aura, rapidly changing participant/amount, progress bar, skip.
- Same Individual Reveal: progress segments, reveal card, biggest-hit treatment, amount reveal, above/below average badges, next/skip actions.
- Same Final Results: mode-complete badge, Fate Has Spoken heading, total bill card, exact sum verification, ranked breakdown with Biggest Hit, Share Results and New Split.
- Same History and Settings information architecture.
- Same share modal actions where Android equivalents are needed.
- Preserve behavior as closely as possible: exact-total allocation, 50-entry local history, settings persistence, sound/haptics, reduced motion.
- Native Android implementation is acceptable, but it must visually and behaviorally reproduce the web app rather than use generic Material 3 screens.

Use the web source files in BILLROULETTE as the implementation reference before changing Android UI.
