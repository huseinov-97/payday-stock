package com.example.paydaystock.model;

import com.example.paydaystock.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private BigDecimal targetPrice;

    private Integer amount;

    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('BOUGHT','SOLD', 'ACTIVE', 'WAITING_FOR_BUY', 'WAITING_FOR_SELL')")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;
}
