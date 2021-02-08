package com.danvarga.reactordemo.reactivesandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("After error"))
                .log();

        stringFlux
                .subscribe(
                        System.out::println,
                        (e) -> System.err.println("Exception is: " + e),
                        () -> System.out.println("Completed"));
    }

    @Test
    void fluxTestElements_Without_Error() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();


    }

    @Test
    void fluxTestElementsCount_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectError(RuntimeException.class)
//                .expectErrorMessage("Exception Occurred")
                .verify();
    }

    @Test
    void fluxTestWithError1() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectErrorMessage("Exception Occurred")
                .verify();
    }

    @Test
    void monoTest() {
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    void monoTestError() {
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void test() {
        boolean huh = false;
        huh = Flux.just("Spring", "Boot")
                .map(word -> word.concat(" boot"))
                .subscribe().isDisposed();

        System.out.println(huh);
    }
}
