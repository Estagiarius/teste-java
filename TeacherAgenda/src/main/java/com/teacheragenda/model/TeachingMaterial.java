package com.teacheragenda.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class TeachingMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob // For potentially longer descriptions
    private String description;

    @Column(nullable = false)
    private String filePath; // Path to the actual file on the user's system

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    private String category; // e.g., "Handout", "Presentation", "Video"

    // Constructors
    public TeachingMaterial() {
        this.uploadDate = LocalDateTime.now(); // Auto-set on creation
    }

    public TeachingMaterial(String name, String filePath, String description, String category) {
        this.name = name;
        this.filePath = filePath;
        this.description = description;
        this.category = category;
        this.uploadDate = LocalDateTime.now(); // Auto-set on creation
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    // Setter for uploadDate is typically not needed if auto-set
    // public void setUploadDate(LocalDateTime uploadDate) {
    // this.uploadDate = uploadDate;
    // }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeachingMaterial that = (TeachingMaterial) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TeachingMaterial{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", uploadDate=" + uploadDate +
                ", category='" + category + '\'' +
                '}';
    }
}
