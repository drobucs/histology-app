package com.drobucs.histology.payment.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "refundCode", uniqueConstraints = @UniqueConstraint(columnNames = {"refundCodeName"}))
@AllArgsConstructor
@NoArgsConstructor
public class RefundCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @Column(name = "refundCodeName")
    private String refundCodeName;
    @Column(name = "refundCode")
    private String refundCode;
}
