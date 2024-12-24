package org.prac.clean.domain.lecture.repository;

import jakarta.persistence.LockModeType;
import org.prac.clean.domain.lecture.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    @Query(value = "SELECT lec " +
            "FROM Lecture lec where 1=1 AND lec.deadLineDate >= :registerDate")
    List<Lecture> getLectureList(@Param("registerDate") LocalDate registerDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lec FROM Lecture lec where 1=1 AND lec.id = :lectureId")
    Optional<Lecture> findByIdForUpdate(long lectureId);
}
