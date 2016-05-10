#!/bin/sh

CURDIR="$(pwd)"
SCRIPT="$(which $0)"
if [ "x$(echo $SCRIPT | grep '^\/')" = "x" ] ; then
    SCRIPT="$PWD/$SCRIPT"
fi
WORKDIR="$(dirname $SCRIPT)"
cd $WORKDIR

JAVA_OPTS="-Xms128M -Xmx512M -Dfile.encoding=UTF-8 -Dlog.dir=../log"
CLASSPATH=$(ls ../lib/*.jar | xargs echo | tr ' ' ':')
COMMAND="java -cp ../etc:$CLASSPATH $JAVA_OPTS br.gov.lexml.borda.Toolkit $*"
#echo $COMMAND
$COMMAND

cd $CURDIR
