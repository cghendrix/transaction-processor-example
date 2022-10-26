package com.transactionprocessorexample.internal;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.Options.DYNAMIC_PORT;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WireMockTestResource implements QuarkusTestResourceLifecycleManager {
    public static final WireMockServer WIRE_MOCK_SERVER = new WireMockServer(DYNAMIC_PORT);

    @Override
    public Map<String, String> start() {
        WIRE_MOCK_SERVER.start();
        setUpStubs();
        return Map.of("quarkus.rest-client.bonus-api.url", WIRE_MOCK_SERVER.baseUrl());
    }

    @Override
    public void stop() {
        WIRE_MOCK_SERVER.stop();
    }

    private void setUpStubs() {
        String bonusMerchantsRegex = ".*(759166376175738|921496015009488|723915558281414).*";

        WIRE_MOCK_SERVER.stubFor(
                post(urlEqualTo("/bonus")).inScenario("Flaky API")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .withRequestBody(new RegexPattern(bonusMerchantsRegex))
                        .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE))
                        .willSetStateTo("Failed once"));

        WIRE_MOCK_SERVER.stubFor(
                post(urlEqualTo("/bonus")).inScenario("Flaky API")
                        .whenScenarioStateIs("Failed once")
                        .withRequestBody(new RegexPattern(bonusMerchantsRegex))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("BonusResponse.json"))
                        .willSetStateTo(Scenario.STARTED));
    }

}
