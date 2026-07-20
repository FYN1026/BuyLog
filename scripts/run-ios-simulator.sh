#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SIMULATOR="${1:-iPhone 16}"
DERIVED_DATA="$ROOT/build/iosDerivedData"
APP="$DERIVED_DATA/Build/Products/Debug-iphonesimulator/iosApp.app"

echo "Building iosApp for simulator: $SIMULATOR"
cd "$ROOT/iosApp"
xcodebuild \
  -project iosApp.xcodeproj \
  -scheme iosApp \
  -destination "platform=iOS Simulator,name=$SIMULATOR" \
  -configuration Debug \
  -derivedDataPath "$DERIVED_DATA" \
  build

echo "Opening Simulator..."
open -a Simulator

echo "Installing and launching BuyLog..."
xcrun simctl boot "$SIMULATOR" 2>/dev/null || true
xcrun simctl install "$SIMULATOR" "$APP"
PID=$(xcrun simctl launch "$SIMULATOR" com.buylog.ios)
sleep 2
if xcrun simctl spawn "$SIMULATOR" launchctl list 2>/dev/null | rg -q "com.buylog.ios"; then
  echo "Done. BuyLog is running in Simulator ($PID)."
else
  echo "Error: App launched but exited immediately. Check ~/Library/Logs/DiagnosticReports/iosApp*.ips"
  exit 1
fi
