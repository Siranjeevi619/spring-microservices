package com.example.lms_20.controller;

import com.example.lms_20.dto.BookResponse;
import com.example.lms_20.model.Book;
import com.example.lms_20.model.BookDTO;
import com.example.lms_20.payload.ApiResponse;
import com.example.lms_20.payload.Status;
import com.example.lms_20.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/book")
@CrossOrigin
public class BookController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private BookService bookService;


    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Book>>> bookList() {
        try {
            List<Book> bookData = bookService.findAll();
            if (bookData.isEmpty()) {
                return ResponseEntity.status(400)
                        .body(new ApiResponse<>(Status.REJECTED, "0 Documents Found", new ArrayList<>()));
            }
            return ResponseEntity.ok(new ApiResponse<>(Status.SUCCESS, bookData.size() + " Document(s) found", bookData));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(Status.REJECTED, e.getMessage(), new ArrayList<>()));
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        Book book = bookService.findBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId());
        bookResponse.setBookName(book.getTitle());
        bookResponse.setBookPrice(book.getPrice());
        bookResponse.setBookAuthor(book.getAuthor());
        return ResponseEntity.ok(bookResponse);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> addBook(
            @RequestPart("book") BookDTO bookDTO,
            @RequestPart("image") MultipartFile imageFile) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Book book = new Book();
            book.setTitle(bookDTO.getTitle());
            book.setAuthor(bookDTO.getAuthor());
            book.setPublisher(bookDTO.getPublisher());
            book.setDescription(bookDTO.getDescription());
            book.setPrice(bookDTO.getPrice());

            book.setImageUrl("/api/book/image/" + fileName);

            bookService.saveBook(book);
            return ResponseEntity.status(200).body(new  ApiResponse<>(Status.SUCCESS, "Book Added Successfully", book));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse<>(Status.REJECTED, "INTERNAL SERVER ERROR: "+e.getMessage(), e ));
        }
    }

    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<ApiResponse<?>> getImage(@PathVariable String fileName) {
        try {
            Path imagePath = Paths.get(uploadDir).resolve(fileName).normalize();
            byte[] imageBytes = Files.readAllBytes(imagePath);

            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            return  ResponseEntity.status(200).body(new ApiResponse<>(Status.SUCCESS, "Image Fetched Successfully", imageBytes));
        } catch (Exception e) {
            return  ResponseEntity.status(200).body(new ApiResponse<>(Status.REJECTED, "Internal Server Error: "+e.getMessage(), e));
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> updateBook(
            @PathVariable("id") long id,
            @RequestPart("book") BookDTO bookDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        try {
            Book existingBook = bookService.findBookById(id);
            if (existingBook == null) {
                return ResponseEntity.status(404)
                        .body(new ApiResponse<>(Status.FAILED, "BOOK NOT FOUND", null));
            }

            existingBook.setTitle(bookDTO.getTitle());
            existingBook.setAuthor(bookDTO.getAuthor());
            existingBook.setPublisher(bookDTO.getPublisher());
            existingBook.setDescription(bookDTO.getDescription());
            existingBook.setPrice(bookDTO.getPrice());
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = imageFile.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                existingBook.setImageUrl("/api/book/image/" + fileName);
            }

            bookService.saveBook(existingBook);

            return ResponseEntity.ok(new ApiResponse<>(Status.SUCCESS, "BOOK UPDATED SUCCESSFULLY", existingBook));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(Status.REJECTED, "INTERNAL_SERVER_ERROR", e));
        }
    }


    @DeleteMapping("/delete-all")
    public ResponseEntity<ApiResponse<?>> deleteAllBooks(){
        try{
            List<Book> books = bookService.deleteAll();
            if(books.isEmpty()){
                return ResponseEntity.status(400).body(new ApiResponse<>(Status.FAILED, "BOOK LIST IS EMPTY", books));
            }
            return ResponseEntity.status(200).body(new ApiResponse<>(Status.SUCCESS, "BOOKS DELETED SUCCESSFULLY AND BOOKS FOUND: "+books.size() , books));

        }
        catch(Exception e){
            return ResponseEntity.status(500).body(new ApiResponse<>(Status.REJECTED, "INTERNAL_SERVER_ERROR : "+e.getMessage(), e));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBookById(@PathVariable("id") long id){
        try{
            Book book = bookService.findBookById(id);
            if(book == null){
                return  ResponseEntity.status(404).body(new ApiResponse<>(Status.FAILED, "BOOK NOT FOUND", null));
            }
            bookService.deleteBook(id);
            return ResponseEntity.status(200).body(new ApiResponse<>(Status.SUCCESS, "BOOK DELETED SUCCESSFULLY", book));
        }
        catch(Exception e){
            return ResponseEntity.status(500).body(new ApiResponse<>(Status.REJECTED, "INTERNAL SERVER ERROR"+e.getMessage() , e));
        }
    }



}
