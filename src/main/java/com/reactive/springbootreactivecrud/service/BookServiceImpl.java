package com.reactive.springbootreactivecrud.service;

import com.reactive.springbootreactivecrud.Exception.CustomException;
import com.reactive.springbootreactivecrud.dto.BookDTO;
import com.reactive.springbootreactivecrud.model.Book;
import com.reactive.springbootreactivecrud.repository.BookRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService{
    BookRepo bookRepo;

    ModelMapper modelMapper;

    @Override
    public Mono<BookDTO> saveBook(BookDTO bookDTO) {
        Book book = modelMapper.map(bookDTO, Book.class);
        return bookRepo.findByNameAndAuthorAndPublishDate(book.getName(), book.getAuthor(), book.getPublishDate()).
                flatMap(book1 -> {
                    return Mono.error(new CustomException(("Book is already there"), HttpStatus.BAD_REQUEST.value()));
                })
                .switchIfEmpty(bookRepo.save(book)).map(book1 -> modelMapper.map(book1, BookDTO.class));
//        Mono<Book> savedBook = bookRepo.save(book);
//        return savedBook.map(book1 -> modelMapper.map(book1, BookDTO.class));
    }

    @Override
    public Flux<BookDTO> getAllBooks() {
        Flux<Book> allBooks = bookRepo.findAll();
        return allBooks.map(book -> modelMapper.map(book, BookDTO.class));
    }

    @Override
    public Mono<BookDTO> getBook(String author) {
        Mono<Book> book = bookRepo.findByAuthor(author);
        return book.map(book1 -> modelMapper.map(book1, BookDTO.class));
    }

    @Override
    public Mono<BookDTO> updateBook(BookDTO bookDTO, String id) {
        return bookRepo.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found")))
                .flatMap(existingBook -> {
                    if (isUpdateNeeded(existingBook, bookDTO)) {
                        updateBookDetails(existingBook, bookDTO);
                        return bookRepo.save(existingBook)
                                .map(updatedBook -> modelMapper.map(updatedBook, BookDTO.class));
                    } else {
                        return Mono.just(modelMapper.map(existingBook, BookDTO.class));
                    }
                });
    }

    private boolean isUpdateNeeded(Book existingBook, BookDTO updatedBookDTO) {
        return !existingBook.getName().equals(updatedBookDTO.getName())
                || !existingBook.getAuthor().equals(updatedBookDTO.getAuthor())
                || !existingBook.getPublishDate().equals(updatedBookDTO.getPublishDate());
    }

    private void updateBookDetails(Book existingBook, BookDTO updatedBookDTO) {
        existingBook.setName(updatedBookDTO.getName());
        existingBook.setAuthor(updatedBookDTO.getAuthor());
        existingBook.setPublishDate(updatedBookDTO.getPublishDate());
    }




    @Override
    public Mono<String> deleteBook(String id) {
        return bookRepo.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return bookRepo.deleteById(id)
                                .thenReturn("Book deleted successfully");
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
                    }
                });
    }





}
