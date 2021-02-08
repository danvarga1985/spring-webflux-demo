package com.danvarga.reactordemo.reactivesandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectNext("A", "B", "C", "D", "E", "F")
//                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void combineUsingMerge_WithDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(500));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(500));

        // After the emission of the first element in the first flux, the second flux starts emitting.
        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
//                .expectNext("A", "B", "C", "D", "E", "F") // Order won't be sequential
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void combineUsingConcat() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectNext("A", "B", "C", "D", "E", "F")
//                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void combineUsingConcat_WithDelay() {
        VirtualTimeScheduler.getOrSet();

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(500));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(500));

        // 'concat' keeps the order.
        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.withVirtualTime(() -> mergedFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofMillis(3000))
//                .expectNext("A", "B", "C", "D", "E", "F")
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void combineUsingZip() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(500));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(500));

        Flux<String> mergedFlux = Flux.zip(flux1, flux2, (t1, t2) -> {
            return t1.concat(t2);
        });

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}
