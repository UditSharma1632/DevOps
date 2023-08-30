package com.reactive.springbootreactivecrud.service;

import com.reactive.springbootreactivecrud.Exception.CustomException;
import com.reactive.springbootreactivecrud.dto.BookDTO;
import com.reactive.springbootreactivecrud.model.Book;
import com.reactive.springbootreactivecrud.repository.BookRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private Mono<Boolean> checkForExistingBook(BookDTO bookDTO) {
        Book book = modelMapper.map(bookDTO, Book.class);
        return bookRepo.findByNameAndAuthorAndPublishDate(book.getName(),
                        book.getAuthor(), book.getPublishDate())
                .map(existingBook -> false) // Book already exists
                .defaultIfEmpty(true); // Book doesn't exist
    }

    @Override
    public Mono<BookDTO> saveBook(BookDTO bookDTO) {
        return checkForExistingBook(bookDTO)
                .flatMap(result -> {
                    if (result) {
                        Book book = modelMapper.map(bookDTO, Book.class);
                        return bookRepo.save(book)
                                .map(savedBook -> modelMapper.map(savedBook, BookDTO.class));
                    } else {
                        return Mono.error(new CustomException("Book is already there",
                                HttpStatus.BAD_REQUEST));
                    }
                });
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
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book does not exist")))
                .flatMap(existingBook -> {
                    return checkForExistingBook(bookDTO)
                            .flatMap(detailsExist -> {
                                if (detailsExist) {
                                    updateBookDetails(existingBook, bookDTO);
                                    return bookRepo.save(existingBook)
                                            .map(updatedBook -> modelMapper.map(updatedBook, BookDTO.class));
                                } else {
                                    return Mono.error(new CustomException
                                            (("Book details already exist for another book")
                                                    , HttpStatus.BAD_REQUEST));
                                }
                            });
                });
    }



    private void updateBookDetails(Book book, BookDTO bookDTO){
        book.setName(bookDTO.getName());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublishDate(bookDTO.getPublishDate());
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
