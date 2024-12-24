package org.prac.clean.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@IdClass(RecordId.class)
@Table(name = "tb_user_lecture_record")
@Getter @Setter
public class UserLectureRecord {

    // 유저 식별번호
    @Id
    private long userId;

    // 강의 식별번호
    @Id
    private long lectureId;

    @Column(name = "reg_date")
    private LocalDate regDate;

    public UserLectureRecord() {
    }
}
