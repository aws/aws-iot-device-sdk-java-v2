#!/usr/bin/env bash

pushd $(dirname $0) > /dev/null

javadoc @javadoc.options

popd > /dev/null
