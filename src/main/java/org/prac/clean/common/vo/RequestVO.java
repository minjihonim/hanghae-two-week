package org.prac.clean.common.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class RequestVO {

    // 특강 식별번호
    private long lectureId;
    // 강의 명
    private String lectureName;
    // 유저 ID
    private long userId;

    public RequestVO(long lectureId, String lectureName, long userId) {
        this.lectureId = lectureId;
        this.lectureName = lectureName;
        this.userId = userId;
    }
}
