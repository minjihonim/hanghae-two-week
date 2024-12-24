package org.prac.clean.api.controller;

import org.prac.clean.application.lecture.LectureFacade;
import org.prac.clean.common.vo.RequestVO;
import org.prac.clean.common.vo.ResponseVO;
import org.prac.clean.domain.lecture.entity.Lecture;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Interface Api
 */
@RestController
public class LectureController {

    private final LectureFacade lectureFacade;

    public LectureController(LectureFacade lectureFacade) {
        this.lectureFacade = lectureFacade;
    }

    /**
     * 특강 신청 가능한 목록 조회 api
     */
    @GetMapping("/api/lecture/list")
    public List<Lecture> getLectureList(@RequestParam("registerDate") String registerDate) {
        return lectureFacade.getLectureList(registerDate);
    }

    /**
     * 특강 신청 API
     */
    @PostMapping("/api/lecture/application")
    public ResponseVO applyToLecture(@RequestBody RequestVO req)  throws Exception {
        return lectureFacade.applyToLecture(req);
    }
}
