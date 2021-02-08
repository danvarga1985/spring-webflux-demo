package com.danvarga.reactordemo.reactivesandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("adam", "eve", "ernst", "knut");

    @Test
    void fluxUsingIterable() {
        Flux<String> fluxNames = Flux.fromIterable(names);

        StepVerifier.create(fluxNames)
                .expectNext("adam", "eve", "ernst", "knut")
                .verifyComplete();
    }

    @Test
    void fluxUsingArray() {
        String[] stringNames = new String[]{"adam", "eve", "ernst", "knut"};

        Flux<String> fluxNames = Flux.fromArray(stringNames);

        StepVerifier.create(fluxNames)
                .expectNext("adam", "eve", "ernst", "knut")
                .verifyComplete();
    }

    @Test
    void fluxUsingStream() {
        Flux<String> fluxNames = Flux.fromStream(names.stream());

        StepVerifier.create(fluxNames)
                .expectNext("adam", "eve", "ernst", "knut")
                .verifyComplete();
    }

    @Test
    void monoUsingJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null); // Mono.Empty()

        StepVerifier.create(mono)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void monoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "ernst";

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        System.out.println(stringSupplier.get());

        StepVerifier.create(stringMono.log())
                .expectNext("ernst")
                .verifyComplete();
    }

    @Test
    void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5).log();

        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }
}
