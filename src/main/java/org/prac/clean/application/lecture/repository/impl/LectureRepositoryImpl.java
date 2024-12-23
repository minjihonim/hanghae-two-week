package org.prac.clean.application.lecture.repository.impl;

import org.prac.clean.application.lecture.repository.LectureRepository;
import org.prac.clean.infrastructure.lecture.JPALectureRepository;
import org.prac.clean.infrastructure.lecture.entity.LectureEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class LectureRepositoryImpl implements LectureRepository {

    private final JPALectureRepository jpaLectureRepository;

    public LectureRepositoryImpl(JPALectureRepository jpaLectureRepository) {
        this.jpaLectureRepository = jpaLectureRepository;
    }

    @Override
    public List<LectureEntity> getLectureList(LocalDate registerDate) {
        return jpaLectureRepository.getLectureList(registerDate);
    }

}
