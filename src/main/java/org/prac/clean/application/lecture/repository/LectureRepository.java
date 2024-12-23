package org.prac.clean.application.lecture.repository;

import org.prac.clean.domain.Lecture;
import org.prac.clean.infrastructure.lecture.entity.LectureEntity;

import java.time.LocalDate;
import java.util.List;

public interface LectureRepository {
    List<LectureEntity> getLectureList(LocalDate registerDate);
}
