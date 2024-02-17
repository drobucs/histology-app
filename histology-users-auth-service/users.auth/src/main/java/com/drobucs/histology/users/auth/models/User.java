package com.drobucs.histology.users.auth.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "login"), name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "login")
    private String login;

    @Column(name = "email")
    private String email;

    @Column(name = "passwordSha")
    private String passwordSha;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "confirmationCode")
    private String confirmationCode;
}
