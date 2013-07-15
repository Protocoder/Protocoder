#!/bin/bash

cd "$(dirname "$0")"

./adb install MakeWithMoto.apk
./adb install MakeWithMotoAppRunner.apk
