package com.example.edgeschval.schemavalidation;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class SchemaValidationWMTest {

    private final static String TEST_PATH = "/api/v1";
    private final static String TEST_TARGET_PATH = "/service-name/api/v5";
    private final static int TEST_BACKEND_PORT = 8585;

    @TestConfiguration
    static class RequestHashingFilterTestConfig {


        @Bean(destroyMethod = "stop")
        WireMockServer wireMockServer() {
            WireMockConfiguration options = wireMockConfig().port(TEST_BACKEND_PORT);
            WireMockServer wireMock = new WireMockServer(options);
            wireMock.start();
            return wireMock;
        }

        @Bean
        RouteLocator testRoutes(RouteLocatorBuilder builder, WireMockServer wireMock) {

            return builder
                    .routes()
                    .route(predicateSpec -> predicateSpec
                            .path(TEST_PATH)
                            .uri(wireMock.baseUrl() + TEST_TARGET_PATH))
                    .build();
        }
    }

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    WireMockServer proxiedServiceMock;

    @Autowired
    private ResourceLoader resourceLoader = null;

    @AfterEach
    void afterEach() {
        proxiedServiceMock.resetAll();
    }

    @BeforeEach
    void mockContextTest() throws IOException {

        ResponseDefinitionBuilder swaggerResourceResponse = new ResponseDefinitionBuilder();
        swaggerResourceResponse
                .withBody(resourceLoader.getResource("classpath:openapi/swagger-resources.json").getContentAsString(Charset.defaultCharset()))
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .build();

        proxiedServiceMock.stubFor(WireMock.get("/v3/api-docs/swagger-config")
                .willReturn(swaggerResourceResponse));


        ResponseDefinitionBuilder serviceInfo = new ResponseDefinitionBuilder();
        serviceInfo
                .withBody(resourceLoader.getResource("classpath:openapi/service-1-info.json").getContentAsString(Charset.defaultCharset()))
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .build();

        proxiedServiceMock.stubFor(WireMock.get("/actuator/info").willReturn(serviceInfo));


        ResponseDefinitionBuilder serviceDescriptionJson = new ResponseDefinitionBuilder();
        serviceDescriptionJson
                .withBody(resourceLoader.getResource("classpath:openapi/petclinic-openapi3.yml").getContentAsByteArray())
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .build();
        proxiedServiceMock.stubFor(WireMock.get("/service-openapi").willReturn(serviceDescriptionJson));


        proxiedServiceMock.stubFor(WireMock.post(WireMock.urlMatching(".*/api/.*")).willReturn(WireMock.ok()));



    }



    @Test
    void canMatchPath(){

        String body = "test body";
        webTestClient.post().uri(TEST_PATH)
                .bodyValue(body)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK);


    }
}