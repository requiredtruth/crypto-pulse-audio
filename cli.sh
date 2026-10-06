#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
case "${1:-test}" in
 test) shift || true; exec ./test.sh "$@";;
 build) exec ./build.sh;;
 install) exec ./install.sh;;
 *) echo 'Usage: ./cli.sh [test|build|install]'; exit 1;;
esac
