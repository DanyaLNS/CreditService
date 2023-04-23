package mts.fintech.creditservice.configuration;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ApplicationConfig {
    @Bean
    JdbcTemplate jdbcTemplate() {
        final String driverClassName = "org.postgresql.Driver";
        final String jdbcUrl = "jdbc:postgresql://localhost:5432/credit_db";
        final String username = "postgres";
        final String password = "postgres";
        final DataSource dataSource = DataSourceBuilder.create().driverClassName(driverClassName).url(jdbcUrl).username(username).password(password).build();
        return new JdbcTemplate(dataSource);
    }
}
