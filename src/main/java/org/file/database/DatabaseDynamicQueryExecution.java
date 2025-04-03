package org.file.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class DatabaseDynamicQueryExecution {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseDynamicQueryExecution.class);
    private final DatabaseConnection databaseConnection;
    private DatabaseType databaseType;

    public DatabaseDynamicQueryExecution(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void selectDatabase(DatabaseType databaseType) {
        try {
            this.databaseType = databaseType;
        } catch (IllegalArgumentException e) {
            logger.error("Error selecting database: {}", databaseType, e);
            throw new RuntimeException(e);
        }
    }
    // Execute SELECT query and return results
    public <T> List<T> executeSelectQuery(String query, Function<ResultSet, T> mapper, Object... params) {

        List<T> results = new ArrayList<>();
        try (Connection connection = databaseConnection.selectConnection(databaseType); PreparedStatement statement = connection.prepareStatement(query)) {

            // Set dynamic parameters
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            // Execute query and map results
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(mapper.apply(resultSet)); // Apply custom mapper
                }
            }

        } catch (SQLException e) {
            logger.error("Error executing SELECT query: {}", query, e);
        }
        return results;
    }

    public ResultSet executeSelectQuery(String query, Object... params) {

        try (Connection connection = databaseConnection.selectConnection(databaseType); PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            return statement.executeQuery();


        } catch (SQLException e) {
            logger.error("Error executing SELECT query: {}", query, e);
            throw new RuntimeException(e);
        }
    }


    // Execute INSERT query with parameters
    public int executeInsertQuery(String query, Object... params) {
        try (Connection connection = databaseConnection.selectConnection(databaseType); PreparedStatement statement = connection.prepareStatement(query)) {

            setParameters(statement, params);
            return statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error executing INSERT query: {}", query, e);
            return -1;
        }
    }

    // Execute UPDATE query
    public int executeUpdateQuery(String query) {
        try (Connection connection = databaseConnection.selectConnection(databaseType);
             Statement statement = connection.createStatement()) {

            return statement.executeUpdate(query);
        } catch (SQLException e) {
            logger.error("Error executing UPDATE query: {}", query, e);
            return -1;
        }
    }

    // Execute batch insert
    public int[] executeBatchInsert(String query, List<Object[]> parametersList) {
        try (Connection connection = databaseConnection.selectConnection(databaseType);
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (Object[] params : parametersList) {
                setParameters(statement, params);
                statement.addBatch();
            }
            return statement.executeBatch();
        } catch (SQLException e) {
            logger.error("Error executing batch INSERT query: {}", query, e);
            return new int[0];
        }
    }

    // Helper method to set parameters for prepared statements
    private void setParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }
}
