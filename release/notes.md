CryptoPulse Audio v1.1.0

- Live Gains prices for BTC, ETH, LINK, LTC, gold, silver, S&P 500, Nasdaq 100, WTI oil and optional EUR/USD.
- Clean price speech: `BTC up 0.13%`, calculated since the previous fresh quote. No extra narration.
- Eleven ranked financial/news feeds; government feeds and .gov URLs blocked.
- News checks minimum/default 60 seconds; persistent ETag/Last-Modified conditional requests avoid repeat body downloads when supported.
- Custom market thresholds/cooldowns, feed ranking, quiet hours, voices, notifications and foreground monitoring.

Offline checks: 81 pass. APK compiled and signatures verified. Production JVM live-stream connection received the four requested crypto tickers; all ten markets loaded in bootstrap. All eleven news feeds passed the production parser. Physical phone/glasses audio and Android power/lifecycle behavior remain device acceptance items; see VALIDATION.md in the source ZIP.
