package com.teacheragenda.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class QuickNote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob // Use @Lob for potentially long text, maps to CLOB or TEXT
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationTimestamp;

    @Column(nullable = false)
    private LocalDateTime lastModifiedTimestamp;

    // Constructors
    public QuickNote() {
        LocalDateTime now = LocalDateTime.now();
        this.creationTimestamp = now;
        this.lastModifiedTimestamp = now;
    }

    public QuickNote(String content) {
        this(); // Call default constructor to set timestamps
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        // Generally not set manually after creation
        this.creationTimestamp = creationTimestamp;
    }

    public LocalDateTime getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(LocalDateTime lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    // Lifecycle Callbacks for timestamps (Alternative to setting in constructor/service)
    // @PrePersist
    // protected void onCreate() {
    //     LocalDateTime now = LocalDateTime.now();
    //     this.creationTimestamp = now;
    //     this.lastModifiedTimestamp = now;
    // }

    // @PreUpdate
    // protected void onUpdate() {
    //     this.lastModifiedTimestamp = LocalDateTime.now();
    // }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuickNote quickNote = (QuickNote) o;
        return Objects.equals(id, quickNote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "QuickNote{" +
                "id=" + id +
                ", contentSnippet='" + (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                ", creationTimestamp=" + creationTimestamp +
                ", lastModifiedTimestamp=" + lastModifiedTimestamp +
                '}';
    }
}
