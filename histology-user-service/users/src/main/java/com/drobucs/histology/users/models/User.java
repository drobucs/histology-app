package com.drobucs.histology.users.models;

import com.drobucs.web.apps.histology.users.Privilege;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.security.Key;
import java.util.Date;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"login", "email", "apiKeySha256"}),
    indexes = @Index(columnList = "creationTime"), name = "users")
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

    @Column(name = "privileges")
    private Privilege privileges;

    @Column(name = "enabled")
    private boolean enabled;

    @CreationTimestamp
    @Column(name = "creationTime")
    private Date creationTime;

    @Column(name = "encryptionKey")
    private Key encyptionKey;

    @Column(name = "recoveryCode")
    private String recoveryCode;

    @Column(name = "apiKeySha256", length = 1023)
    private String apiKeySha512;

    @Column(name = "privilegeExpires")
    private Date privilegeExpires;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "appVersionCode")
    private int appVersionCode;

    @Column(name = "appVersionName")
    private String appVersionName;
}
