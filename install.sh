#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
command -v java >/dev/null || { echo 'Install OpenJDK 17 first.'; exit 1; }
java -m jdk.compiler/com.sun.tools.javac.Main -version >/dev/null 2>&1 || { echo 'Install a full OpenJDK 17 JDK (compiler module required).'; exit 1; }
for tool in curl unzip zip; do command -v "$tool" >/dev/null || { echo "Install $tool first."; exit 1; }; done
SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$PWD/.android-sdk}}"
if [ -f "$SDK/platforms/android-35/android.jar" ] && [ -x "$SDK/build-tools/35.0.0/d8" ]; then echo 'Android build dependencies are ready.'; exit 0; fi
if [ ! -x "$SDK/cmdline-tools/latest/bin/sdkmanager" ]; then
 TMP_INSTALL="$(mktemp -d)"
 trap 'rm -rf "$TMP_INSTALL"' EXIT
 curl --fail --location https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -o "$TMP_INSTALL/sdk.zip"
 unzip -q "$TMP_INSTALL/sdk.zip" -d "$TMP_INSTALL"
 mkdir -p "$SDK/cmdline-tools"
 mv "$TMP_INSTALL/cmdline-tools" "$SDK/cmdline-tools/latest"
fi
# sdkmanager prompts for Google's licenses. Read and accept them interactively.
"$SDK/cmdline-tools/latest/bin/sdkmanager" --sdk_root="$SDK" --licenses
"$SDK/cmdline-tools/latest/bin/sdkmanager" --sdk_root="$SDK" 'platforms;android-35' 'build-tools;35.0.0' 'platform-tools'
echo 'Ready. Run ./build.sh.'
