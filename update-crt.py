#!/usr/bin/env python3

import argparse
import os
import re

VERSION_PATTERN = '\d+\.\d+\.\d+'

# Get new version
parser = argparse.ArgumentParser(description="Update files that contain aws-crt-java version number.")
parser.add_argument('version', help='aws-crt-java version. i.e. "0.1.2"')
args = parser.parse_args()
if re.fullmatch(VERSION_PATTERN, args.version) is None:
    exit(f'Invalid version: "{args.version}". Version must look like "0.1.2"')

os.chdir(os.path.dirname(__file__))


def update_file(filepath, *, preceded_by, followed_by):
    with open(filepath, 'r+') as f:
        txt_old = f.read()

        full_pattern = rf'({preceded_by}){VERSION_PATTERN}({followed_by})'
        full_replacement = rf'\g<1>{args.version}\g<2>'

        if len(re.findall(full_pattern, txt_old)) == 0:
            exit(f'Version not found in: {filepath}\n' +
                 f'Preceded by: "{preceded_by}"')

        txt_new = re.sub(full_pattern, full_replacement, txt_old)
        f.seek(0)
        f.write(txt_new)
        f.truncate()


update_file(filepath='sdk/pom.xml',
            preceded_by=r'<artifactId>aws-crt</artifactId>\s*<version>',
            followed_by=r'</version>')

update_file(filepath='README.md',
            preceded_by=r'--branch v',
            followed_by=r' .*aws-crt-java.git')

update_file(filepath='README.md',
            preceded_by=r"implementation 'software.amazon.awssdk.crt:android:",
            followed_by=r"'")

update_file(filepath='android/iotdevicesdk/build.gradle',
            preceded_by=r"api 'software.amazon.awssdk.crt:aws-crt-android:",
            followed_by=r"'")
