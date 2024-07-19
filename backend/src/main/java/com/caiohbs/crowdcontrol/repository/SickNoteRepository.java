package com.caiohbs.crowdcontrol.repository;

import com.caiohbs.crowdcontrol.model.SickNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SickNoteRepository extends JpaRepository<SickNote, Long> {

    List<SickNote> findByUserUserId(long userId);

}
