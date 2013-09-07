#!/bin/bash

cd "$(dirname "$0")"

DEVICES=$(./adb devices | tail -n +2 | awk '{print $1}')
[[ -z "$DEVICES" ]] || echo "DEVICES!"

./adb forward tcp:8585 tcp:8585
./adb forward tcp:8587 tcp:8587

URL="http://localhost:8585"

[[ -x $BROWSER ]] && exec "$BROWSER" "$URL"

path=$(which xdg-open || which gnome-open || which open) && exec "$path" "$URL"

echo "Can't find browser"
