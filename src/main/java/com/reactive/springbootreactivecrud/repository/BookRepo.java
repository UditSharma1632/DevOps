package com.reactive.springbootreactivecrud.repository;

import com.reactive.springbootreactivecrud.model.Book;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface BookRepo extends ReactiveCrudRepository<Book, String> {

    Mono<Book> findByAuthor(String author);
    Mono<Book> findByNameAndAuthorAndPublishDate(String name, String Author, String publishDate);
    Mono<Book> findById(String id);
    Mono<Boolean> existsByNameAndIdNot(String name, String id);
}

