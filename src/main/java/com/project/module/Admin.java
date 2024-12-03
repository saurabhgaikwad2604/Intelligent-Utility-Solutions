package com.project.module;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    protected String name;
    @Column(unique = true)
    protected String email;
    protected String password;
    protected String contactno;
    protected String role;
}
