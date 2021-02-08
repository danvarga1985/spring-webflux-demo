package com.danvarga.reactordemo.reactivesandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    void testingWithoutVirtualTime() {
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .log();

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    void testingWithVirtualTime() {
        VirtualTimeScheduler.getOrSet(); // Overwrites the system clock with a virtual one, so that long delays can be simulated

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .log();

        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }
}
