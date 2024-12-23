package org.prac.clean.application.lecture.service;

import org.modelmapper.ModelMapper;
import org.prac.clean.application.lecture.repository.LectureRepository;
import org.prac.clean.domain.Lecture;
import org.prac.clean.infrastructure.lecture.entity.LectureEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private static final ModelMapper modelMapper = new ModelMapper();

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public List<Lecture> getLectureList(String registerDate) {
        // 문자열 -> 날짜 데이터 변환
        LocalDate parseRegisterDate = getDate(registerDate);

        List<LectureEntity> result = lectureRepository.getLectureList(parseRegisterDate);

        // ModelMapper 로 자동 변환
        return result.stream()
                .map(entity -> modelMapper.map(entity, Lecture.class))
                .collect(Collectors.toList());
    }

    private static LocalDate getDate(String registerDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate parseDate = LocalDate.parse(registerDate, formatter);
        return parseDate;
    }
}
