#!/bin/bash

cd ${project.build.directory}

tar -xvzf ${project.artifactId}-${project.version}-bundle.tar.gz

cd ${project.artifactId}-${project.version}

./bin/qabatch.sh "$1" "$2"
