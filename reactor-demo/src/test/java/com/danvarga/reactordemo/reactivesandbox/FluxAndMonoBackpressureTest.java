package com.danvarga.reactordemo.reactivesandbox;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackpressureTest {

    @Test
    void backpressureTest() {
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    void backpressure() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        // Deprecated!
//        finiteFlux.subscribe((element) -> System.out.println("Element is " + element),
//                (exception) -> System.err.println("Exception is: " + exception),
//                () -> System.out.println("Done"),
//                (subscription -> subscription.request(10)));
//
//        System.out.println(" ************ ");

        finiteFlux.subscribeWith(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                subscription.request(5);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("Element is: " + value);
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.err.println("Exception is: " + throwable.getMessage());
            }
        });
    }

    @Test
    void backpressure_Cancel() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribeWith(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                subscription.cancel();
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("Element is: " + value);
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.err.println("Exception is: " + throwable.getMessage());
            }
        });
    }

    @Test
    void customized_backpressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribeWith(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value received is: " + value);
                if (value == 4) {
                    cancel();
                }
            }
        });
    }

}
