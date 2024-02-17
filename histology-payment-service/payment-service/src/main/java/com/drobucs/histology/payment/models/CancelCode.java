package com.drobucs.histology.payment.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "cancelCode", uniqueConstraints = @UniqueConstraint(columnNames = {"cancelCodeName"}))
@AllArgsConstructor
@NoArgsConstructor
public class CancelCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @Column(name = "cancelCodeName")
    private String cancelCodeName;
    @Column(name = "code")
    private String code;
}
