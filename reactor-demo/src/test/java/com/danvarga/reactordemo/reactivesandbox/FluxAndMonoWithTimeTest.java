package com.danvarga.reactordemo.reactivesandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .log(); // Starts from 0 --> ....

        infiniteFlux.subscribe(System.out::println);

        // Only during sleep will the publisher emit elements - won't run infinitely.
        Thread.sleep(3000);
    }

    @Test
    void infiniteSequenceTest() {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log(); // Starts from 0 --> ....

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    void infiniteSequenceMap() {
        Flux<Integer> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .map(Long::intValue)
                .take(3)
                .log(); // Starts from 0 --> ....

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    void infiniteSequenceMap_WithDelay() {
        Flux<Integer> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofMillis(1000))
                .map(Long::intValue)
                .take(3)
                .log(); // Starts from 0 --> ....

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
}
