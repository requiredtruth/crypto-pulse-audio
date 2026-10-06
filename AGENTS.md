# Repository instructions

This is a PUBLIC Android project. The owner authorizes routine development commits and releases directly on `main`; always use `main`, with no feature branches or pull requests unless the owner changes that instruction. Releases must include a signed APK, source ZIP and Markdown specification. Do not create cron/scheduled workflows or external scheduled tasks.

Keep signing keys, credentials, personal data, captured article bodies, SDK/build caches and local logs out of the public repository and source ZIP. Reuse the owner's private signing key for compatible APK updates; never publish it.

Product constraints: BTC, ETH, LINK and LTC are the only cryptocurrencies. Important other markets include gold and silver. Spoken price updates are exactly `TICKER up/down 0.13%`, calculated since the previous fresh accepted price check, with no extra narration. First quote is silent; suppressed ticks still advance comparison. News checks default to and enforce at least 60 seconds; retain server ETag/Last-Modified validators and avoid body reads on HTTP 304. Government feeds and `.gov` hosts are blocked. Preserve customization, ranked repetition suppression, notifications and Bluetooth-headset/background audio behavior.

Run `./test.sh` for offline validation and rebuild/sign/verify the APK for app changes. The release workflow verifies prepared artifact hashes and production live crypto transport before publishing; commodity/index market closures must not fail the transport check. Record physical-device testing limits honestly.
