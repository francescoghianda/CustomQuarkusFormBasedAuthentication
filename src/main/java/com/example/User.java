package com.example;

//import io.quarkus.security.jpa.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {

    @Id
    private int id;

    private String username;

    private String password;

    private String role;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString(){
        return "[username="+username+"; password="+password+"; role="+role+"]";
    }
}
