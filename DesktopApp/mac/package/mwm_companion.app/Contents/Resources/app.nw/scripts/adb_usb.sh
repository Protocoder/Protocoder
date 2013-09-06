#!/bin/bash

cd "$(dirname "$0")"

./adb forward tcp:8080 tcp:8080
./adb forward tcp:8081 tcp:8081

