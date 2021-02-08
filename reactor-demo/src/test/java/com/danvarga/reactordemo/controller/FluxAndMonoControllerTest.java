package com.danvarga.reactordemo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux_approach1() {
        Flux<Integer> integerFlux = webTestClient
                .get().uri("/flux")
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
                .verifyComplete();
    }

    @Test
    void flux_approach2() {
        webTestClient
                .get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // invokes the endpoint
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(3);
    }

    @Test
    void flux_approach3() {
        List<Integer> expectedList = Arrays.asList(1, 2, 3);

        EntityExchangeResult<List<Integer>> result = webTestClient
                .get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // invokes the endpoint
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        assertEquals(expectedList, result.getResponseBody());
    }

    @Test
    void flux_approach4() {
        List<Integer> expectedList = Arrays.asList(1, 2, 3);

        webTestClient
                .get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // invokes the endpoint
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(expectedList, response.getResponseBody());
                });
    }

    @Test
    void fluxStream() {
        Flux<Long> longFlux = webTestClient
                .get().uri("/fluxstream")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange() // invokes the endpoint
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .thenCancel()
                .verify();
    }

    @Test
    void mono() {
        Integer expectedValue = 1;

        webTestClient
                .get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(expectedValue, response.getResponseBody());
                });
    }
}