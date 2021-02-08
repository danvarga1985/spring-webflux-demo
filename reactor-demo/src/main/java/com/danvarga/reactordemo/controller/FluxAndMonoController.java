package com.danvarga.reactordemo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> returnFlux() {
        return Flux.just(1, 2, 3)
//                .delayElements(Duration.ofMillis(1000))
                .log();
    }

    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Long> returnFluxStream() {
        return Flux.interval(Duration.ofMillis(1000))
                .log();
    }

    @GetMapping("/mono")
    public Mono<Integer> returnMono() {
        return Mono.just(1)
                .log();
    }
}
