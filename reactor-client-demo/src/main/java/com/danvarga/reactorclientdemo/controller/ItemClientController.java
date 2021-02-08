package com.danvarga.reactorclientdemo.controller;

import com.danvarga.reactorclientdemo.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ItemClientController {


    WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {
        return webClient.get().uri("/v1/fun/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in Client Project using retrieve");
    }

    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange() {
        return webClient.get().uri("/v1/fun/items")
                .exchangeToFlux(item -> item.bodyToFlux(Item.class)) // Provides more control compared to 'retrieve'.
                .log("Items in Client project using exchange");
    }

    @GetMapping("/client/retrieve/{id}")
    public Mono<Item> getOneItemUsingRetrieve(@PathVariable String id) {
        // id for testing: "ASD"
        return webClient.get().uri("v1/fun/items/" + id)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Item in Client Project using retrieve");
    }

    @GetMapping("/client/exchange/{id}")
    public Mono<Item> getOneItemUsingExchange(@PathVariable String id) {
        // id for testing: "ASD"
        return webClient.get().uri("v1/fun/items/" + id)
                .exchangeToMono(item -> item.bodyToMono(Item.class))
                .log("Item in Client Project using exchange");
    }

    @PostMapping("/client/post")
    public Mono<Item> createItem(@RequestBody Item item) {
        Mono<Item> itemMono = Mono.just(item);

        return webClient.post().uri("/v1/fun/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemMono, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log();
    }

    @PutMapping("/client/put/{id}")
    public Mono<Item> createItem(@RequestBody Item item, @PathVariable String id) {
        // id for testing: "ASD"
        Mono<Item> itemMono = Mono.just(item);

        return webClient.put().uri("/v1/fun/items/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemMono, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log();
    }

    @DeleteMapping("/client/delete/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        // id for testing: "ASD"

        return webClient.delete().uri("/v1/fun/items/" + id)
                .retrieve()
                .bodyToMono(Void.class)
                .log();
    }

    @GetMapping("client/retrieve/error")
    public Flux<Item> getAllItemsErrorUsingRetrieve() {
        return webClient.get().uri("fun/runtimeexception")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    Mono<String> errorMono = clientResponse.bodyToMono(String.class);
                    return errorMono.flatMap((errorMessage) -> {
                        log.info("The error message is: " + errorMessage);
                        return Mono.error(new RuntimeException(errorMessage));
                    });
                })
                .bodyToFlux(Item.class);
    }


    @GetMapping("client/exchange/error")
    public Flux<Item> getAllItemsErrorUsingExchange() {
        // With deprecated 'exchange'.
//        return webClient.get().uri("fun/runtimeexception")
//                .exchange()
//                .flatMapMany((clientResponse -> {
//                    if (clientResponse.statusCode().is5xxServerError()) {
//                        return clientResponse.bodyToMono(String.class)
//                                .flatMap(errorMessage -> {
//                                    log.info("Error message in errorExchange: " + errorMessage);
//                                    return Mono.error(new RuntimeException(errorMessage));
//                                });
//                    } else {
//                        return clientResponse.bodyToFlux(Item.class);
//                    }
//                }));

        return webClient.get().uri("fun/runtimeexception")
                .exchangeToFlux(clientResponse -> { // ServerResponse
                    if (clientResponse.statusCode().is5xxServerError()) {
                        return clientResponse.bodyToFlux(String.class) // Flux<String>
                                .flatMap(errorMessage -> {
                                    log.info("Error message in errorExchange: " + errorMessage);
                                    return Flux.error(new RuntimeException(errorMessage));
                                });
                    } else {
                        return clientResponse.bodyToFlux(Item.class);
                    }
                });
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error("Exception caught in handleRuntimeException: {} ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
