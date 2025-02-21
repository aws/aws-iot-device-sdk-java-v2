import Builder
import sys
import os
import os.path


class V2SdkTest(Builder.Action):

    def _run_service_tests(self, test_suite_name, *extra_args):
        if os.path.exists('log.txt'):
            os.remove('log.txt')

        cmd_args = [
            "mvn", "-B",
            "-DredirectTestOutputToFile=true",
            "-DreuseForks=false",
            "-Daws.crt.aws_trace_log_per_test",
            "-Daws.crt.ci=true"
        ]
        cmd_args.extend(extra_args)
        cmd_args.append("test")
        cmd_args.append("-Dtest=" + test_suite_name)

        result = self.env.shell.exec(*cmd_args, check=False)
        if result.returncode:
            if os.path.exists('log.txt'):
                print("--- CRT logs from failing test ---")
                with open('log.txt', 'r') as log:
                    print(log.read())
                print("----------------------------------")
            sys.exit(f"Tests failed")

    def start_maven_tests(self, env):
        self._run_service_tests("JobsTests", "-DrerunFailingTestsCount=5")
        self._run_service_tests("IdentityTests", "-DrerunFailingTestsCount=5")
        self._run_service_tests("ShadowTests", "-DrerunFailingTestsCount=5")


    def run(self, env):
        self.env = env

        return Builder.Script([
            Builder.SetupCrossCICrtEnvironment(),
            self.start_maven_tests  # Then run the Maven stuff
        ])
