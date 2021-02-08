package com.danvarga.reactordemo.reactivesandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

// The whole reactivesandbox package is excluded from the tests in 'build.gradle' - none of it will run.
public class ColdAndHotPublisherTest {

    @Test
    void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofMillis(1000));

        stringFlux.subscribe(s -> System.out.println("Subscriber 1: " + s)); // Emits the value from the beginning

        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("Subscriber 2: " + s)); // Emits the value from the beginning

        Thread.sleep(2000);
    }

    @Test
    void hotPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofMillis(1000));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();

        connectableFlux.connect();

        connectableFlux.subscribe((s -> System.out.println("Subscriber 1: " + s)));

        Thread.sleep(3000);

        connectableFlux.subscribe((s -> System.out.println("Subscriber 2: " + s))); // Does not emit values from the beginning

        Thread.sleep(4000);
    }
}
