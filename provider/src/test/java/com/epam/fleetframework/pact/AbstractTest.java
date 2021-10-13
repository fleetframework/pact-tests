package com.epam.fleetframework.pact;

import au.com.dius.pact.provider.junit.Consumer;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junit5.HttpsTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

@Consumer("rp-user")
@PactBroker(scheme = "https", host = "${brokerUrl}",
        authentication = @PactBrokerAuth(username = "${brokerUsername}", password = "${brokerPassword}"))
public abstract class AbstractTest {
    @BeforeAll
    static void enablePublishingPact() {
        System.setProperty("pact.verifier.publishResults", "true");
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new HttpsTestTarget(System.getenv("url"), 443));
    }

}
