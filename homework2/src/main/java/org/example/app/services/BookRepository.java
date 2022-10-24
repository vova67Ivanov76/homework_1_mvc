package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepository implements ProjectRepository<Book>, ApplicationContextAware {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    //private final List<Book> repo = new ArrayList<>();
    private ApplicationContext context;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Book> retrieveAll() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM books", (ResultSet rs, int rowNum) -> {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setSize(rs.getInt("size"));
            return book;
        });
        return new ArrayList<>(books);
    }

    @Override
    public void store(Book book) {
       // book.setId(context.getBean(IdProvider.class).provideId(book));
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author", book.getAuthor());
        parameterSource.addValue("title", book.getTitle());
        parameterSource.addValue("size", book.getSize());
        jdbcTemplate.update("INSERT INTO books(author,title,size) VALUES(:author,:title,:size)", parameterSource);
        logger.info("store new book: " + book);
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", bookIdToRemove);
        jdbcTemplate.update("DELETE FROM books WHERE id = :id", parameterSource);
        logger.info("remove book completed");
        return true;
//        for(Book book : retrieveAll()){
//            if (book.getId().equals(bookIdToRemove)) {
//                logger.info("remove book completed: " + book);
//                return true; //repo.remove(book);
//            }
//        }
//        return false;
    }

    @Override
    public boolean removeItemByAuthor(String author) {
        if (!author.isEmpty()) {
            for (Book book : retrieveAll()) {
                if (book.getAuthor().contains(author)) {
                    logger.info("remove books by author completed: " + book);
                    //repo.remove(book);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeItemByTitle(String title) {
        if (!title.isEmpty()){
            for (Book book : retrieveAll()) {
                if (book.getTitle().contains(title)) {
                    logger.info("remove books by title completed: " + book);
                    //repo.remove(book);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeItemBySize(Integer size) {
        if (size != null) {
            for (Book book : retrieveAll()) {
                if (book.getSize().equals(size)) {
                    logger.info("remove books by size completed: " + book);
                    //repo.remove(book);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private void defaultInit() {
        logger.info("default INIT in book repo bean");
    }

    private void defaultDestroy() {
        logger.info("default DESTROY in book repo bean");
    }

}
