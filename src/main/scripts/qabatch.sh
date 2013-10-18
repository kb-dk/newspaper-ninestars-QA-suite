#!/bin/bash

#!/bin/sh

SCRIPT_DIR=$(dirname "$0")

java -classpath "$SCRIPT_DIR/../lib/*" \
    dk.statsbiblioteket.medieplatform.newspaper.ninestars.NinestarsSuite  \
    $SCRIPT_DIR/../conf/config.properties \
    -b $1

