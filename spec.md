# CryptoPulse Audio v1.1.0 — specification

## Goal and deliverables
Native Android phone companion for Bluetooth audio glasses/headsets: stream financial-market prices, check ranked news and speak accepted updates in the background. Signed APK with launcher and notification icons, complete source ZIP and this Markdown spec are published as a GitHub Release.

Public repository: https://github.com/requiredtruth/crypto-pulse-audio. Development commits always go to `main`. No scheduled task or cron workflow. Release publication runs on an explicit prepared-release commit or manual workflow invocation.

## Markets
| Market | Exact Gains base | Default | Unit |
|---|---|---|---|
| Bitcoin | BTC | On | USD |
| Ethereum | ETH | On | USD |
| Chainlink | LINK | On | USD |
| Litecoin | LTC | On | USD |
| Gold | XAU | On | USD/troy ounce |
| Silver | XAG | On | USD/troy ounce |
| S&P 500 | SPX500 | On | Index points |
| Nasdaq 100 | NAS100 | On | Index points |
| WTI oil | WTI | On | USD/barrel |
| EUR/USD | EUR | Off | USD/euro |

Crypto selection is restricted to the four requested coins. Resolve market IDs by exact registry base/quote; the S&P 500 index uses `SPX500/USD`, not the unrelated SPX token. No trending-token or altcoin discovery.

## Live prices and clean ticker speech
- Stream: `wss://backend-pricing.eu.gains.trade/v4`.
- Registry: `https://backend-polygon.gains.trade/trading-variables/pairs`; array index identifies each exact `from`/`to=USD` market. Cached mapping supports a temporary registry outage; bootstrap refreshes it per session/after six hours.
- Current snapshot: `https://backend-pricing.eu.gains.trade/charts`, for bootstrap and rate-limited recovery. It is not a historical-candle endpoint.
- Support v4 `{i:[pair,price,...],m:[pair,price,...],t:timestamp}`, preferring an index observation to the mark observation for the same ID in a frame. Also support observed legacy flat price arrays and timestamp checkpoints. Source labels distinguish index, mark, legacy live and snapshot values.
- Reject invalid prices/IDs, odd arrays, price frames older than 30 seconds or over five seconds in the future, and snapshots older than two minutes. Normalize second/millisecond timestamps. Older ticks and exact same-time/same-price duplicates are ignored. Changed prices sharing a stream checkpoint are accepted in arrival order.
- Alert evaluation occurs on each accepted selected-market tick, independently of news network tasks. Reconnect backoff reaches 30 seconds; a 20-second stream-silence watchdog and connection checks trigger recovery. Snapshot recovery is limited to approximately once per minute while disconnected.
- Monitoring percentage is `(current / first monitoring baseline - 1) × 100`. Alert percentage uses the immediately preceding fresh quote in the active service session. Every newer accepted quote advances the comparison, even below threshold or during cooldown; it never accumulates movement since a prior alert. The first quote initializes it without speaking. Restarting monitoring reinitializes this check comparison; the overview monitoring baseline persists locally and can be reset. Labels show their time; no current OHLC opening value is presented as a 24-hour opening price.
- Global movement threshold defaults to **0.25%**, editable **0.01%–50%**. Individual market overrides are supported. Input is hundredths of a percent: `25 = 0.25%`. Exact threshold crossings include floating-point tolerance.
- Per-market cooldown defaults to **30 seconds**, editable **0–3600 seconds**. Zero allows all threshold crossings; unchanged ticks do not create announcements.
- Every price announcement is exactly `TICKER up/down 0.13%`, for example `BTC up 0.13%` or `ETH down 0.13%`. Fixed two decimal places, absolute percentage, no human name, current price, source, time, prefix or suffix. “Since last checked” describes the calculation and is not spoken. TTS pronunciation of the percent sign depends on the selected Android voice.
- Periodic digest is off by default. If enabled, interval defaults to 30 minutes, editable 5–720 minutes, and each recent market entry uses the same clean ticker/change format with no extra narration. Pending market alerts replace that market's older unspoken alert while preserving active speech.
- Overview refreshes once per second. In-memory quote publication is throttled to once per second and disk snapshots to roughly once per 30 seconds. Update/source/baseline timestamps remain visible; observations over one minute old are labeled no-recent-tick/may-be-closed. Indices, commodities and forex may close outside their trading sessions. A global snapshot time does not certify every individual market was trading at that instant.

