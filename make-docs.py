#!/usr/bin/env python3
import os

if os.system("mvn clean package") != 0:
    exit("maven clean package not built.")

if os.system("mvn javadoc:javadoc") != 0:
    exit("Maven javadoc plugin did not run correctly.")