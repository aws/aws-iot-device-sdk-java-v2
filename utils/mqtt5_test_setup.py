# Built-in
import argparse
import os
import sys
# Needs to be installed via pip
import boto3  # - for launching sample

def main():
    argument_parser = argparse.ArgumentParser(
        description="Run Sample in CI")
    argument_parser.add_argument("--s3_bucket", metavar="<S3 Bucket containing environment file>", required=True,
                                 help="The S3 bucket containing the environment variables file")
    argument_parser.add_argument("--s3_file", metavar="<S3 Bucket containing environment file>", required=True,
                                 help="The S3 bucket containing the environment variables file")
    argument_parser.add_argument("--cleanup", help="If set to 'true', then the files and data will be cleaned",
                                default="false", required=False)
    parsed_commands = argument_parser.parse_args()

    client = None
    try:
        client = boto3.client('s3')
    except Exception as ex:
        print ("ERROR - could not make client! Credentials may be missing")
        print (ex)
        sys.exit(1)

    try:
        client.download_file(parsed_commands.s3_bucket, parsed_commands.s3_file, "environment_files.txt")
    except Exception as ex:
        print ("ERROR - could not download S3 file! Credentials may not allow access to file or S3 URL is incorrect!")
        print (ex)
        sys.exit(1)

    with open ("environment_files.txt", 'r') as file:
        lines = file.read().splitlines()

        for line in lines:
            if "#" in line:
                continue
            data = line.split("=")
            if (len(data) == 2):
                # On Windows, we use setx - not because it is the best solution, but because it
                # persists and that is what we need.
                # NOTE: There is a 1024 character limit, but we do not have anything that long (usually)
                if (sys.platform == "win32"):
                    if (parsed_commands.cleanup == "true"):
                        # Set it to EMPTY to clear it
                        os.system(f"setx {data[0]} EMPTY")
                    else:
                        os.system(f"setx {data[0]} {data[1]}")
                else:
                    # untested...
                    if (parsed_commands.cleanup == "true"):
                        os.system(f"{data[0]}=EMPTY")
                    else:
                        os.system(f"{data[0]}={data[1]}")

    if (parsed_commands.cleanup == "true"):
        os.remove("environment_files.txt")

    print ("Environment variables set!")
    sys.exit(0)

if __name__ == "__main__":
    main()