## News catalog
| Rank | Source | Feed |
|---|---|---|
| 1 | CoinDesk | https://www.coindesk.com/arc/outboundfeeds/rss/ |
| 2 | CNBC Markets | https://search.cnbc.com/rs/search/combinedcms/view.xml?partnerId=wrss01&id=100003114 |
| 3 | Bloomberg Markets | https://feeds.bloomberg.com/markets/news.rss |
| 4 | MarketWatch | https://feeds.marketwatch.com/marketwatch/topstories/ |
| 5 | Financial Times | https://www.ft.com/rss/home |
| 6 | BBC Business | https://feeds.bbci.co.uk/news/business/rss.xml |
| 7 | Guardian Business | https://www.theguardian.com/business/rss |
| 8 | Investing Commodities | https://www.investing.com/rss/news_11.rss |
| 9 | FXStreet | https://www.fxstreet.com/rss/news |
| 10 | Decrypt | https://decrypt.co/feed |
| 11 | Cointelegraph | https://cointelegraph.com/rss |

Ranks are editable preferences, not factual reliability scores. Each feed can be disabled or ranked 1–99; up to ten custom feeds can be added/removed.

### Government-source policy
Federal Reserve/government feeds are removed. All app HTTP requests, redirects, custom feed URLs, parsed article URLs and direct article opens require HTTPS and reject host labels named `gov`, including `gov.uk`. Embedded URL credentials are rejected. The external browser handles its own later redirects. Private-publisher reporting about monetary policy remains available.

Upgrade migration replaces the former crypto watchlist, clears former exchange quotes and pending speech/logs, removes government story/dedup/custom-source entries and initializes live-alert defaults while preserving other voice/background preferences.

### Conditional checks and timing
- Default and hard minimum **60 seconds**; configurable **60–3600 seconds**.
- Server `ETag` and `Last-Modified` values are stored and reused as `If-None-Match`/`If-Modified-Since`, including after service restart. A supporting server returns HTTP **304 Not Modified**, with no news-body download or reparse. Changed feeds return their new body and validators. Servers without validators or which ignore them can still return a body; the app cannot promise metadata-only checks from every publisher.
- Six-worker network pool, one active request per source. Successful next-check timing starts at completion, maintaining the background minimum interval. Failed sources independently back off 60, 120, 240, 480, then at most 600 seconds; cached validators are cleared after failures.
- Newly fetched stories enter a **one-second arrival batch**, then grouping/ranking and delivery. Slow feeds do not block price events or hold all other news. Highest-ranked available similar story wins the batch; a later higher-ranked version does not reread accepted news.
- Publisher feed publishing/caching, HTTP limits, connectivity and Android power rules add latency. This is conditional RSS polling; prices are streamed. The app cannot receive news before the publisher exposes it in its public feed. Manual Check now is a deliberate refresh command.

### Filtering, ranking and repetition
News must match selected-market aliases or optional macro/financial keywords. Crypto-specific titles require a selected BTC/ETH/LINK/LTC match; general business mode does not bypass this. Titles about another coin can still qualify if they also explicitly concern a requested selected coin.

General-business mode defaults on for mainstream business/commodity feeds, accepting regular corporate and financial headlines without a specific market keyword. Macro mode includes rates, inflation, stocks, earnings, banking, bonds, dollar/tariff/economic news. Both modes can be disabled. Custom feeds use the market/macro filter.

Default maximum ten new headlines per arrival batch (1–20 configurable); default maximum age six hours (1–48 configurable). Undated/unparseable items and those over five minutes in the future are skipped. Read attributed titles by default; optional short RSS synopsis is capped at 240 characters. Original links are available; no paywall/full-article scraping or independent fact-checking.

