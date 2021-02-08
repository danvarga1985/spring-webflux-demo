package com.danvarga.reactordemo.reactivesandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("adam", "eve", "ernst", "knut");

    @Test
    void filterTest() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.startsWith("e"))
                .log();

        namesFlux
                .subscribe(
                        System.out::println,
                        (e) -> System.err.println("Exception is: " + e),
                        () -> System.out.println("Completed"));
    }

    @Test
    void filterLengthTest() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .log();

        namesFlux
                .subscribe(
                        (name) -> System.out.println("The name is: " + name),
                        (e) -> System.err.println("Error: " + e),
                        () -> System.out.println("Complete")
                );
    }
}
