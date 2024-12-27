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
package org.web3j.console.services;

import java.io.IOException;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import org.web3j.console.utils.CliVersion;

import static org.web3j.console.config.ConfigManager.config;

public class Updater {
    private static final String GITHUB_API_URL =
            "https://api.github.com/repos/hyperledger-web3j/web3j-cli/releases/latest";

    public static void promptIfUpdateAvailable() throws IOException {
        String version = CliVersion.getVersion(); // Get current version from CLI
        String latestVersion = getLatestVersionFromGitHub(); // Fetch latest version from GitHub

        if (latestVersion != null
                && Version.valueOf(latestVersion).greaterThan(Version.valueOf(version))
                && !version.contains("SNAPSHOT")) {
            System.out.println(
                    String.format(
                            "Your current Web3j version is: "
                                    + version
                                    + ". The latest Version is: "
                                    + latestVersion
                                    + ". To update, run: %s",
                            config.getUpdatePrompt()));
        }
    }

    public static void onlineUpdateCheck() {
        try {
            promptIfUpdateAvailable();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLatestVersionFromGitHub() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(GITHUB_API_URL).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    JsonObject jsonObject = JsonParser.parseString(body.string()).getAsJsonObject();
                    String tagName = jsonObject.get("tag_name").getAsString();
                    return tagName.replace("v", "");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
