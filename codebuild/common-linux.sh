#!/bin/bash

set -e

env

# build java package
cd $CODEBUILD_SRC_DIR

ulimit -c unlimited
mvn compile
