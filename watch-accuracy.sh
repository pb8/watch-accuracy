#!/bin/sh

arg1=$1
arg2=$2

#echo "arg1: $arg1"
#echo "arg2: $arg2"

##directory where jar file is located    
dir=out/artifacts/watch_accuracy_jar

##jar file name
jar_name=watch-accuracy.jar

## Permform some validation on input arguments, one example below
#if [ -z "$1" ] || [ -z "$2" ]; then
#        echo "Missing arguments, exiting.."
#        echo "Usage : $0 arg1 arg2"
#        exit 1
#fi

java -jar $dir/$jar_name $arg1 $arg2

