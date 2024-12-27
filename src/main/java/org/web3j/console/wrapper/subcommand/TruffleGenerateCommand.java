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
package org.web3j.console.wrapper.subcommand;

import java.io.File;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.web3j.codegen.Console;
import org.web3j.codegen.TruffleJsonFunctionWrapperGenerator;
import org.web3j.console.Web3jVersionProvider;

import static picocli.CommandLine.Help.Visibility.ALWAYS;

@Command(
        name = "truffle",
        description = "Generate Java smart contract wrappers from truffle json",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class TruffleGenerateCommand implements Runnable {

    static final String JAVA_TYPES_ARG = "--javaTypes";
    static final String SOLIDITY_TYPES_ARG = "--solidityTypes";

    @Option(
            names = {"-t", "--truffle-json"},
            description = "ABI file with contract definition.",
            required = true)
    private File jsonFileLocation;

    @Option(
            names = {"-o", "--outputDir"},
            description = "Destination base directory.",
            required = true)
    private File destinationDirLocation;

    @Option(
            names = {"-p", "--package"},
            description = "Base package name.",
            required = true)
    private String basePackageName;

    @Option(
            names = {"-jt", JAVA_TYPES_ARG},
            description = "Use native Java types.",
            showDefaultValue = ALWAYS)
    private boolean javaTypes = true;

    @Option(
            names = {"-st", SOLIDITY_TYPES_ARG},
            description = "Use Solidity types.")
    private boolean solidityTypes;

    @Option(
            names = {"-B", "--generateBoth"},
            description = "Generate both send_ and call_ functions.")
    private boolean generateBoth = false;

    @Override
    public void run() {

        boolean useJavaNativeTypes = useJavaNativeTypes();

        try {
            new TruffleJsonFunctionWrapperGenerator(
                    jsonFileLocation.getAbsolutePath(),
                    destinationDirLocation.getAbsolutePath(),
                    basePackageName,
                    useJavaNativeTypes,
                    generateBoth)
                    .generate();
        } catch (Exception e) {
            Console.exitError(e);
        }
    }

    private boolean useJavaNativeTypes() {
        boolean useJavaNativeTypes = true;
        if ((!solidityTypes && !javaTypes) || (solidityTypes && javaTypes)) {
            Console.exitError(
                    "Invalid project type. Expecting one of "
                            + SOLIDITY_TYPES_ARG
                            + " or "
                            + JAVA_TYPES_ARG);
        }
        if (solidityTypes) {
            useJavaNativeTypes = false;
        }
        return useJavaNativeTypes;
    }
}
