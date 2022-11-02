# Part of standard packages in Python 3.4+
import subprocess
import time
import argparse

parser = argparse.ArgumentParser(
    prog="SBA sample launcher")
parser.add_argument("-d", "--duration")
parser.add_argument("-f", "--file_name")
args = parser.parse_args()

arg_duration = int(args.duration)
arg_filename = args.file_name

print ("\nPYTHON HELPER START\n")

# Install the SDK
subprocess.Popen("mvn clean install -Dmaven.test.skip=true", shell=True).wait(timeout=60)

application_command = "mvn compile exec:java -pl samples/SBA_Sample -Dexec.mainClass=sba_sample.SBA_Sample -Dexec.args="
# Add 2 to the duration to account for the sleep time
application_command += f"\"--max_time {arg_duration+2}\""
application_process = subprocess.Popen(application_command, shell=True)
# Can get PID using application_process.pid

# Sleep 2 seconds to let it all start up and get ready
time.sleep(2)

record_command = f"psrecord {application_process.pid} --plot {arg_filename} --include-children --duration {arg_duration}"
record_process = subprocess.Popen(record_command, shell=True)

while True:
    try:
        return_code = application_process.poll()
        if (return_code != None):
            print ("\n[PYTHON] APPLICATION FINISHED!\n")
            record_process.terminate()
            record_process.wait()
            exit(0)
        return_code_record = record_process.poll()
        if (return_code_record != None):
            print ("\n[PYTHON] RECORD FINISHED!\n")
            application_process.terminate()
            application_process.wait()
            exit (0)
    except Exception as ex:
        print ("\n[PYTHON] EXCEPTION OCCURRED!\n")
        print (ex)
        exit(0)

    time.sleep(1)
