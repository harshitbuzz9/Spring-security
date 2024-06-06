package com.bridge.herofincorp.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor@Builder
@Entity
@Table(name = "staff_data_role")
public class AssociateRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer staffRoleId;
    @NonNull
    private Integer staffId;
    @NonNull
    private String role;
    @NonNull
    private Timestamp created;
    @NonNull
    private Timestamp updated;
    //    @NonNull
    private String createdBy;
    //    @NonNull
    private String updatedBy;
}
