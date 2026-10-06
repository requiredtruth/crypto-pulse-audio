# Validation — v1.1.1

- 123 offline JVM checks pass: 28 core/feed checks, 71 live-price/policy checks, eight conditional HTTP checks and 16 upgrade migration checks.
- Regression coverage includes positive/negative tiny moves, signed zero, invalid changes, two-decimal rounding boundaries, zero-only and mixed digests, clean ticker-only speech, excluded publisher roots/subdomains, stale queued speech/history/cache cleanup, customization preservation and idempotent upgrades.
- Android platform 35 compilation, dex/resources/ZIP alignment and APK signature verification pass. Same private signing key as v1.1.0; minimum API 26, target API 35.
- The release workflow verifies prepared checksums and production live crypto transport with all four requested tickers plus the complete ten-market bootstrap snapshot. Commodity/index market closures do not fail transport validation.
- The nine retained news feeds were successfully parsed during v1.1.0 validation. Network availability and publisher delays can change.
- No physical Android phone or Bluetooth glasses is available here; audio routing, TTS pronunciation, screen-off delivery and lifecycle behavior require device acceptance testing.

Price speech contains only `BTC up 0.13%` style messages since the immediately preceding fresh quote. Every newer accepted quote advances the comparison even when speech is suppressed; zero/rounded-zero changes are silent. News interval remains minimum/default 60 seconds with persisted ETag/Last-Modified conditional requests and no body reads on HTTP 304.
