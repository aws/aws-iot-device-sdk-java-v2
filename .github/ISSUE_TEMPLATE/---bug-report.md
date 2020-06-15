---
name: "\U0001F41B Bug report"
about: Create a report to help us improve
title: ''
labels: bug, needs-triage
assignees: ''

---

Confirm by changing [ ] to [x] below to ensure that it's a bug:
- [ ] I've searched for [previous similar issues](https://github.com/aws/aws-iot-device-sdk-java-v2/issues) and didn't find any solution

**Known Issue**
- [ ] I'm using ATS data type endpoint: the endpoint should look like `<prefix>-ats.iot.<region>.amazonaws.com`

**Describe the bug**
A clear and concise description of what the bug is.

**SDK version number**

**Platform/OS/Hardware/Device**
What are you running the sdk on?

**To Reproduce (observed behavior)**
Steps to reproduce the behavior (please share code)

**Expected behavior**
A clear and concise description of what you expected to happen.

**Logs/output**
If applicable, add logs or error output.

To enable logging, set the following system properties:

*REMEMBER TO SANITIZE YOUR PERSONAL INFO*

```
-Daws.crt.debugnative=true
-Daws.crt.log.destination=File
-Daws.crt.log.level=Trace
-Daws.crt.log.filename=<path and filename>
```

**Additional context**
Add any other context about the problem here.
