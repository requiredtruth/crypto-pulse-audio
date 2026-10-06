# Validation — v1.1.0

- Offline JVM checks: 28 core/feed checks, 45 price/policy checks and eight conditional HTTP checks pass, including exact ticker-only messages, signs, all ten markets, timestamp rejection, thresholds, government URL rejection and XML safety.
- Android platform 35 Java compile, dex, resources, ZIP alignment and APK signature verification pass. Minimum API 26; target 35.
- Public Gains registry and current snapshot resolve all ten exact markets. Public `/v4` WebSocket observations include requested crypto and precious metals. Both documented v4 and observed legacy frame formats are supported.
- All eleven catalog RSS URLs were successfully retrieved in the preparation environment. Availability, caching and publication lag can change.
- The preparation environment's HTTP tunnel allows a Python live WebSocket observation but returns HTTP 400 to the JVM WebSocket integration check. The release workflow runs the production JVM client on GitHub's network and requires its stream check to pass before publication.
- No physical Android phone or Bluetooth glasses was available. Audio routing, installed TTS pronunciation, foreground service lifecycle, screen-off delivery and reboot behavior require device acceptance checks.

The price comparison is against the immediately preceding newer accepted quote, not the last announcement. First quote is silent; suppressed ticks still advance the comparison. Spoken alerts contain only `BTC up 0.13%` style text. News checks default to and enforce at least 60 seconds, sending stored server ETag/Last-Modified validators; compliant servers return 304 without a feed body.
