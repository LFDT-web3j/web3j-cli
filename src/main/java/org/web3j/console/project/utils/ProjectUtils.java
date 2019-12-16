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
package org.web3j.console.project.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.apache.commons.lang3.RandomStringUtils;

import org.web3j.codegen.Console;

public class ProjectUtils {

    public static void deleteFolder(Path directoryToDeleted) {
        try {
            Files.walk(directoryToDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(ProjectUtils::deleteFile);
        } catch (IOException e) {
            Console.exitError(e);
        }
    }

    static void deleteFile(File fileToDelete) {
        if (!fileToDelete.delete()) {
            Console.exitError(
                    "Could not delete "
                            + fileToDelete.getName()
                            + " at location "
                            + fileToDelete.getPath());
        }
    }

    public static String generateWalletPassword() {
        return RandomStringUtils.random(10, true, true);
    }
}
