package com.danvarga.reactordemo.controller.v1;

import com.danvarga.reactordemo.constants.ItemConstants;
import com.danvarga.reactordemo.document.Item;
import com.danvarga.reactordemo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    @GetMapping(ItemConstants.ITEM_ENDPOINT_V1)
    public Flux<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {
        return itemRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ItemConstants.ITEM_ENDPOINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    @DeleteMapping(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<Void> deleteItemById(@PathVariable String id) {
        return itemRepository.deleteById(id);
    }

    @GetMapping(ItemConstants.ITEM_ENDPOINT_V1 + "/runtimeException")
    public Flux<Item> runtimeException() {
        return itemRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("RuntimeException Occurred!")));
    }


    @PutMapping(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@RequestBody Item item, @PathVariable String id) {
        return itemRepository.findById(id)
                .flatMap(currentItem -> {
                    boolean changed = false;

                    if (!item.getDescription().isEmpty() && !item.getDescription().equals(currentItem.getDescription())) {
                        currentItem.setDescription(item.getDescription());
                        changed = true;
                    }
                    if (item.getPrice() != null && !item.getPrice().equals(currentItem.getPrice())) {
                        currentItem.setPrice(item.getPrice());
                        changed = true;
                    }
                    if (changed) {
                        return itemRepository.save(currentItem);
                    } else {
                        return Mono.just(currentItem);
                    }
                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
}
