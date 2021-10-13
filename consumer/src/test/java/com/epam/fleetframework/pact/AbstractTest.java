package com.epam.fleetframework.pact;


import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public abstract class AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTest.class);
    protected String token = getAuthToken();
    protected long dateTimeInMills = new Date().getTime();

    @Pact(consumer = "rp-user")
    protected RequestResponsePact getServerSettings(PactDslWithProvider builder) {
        System.setProperty("pact.rootDir", "./pacts");
        DslPart bodyResponse = new PactDslJsonBody()
                .stringMatcher("server.details.instance", "^.{1,36}$","54038958-70ca-47b6-a6a6-41db89fab755")
                .stringValue("server.analytics.all", "true");

        return builder
                .given("server settings")
                .uponReceiving("get server settings")
                .method("GET")
                .path("/api/v1/settings")
                .headers("Authorization", "Bearer " + token)
                .willRespondWith()
                .status(HttpStatus.SC_OK)
                .body(bodyResponse).toPact();
    }

    @Pact(consumer = "rp-user")
    protected RequestResponsePact startLaunch(PactDslWithProvider builder) {
        System.setProperty("pact.rootDir", "./pacts");
        DslPart bodyRequest = new PactDslJsonBody()
                .stringValue("name", "Pact_launch")
                .stringValue("mode", "DEFAULT")
                .stringValue("startTime", String.valueOf(dateTimeInMills));

        DslPart bodyResponse = new PactDslJsonBody()
                .stringMatcher("id", "^.{1,36}$","9528992f-e892-40c9-87e5-da68fc29753b");

        return builder
                .given("starting launch")
                .uponReceiving("start launch")
                .method("POST")
                .path(String.format("/api/v2/%s/launch", System.getenv("project")))
                .headers("Authorization", "Bearer " + token)
                .body(bodyRequest)
                .willRespondWith()
                .status(HttpStatus.SC_CREATED)
                .body(bodyResponse).toPact();
    }

    @Pact(consumer = "rp-user")
    protected RequestResponsePact getLdapSettings(PactDslWithProvider builder) {
        System.setProperty("pact.rootDir", "./pacts");

        DslPart bodyResponse = new PactDslJsonBody()
                .stringValue("type", "ldap")
                .object("ldapAttributes")
                .booleanValue("enabled", false)
                .object("synchronizationAttributes")
                .closeObject()
                .closeObject();

        return builder
                .given("get ldap settings")
                .uponReceiving("check ldap settings")
                .method(HttpGet.METHOD_NAME)
                .path("/uat/settings/auth/ldap")
                .headers("Authorization", "Bearer " + token)
                .willRespondWith()
                .status(HttpStatus.SC_OK)
                .body(bodyResponse).toPact();
    }

    protected String getAuthToken() {
        List<NameValuePair> body = Form.form().add("grant_type", "password")
                .add("username", System.getenv("username"))
                .add("password", System.getenv("password")).build();
        HttpResponse httpResponse;
        String jsonResponse = "";
        try {
            httpResponse = Request.Post("https://dev.fleetframework.io/" + "uat/sso/oauth/token")
                    .addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
                    .addHeader("Authorization", "Basic dWk6dWltYW4")
                    .bodyForm(body)
                    .execute().returnResponse();
            jsonResponse = EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
        return jsonObject.get("access_token").getAsString();
    }
}
