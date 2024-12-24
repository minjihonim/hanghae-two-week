package org.prac.clean.application.lecture;

import org.prac.clean.common.vo.RequestVO;
import org.prac.clean.common.vo.ResponseVO;
import org.prac.clean.domain.lecture.entity.Lecture;
import org.prac.clean.domain.lecture.service.LectureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LectureFacade {

    private final LectureService lectureService;

    public LectureFacade(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    /**
     * 특강 신청 가능한 목록 조회
     * @param registerDate
     * @return
     */
    @Transactional(readOnly = true)
    public List<Lecture> getLectureList(String registerDate) {
        return lectureService.getLectureList(registerDate);
    }

    /**
     * 특강 신청
     */
    public ResponseVO applyToLecture(RequestVO req) {
        return lectureService.applyToLecture(req);
    }
}
