import os
from pathlib import Path
import re
import subprocess
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]


class BootstrapTests(unittest.TestCase):
    def commands(self, temp):
        template = (ROOT / "src/main/resources/project/Dockerfile.template").read_text().replace("\\\n", "")
        command = template.split("&& curl ", 1)[1].split("&& /root/.web3j/web3j", 1)[0]
        command = "curl " + command.rstrip().removesuffix("&&").rstrip()
        yield "Dockerfile", command.replace("/tmp/web3j-installer.sh", str(temp / "installer.sh"))
        readme = (ROOT / "README.rst").read_text()
        block = readme.split(".. code-block:: bash", 1)[1].split("This script", 1)[0]
        yield "README", "\n".join(line.strip() for line in block.strip().splitlines())

    def test_download_must_complete_before_execution(self):
        for failure in (False, True):
            with tempfile.TemporaryDirectory() as directory:
                temp = Path(directory)
                curl = temp / "curl"
                curl.write_text("#!" + sys.executable + "\n" + '''
import os, pathlib, sys
args = sys.argv[1:]
assert args[:5] == ["--disable", "--proto", "=https", "--proto-redir", "=https"], args
for flag in ["--fail", "--show-error", "--silent", "--location"]: assert flag in args
assert "https://get.web3j.io" in args
assert "|" not in args
output = pathlib.Path(args[args.index("-o") + 1])
output.write_text('printf executed > "$TEST_EXECUTION_MARKER"\\n')
if os.environ["TEST_FAIL_DOWNLOAD"] == "1": sys.exit(18)
''')
                curl.chmod(0o755)
                for name, command in self.commands(temp):
                    with self.subTest(source=name, failure=failure):
                        marker = temp / "executed"
                        marker.unlink(missing_ok=True)
                        env = dict(os.environ, PATH=str(temp) + ":/usr/bin:/bin",
                                   TEST_FAIL_DOWNLOAD="1" if failure else "0",
                                   TEST_EXECUTION_MARKER=str(marker), TMPDIR=str(temp))
                        result = subprocess.run(["sh", "-c", command], env=env, cwd=temp,
                                                capture_output=True, text=True, timeout=10)
                        if failure:
                            self.assertNotEqual(result.returncode, 0)
                            self.assertFalse(marker.exists(), "Partial download was executed")
                        else:
                            self.assertEqual(result.returncode, 0, result.stderr)
                            self.assertEqual(marker.read_text(), "executed")
                            self.assertFalse((temp / "installer.sh").exists())

    def test_container_installs_https_and_metadata_dependencies(self):
        text = (ROOT / "src/main/resources/project/Dockerfile.template").read_text()
        packages = text.split("apt-get install -y", 1)[1].split("&&", 1)[0]
        self.assertIn("ca-certificates", packages)
        self.assertRegex(packages, r"\bjq\b")


if __name__ == "__main__":
    unittest.main()

