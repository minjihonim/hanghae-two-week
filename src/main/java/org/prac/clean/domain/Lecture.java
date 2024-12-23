package org.prac.clean.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@Builder
public class Lecture {
    
    // 특강 일련번호
    private long id;

    // 특강명
    private String name;

    // 강사
    private String speaker;

    // 참석자
    private int attendeeCount;

    // 신청 마감일
    private LocalDate deadLineDate;

    public Lecture(long id, String name, String speaker, int attendeeCount, LocalDate deadLineDate) {
        this.id = id;
        this.name = name;
        this.speaker = speaker;
        this.attendeeCount = attendeeCount;
        this.deadLineDate = deadLineDate;
    }

    public Lecture() {
    }
}
