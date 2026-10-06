#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$PWD/.android-sdk}}"
BT="$SDK/build-tools/35.0.0"
JAR="$SDK/platforms/android-35/android.jar"
[ -f "$JAR" ] || { echo 'Set ANDROID_SDK_ROOT to Android SDK (platform 35 and build-tools 35.0.0).'; exit 1; }
mkdir -p build/classes build/dex dist
rm -f build/classes/com/cryptopulse/app/*.class build/dex/classes*.dex
java -m jdk.compiler/com.sun.tools.javac.Main -source 8 -target 8 -bootclasspath "$JAR:$BT/core-lambda-stubs.jar" -classpath "libs/Java-WebSocket-1.5.7.jar:libs/slf4j-api-2.0.6.jar" -d build/classes app/src/main/java/com/cryptopulse/app/*.java
"$BT/d8" --lib "$JAR" --min-api 26 --output build/dex build/classes/com/cryptopulse/app/*.class libs/Java-WebSocket-1.5.7.jar libs/slf4j-api-2.0.6.jar
"$BT/aapt" package -f -M app/src/main/AndroidManifest.xml -I "$JAR" -S app/src/main/res -F build/unsigned.apk
(cd build/dex && zip -q -u ../unsigned.apk classes*.dex)
"$BT/zipalign" -f -p 4 build/unsigned.apk build/aligned.apk
# Keep the generated local key private and stable for future APK updates.
if [ ! -f build/signing.jks ]; then keytool -genkeypair -keystore build/signing.jks -storepass android -keypass android -alias cryptopulse -keyalg RSA -keysize 2048 -validity 10000 -dname 'CN=CryptoPulse Audio Local Build'; fi
"$BT/apksigner" sign --ks build/signing.jks --ks-pass pass:android --out dist/CryptoPulse-Audio-v1.1.1.apk build/aligned.apk
"$BT/apksigner" verify --verbose dist/CryptoPulse-Audio-v1.1.1.apk
