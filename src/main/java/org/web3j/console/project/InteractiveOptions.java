/*
 * Copyright 2019 Web3 Labs Ltd.
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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.web3j.account.LocalWeb3jAccount;
import org.web3j.console.project.utils.InputVerifier;

import static java.io.File.separator;
import static org.web3j.codegen.Console.exitError;
import static org.web3j.console.project.utils.ProjectUtils.deleteFolder;

public class InteractiveOptions {
    static Scanner scanner = new Scanner(System.in);

    public static String getProjectName() {
        print("Please enter the project name [Web3App]:");
        String projectName = getUserInput();
        if (projectName.trim().isEmpty()) {
            return "Web3App";
        }
        while (!InputVerifier.classNameIsValid(projectName)) {
            projectName = getUserInput();
        }
        return projectName;
    }

    public static String getPackageName() {
        print("Please enter the package name for your project [io.web3j]:");
        String packageName = getUserInput();
        if (packageName.trim().isEmpty()) {
            return "io.web3j";
        }
        while (!InputVerifier.packageNameIsValid(packageName)) {
            packageName = getUserInput();
        }
        return packageName;
    }

    public static Optional<String> getProjectDestination(final String projectName) {
        print(
                "Please enter the destination of your project ["
                        + System.getProperty("user.dir")
                        + "]: ");
        final String projectDest = getUserInput();
        final String projectPath = projectDest + separator + projectName;
        if (new File(projectPath).exists()) {
            if (overrideExistingProject()) {
                Path path = new File(projectPath).toPath();
                deleteFolder(path);
                return Optional.of(projectDest);
            } else {
                exitError("Project creation was canceled.");
            }
        }
        return projectDest.isEmpty() ? Optional.empty() : Optional.of(projectDest);
    }

    public static Optional<String> getGeneratedWrapperLocation() {
        print(
                "Please enter the path of the generated contract wrappers ["
                        + String.join(
                                separator,
                                System.getProperty("user.dir"),
                                "build",
                                "generated",
                                "source",
                                "web3j",
                                "main",
                                "java")
                        + "]");
        String pathToTheWrappers = getUserInput();
        return pathToTheWrappers.isEmpty()
                ? Optional.of(
                        String.join(
                                separator,
                                System.getProperty("user.dir"),
                                "build",
                                "generated",
                                "source",
                                "web3j",
                                "main",
                                "java"))
                : Optional.of(pathToTheWrappers);
    }

    public static Optional<String> setGeneratedTestLocationJava() {
        print(
                "Where would you like to save your tests ["
                        + String.join(
                                separator, System.getProperty("user.dir"), "src", "test", "java")
                        + "]");
        String outputPath = getUserInput();
        return outputPath.isEmpty()
                ? Optional.of(
                        String.join(
                                separator, System.getProperty("user.dir"), "src", "test", "java"))
                : Optional.of(outputPath);
    }

    public static Optional<String> setGeneratedTestLocationKotlin() {
        print(
                "Where would you like to save your tests ["
                        + String.join(
                                separator, System.getProperty("user.dir"), "src", "test", "kotlin")
                        + "]");
        String outputPath = getUserInput();
        return outputPath.isEmpty()
                ? Optional.of(
                        String.join(
                                separator, System.getProperty("user.dir"), "src", "test", "kotlin"))
                : Optional.of(outputPath);
    }

    public static boolean userWantsTests() {
        print("Would you like to generate unit test for your solidity contracts [Y/n] ? ");
        String userAnswer = getUserInput();
        return userAnswer.trim().toLowerCase().equals("y") || userAnswer.trim().equals("");
    }

    public static String getSolidityProjectPath() {
        System.out.println("Please enter the path to your solidity file/folder [Required Field]: ");
        return getUserInput();
    }

    static String getUserInput() {
        return scanner.nextLine();
    }

    private static void print(final String text) {
        System.out.println(text);
    }

    public static boolean overrideExistingProject() {
        print("Looks like the project exists. Would you like to overwrite it [y/N] ?");
        String userAnswer = getUserInput();
        return userAnswer.toLowerCase().equals("y");
    }

    public static boolean userHasWeb3jAccount() throws IOException {
        if (LocalWeb3jAccount.configExists()) {
            ObjectNode objectNode = LocalWeb3jAccount.readConfigAsJson();
            return LocalWeb3jAccount.loginTokenExists(objectNode);
        }
        return false;
    }

    public static boolean configFileExists() {
        return LocalWeb3jAccount.configExists();
    }

    public static boolean userWantsWeb3jAccount() throws IOException {

        print("It looks like you don’t have a Web3j account, would you like to create one?");
        print("This will provide free access to the Ethereum network [Y/n]");
        String userAnswer = getUserInput();
        return userAnswer.toLowerCase().equals("y") || userAnswer.trim().equals("");
    }
}
