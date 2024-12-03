
package com.example.quizcards.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FullTextIndexInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public FullTextIndexInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        addFullTextIndexIfNotExists("set_flashcards", "title");
        addFullTextIndexIfNotExists("category_set_flashcards", "category_name");
    }

    private void addFullTextIndexIfNotExists(String tableName, String columnName) {
        String checkIndexQuery = "SELECT COUNT(1) " +
                "FROM INFORMATION_SCHEMA.STATISTICS " +
                "WHERE TABLE_NAME = ? AND COLUMN_NAME = ? AND INDEX_TYPE = 'FULLTEXT'";

        Integer count = jdbcTemplate.queryForObject(checkIndexQuery, Integer.class, tableName, columnName);

        if (count == null || count == 0) {
            String addIndexQuery = String.format("ALTER TABLE %s ADD FULLTEXT(%s)", tableName, columnName);
            try {
                jdbcTemplate.execute(addIndexQuery);
                System.out.println("FULLTEXT index created successfully on " + tableName + "." + columnName);
            } catch (Exception e) {
                System.err.println("Error creating FULLTEXT index on " + tableName + "." + columnName + ": " + e.getMessage());
            }
        } else {
            System.out.println("FULLTEXT index already exists on " + tableName + "." + columnName);
        }
    }
}
