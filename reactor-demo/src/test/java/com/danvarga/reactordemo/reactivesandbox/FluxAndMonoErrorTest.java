package com.danvarga.reactordemo.reactivesandbox;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
public class FluxAndMonoErrorTest {

    @Test
    void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume((e) -> {
                    System.out.println("Exception is: " + e);
                    return Flux.just("default", "default1");
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
//                .expectError(RuntimeException.class)
//                .verify();
                .expectNext("default", "default1")
                .verifyComplete();
    }

    @Test
    void fluxErrorHandling_OnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default")
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
//                .expectError(RuntimeException.class)
//                .verify();
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void fluxErrorHandling_OnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    void fluxErrorHandling_OnErrorMap_WithRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
//                .retry(1);
                .retryWhen(Retry.backoff(1, Duration.ofMillis(10000)));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
//                .expectError(CustomException.class)
                .expectError(IllegalStateException.class) // Different error is being thrown with 'Retry.backoff'.
                .verify();
    }
}
