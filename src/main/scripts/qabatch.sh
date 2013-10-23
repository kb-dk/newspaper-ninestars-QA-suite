#!/bin/bash

SCRIPT_DIR=$(dirname $(readlink -f $0))

java -classpath "$SCRIPT_DIR/../conf:$SCRIPT_DIR/../lib/*" \
    dk.statsbiblioteket.medieplatform.newspaper.ninestars.NinestarsBatchQA  \
   "$1" "$2"

