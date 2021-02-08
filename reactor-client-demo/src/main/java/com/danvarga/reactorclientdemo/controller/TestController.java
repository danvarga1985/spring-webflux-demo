package com.danvarga.reactorclientdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class TestController {

    private static String story = "the quick brown fox jumped over the lazy fence and then noticed another quick black fox " +
            "that was much quicker than the original fox but the original fox was able to jump higher over the fence";

    @GetMapping(value = {"/word-count/v3", "/word-count/v3/{limit}"})
    public Flux<Tuple2<String, Long>> wordCount3(@PathVariable(required = false) Integer limit) {
        // 1. String - String[]
        // 2. lowercase
        // 3. word count
        // 4. sort
        // 5. take()

        return Flux.fromArray(story.split(" ")) // Flux<String[]> OR String[] ??????
                .map(String::toLowerCase) // String[] lowerCase: ["the", "quick" ...]
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())) // MAP: { {"the": 5}, {"quick": 10} ...}
                .flatMapIterable(Map::entrySet)
                .sort((a, b) -> b.getValue().compareTo(a.getValue())) // MAP: { {"quick": 10}, {"the": 5} ...}
                .map(a -> Tuples.of(a.getKey(), a.getValue()))
                .take(limit != null ? limit: 2); // 1: TUPLE<String, Long>: {"quick", 10} -> 2: TUPLE<String, Long>: {"the", 5} ...

    }


}
