#!/usr/bin/env python3

import argparse
import os
import re
import subprocess

VERSION_PATTERN = '\d+\.\d+\.\d+'

parser = argparse.ArgumentParser(
    description="Update files containing hard-coded aws-crt-java version numbers.")
parser.add_argument('version', nargs='?',
                    help='version to use (i.e. "0.1.2"). default: automatically detect latest version')
args = parser.parse_args()


def main():
    if args.version is None:
        args.version = get_latest_crt_version()
        print(f'Latest version: {args.version}')

    if re.fullmatch(VERSION_PATTERN, args.version) is None:
        exit(f'Invalid version: "{args.version}". Must look like "0.1.2"')

    os.chdir(os.path.dirname(__file__))

    update(filepath='sdk/pom.xml',
           preceded_by=r'<artifactId>aws-crt</artifactId>\s*<version>',
           followed_by=r'</version>')

    update(filepath='README.md',
           preceded_by=r'--branch v',
           followed_by=r' .*aws-crt-java.git')

    update(filepath='README.md',
           preceded_by=r"implementation 'software.amazon.awssdk.crt:android:",
           followed_by=r"'")

    update(filepath='android/iotdevicesdk/build.gradle',
           preceded_by=r"api 'software.amazon.awssdk.crt:aws-crt-android:",
           followed_by=r"'")


def update(*, filepath, preceded_by, followed_by):
    """
    Args:
        filepath: File containing hard-coded CRT version numbers.
        preceded_by: Regex pattern for text preceding the CRT version number.
        followed_by: Regex pattern for text following the CRT version number.
    """
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


def get_latest_crt_version():
    repo = 'https://github.com/awslabs/aws-crt-java.git'
    cmd = ['git', 'ls-remote', '--tags', repo]
    results = subprocess.run(cmd, check=True, capture_output=True, text=True)

    latest_str = None
    latest_nums = None
    for line in results.stdout.splitlines():
        # line looks like: "e18f041a0c8d17189f2eae2a32f16e0a7a3f0f1c refs/tags/v0.5.18"
        pattern = r'[a-f0-9]+\s+refs/tags/v((\d+)\.(\d+)\.(\d+))'
        match = re.fullmatch(pattern, line)
        if not match:
            continue

        # we only want the latest version
        # convert to tuple of numbers so we can compare
        version_str = match.group(1)
        version_nums = [int(match.group(x)) for x in range(3, 5)]
        if latest_nums is None or (version_nums > latest_nums):
            latest_nums = version_nums
            latest_str = version_str

    return latest_str


if __name__ == '__main__':
    main()
