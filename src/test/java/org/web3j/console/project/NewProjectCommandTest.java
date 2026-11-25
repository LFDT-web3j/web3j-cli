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

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import org.web3j.console.Web3j;
import org.web3j.console.project.utils.ClassExecutor;
import org.web3j.console.project.utils.Folders;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewProjectCommandTest extends ClassExecutor {
    private static String tempDirPath;

    @BeforeEach
    void setUpStreams() {
        tempDirPath = Folders.tempBuildFolder().getAbsolutePath();
    }

    @Test
    public void testCorrectArgs() {
        final String[] args = {"-p=org.com", "-n=Test", "-o=" + tempDirPath};
        final NewProjectCommand newProjectCommand = new NewProjectCommand();
        new CommandLine(newProjectCommand).parseArgs(args);
        assertEquals("org.com", newProjectCommand.projectOptions.packageName);
        assertEquals("Test", newProjectCommand.projectOptions.projectName);
        assertEquals(tempDirPath, newProjectCommand.projectOptions.outputDir);
    }

    @Test
    public void testWhenNonDefinedArgsArePassed() {
        final String[] args = {"-u=org.org", "-b=test", "-z=" + tempDirPath};
        final CommandLine commandLine = new CommandLine(NewProjectCommand.class);
        Assertions.assertThrows(
                CommandLine.UnmatchedArgumentException.class, () -> commandLine.parseArgs(args));
    }

    @Test
    public void testWhenDuplicateArgsArePassed() {
        final String[] args = {"-p=org.org", "-n=test", "-n=OverrideTest", "-o=" + tempDirPath};
        final CommandLine commandLine = new CommandLine(NewProjectCommand.class);
        Assertions.assertThrows(
                CommandLine.OverwrittenOptionException.class, () -> commandLine.parseArgs(args));
    }

    @Test
    public void testCorrectArgsJavaProjectGeneration() {
        final String[] args = {"-p", "org.com", "-n", "Test", "-o", tempDirPath};
        int exitCode = new CommandLine(NewProjectCommand.class).execute(args);
        assertEquals(0, exitCode);
        final File pathToTests =
                new File(
                        String.join(
                                separator,
                                tempDirPath,
                                "Test",
                                "src",
                                "test",
                                "java",
                                "org",
                                "com",
                                "generated",
                                "contracts",
                                "HelloWorldTest.java"));
        assertTrue(pathToTests.exists());
    }

    @Disabled
    @Test
    public void testCorrectArgsJavaErc777ProjectGeneration()
            throws IOException, InterruptedException {
        final String[] args = {"new", "ERC777", "-o", tempDirPath};
        Process process =
                executeClassAsSubProcessAndReturnProcess(
                                Web3j.class, Collections.emptyList(), Arrays.asList(args), false)
                        .start();
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.write("ERC777Test", 0, "ERC777Test".length());
        writer.newLine();
        writer.write("erc777", 0, "erc777".length());
        writer.newLine();
        writer.write("10000000", 0, "10000000".length());
        writer.newLine();
        writer.newLine();
        writer.close();

        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            List<String> stringList = reader.lines().collect(Collectors.toList());
            stringList.forEach(System.out::println);
        }
        process.waitFor();

        assertEquals(0, process.exitValue());
    }

    @Test
    public void testCorrectArgsJavaErc20ProjectGeneration()
            throws IOException, InterruptedException {
        final String[] args = {"new", "ERC20", "-o", tempDirPath};
        Process process =
                executeClassAsSubProcessAndReturnProcess(
                                Web3j.class, Collections.emptyList(), Arrays.asList(args), false)
                        .start();
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.write("ERC20Test", 0, "ERC20Test".length());
        writer.newLine();
        writer.write("erc20", 0, "erc20".length());
        writer.newLine();
        writer.write("10000000", 0, "10000000".length());
        writer.newLine();
        writer.close();

        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            List<String> stringList = reader.lines().collect(Collectors.toList());
            stringList.forEach(System.out::println);
        }

        process.waitFor();

        assertEquals(0, process.exitValue());
    }

    @Test
    public void testCorrectArgsJavaErc721ProjectGeneration()
            throws IOException, InterruptedException {
        final String[] args = {"new", "ERC721", "-o", tempDirPath};
        Process process =
                executeClassAsSubProcessAndReturnProcess(
                                Web3j.class, Collections.emptyList(), Arrays.asList(args), false)
                        .start();
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.write("ERC721Test", 0, "ERC721Test".length());
        writer.newLine();
        writer.write("erc721", 0, "erc721".length());
        writer.newLine();
        writer.close();

        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            List<String> stringList = reader.lines().collect(Collectors.toList());
            stringList.forEach(System.out::println);
        }

        process.waitFor();

        assertEquals(0, process.exitValue());
    }
}
