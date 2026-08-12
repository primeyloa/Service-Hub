#!/usr/bin/env bash
# Compiles and runs the Correctness Pack without needing Maven installed.
# Usage: ./run_tests.sh
set -euo pipefail
cd "$(dirname "$0")"

mkdir -p out
echo "Compiling..."
javac -d out src/main/java/ds/*.java
javac -cp "out:lib/*" -d out src/test/java/correctness/util/*.java src/test/java/correctness/*.java

echo "Running the Correctness Pack..."
java -jar lib/junit-platform-console-standalone.jar \
  --class-path out \
  --scan-class-path \
  --details=tree
