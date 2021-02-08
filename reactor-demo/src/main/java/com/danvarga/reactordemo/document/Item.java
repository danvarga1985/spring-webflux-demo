package com.danvarga.reactordemo.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Item {

    @Id
    private String id;
    private String description;
    private Double price;
}
