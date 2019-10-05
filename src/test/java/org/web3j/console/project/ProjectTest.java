/*
 * Copyright 2019 Web3 Labs LTD.
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
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectTest {
    private ProjectStructure projectStructure;
    private TemplateProvider templateProviderNew;

    @BeforeEach
    public void setUpProject(@TempDir Path tempDirPath) throws Exception {
        projectStructure = new ProjectStructure(tempDirPath.toFile().getPath(), "test", "test");
        templateProviderNew =
                new TemplateProvider.Builder()
                        .loadGradlewBatScript("gradlew.bat.template")
                        .loadGradlewScript("gradlew.template")
                        .loadMainJavaClass("Template.java")
                        .loadGradleBuild("build.gradle.template")
                        .loadGradleSettings("settings.gradle.template")
                        .loadGradlewWrapperSettings("gradlew-wrapper.properties.template")
                        .loadGradleJar("gradle-wrapper.jar")
                        .loadSolidityGreeter("Greeter.sol")
                        .withPackageNameReplacement(s -> s.replace("<package_name>", "test"))
                        .withProjectNameReplacement(s -> s.replace("<project_name>", "test"))
                        .build();
        Project.builder()
                .withTemplateProvider(templateProviderNew)
                .withProjectStructure(projectStructure)
                .build();
    }

    @Test
    public void directoryCreationTest() {
        final boolean mainProjectDir = new File(projectStructure.getMainPath()).exists();
        final boolean gradleWrapperDir = new File(projectStructure.getWrapperPath()).exists();
        final boolean testProjectDir = new File(projectStructure.getTestPath()).exists();
        final boolean solidityPath = new File(projectStructure.getSolidityPath()).exists();

        assertTrue(mainProjectDir && gradleWrapperDir && testProjectDir && solidityPath);
    }

    @Test
    public void fileCreationTest() {
        final boolean mainJavaClass =
                new File(projectStructure.getMainPath() + File.separator + "test.java").exists();
        final boolean greeterContract =
                new File(projectStructure.getSolidityPath() + File.separator + "Greeter.sol")
                        .exists();
        final boolean gradleBuild =
                new File(projectStructure.getProjectRoot() + File.separator + "build.gradle")
                        .exists();
        final boolean gradleSettings =
                new File(projectStructure.getProjectRoot() + File.separator + "settings.gradle")
                        .exists();
        final boolean gradleWrapperSettings =
                new File(
                                projectStructure.getWrapperPath()
                                        + File.separator
                                        + "gradle-wrapper.properties")
                        .exists();
        final boolean gradleWrapperJar =
                new File(projectStructure.getWrapperPath() + File.separator + "gradle-wrapper.jar")
                        .exists();
        final boolean gradlewBatScript =
                new File(projectStructure.getProjectRoot() + File.separator + "gradlew.bat")
                        .exists();
        final boolean gradlewScript =
                new File(projectStructure.getProjectRoot() + File.separator + "gradlew").exists();

        assertTrue(
                mainJavaClass
                        && greeterContract
                        && gradleBuild
                        && gradleSettings
                        && gradleWrapperSettings
                        && gradleWrapperJar
                        && gradlewBatScript
                        && gradlewScript);
    }
}
