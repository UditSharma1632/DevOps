package com.reactive.springbootreactivecrud.controller;

import com.reactive.springbootreactivecrud.dto.BookDTO;
import com.reactive.springbootreactivecrud.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveBookSuccessCase() {
        BookDTO bookDTO = new BookDTO("1", "Title", "Author", "2022-09-20");

        when(bookService.saveBook(bookDTO)).thenReturn(Mono.just(bookDTO));

        ResponseEntity<BookDTO> responseEntity = bookController.saveBook(bookDTO).block();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(bookDTO, responseEntity.getBody());

        verify(bookService, times(1)).saveBook(bookDTO);

        System.out.println("Response Entity: " + responseEntity);
    }

    @Test
    void testSaveBookNotSuccess() {

        BookDTO bookDTO = new BookDTO("1", "Title", "Author", "2022-09-20");
        when(bookService.saveBook(bookDTO)).thenReturn(Mono.empty()); // Simulate a conflict

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> bookController.saveBook(bookDTO).block());

        assertNotNull(exception);
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Already Exists", exception.getReason());

        verify(bookService, times(1)).saveBook(bookDTO);
    }


    @Test
    void getAllBooksSuccess() {
        BookDTO book1 = new BookDTO("1", "Title1", "Author1", "2022-09-20");
        BookDTO book2 = new BookDTO("2", "Title2", "Author2", "2022-09-21");
        when(bookService.getAllBooks()).thenReturn(Flux.just(book1, book2));

        Flux<BookDTO> response = bookController.getAllBooks();

        assertNotNull(response);
        assertEquals(2, response.count().block());

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void getAllBooksNotSuccess() {
        when(bookService.getAllBooks()).thenReturn(Flux.empty()); // Simulate no books found

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> bookController.getAllBooks().collectList().block());

        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No books found", exception.getReason());

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void getBookSuccess() {
        String author = "Author";
        BookDTO bookDTO = new BookDTO("1", "Title", author, "2022-09-20");
        when(bookService.getBook(author)).thenReturn(Mono.just(bookDTO));

        Mono<BookDTO> response = bookController.getBook(author);

        assertNotNull(response);
        assertEquals(bookDTO, response.block());

        verify(bookService, times(1)).getBook(author);
    }

    @Test
    void getBookNotSuccess() {
        String author = "NonExistentAuthor";
        when(bookService.getBook(author)).thenReturn(Mono.empty()); // Simulate no book found for author

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> bookController.getBook(author).block());

        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No books for this author", exception.getReason());

        verify(bookService, times(1)).getBook(author);
    }

    @Test
    void updateBookSuccess() {
        String bookId = "1";
        BookDTO bookDTO = new BookDTO("1", "Updated Title", "Updated Author", "2022-09-22");
        when(bookService.updateBook(eq(bookDTO), eq(bookId))).thenReturn(Mono.just(bookDTO));

        ResponseEntity<BookDTO> responseEntity = bookController.updateBook(bookDTO, bookId).block();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(bookDTO, responseEntity.getBody());

        verify(bookService, times(1)).updateBook(eq(bookDTO), eq(bookId));
    }

    @Test
    void updateBookNotSuccess() {
        String bookId = "1";
        BookDTO bookDTO = new BookDTO("1", "Title", "Author", "2022-09-20");
        when(bookService.updateBook(eq(bookDTO), eq(bookId))).thenReturn(Mono.empty()); // Simulate a bad request

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> bookController.updateBook(bookDTO, bookId).block());

        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Not Saved", exception.getReason());
        verify(bookService, times(1)).updateBook(eq(bookDTO), eq(bookId));
    }

    @Test
    void deleteBook() {

        String bookId = "1";
        when(bookService.deleteBook(bookId)).thenReturn(Mono.just("Book deleted successfully"));

        ResponseEntity<String> responseEntity = bookController.deleteBook(bookId).block();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Book deleted successfully", responseEntity.getBody());

        verify(bookService, times(1)).deleteBook(bookId);
    }

}