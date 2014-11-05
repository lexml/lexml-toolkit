#!/bin/sh

CURDIR="$(pwd)"
SCRIPT="$(which $0)"
if [ "x$(echo $SCRIPT | grep '^\/')" = "x" ] ; then
    SCRIPT="$PWD/$SCRIPT"
fi
WORKDIR="$(dirname $SCRIPT)"
cd $WORKDIR

./toolkit.sh instalar

cd $CURDIR
