#!/bin/bash

cd "$(dirname "$0")"

./adb shell am start -n com.makewithmoto/com.makewithmoto.LauncherActivity

