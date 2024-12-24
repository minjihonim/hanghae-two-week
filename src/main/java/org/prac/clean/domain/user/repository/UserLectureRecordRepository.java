package org.prac.clean.domain.user.repository;

import org.prac.clean.domain.user.entity.RecordId;
import org.prac.clean.domain.user.entity.UserLectureRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLectureRecordRepository extends JpaRepository<UserLectureRecord, RecordId> {
}
