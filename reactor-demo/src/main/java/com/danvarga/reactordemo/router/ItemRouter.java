package com.danvarga.reactordemo.router;

import com.danvarga.reactordemo.constants.ItemConstants;
import com.danvarga.reactordemo.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemRouter {

    @Bean
    public RouterFunction<ServerResponse> itemRoute(ItemHandler itemHandler) {
        return RouterFunctions
                .route(GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
                                .and(accept(MediaType.APPLICATION_JSON)),
                        itemHandler::getAllItems)

                .andRoute(GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1 + "/{id}")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        itemHandler::getOneItemById)

                .andRoute(POST(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
                                .and(accept(MediaType.APPLICATION_JSON)),
                        itemHandler::createItem)

                .andRoute(DELETE(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1 + "/{id}")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        itemHandler::deleteItem)

                .andRoute(PUT(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1 + "/{id}")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        itemHandler::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemHandler itemHandler) {
        return RouterFunctions
                .route(GET("/fun/runtimeexception")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        itemHandler::itemEx);
    }

    @Bean
    public RouterFunction<ServerResponse> streamItemsRoute(ItemHandler itemHandler) {
        return RouterFunctions.route(GET(ItemConstants.ITEM_STREAM_FUNCTIONAL_ENDPOINT_V1)
                        .and(accept(MediaType.APPLICATION_NDJSON)),
                itemHandler::streamAllItems);
    }

}
