package com.danvarga.reactordemo.bootstrap;

import com.danvarga.reactordemo.document.Item;
import com.danvarga.reactordemo.document.ItemCapped;
import com.danvarga.reactordemo.repository.ItemCappedRepository;
import com.danvarga.reactordemo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    private final ItemRepository itemRepository;
    private final ItemCappedRepository itemCappedRepository;

    private final ReactiveMongoOperations mongoOperations;

    @Override
    public void run(String... args) {
        initialDataSetup();
        createCappedCollection();
        dataSetupForCappedCollection();
    }

    private void createCappedCollection() {
        /*
         A. Capped collections in mongodb cannot be used as permanent storage, since items cannot be deleted,
         only the collections can be dropped.
         B. Need to block the 'dropCollection' (a more appealing alternative to Thread.sleep), unless:
         'reactor.core.Exceptions$ErrorCallbackNotImplemented' ...(NamespaceExists): 'a collection
         'reactordemo.itemCapped' already exists'
         - My suspicion is that the 'dropCollection' command is too "slow" and the 'createCollection' finds an already
         existing collection. Mongodb is running is a Docker container. 2021.02.08
        */
        mongoOperations.dropCollection(ItemCapped.class).block();
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().capped().size(50000).maxDocuments(20)).subscribe();
    }

    public List<Item> data() {
        return Arrays.asList(Item.builder().id(null).description("LCD TV").price(1300.0).build(),
                Item.builder().id(null).description("Hat").price(67.99).build(),
                Item.builder().id(null).description("Chunk of rancid  pork").price(10.0).build(),
                Item.builder().id("ASD").description("A cane").price(30.0).build());
    }

    private void initialDataSetup() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemRepository::save)
                .thenMany(itemRepository.findAll())
                .subscribe(item -> {
                    System.out.println("Item inserted from CommandLineRunner: " + item);
                });
    }

    private void dataSetupForCappedCollection() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(1000))
                .map(i -> ItemCapped.builder().description("Random Item - " + i).price(100.0 + i).build());

        itemCappedRepository
                .insert(itemCappedFlux)
                .subscribe(itemCapped -> {
                    log.info("Inserted ItemCapped is: " + itemCapped);
                });
    }

}
