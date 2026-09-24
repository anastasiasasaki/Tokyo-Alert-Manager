#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC="$JAVA_HOME/bin/javac"
    JAVA="$JAVA_HOME/bin/java"
else
    JAVAC=javac
    JAVA=java
fi
if ! command -v "$JAVAC" >/dev/null 2>&1; then
    echo "JDK 17 or later is required. Select a JDK in your IDE or set JAVA_HOME."
    exit 1
fi
mkdir -p build
rm -rf build/classes
mkdir -p build/classes
find "$PWD/src/main/java" -name '*.java' -print | while IFS= read -r file; do
    printf '"%s"\n' "$file"
done > build/sources.txt
"$JAVAC" --release 17 -encoding UTF-8 -d build/classes @build/sources.txt
"$JAVA" -cp build/classes alerts.Main
