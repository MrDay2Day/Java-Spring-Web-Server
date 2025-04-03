package org.file.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseConnection {
    private final DataSource primaryDataSource;
    private final DataSource secondaryDataSource;

    public DatabaseConnection(
            @Qualifier("primaryDataSource") DataSource primaryDataSource,
            @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        this.primaryDataSource = primaryDataSource;
        this.secondaryDataSource = secondaryDataSource;
    }

//    public Connection getPrimaryConnection() throws SQLException {
//        return primaryDataSource.getConnection();
//    }
//
//    public Connection getSecondaryConnection() throws SQLException {
//        return secondaryDataSource.getConnection();
//    }

    public Connection selectConnection(DatabaseType databaseType) throws SQLException {

        return switch (databaseType) {
            case PRIMARY -> primaryDataSource.getConnection();
            case SECONDARY -> secondaryDataSource.getConnection();
            default -> throw new IllegalArgumentException("Invalid database type: " + databaseType);
        };

    }
}
