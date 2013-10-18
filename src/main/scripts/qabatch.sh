#!/bin/bash

#!/bin/sh

$(dirname $(readlink -f $0))

java -classpath "$SCRIPT_DIR/../lib/*" \
    dk.statsbiblioteket.medieplatform.newspaper.ninestars.NinestarsSuite  \
   $1 $2

