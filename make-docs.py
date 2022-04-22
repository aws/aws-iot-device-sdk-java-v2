#!/usr/bin/env python3
import os
import shutil

if os.system('mvn clean') != 0:
    exit(1)

if os.system('mvn install') != 0:
    exit(1)

if os.system('mvn javadoc:javadoc') != 0:
    exit(1)

if os.path.exists('docs'):
    shutil.rmtree('docs')
shutil.copytree('sdk/target/site/apidocs', 'docs')
