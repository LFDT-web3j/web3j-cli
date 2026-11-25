/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.console.project;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

import picocli.CommandLine.Mixin;

import org.web3j.console.project.utils.InputVerifier;
import org.web3j.console.project.utils.ProjectUtils;

import static org.web3j.codegen.Console.exitError;

public abstract class AbstractProjectCommand {

    @Mixin public ProjectOptions projectOptions = new ProjectOptions();

    protected final InteractiveOptions interactiveOptions;
    protected final InputVerifier inputVerifier;

    public AbstractProjectCommand() {
        this(System.in, System.out);
    }

    public AbstractProjectCommand(InputStream inputStream, PrintStream outputStream) {
        this(new InteractiveOptions(inputStream, outputStream), new InputVerifier(outputStream));
    }

    public AbstractProjectCommand(
            InteractiveOptions interactiveOptions, InputVerifier inputVerifier) {
        this.interactiveOptions = interactiveOptions;
        this.inputVerifier = inputVerifier;
    }

    protected void setupProject() {
        if (!inputIsValid(projectOptions.projectName, projectOptions.packageName)) return;

        projectOptions.projectName =
                projectOptions.projectName.substring(0, 1).toUpperCase()
                        + projectOptions.projectName.substring(1);
        final File projectDirectory =
                new File(projectOptions.outputDir, projectOptions.projectName);
        if (projectDirectory.exists()) {
            if (projectOptions.overwrite || interactiveOptions.overrideExistingProject()) {
                ProjectUtils.deleteFolder(projectDirectory.toPath());
            } else {
                exitError("Project creation was canceled.");
            }
        }
    }

    private boolean inputIsValid(String... requiredArgs) {
        return inputVerifier.requiredArgsAreNotEmpty(requiredArgs)
                && inputVerifier.classNameIsValid(projectOptions.projectName)
                && inputVerifier.packageNameIsValid(projectOptions.packageName);
    }
}
