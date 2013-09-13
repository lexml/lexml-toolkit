#!/bin/sh

CURDIR="$(pwd)"
SCRIPT="$(which $0)"
if [ "x$(echo $SCRIPT | grep '^\/')" = "x" ] ; then
    SCRIPT="$PWD/$SCRIPT"
fi
WORKDIR="$(dirname $SCRIPT)"
cd $WORKDIR

java -cp . LauncherBootstrap -executablename lexml-toolkit run instalar

cd $CURDIR
