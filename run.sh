#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
./install.sh
./build.sh
SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$PWD/.android-sdk}}"
if [ -x "$SDK/platform-tools/adb" ]; then
 "$SDK/platform-tools/adb" install -r dist/CryptoPulse-Audio-v1.1.0.apk
 "$SDK/platform-tools/adb" shell am start -n com.cryptopulse.app/.MainActivity
else
 echo 'APK built in dist/. Copy it to your Android phone and install it.'
fi
