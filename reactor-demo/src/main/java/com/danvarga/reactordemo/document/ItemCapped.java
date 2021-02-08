package com.danvarga.reactordemo.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class ItemCapped {

    @Id
    private String id;
    private String description;
    private Double price;
}
