package com.reactive.springbootreactivecrud.controller;

import com.reactive.springbootreactivecrud.dto.BookDTO;
import com.reactive.springbootreactivecrud.service.BookService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/books")
@AllArgsConstructor
@Validated
public class BookController {
    BookService bookService;
    @PostMapping("/add")
    public Mono<ResponseEntity<BookDTO>> saveBook(@Valid @RequestBody BookDTO bookDTO) {
        return bookService.saveBook(bookDTO)
                .map(savedBookDTO -> ResponseEntity.status(HttpStatus.CREATED).body(savedBookDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Already Exists")));
    }


    @GetMapping("getAll")
    public Flux<BookDTO> getAllBooks(){
        return bookService.getAllBooks()
                .switchIfEmpty(Mono.error
                        (new ResponseStatusException(HttpStatus.NOT_FOUND,"No books found" )));
    }

    @GetMapping("book/{author}")
    public Mono<BookDTO> getBook(@PathVariable String author){
        return bookService.getBook(author)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND
                , "No books for this author")));
    }

    @PutMapping("/updateBook/{id}")
    public Mono<ResponseEntity<BookDTO>> updateBook(@Valid @RequestBody BookDTO bookDTO, @PathVariable String id) {
        return bookService.updateBook(bookDTO, id)
                .map(savedBookDTO -> ResponseEntity.status(HttpStatus.CREATED).body(savedBookDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Saved")));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> deleteBook(@PathVariable String id) {
        return bookService.deleteBook(id)
                .map(message -> ResponseEntity.ok(message))
                .onErrorResume(ResponseStatusException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    
}
