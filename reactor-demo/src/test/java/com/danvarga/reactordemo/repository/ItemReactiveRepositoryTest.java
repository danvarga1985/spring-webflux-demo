package com.danvarga.reactordemo.repository;

import com.danvarga.reactordemo.document.Item;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@DataMongoTest
@DirtiesContext // For whenever the state of the application changes during test cases -> new Application Context for each test case
@ActiveProfiles("test")
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    // 'Arrays.asList' doesn't work - won't recognize 'Item' class.
    List<Item> itemList = Arrays.stream(new Item[]{
            Item.builder().id(UUID.randomUUID().toString()).description("LCD TV").price(1345.99).build(),
            Item.builder().id(UUID.randomUUID().toString()).description("Flip-phone").price(175.0).build(),
            Item.builder().id("ASD").description("Faux-leather wallet").price(45.0).build(),
            Item.builder().id(null).description("Vegan-leather wallet").price(65.99).build(),
            Item.builder().id(new ObjectId().toString()).description("Headphones").price(45.0).build()})
            .collect(Collectors.toList());


    @BeforeEach
    void setUp() {
        itemRepository
                .deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item: " + item.getDescription() + " saved!");
                })
                .log()
                .blockLast(); // Wait until all the items are saved. ONLY FOR TESTING!
    }

    @Test
    void getAllItemsTest() {
        StepVerifier.create(itemRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void getItemByIdTest() {
        StepVerifier.create(itemRepository.findById("ASD").log())
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equals("Faux-leather wallet")))
                .verifyComplete();
    }

    @Test
    void findByDescriptionTest() {
        StepVerifier.create(itemRepository.findByDescription("Faux-leather wallet").log("findItemByDescription : "))
                .expectSubscription()
//                .expectNextCount(1)
                .expectNextMatches(item -> item.getId().equals("ASD"))
                .verifyComplete();
    }

    @Test
    void findByDescriptionLikeTest() {
        StepVerifier.create(itemRepository.findByDescriptionLike("wallet").log("findItemByDescriptionLike : "))
                .expectSubscription()
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void saveItemTest() {
        Item newItem = Item.builder().id(null).description("Pink cowboy boots").price(300.0).build();
        Mono<Item> savedItem = itemRepository.save(newItem);

        StepVerifier.create(savedItem)
                .expectSubscription()
                .expectNextMatches(item1 -> (item1.getId() != null && item1.getDescription().equals("Pink cowboy boots")))
                .verifyComplete();

        System.out.println("Items in the repository: " + itemRepository.findAll().collectList().block());
    }

    @Test
    void deleteItemByIdTest() {
        Mono<Void> deletedItem = itemRepository.findById("ASD") // Mono<Item>
                .map(Item::getId) //get Id - transform from one type to another - Mono<Item> to Mono<String>
                .flatMap((id) -> {
                    return itemRepository.deleteById(id);
                });

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemRepository.findAll().log("The new list: "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();

        System.out.println("Items in the repository: " + itemRepository.findAll().collectList().block());
    }

    @Test
    void deleteItemTest() {
        Mono<Void> deletedItem = itemRepository.findByDescription("LCD TV") // Mono<Item>
                .flatMap((item) -> {
                    return itemRepository.delete(item);
                });

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemRepository.findAll().log("The new list: "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();

        System.out.println("Items in the repository: " + itemRepository.findAll().collectList().block());
    }

    @Test
    void updateItemByIdTest() {

        Mono<Item> foundItem = itemRepository.findById("ASD").log();

        Mono<Item> updatedItem = foundItem
                .map(item -> {
                    item.setDescription("Armadillo-leather wallet");
                    item.setPrice(100.0);
                    return item;
                })
                .flatMap(item -> {
                    return itemRepository.save(item);
                });

        StepVerifier.create(updatedItem.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Armadillo-leather wallet"))
                .verifyComplete();

        System.out.println("Items in the repository: " + itemRepository.findAll().collectList().block());
    }
}
