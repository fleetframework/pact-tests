package com.epam.fleetframework.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.epam.fleetframework.pact.models.StartLaunchRequestEntity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "service-api")
public class ReportPortalUserConsumerTest extends AbstractTest {

    @Test
    @PactTestFor(pactMethod = "getServerSettings", port = "11111")
    void testGetProjectLog(MockServer mockServer) throws IOException {
        HttpResponse httpResponse = Request.Get(mockServer.getUrl() + "/api/v1/settings")
                .addHeader("Authorization", "Bearer " + token)
                .execute().returnResponse();
        String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);

        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(jsonObject.get("server.details.instance").getAsString()).isEqualTo("54038958-70ca-47b6-a6a6-41db89fab755");
        assertThat(jsonObject.get("server.analytics.all").getAsString()).isEqualTo("true");
    }

    @Test
    @PactTestFor(pactMethod = "startLaunch", port = "22222")
    void testStartLaunch(MockServer mockServer) throws IOException {
        StartLaunchRequestEntity entity =
                new StartLaunchRequestEntity("Pact_launch", "DEFAULT", String.valueOf(dateTimeInMills));
        String jsonBody = new Gson().toJson(entity);
        HttpResponse httpResponse = Request.Post(mockServer.getUrl() + "/api/v2/jdi-tests/launch")
                .bodyString(jsonBody, ContentType.APPLICATION_JSON)
                .addHeader("Authorization", "Bearer " + token)
                .execute().returnResponse();
        String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);

        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(jsonObject.get("id").getAsString()).hasSize(36);
    }

    @Test
    @PactTestFor(providerName = "uat-api", pactMethod = "getLdapSettings", port = "33333")
    void testLdapSettings(MockServer mockServer) throws IOException {
        HttpResponse httpResponse = Request.Get(mockServer.getUrl() + "/uat/settings/auth/ldap")
                .addHeader("Authorization", "Bearer " + token)
                .execute().returnResponse();
        String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);

        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(jsonObject.get("type").getAsString()).isEqualTo("ldap");
        assertThat(jsonObject.getAsJsonObject("ldapAttributes").get("enabled").getAsBoolean()).isFalse();
        assertThat(jsonObject.getAsJsonObject("ldapAttributes").get("synchronizationAttributes").isJsonObject()).isTrue();
    }
}
