package com.reactive.springbootreactivecrud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {

    private String id;
    private String name;
    private String author;
    private String publishDate;

    public BookDTO(String error) {
    }
}
