package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "call_logs")
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    private Long userId;

    @Column(nullable = false)
    private LocalDate callDate;

    private String fileName;

    @Column(nullable = false)
    private String filePath;

    private LocalDate createdAt;

    @Column(name = "status_kakunin", nullable = false)
    private int statusKakunin;  // 0=未確認, 1=確認済（確認者用）

    @Column(name = "status_kanri", nullable = false)
    private int statusKanri;    // 0=未確認, 1=確認済（管理者用）

    private LocalDate updatedAt;

    // -------------------- Getter/Setter --------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getCallDate() { return callDate; }
    public void setCallDate(LocalDate callDate) { this.callDate = callDate; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public int getStatusKakunin() { return statusKakunin; }
    public void setStatusKakunin(int statusKakunin) { this.statusKakunin = statusKakunin; }

    public int getStatusKanri() { return statusKanri; }
    public void setStatusKanri(int statusKanri) { this.statusKanri = statusKanri; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

//    @Override
//    public String toString() {
//        return "CallLog{id=" + id + ", userId=" + userId + ", callDate=" + callDate
//               + ", fileName='" + fileName + "', filePath='" + filePath + "'"
//               + ", createdAt=" + createdAt + ", status=" + status + ", updatedAt=" + updatedAt + "}";
//    }
    @Override
    public String toString() {
        return "CallLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", callDate=" + callDate +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", createdAt=" + createdAt +
                ", statusKakunin=" + statusKakunin +
                ", statusKanri=" + statusKanri +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
