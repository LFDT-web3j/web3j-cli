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

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import org.web3j.console.config.CliConfig;
import org.web3j.console.config.ConfigManager;
import org.web3j.console.utils.CliVersion;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdaterTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8089));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testPromptIfUpdateAvailableWhenUpdateIsAvailable() throws IOException {
        wireMockServer.start();
        try (MockedStatic<CliVersion> cliVersionMock = mockStatic(CliVersion.class)) {
            cliVersionMock.when(CliVersion::getVersion).thenReturn("1.0.0");

            CliConfig mockConfig = mock(CliConfig.class);
            ConfigManager.config = mockConfig;

            // Mock config's getUpdatePrompt() method
            when(mockConfig.getUpdatePrompt())
                    .thenReturn("curl -L get.web3j.io | sh && source ~/.web3j/source.sh");

            wireMockServer.stubFor(
                    get(urlEqualTo("/repos/hyperledger-web3j/web3j-cli/releases/latest"))
                            .willReturn(
                                    aResponse()
                                            .withStatus(200)
                                            .withBody("{ \"tag_name\": \"v1.1.0\" }")));

            try (MockedStatic<Updater> updaterMock =
                    mockStatic(Updater.class, CALLS_REAL_METHODS)) {
                updaterMock.when(Updater::getLatestVersionFromGitHub).thenReturn("1.1.0");
                Updater.promptIfUpdateAvailable();
                // Verify that the update prompt was called with the correct message
                verify(mockConfig).getUpdatePrompt();
            }
        } finally {
            wireMockServer.stop();
        }
    }

    @Test
    public void testPromptIfUpdateAvailableWhenNoUpdateIsAvailable() throws IOException {
        wireMockServer.start();
        try (MockedStatic<CliVersion> cliVersionMock = mockStatic(CliVersion.class)) {
            cliVersionMock.when(CliVersion::getVersion).thenReturn("1.1.0");

            CliConfig mockConfig = mock(CliConfig.class);
            ConfigManager.config = mockConfig;

            wireMockServer.stubFor(
                    get(urlEqualTo("/repos/hyperledger-web3j/web3j-cli/releases/latest"))
                            .willReturn(
                                    aResponse()
                                            .withStatus(200)
                                            .withBody("{ \"tag_name\": \"v1.1.0\" }")));

            try (MockedStatic<Updater> updaterMock =
                    mockStatic(Updater.class, CALLS_REAL_METHODS)) {
                updaterMock.when(Updater::getLatestVersionFromGitHub).thenReturn("1.1.0");
                Updater.promptIfUpdateAvailable();
                // Verify that getUpdatePrompt was never called, as no update is available
                verify(mockConfig, never()).getUpdatePrompt();
            }
        } finally {
            wireMockServer.stop();
        }
    }
}
