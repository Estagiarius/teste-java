package com.teacheragenda.repository;

import com.teacheragenda.model.TeachingMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeachingMaterialRepository extends JpaRepository<TeachingMaterial, Long> {

    // Find all materials, ordered by name alphabetically
    List<TeachingMaterial> findAllByOrderByNameAsc();

    // Find all materials for a given category, ordered by name
    List<TeachingMaterial> findByCategoryOrderByNameAsc(String category);

    // Find materials by name containing a keyword (case-insensitive)
    List<TeachingMaterial> findByNameContainingIgnoreCaseOrderByNameAsc(String nameKeyword);

    // You can add other specific queries if needed
}
