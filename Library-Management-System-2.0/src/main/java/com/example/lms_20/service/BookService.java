package com.example.lms_20.service;

import com.example.lms_20.model.Book;
import com.example.lms_20.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findBookById(long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void  saveBook(Book book) {
        bookRepository.save(book);
    }

    public List<Book> deleteAll() {
        List<Book> books = bookRepository.findAll();
         bookRepository.deleteAll();
         return books;
    }


    public void deleteBook(long id) {
        bookRepository.deleteById(id);
    }


}
