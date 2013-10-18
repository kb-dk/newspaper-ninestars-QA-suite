#!/bin/bash

#!/bin/sh

SCRIPT_DIR=$(dirname "$0")

java -classpath "$SCRIPT_DIR/../lib/*" \
    dk.statsbiblioteket.medieplatform.newspaper.ninestars.NinestarsSuite  \
   $1 $2

