# CryptoPulse Audio

Android companion for Bluetooth audio glasses/headsets: live Gains prices, ticker-first spoken movement alerts, and ranked financial news. Android 8+, notifications and background monitoring. No account, API key, wallet or trading access.

Crypto is limited to **BTC, ETH, LINK and LTC**. Gold, silver, S&P 500, Nasdaq 100 and WTI oil are enabled by default; EUR/USD is optional. Spoken price updates contain only **“BTC up 0.13%”** or **“ETH down 0.13%”**: ticker, direction and percentage since the previous fresh price check. No price, source or extra narration. Moves rounding to `0.00%` are silent in both alerts and optional digests. The first quote sets the comparison; every newer accepted quote advances it, including when threshold/cooldown suppress speech.

Nine ranked feeds cover mainstream business, markets, commodities and the selected crypto. Government feeds, `.gov` URLs, CNBC and MarketWatch are blocked. **News checks default to 60 seconds and cannot be set below 60 seconds.** Server ETag/Last-Modified validators are retained; supporting feeds return HTTP 304 without redownloading their body. Prices use a persistent Gains WebSocket independently of news timing. RSS publishing/caching can introduce news delays.

## Install and use
Download the APK from [Releases](https://github.com/requiredtruth/crypto-pulse-audio/releases/latest), allow notifications, select the glasses as Android media output, then tap **Test headset → Start monitoring**. Customize markets, global/per-market movement thresholds, alert cooldown in seconds, source ranking, quiet hours, offline English voices, speed/pitch/volume and background settings. The headset setting checks connection presence; Android selects the audio route. A user-granted battery exemption can improve screen-off delivery; Android power rules and force-stop can still affect it.

## Build and test
Full OpenJDK 17, curl, zip and unzip are required on Linux.

```bash
./install.sh      # Idempotent Android SDK setup and license review
./test.sh         # Offline core, feed parser, price/policy checks
./test.sh --live  # Optional production live-feed integration checks
./build.sh        # Signed APK in dist/
./run.sh          # Setup/build and ADB install/launch
```

Set `ANDROID_SDK_ROOT` to reuse SDK platform 35/build-tools 35.0.0. `cli.sh` exposes test/build/install. Bundled Java-WebSocket and SLF4J support live prices; JSON-java is JVM-test-only. Dependency notices are in `licenses/`.

Public code and source ZIP exclude signing keys, credentials, SDK/build caches, local logs and captured article snapshots. Keep a stable private signing key for compatible updates. A local build creates a key under `build/` if absent. Releases contain `.apk`, source `.zip` and `spec.md`; publication is driven by explicit `main` commits or manual workflow invocation, with no cron/scheduled workflow.

See [spec.md](spec.md) for behavior and validation limits. Actual phone/glasses audio and Android lifecycle/power behavior require device acceptance testing.
