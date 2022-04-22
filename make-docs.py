#!/usr/bin/env python3
import os
import shutil

if os.system('mvn clean javadoc:javadoc --projects sdk') != 0:
    exit(1)

if os.path.exists('docs'):
    shutil.rmtree('docs')
shutil.copytree('sdk/target/site/apidocs', 'docs')
