#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p build/tests
CLASSPATH_TEST='tests/json-20240303.jar:libs/Java-WebSocket-1.5.7.jar:libs/slf4j-api-2.0.6.jar'
java -m jdk.compiler/com.sun.tools.javac.Main -classpath "$CLASSPATH_TEST" -d build/tests app/src/main/java/com/cryptopulse/app/Core.java app/src/main/java/com/cryptopulse/app/FeedReader.java app/src/main/java/com/cryptopulse/app/PriceParser.java app/src/main/java/com/cryptopulse/app/Fetcher.java app/src/main/java/com/cryptopulse/app/GainsFeed.java tests/CoreTest.java tests/PriceTest.java tests/LiveTest.java tests/ProductionHttp.java tests/RuntimeProxy.java tests/ConditionalTest.java
java -cp "build/tests:$CLASSPATH_TEST" CoreTest
java -cp "build/tests:$CLASSPATH_TEST" PriceTest
java -cp "build/tests:$CLASSPATH_TEST" com.cryptopulse.app.ConditionalTest
if [ "${1:-}" = '--live' ]; then java -cp "build/tests:$CLASSPATH_TEST" LiveTest; fi
if [ "${1:-}" = '--stream' ]; then java -cp "build/tests:$CLASSPATH_TEST" LiveTest --stream; fi
