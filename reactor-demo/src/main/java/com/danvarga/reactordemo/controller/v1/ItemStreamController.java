package com.danvarga.reactordemo.controller.v1;

import com.danvarga.reactordemo.constants.ItemConstants;
import com.danvarga.reactordemo.document.ItemCapped;
import com.danvarga.reactordemo.repository.ItemCappedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class ItemStreamController {

    private final ItemCappedRepository itemCappedRepository;

    // ND_JSON_STREAM_VALUE will force the browser to download the values into a file, instead of displaying them - 2021.02.8
    @GetMapping(value = ItemConstants.ITEM_STREAM_ENDPOINT_V1, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ItemCapped> getItemsStream() {
        return itemCappedRepository.findItemsBy();
    }
}
