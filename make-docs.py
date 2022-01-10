#!/usr/bin/env python3
import os
import shutil
import subprocess

subprocess.run(['mvn', 'clean', 'javadoc:javadoc'], check=True)

if os.path.exists('docs'):
    shutil.rmtree('docs')
shutil.copytree('sdk/target/site/apidocs', 'docs')
