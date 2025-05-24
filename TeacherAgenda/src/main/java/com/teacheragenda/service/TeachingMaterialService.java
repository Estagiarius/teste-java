package com.teacheragenda.service;

import com.teacheragenda.model.TeachingMaterial;
import com.teacheragenda.repository.TeachingMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeachingMaterialService {

    private final TeachingMaterialRepository materialRepository;

    @Autowired
    public TeachingMaterialService(TeachingMaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Transactional
    public TeachingMaterial saveMaterial(TeachingMaterial material) {
        return materialRepository.save(material);
    }

    @Transactional
    public void deleteMaterial(Long materialId) {
        // This only deletes the metadata record. The actual file remains.
        // The UI should inform the user about this.
        if (!materialRepository.existsById(materialId)) {
            // Optionally throw an exception or log if material not found
            // throw new EntityNotFoundException("TeachingMaterial not found with ID: " + materialId);
            return;
        }
        materialRepository.deleteById(materialId);
    }

    @Transactional(readOnly = true)
    public List<TeachingMaterial> getAllMaterials() {
        return materialRepository.findAllByOrderByNameAsc(); // Default sort by name
    }

    @Transactional(readOnly = true)
    public Optional<TeachingMaterial> getMaterialById(Long materialId) {
        return materialRepository.findById(materialId);
    }

    @Transactional(readOnly = true)
    public List<TeachingMaterial> findByCategory(String category) {
        return materialRepository.findByCategoryOrderByNameAsc(category);
    }

    @Transactional(readOnly = true)
    public List<TeachingMaterial> findByNameContaining(String nameKeyword) {
        return materialRepository.findByNameContainingIgnoreCaseOrderByNameAsc(nameKeyword);
    }
}
