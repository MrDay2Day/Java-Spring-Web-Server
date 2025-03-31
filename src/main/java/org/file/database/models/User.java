package org.file.database.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class User {
    private int id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;

    // Constructors
    public User() {}

    public User(int id, String email, String password,
                String firstName, String lastName,
                LocalDate dob) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob=" + dob +
                '}';
    }

    // Map ResultSet to User object
    public static User mapUser(ResultSet rs) {
        try {
            return new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getObject("dob", LocalDate.class)
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping user", e);
        }
    }

    public UserPublicInfo toPublicInfo(String webSocketToken) {
        return new UserPublicInfo(
                this.id,
                this.email,
                this.firstName,
                this.lastName,
                webSocketToken
        );
    }
}

