package com.danvarga.reactordemo.reactivesandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "eve", "ernst", "knut");

    @Test
    void transformUsingMapCase() {
        Flux<String> fluxNames = Flux.fromIterable(names)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(fluxNames)
                .expectNext("ADAM", "EVE", "ERNST", "KNUT")
                .verifyComplete();
    }

    @Test
    void transformUsingMapLength() {
        Flux<Integer> fluxNames = Flux.fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();

        StepVerifier.create(fluxNames)
                .expectNext(4, 3, 5, 4)
                .expectNext(4, 3, 5, 4)
                .verifyComplete();

    }

    @Test
    void transformAfterFilter() {
        Flux<String> fluxNames = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(fluxNames)
                .expectNext("ERNST")
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMap() {
        Flux<String> fluxNames = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // A, B, C, D, E, F
                .flatMap(s -> {
//                    return Flux.fromIterable(convertToList(s));
                    return Flux.just(s.concat(" New Value"));
                })
                .log(); // db or external service call that returns a flux -> s -> Flux<String>

        StepVerifier.create(fluxNames)
                .expectNextCount(6)
//                .expectNextCount(12)
                .verifyComplete();
    }


    @Test
    void transformUsingFlatMap_UsingParallel() {
        Flux<String> fluxNames = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
                .window(2) // Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                .flatMap((s) ->
                        s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
                .log(); // db or external service call that returns a flux -> s -> Flux<String>

        StepVerifier.create(fluxNames)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMap_UsingParallel_MaintainOrder() {
        Flux<String> fluxNames = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
                .window(2) // Flux<Flux<String>> -> (A,B), (C,D), (E,F)
//                .concatMap((s) ->
//                        s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
                .flatMapSequential((s) ->
                        s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
                .log(); // db or external service call that returns a flux -> s -> Flux<String>

        StepVerifier.create(fluxNames)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Arrays.asList(s, "New Value");
    }
}
