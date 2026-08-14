#!/usr/bin/env bash
# DCIT204 - Runs Unified Test Pack with Maven (no direct javac .sh)
set -e
cd "$(dirname "$0")"
echo "Running Unified Data Structure Test Pack with Maven..."
mvn clean test
