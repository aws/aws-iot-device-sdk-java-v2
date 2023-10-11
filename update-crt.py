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
parser.add_argument('--crt_version', nargs='?',
                    help='crt library version to use (i.e. "0.1.2"). default: automatically detect latest version')
parser.add_argument('--check-consistency', action='store_true',
                    help='Exit with error if version is inconsistent between files')
parser.add_argument('--update_samples', action='store_true',
                    help="Update the SDK samples to the latest SDK release OR to the passed in version")
parser.add_argument('--update_sdk_text', action='store_true',
                    help="Update the SDK text (readme, etc) to the latest SDK release OR to the passed in version")
args = parser.parse_args()

consistency_version = None
crt_version = None
sdk_version = None
def main():
    if args.crt_version is None:
        crt_version = get_latest_github_version("https://github.com/awslabs/aws-crt-java.git")
    elif re.fullmatch(VERSION_PATTERN, args.crt_version) is None:
        exit(f'Invalid CRT version: "{args.crt_version}". Must look like "0.1.2"')
    else:
        crt_version = args.crt_version
    print(f'Set CRT version: {crt_version}')

    if args.version is None:
        sdk_version = get_latest_github_version("https://github.com/aws/aws-iot-device-sdk-java-v2.git")
    elif re.fullmatch(VERSION_PATTERN, args.version) is None:
            exit(f'Invalid SDK version: "{args.version}". Must look like "0.1.2"')
    else:
        sdk_version = args.version
    print(f'Set SDK version: {sdk_version}')

    os.chdir(os.path.dirname(os.path.abspath(__file__)))

    # Check the sdk/pom.xml file first
    update(filepath='sdk/pom.xml',
        preceded_by=r'<artifactId>aws-crt</artifactId>\s*<version>',
        followed_by=r'</version>',
        update_version=crt_version)
    update(filepath='android/iotdevicesdk/build.gradle',
        preceded_by=r"api 'software.amazon.awssdk.crt:aws-crt-android:",
        followed_by=r"'",
        update_version=crt_version)

    # Update other files specified by the args
    if args.update_samples or args.check_consistency:
        update_samples(sdk_version)
    if args.update_sdk_text or args.check_consistency:
        update(filepath='README.md',
            preceded_by=r'<artifactId>aws-iot-device-sdk</artifactId>\s*<version>',
            followed_by=r'</version>',
            update_version=sdk_version)
        update(filepath='README.md',
            preceded_by=r"Replace `",
            followed_by=r"` in `<version>.*</version>` with the latest release version for the SDK.",
            update_version=sdk_version)
        update(filepath='README.md',
            preceded_by=r"Replace .* in `<version>",
            followed_by=r"</version>` with the latest release version for the SDK.",
            update_version=sdk_version)

def update(*, filepath, preceded_by, followed_by, update_version=None):
    """
    Args:
        filepath: File containing hard-coded CRT version numbers.
        preceded_by: Regex pattern for text preceding the CRT version number.
        followed_by: Regex pattern for text following the CRT version number.
    """
    with open(filepath, 'r+') as f:
        txt_old = f.read()

        full_pattern = rf'({preceded_by})({VERSION_PATTERN})({followed_by})'
        full_replacement = rf'\g<1>{update_version}\g<3>'

        matches = re.findall(full_pattern, txt_old)
        if len(matches) == 0:
            exit(f'Version not found in: {filepath}\n' +
                 f'Preceded by: "{preceded_by}"')

        if args.check_consistency:
            # in --check-consistency mode we remember the version from the first
            # file we scan, and then ensure all subsequent files use that version too
            for match in matches:
                found_version = match[1]
                if consistency_version is None:
                    print(f'Found version {found_version} in: {filepath}')
                    consistency_version = found_version
                elif found_version != consistency_version:
                    exit(f'Found different version {found_version} in: {filepath}')
        else:
            # running in normal mode, update the file
            txt_new = re.sub(full_pattern, full_replacement, txt_old)
            f.seek(0)
            f.write(txt_new)
            f.truncate()


def update_samples(sdk_version):
    sample_folders = [x[0] for x in os.walk("samples")]
    for sample_folder in sample_folders:
        sample_files = os.walk(sample_folder).__next__()[2]
        for file in sample_files:
            if file.endswith("pom.xml"):
                update(filepath=sample_folder + "/" + file,
                    preceded_by=r'<artifactId>aws-iot-device-sdk</artifactId>\s*<version>',
                    followed_by=r'</version>',
                    update_version=sdk_version)


def get_latest_github_version(github_repo="https://github.com/awslabs/aws-crt-java.git"):
    repo = github_repo
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
