package org.prac.clean.domain.user.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * 유저의 특강 신청 내역 확인을 위한 복합키 지정 클래스
 */
@Embeddable
public class RecordId implements Serializable {

    private long userId;
    private long lectureId;

    public RecordId(long userId, long lectureId) {
        this.userId = userId;
        this.lectureId = lectureId;
    }

    public RecordId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordId recordId = (RecordId) o;
        return userId == recordId.userId && lectureId == recordId.lectureId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, lectureId);
    }
}
