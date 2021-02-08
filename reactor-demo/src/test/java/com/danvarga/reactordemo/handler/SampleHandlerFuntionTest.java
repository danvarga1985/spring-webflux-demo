package com.danvarga.reactordemo.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
/*
 ABSOLUTELY NOT OPTIONAL if we want WebTestClient autowired without '@WebfluxTest', which does not pick up @Beans,
 that are needed for testing functional endpoints.
*/
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class SampleHandlerFuntionTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux_approach1() {
        Flux<Integer> integerFlux = webTestClient
                .get().uri("/functional/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // invokes the endpoint
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .verifyComplete();
    }

    @Test
    void mono() {
        Integer expectedValue = 2;

        webTestClient
                .get().uri("/functional/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(expectedValue, response.getResponseBody());
                });
    }
}
