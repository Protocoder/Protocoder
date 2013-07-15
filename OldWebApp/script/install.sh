#!/bin/bash -ex

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

(
  cd $DIR/../../android_scripting/MakeWithMotoAppRunner
  ant install
)

(
  cd $DIR/../../android_scripting/MakeWithMoto
  ant debug install
)