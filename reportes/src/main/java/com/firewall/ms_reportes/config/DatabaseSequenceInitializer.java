package com.firewall.ms_reportes.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseSequenceInitializer {

    @Bean
    ApplicationRunner alignIdentitySequences(JdbcTemplate jdbcTemplate) {
        return args -> {
            alignSequence(jdbcTemplate, "usuario");
            alignSequence(jdbcTemplate, "reporte");
            alignSequence(jdbcTemplate, "multimedia");
        };
    }

    private void alignSequence(JdbcTemplate jdbcTemplate, String tableName) {
        jdbcTemplate.queryForObject("""
                select setval(
                    pg_get_serial_sequence(current_schema() || '.%s', 'id'),
                    coalesce((select max(id) from %s), 0) + 1,
                    false
                )
                """.formatted(tableName, tableName), Long.class);
    }
}
