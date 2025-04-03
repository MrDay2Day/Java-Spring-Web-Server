package org.file.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${postgresql.conn.host}")
    private String postgresql_host;
    @Value("${postgresql.conn.database}")
    private String postgresql_database;
    @Value("${postgresql.conn.username}")
    private String postgresql_username;
    @Value("${postgresql.conn.password}")
    private String postgresql_password;


    @Value("${mysql.conn.host}")
    private String mysql_host;
    @Value("${mysql.conn.database}")
    private String mysql_database;
    @Value("${mysql.conn.username}")
    private String mysql_username;
    @Value("${mysql.conn.password}")
    private String mysql_password;

    @Bean
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://"+postgresql_host+"/"+postgresql_database)
                .username(postgresql_username)
                .password(postgresql_password)
                .build();

    }

    @Bean
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:mysql://"+mysql_database+"/"+mysql_host)
                .username(mysql_username)
                .password(mysql_password)
                .build();
    }

    @Bean
    public Map<String, DataSource> dataSources(
            @Qualifier("primaryDataSource") DataSource primaryDataSource,
            @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        Map<String, DataSource> dataSources = new HashMap<>();
        dataSources.put("primary", primaryDataSource);
        dataSources.put("secondary", secondaryDataSource);
        return dataSources;
    }


}