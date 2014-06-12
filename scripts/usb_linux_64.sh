#!/bin/bash

cd "$(dirname "$0")"

./adb forward tcp:8585 tcp:8585
./adb forward tcp:8587 tcp:8587

