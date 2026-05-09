#!/usr/bin/env bash
set -euo pipefail
GRADLE_VERSION="9.0.0"
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
DIST_DIR="$ROOT_DIR/.gradle/local-gradle-$GRADLE_VERSION"
ZIP_FILE="$ROOT_DIR/.gradle/gradle-$GRADLE_VERSION-bin.zip"
if [ ! -x "$DIST_DIR/bin/gradle" ]; then
  mkdir -p "$ROOT_DIR/.gradle"
  curl -L "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$ZIP_FILE"
  unzip -q "$ZIP_FILE" -d "$ROOT_DIR/.gradle"
  mv "$ROOT_DIR/.gradle/gradle-$GRADLE_VERSION" "$DIST_DIR"
fi
exec "$DIST_DIR/bin/gradle" "$@"
