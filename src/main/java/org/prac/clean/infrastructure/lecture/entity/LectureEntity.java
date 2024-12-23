package org.prac.clean.infrastructure.lecture.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tb_lecture")
@Getter @Setter
@Builder
public class LectureEntity {

    // 특강 일련번호 PK
    @Column(nullable = false, name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 특강명
    private String name;

    // 강사
    private String speaker;

    // 참석자
    @Column(nullable = false, name = "attendee_count")
    private int attendeeCount;

    // 신청 마감일
    @Column(name = "dead_line_date")
    private LocalDate deadLineDate;

    public LectureEntity(long id, String name, String speaker, int attendeeCount, LocalDate deadLineDate) {
        this.id = id;
        this.name = name;
        this.speaker = speaker;
        this.attendeeCount = attendeeCount;
        this.deadLineDate = deadLineDate;
    }

    public LectureEntity() {
    }
}
