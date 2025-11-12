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

import org.web3j.abi.datatypes.Address;
import org.web3j.codegen.Console;
import org.web3j.codegen.SolidityFunctionWrapperGenerator;
import org.web3j.console.Web3jVersionProvider;
import org.web3j.tx.Contract;

import static org.web3j.codegen.Console.exitError;
import static picocli.CommandLine.Help.Visibility.ALWAYS;

@Command(
        name = "solidity",
        description = "Generate Java smart contract wrappers from Solidity",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class SolidityGenerateCommand implements Runnable {

    static final String JAVA_TYPES_ARG = "--javaTypes";
    static final String SOLIDITY_TYPES_ARG = "--solidityTypes";
    static final String PRIMITIVE_TYPES_ARG = "--primitiveTypes";

    @Option(
            names = {"-a", "--abiFile"},
            description = "ABI file with contract definition.",
            required = true)
    private File abiFile;

    @Option(
            names = {"-b", "--binFile"},
            description =
                    "BIN file with contract compiled code "
                            + "in order to generate deploy methods.")
    private File binFile;

    @Option(
            names = {"-c", "--contractName"},
            description = "Contract name (defaults to ABI file name).")
    private String contractName;

    @Option(
            names = {"-o", "--outputDir"},
            description = "Destination base directory.",
            required = true)
    private File destinationFileDir;

    @Option(
            names = {"-p", "--package"},
            description = "Base package name.",
            required = true)
    private String packageName;

    @Option(
            names = {"-al", "--addressLength"},
            description = "Address length in bytes (defaults to 20).")
    private int addressLength = Address.DEFAULT_LENGTH / Byte.SIZE;

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
            names = {"-pt", PRIMITIVE_TYPES_ARG},
            description = "Use Java primitive types.")
    private boolean primitiveTypes = false;

    @Option(
            names = {"-B", "--generateBoth"},
            description = "Generate both send_ and call_ functions.")
    private boolean generateBoth = false;

    @Option(
            names = {"-r", "--abiFuncs"},
            description = "ABI encoded function call getters.",
            required = false)
    private boolean abiFuncs = false;

    @Override
    public void run() {
        try {
            boolean useJavaTypes = useJavaNativeTypes();

            if (contractName == null || contractName.isEmpty()) {
                contractName = getFileNameNoExtension(abiFile.getName());
            }

            new SolidityFunctionWrapperGenerator(
                            binFile,
                            abiFile,
                            destinationFileDir,
                            contractName,
                            packageName,
                            useJavaTypes,
                            primitiveTypes,
                            generateBoth,
                            Contract.class,
                            addressLength,
                            abiFuncs)
                    .generate();
        } catch (Exception e) {
            exitError(e);
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

    private String getFileNameNoExtension(String fileName) {
        String[] splitName = fileName.split("\\.(?=[^.]*$)");
        return splitName[0];
    }
}
