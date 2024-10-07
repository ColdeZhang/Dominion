#!/usr/bin bash

mode=full

if [ $# -eq 1 ]; then
    mode=$1
fi

./gradlew clean

# all full lite
if [ $mode == "all" ]; then
    ./gradlew shadowJar -PBuildFull=false
    ./gradlew shadowJar -PBuildFull=true
elif [ $mode == "lite" ]; then
    ./gradlew shadowJar -PBuildFull=false
elif [ $mode == "full" ]; then
    ./gradlew shadowJar -PBuildFull=true
else
    echo "Invalid mode: $mode"
    echo "Usage: $0 [all|full|lite]"
    exit 1
fi

mkdir -p staging
mv build/libs/*.jar staging/
