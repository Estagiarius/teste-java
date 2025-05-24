package com.teacheragenda.repository;

import com.teacheragenda.model.QuickNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuickNoteRepository extends JpaRepository<QuickNote, Long> {

    /**
     * Retrieves all QuickNotes, ordering them by the last modified timestamp in descending order.
     * This means the most recently modified notes will appear first.
     *
     * @return A list of QuickNotes sorted by lastModifiedTimestamp descending.
     */
    List<QuickNote> findAllByOrderByLastModifiedTimestampDesc();

    // You can add other custom queries if needed, for example:
    // List<QuickNote> findByContentContainingIgnoreCase(String keyword);
}
