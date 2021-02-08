package com.danvarga.reactordemo.controller.v1;

import com.danvarga.reactordemo.constants.ItemConstants;
import com.danvarga.reactordemo.document.Item;
import com.danvarga.reactordemo.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    WebTestClient webTestClient;


    public List<Item> data() {
        return Arrays.asList(Item.builder().id(null).description("LCD TV").price(1300.0).build(),
                Item.builder().id(null).description("Hat").price(67.99).build(),
                Item.builder().id(null).description("Chunk of rancid  pork").price(10.0).build(),
                Item.builder().id("ASD").description("A cane").price(30.0).build());
    }

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted item is: " + item);
                })
                .blockLast();
    }

    @Test
    void getAllItemsTest() {
        webTestClient
                .get().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBodyList(Item.class).hasSize(4);

    }

    @Test
    void getAllItemsTest2() {
        webTestClient
                .get().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBodyList(Item.class).hasSize(4)
                .consumeWith((response) -> {
                    List<Item> itemList = response.getResponseBody();

                    assert itemList != null;
                    itemList.forEach(Assertions::assertNotNull);
                });
    }

    @Test
    void getAllItemsTest3() {
        Flux<Item> itemFlux = webTestClient
                .get().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void getOneItemTest() {
        webTestClient
                .get().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ASD")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody().jsonPath("$.price", 30.0);
    }

    @Test
    void createItemTest() {
        Item newItem = Item.builder()
                .description("A new item")
                .price(100.0)
                .build();

        webTestClient
                .post().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newItem), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.description").isEqualTo("A new item")
                    .jsonPath("$.price").isEqualTo(100.0);

    }

    @Test
    void deleteItemByIdTest() {
        webTestClient
                .delete().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ASD")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    void updateItemTest() {
        Item itemUpdate = Item.builder()
                .description("A candle")
                .price(1.0)
                .build();

        webTestClient
                .put().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ASD")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemUpdate), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.description").isEqualTo("A candle")
                    .jsonPath("$.price").isEqualTo(1.0)
                    .jsonPath("id").isEqualTo("ASD");
    }

    @Test
    void updateItem_NotFoundTest() {
        Item itemUpdate = Item.builder()
                .description("A candle")
                .price(1.0)
                .build();

        webTestClient
                .put().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "invalidId")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemUpdate), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void runtimeExceptionTest() {
        webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/runtimeException"))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                    .isEqualTo("RuntimeException Occurred!");
    }
}
