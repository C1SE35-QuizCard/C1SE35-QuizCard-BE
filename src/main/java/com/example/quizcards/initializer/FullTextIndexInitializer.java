//package com.example.quizcards.initializer;
//
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class FullTextIndexInitializer implements CommandLineRunner {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public FullTextIndexInitializer(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @Override
//    public void run(String... args) {
//        jdbcTemplate.execute("ALTER TABLE set_flashcards ADD FULLTEXT(title)");
//        jdbcTemplate.execute("ALTER TABLE category_set_flashcards ADD FULLTEXT(category_name)");
//    }
//}