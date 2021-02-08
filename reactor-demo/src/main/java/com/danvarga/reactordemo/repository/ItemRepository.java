package com.danvarga.reactordemo.repository;

import com.danvarga.reactordemo.document.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

    Mono<Item> findByDescription(String description);
    Flux<Item> findByDescriptionLike(String description);
}
