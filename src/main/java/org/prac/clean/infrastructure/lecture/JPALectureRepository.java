package org.prac.clean.infrastructure.lecture;

import org.prac.clean.infrastructure.lecture.entity.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JPALectureRepository extends JpaRepository<LectureEntity, Long> {

    @Query(value = "SELECT lec " +
            "FROM LectureEntity lec where 1=1 AND lec.deadLineDate >= :registerDate")
    List<LectureEntity> getLectureList(@Param("registerDate") LocalDate registerDate);

}
