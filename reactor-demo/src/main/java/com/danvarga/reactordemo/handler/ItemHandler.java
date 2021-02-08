package com.danvarga.reactordemo.handler;

import com.danvarga.reactordemo.document.Item;
import com.danvarga.reactordemo.document.ItemCapped;
import com.danvarga.reactordemo.repository.ItemCappedRepository;
import com.danvarga.reactordemo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class ItemHandler {

    private final ItemRepository itemRepository;
    private final ItemCappedRepository itemCappedRepository;
    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRepository.findAll(), Item.class);

    }

    public Mono<ServerResponse> getOneItemById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        Mono<Item> itemMono = itemRepository.findById(id);

        return itemMono
                .flatMap(item ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(item)))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        Mono<Item> newItem = serverRequest.bodyToMono(Item.class);

        return newItem
                .flatMap(item ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(itemRepository.save(item), Item.class)
                );
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        Mono<Void> deleteItem = itemRepository.deleteById(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap(item -> itemRepository.findById(id)
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
                );

        return updatedItem.flatMap(item -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON).body(fromValue(item)))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> itemEx(ServerRequest serverRequest) {
        throw new RuntimeException("RuntimeException Occurred!");
    }

    public Mono<ServerResponse> streamAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(itemCappedRepository.findItemsBy(), ItemCapped.class);
    }
}
