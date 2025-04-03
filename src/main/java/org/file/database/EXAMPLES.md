# DatabaseDynamicQueryExecution Examples

This document provides examples for using the `DatabaseDynamicQueryExecution` class. This class allows for dynamic execution of SQL queries against a database, supporting SELECT, INSERT, UPDATE, and batch INSERT operations.

## Setup

First, ensure you have the `DatabaseDynamicQueryExecution` and `DatabaseConnection` classes correctly configured and injected into your Spring application. You also need to have a `DatabaseType` enum defined.

```java
package org.file.database;

public enum DatabaseType {
    MYSQL,
    POSTGRESQL,
    ORACLE,
    SQLSERVER
}
```

## Selecting a Database
Before executing any queries, select the database type you want to use.
```java
import org.file.database.DatabaseDynamicQueryExecution;
import org.file.database.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyDatabaseService {

    @Autowired
    private DatabaseDynamicQueryExecution databaseDynamicQueryExecution;

    public void setDatabaseType(DatabaseType databaseType) {
        databaseDynamicQueryExecution.selectDatabase(databaseType);
    }
}
```

## Executing a SELECT Query with Mapping
This example demonstrates how to execute a `SELECT` query and map the results to a list of objects using a `Function`.

```java
import org.file.database.DatabaseDynamicQueryExecution;
import org.file.database.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class MyDatabaseService {

    @Autowired
    private DatabaseDynamicQueryExecution databaseDynamicQueryExecution;

    public List<User> getUsers(int age) {
        databaseDynamicQueryExecution.selectDatabase(DatabaseType.MYSQL); // Example database type
        String query = "SELECT id, name, age FROM users WHERE age > ?";
        return databaseDynamicQueryExecution.executeQuery(query, this::mapUser, age);
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setAge(resultSet.getInt("age"));
        return user;
    }

    static class User {
        private int id;
        private String name;
        private int age;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }
}
```

## Executing a SELECT Query Returning ResultSet
This example demonstrates how to execute a `SELECT` query and return a `ResultSet`.

```java
import org.file.database.DatabaseDynamicQueryExecution;
import org.file.database.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class MyDatabaseService {

    @Autowired
    private DatabaseDynamicQueryExecution databaseDynamicQueryExecution;

    public ResultSet getRawUsers(int age) {
        databaseDynamicQueryExecution.selectDatabase(DatabaseType.POSTGRESQL);
        String query = "SELECT id, name, age FROM users WHERE age > ?";
        return databaseDynamicQueryExecution.executeQuery(query, age);
    }
}
```

## Executing an INSERT Query
This example shows how to execute an `INSERT` query with parameters.

```java
import org.file.database.DatabaseDynamicQueryExecution;
import org.file.database.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyDatabaseService {

    @Autowired
    private DatabaseDynamicQueryExecution databaseDynamicQueryExecution;

    public int addUser(String name, int age) {
        databaseDynamicQueryExecution.selectDatabase(DatabaseType.SQLSERVER);
        String query = "INSERT INTO users (name, age) VALUES (?, ?)";
        return databaseDynamicQueryExecution.executeInsertQuery(query, name, age);
    }
}
```

## Executing an UPDATE Query
This example shows how to execute an UPDATE query.

```Java

import org.file.database.DatabaseDynamicQueryExecution;
import org.file.database.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyDatabaseService {

    @Autowired
    private DatabaseDynamicQueryExecution databaseDynamicQueryExecution;

    public int updateUserAge(int age, int id) {
        databaseDynamicQueryExecution.selectDatabase(DatabaseType.ORACLE);
        String query = "UPDATE users SET age = " + age + " WHERE id = " + id;
        return databaseDynamicQueryExecution.executeUpdateQuery(query);
    }
}
```

## Executing a Batch INSERT Query
This example shows how to execute a batch INSERT query.

```Java
import org.file.database.DatabaseDynamicQueryExecution;
import org.file.database.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MyDatabaseService {

    @Autowired
    private DatabaseDynamicQueryExecution databaseDynamicQueryExecution;

    public int[] addUsersBatch(List<Object[]> users) {
        databaseDynamicQueryExecution.selectDatabase(DatabaseType.MYSQL);
        String query = "INSERT INTO users (name, age) VALUES (?, ?)";
        return databaseDynamicQueryExecution.executeBatchInsert(query, users);
    }

    public void addMultipleUsers() {
        List<Object[]> users = Arrays.asList(
                new Object[]{"Alice", 30},
                new Object[]{"Bob", 25},
                new Object[]{"Charlie", 35}
        );
        int[] results = addUsersBatch(users);

        for (int result : results) {
            System.out.println("Batch result: " + result);
        }
    }
}
```

These examples should provide a good starting point for using the `DatabaseDynamicQueryExecution` class. Adjust the queries and data types to match your specific database schema and requirements.