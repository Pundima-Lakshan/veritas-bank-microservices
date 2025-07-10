package com.veritas.transaction.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.List;

/**
 * Represents a transaction.
 */
@Entity
@Table(name = "t_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionId;
    private String userId; // Auth0 user ID
    private String sourceAccountId; // For transfers
    private String destinationAccountId; // For transfers
    private String type; // deposit, withdrawal, transfer
    @OneToMany(cascade = CascadeType.ALL)
    private List<TransactionItems> transactionItemsList;
}
