#!/usr/bin/env python3
"""Optional integration check using the current production client and feed catalog."""
import subprocess
raise SystemExit(subprocess.call(["./test.sh", "--live"]))
