package com.teacheragenda.service;

import com.teacheragenda.model.QuickNote;
import com.teacheragenda.repository.QuickNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuickNoteService {

    private final QuickNoteRepository noteRepository;

    @Autowired
    public QuickNoteService(QuickNoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional
    public QuickNote saveNote(QuickNote note) {
        LocalDateTime now = LocalDateTime.now();
        if (note.getId() == null) { // New note
            note.setCreationTimestamp(now);
        }
        note.setLastModifiedTimestamp(now);
        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long noteId) {
        if (!noteRepository.existsById(noteId)) {
            // Optionally throw an exception or log if note not found
            // throw new EntityNotFoundException("QuickNote not found with ID: " + noteId);
            return;
        }
        noteRepository.deleteById(noteId);
    }

    @Transactional(readOnly = true)
    public List<QuickNote> getAllNotesSorted() {
        return noteRepository.findAllByOrderByLastModifiedTimestampDesc();
    }

    @Transactional(readOnly = true)
    public Optional<QuickNote> getNoteById(Long noteId) {
        return noteRepository.findById(noteId);
    }
}