Exact links and normalized-title Jaccard similarity suppress repetitions for seven days, capped at 1000 records. Default similarity 60% (35–95% editable). Distinct named market sets remain separate. Lexical matching can miss differently phrased repetitions or group similarly worded separate events. History/delivery logs are capped and source health/last-check/304 states are visible.

## Android, audio and background
Android 8/API 26 minimum, API 35 target/compile. Native Java/framework UI, four pages: Overview, News, Controls, Sources. Custom vector app icon and monochrome notification icon. No WebView, server, account, analytics, API key, wallet or trading operations.

User-started foreground service declares Android `specialUse` for continuous monitoring with intermittent speech. Persistent low-importance notification provides Check now, Mute/Unmute and Stop. Separate configurable news/market notifications retain at most twenty updates; Android 13+ permission is requested. A future Play Store submission would need review of the special-use declaration; this release is a sideload APK.

Android TextToSpeech uses installed English voices, offline voice selection, speed 50–200%, pitch 50–150%, volume 5–100% of Android media volume, navigation/speech audio attributes and transient focus/ducking. Focus loss interrupts and retains speech; three-minute watchdog, retry and completion history are supported.

Connected-headset requirement defaults on and detects Bluetooth A2DP/SCO/BLE, wired or USB headsets. Android chooses the active output; select the glasses for media audio and verify with Test headset. Last-headset disconnect/noisy-audio event interrupts playback. One utterance at a time, persisted queue capped at twenty, default expiry thirty minutes (5–180 editable), explicit clear queue. Test headset does not enable continuous monitoring from the stopped state.

Local quiet hours support overnight/all-day windows and silence speech while checks/notifications continue. Voice mute is independent. A partial CPU wake lock is enabled during monitoring by default, can be disabled, and is released on Stop. User-requested battery exemption and App info shortcuts help reliability. START_STICKY and configurable reboot resume/reminder are included. Android power rules, force-stop, unavailable TTS voices/connectivity and market closures can still delay/stop delivery. No exact alarm or scheduled ChatGPT task.

Permissions: Internet, foreground/special-use service, notifications, wake lock, boot reception and user-requested battery exemption. No microphone, camera, contacts, location, wallet or Bluetooth scan/pairing permission.

## Data, source and validation
Preferences, baselines, capped histories, pending speech and registry/HTTP-validator caches are local private app data; backup is disabled. Requests go directly to public providers and reveal ordinary network metadata. HTTPS-only redirects capped at five, bodies at 2 MB. RSS timeouts six/eight seconds and Gains bootstrap ten/twelve seconds. XML DTD/entities/external resolution are blocked; BOM and RSS/Atom/ISO/plain UTC dates are supported.

Public source/release ZIP excludes private signing keys, credentials, SDK/build caches, local logs and captured article snapshots. Dependencies: Java-WebSocket 1.5.7 and SLF4J API 2.0.6 for the app; JSON-java 20240303 for JVM tests only. License texts are in `licenses/`. Test-only runtime proxy/CA support uses the test environment's configured settings with TLS verification enabled and is not packaged into Android.

`install.sh` handles idempotent SDK setup/interactive licenses; `build.sh` compiles/dexes/packages/aligns/signs/verifies; `test.sh` runs deterministic checks and `--live` adds production feed integration; `run.sh` installs/launches with ADB; `cli.sh` exposes commands. Private release keys stay outside public code. Prepared, tested artifacts are published from `main` with `.apk`, source `.zip`, `spec.md` assets.

Validation is recorded in `VALIDATION.md`. Automated compilation/signature checks, logic/parser tests and public endpoint observations do not replace actual-phone/glasses routing, TTS, power, foreground-service or reboot acceptance testing. No physical Android/glasses was available here.

## Primary references
- https://docs.gains.trade/developer/integrators/price-feed
- https://docs.gains.trade/developer/integrators/guides/mark-%2B-index-introduction
- https://docs.gains.trade/developer/integrators/guides/building-the-virtual-order-book
- https://www.investing.com/webmaster-tools/rss
- https://developer.android.com/develop/background-work/services/fgs/service-types
