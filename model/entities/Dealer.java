package com.bridge.herofincorp.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "partner_new")
public class Dealer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long partnerId;
    private String dealerCode;
    private String email;
    private LocalDate birthday;
    private LocalDate marriageAnniversary;
    private String name;
    private String product;
    private String spouseName;
    private LocalDate spouseBirthday;
    private LocalDate onboardingDate;
    private Timestamp created;
    private Timestamp updated;
    private Integer noOfChildren;
}
