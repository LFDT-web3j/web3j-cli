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
package org.web3j.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.tx.Contract;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SolidityFunctionWrapperGenerator extends FunctionWrapperGenerator {

    public SolidityFunctionWrapperGenerator(
            File binFile,
            File abiFile,
            File destinationDir,
            String contractName,
            String basePackageName,
            boolean useJavaNativeTypes,
            boolean useJavaPrimitiveTypes,
            boolean generateBothCallAndSend,
            Class<? extends Contract> contractClass,
            int addressLength,
            boolean generateSendTxForCalls) {
        super(
                binFile,
                abiFile,
                destinationDir,
                contractName,
                basePackageName,
                useJavaNativeTypes,
                useJavaPrimitiveTypes,
                generateBothCallAndSend,
                contractClass,
                addressLength,
                generateSendTxForCalls);
    }

    @Override
    protected void generate() throws IOException, ClassNotFoundException {
        super.generate();
    }

    @Override
    protected TypeName getTypeName(String type) {
        if (type.equals("address")) {
            return ClassName.get(Address.class);
        } else if (type.equals("uint256")) {
            return ClassName.get(Uint256.class);
        } else if (type.startsWith("tuple")) {
            return getTupleTypeName(type);
        } else {
            return super.getTypeName(type);
        }
    }

    private TypeName getTupleTypeName(String type) {
        // Handle parameterized types
        if (type.contains("<")) {
            String rawType = type.substring(0, type.indexOf('<'));
            String parameterType = type.substring(type.indexOf('<') + 1, type.lastIndexOf('>'));
            return ParameterizedTypeName.get(
                    ClassName.get("", rawType), getTypeName(parameterType));
        } else {
            return ClassName.get("", type);
        }
    }

    @Override
    protected void processAbiFile() throws IOException, ClassNotFoundException {
        List<AbiDefinition> functionDefinitions = loadContractDefinition(abiFile);
        for (AbiDefinition functionDefinition : functionDefinitions) {
            if (functionDefinition.getType().equals("function")) {
                processFunctionDefinition(functionDefinition);
            } else if (functionDefinition.getType().equals("event")) {
                processEventDefinition(functionDefinition);
            }
        }
    }
}
