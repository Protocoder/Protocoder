README
======
launch intent
./adb shell am start -n com.makewithmoto.apprunner/com.makewithmoto.apprunner.AppRunnerActivity -e project_name ioio_1

transfer files ssh 
scp <file to upload> <username>@<hostname>:<destination path>


=====================
#!/bin/bash


adb forward tcp:8080 tcp:8080
adb forward tcp:8081 tcp:8081

URL="http://localhost:8080"

[[ -x $BROWSER ]] && exec "$BROWSER" "$URL"

path=$(which xdg-open || which gnome-open || which open) && exec "$path" "$URL"

echo "Can't find browser"
=====================

