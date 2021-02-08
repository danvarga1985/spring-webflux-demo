package com.danvarga.reactordemo.controller.v1;

import com.danvarga.reactordemo.constants.ItemConstants;
import com.danvarga.reactordemo.document.ItemCapped;
import com.danvarga.reactordemo.repository.ItemCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemStreamControllerTest {

    @Autowired
    ItemCappedRepository itemCappedRepository;

    @Autowired
    ReactiveMongoOperations reactiveMongoOperations;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        reactiveMongoOperations.dropCollection(ItemCapped.class).block();
        reactiveMongoOperations.createCollection(ItemCapped.class,
                CollectionOptions.empty().maxDocuments(20).size(5000).capped())
                .subscribe();

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(1))
                .map(i -> ItemCapped.builder().description("Random Item - " + i).price(100.0 + i).build())
                .take(5);

        itemCappedRepository
                .insert(itemCappedFlux)
                .doOnNext(itemCapped -> {
                    System.out.println("Inserted Item in setUp: " + itemCapped);
                })
                .blockLast();
    }

    @Test
    void streamAllItemsTest() {
        Flux<ItemCapped> itemCappedFlux = webTestClient.get()
                .uri(ItemConstants.ITEM_STREAM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux)
                .expectSubscription()
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }
}
