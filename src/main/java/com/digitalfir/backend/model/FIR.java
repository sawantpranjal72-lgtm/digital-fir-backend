package com.digitalfir.backend.model;
import com.digitalfir.backend.model.FirStatus;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fir")
public class FIR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String complainantName;

    @Column(length = 1000)
    private String complaintDetails;

    @Enumerated(EnumType.STRING)
    private FirStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long createdBy; // user id (citizen)

    // ================= JPA LIFECYCLE =================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = FirStatus.CREATED;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public String getComplainantName() {
        return complainantName;
    }

    public void setComplainantName(String complainantName) {
        this.complainantName = complainantName;
    }

    public String getComplaintDetails() {
        return complaintDetails;
    }

    public void setComplaintDetails(String complaintDetails) {
        this.complaintDetails = complaintDetails;
    }

    public FirStatus getStatus() {
        return status;
    }

    public void setStatus(FirStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}



