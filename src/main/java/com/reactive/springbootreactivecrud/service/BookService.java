package com.reactive.springbootreactivecrud.service;

import com.reactive.springbootreactivecrud.dto.BookDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface BookService {

    Mono<BookDTO> saveBook(BookDTO bookDTO);
    Flux<BookDTO> getAllBooks();
    Mono<BookDTO> getBook(String author);
    Mono<BookDTO> updateBook(BookDTO bookDTO, String id);
    Mono<String> deleteBook(String id);


}
