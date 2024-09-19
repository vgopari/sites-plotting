package com.demo.database;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class DBConnectionVerifier {

    private final Logger logger = LoggerFactory.getLogger("DBConnectionVerifier");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        assertDoesNotThrow(() -> {
            jdbcTemplate.execute("SELECT 1");
            logger.info("Database connection verified!");
        });
    }
}
