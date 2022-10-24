package org.example.web.controllers;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.example.app.services.BookService;
import org.example.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.*;

@Controller
@RequestMapping(value = "books")
@Scope("singleton")
public class BookShelfController {

    private Logger logger = Logger.getLogger(BookShelfController.class);
    private BookService bookService;

    @Autowired
    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/shelf")
    public String books(Model model){
        logger.info(this.toString());
        model.addAttribute("book", new Book());
        setAttributes(model);
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(@Valid Book book, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("book", book);
            setAttributes(model);
            return "book_shelf";
        } else {
            bookService.saveBook(book);
            logger.info("current repository size: " + bookService.getAllBooks().size());
            return "redirect:/books/shelf";
        }
//        if (book.is_valid()){
//            bookService.saveBook(book);
//            logger.info("current repository size: " + bookService.getAllBooks().size());
//        }
//        return "redirect:/books/shelf";
    }

    @PostMapping("/remove")
    public String removeBook(@Valid BookIdToRemove bookIdToRemove, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            setAttributesForRemoving(model);
            model.addAttribute("bookAuthorToRemove", new BookAuthorToRemove());
            model.addAttribute("bookTitleToRemove", new BookTitleToRemove());
            model.addAttribute("bookSizeToRemove", new BookSizeToRemove());
            return "book_shelf";
        } else {
            bookService.removeBookById(bookIdToRemove.getId());
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/removeByAuthor")
    public String removeBookByAuthor(@Valid BookAuthorToRemove bookAuthorToRemove, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            setAttributesForRemoving(model);
            model.addAttribute("bookIdToRemove", new BookIdToRemove());
            model.addAttribute("bookTitleToRemove", new BookTitleToRemove());
            model.addAttribute("bookSizeToRemove", new BookSizeToRemove());
            return "book_shelf";
        } else {
            bookService.removeBooksByAuthor(bookAuthorToRemove.getAuthor());
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/removeByTitle")
    public String removeBookByTitle(@Valid BookTitleToRemove bookTitleToRemove, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            setAttributesForRemoving(model);
            model.addAttribute("bookIdToRemove", new BookIdToRemove());
            model.addAttribute("bookAuthorToRemove", new BookAuthorToRemove());
            model.addAttribute("bookSizeToRemove", new BookSizeToRemove());
            return "book_shelf";
        } else {
            bookService.removeBooksByTitle(bookTitleToRemove.getTitle());
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/removeBySize")
    public String removeBookBySize(@Valid BookSizeToRemove bookSizeToRemove, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            setAttributesForRemoving(model);
            model.addAttribute("bookIdToRemove", new BookIdToRemove());
            model.addAttribute("bookAuthorToRemove", new BookAuthorToRemove());
            model.addAttribute("bookTitleToRemove", new BookTitleToRemove());
            return "book_shelf";
        } else {
            bookService.removeBooksBySize(bookSizeToRemove.getSize());
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file")MultipartFile file) throws FileUploadException, IOException {
        if (file.isEmpty()) {
            throw new FileUploadException("upload file not selected");
        }
        String name = file.getOriginalFilename();
        byte[] bytes = file.getBytes();

        String rootPath = System.getProperty("catalina.home");
        File dir = new File(rootPath + File.separator + "external_uploads");
        if (!dir.exists()){
            dir.mkdirs();
        }

        File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(bytes);
        stream.close();

        logger.info("new file saved at: " + serverFile.getAbsolutePath());

        return "redirect:/books/shelf";
    }

    @ExceptionHandler(FileUploadException.class)
    public String handleEmptyUploadFile(Model model, FileUploadException exception) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/500";
    }

    private void setAttributes(Model model){
        model.addAttribute("bookList", bookService.getAllBooks());
        model.addAttribute("bookIdToRemove", new BookIdToRemove());
        model.addAttribute("bookAuthorToRemove", new BookAuthorToRemove());
        model.addAttribute("bookTitleToRemove", new BookTitleToRemove());
        model.addAttribute("bookSizeToRemove", new BookSizeToRemove());
    }

    private void setAttributesForRemoving(Model model){
        model.addAttribute("book", new Book());
        model.addAttribute("bookList", bookService.getAllBooks());
    }
}
