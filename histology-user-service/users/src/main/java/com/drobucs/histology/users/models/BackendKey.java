package com.drobucs.histology.users.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"nameKey", "apiKey"}),
        name = "backendKey")
@AllArgsConstructor
@NoArgsConstructor
public class BackendKey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @Column(name = "nameKey")
    private String nameKey;
    @Column(name = "apiKey", length = 1023)
    private String apiKey;
}
