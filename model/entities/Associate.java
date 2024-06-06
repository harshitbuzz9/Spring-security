package com.bridge.herofincorp.model.entities;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "staff_data")
public class Associate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "staff_data_id")
    private Integer staffId;
    @NonNull
    private String dealerCode;
    @Column(name="phone",unique=true,nullable = false)
    private String phone;
    @NonNull
    private String status;
    @NonNull
	private String name;
    @NonNull
    private String lastName;
    private String email;
    @NonNull
    private String product;
    @NonNull
    private Timestamp created;
    @NonNull
    private Timestamp updated;
    @NonNull
    private String createdBy;
    @NonNull
    private String updatedBy;
}
