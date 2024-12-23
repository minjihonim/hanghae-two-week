package org.prac.clean.api.controller;

import org.prac.clean.application.lecture.LectureFacade;
import org.prac.clean.domain.Lecture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
