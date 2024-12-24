package org.prac.clean.domain.lecture.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.prac.clean.common.code.ErrorCode;
import org.prac.clean.common.vo.RequestVO;
import org.prac.clean.common.vo.ResponseVO;
import org.prac.clean.domain.lecture.entity.Lecture;
import org.prac.clean.domain.lecture.repository.LectureRepository;
import org.prac.clean.domain.user.entity.RecordId;
import org.prac.clean.domain.user.entity.UserLectureRecord;
import org.prac.clean.domain.user.repository.UserLectureRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureService {

    private final LectureRepository lectureRepository;
    private final UserLectureRecordRepository userLectureRecordRepository;
    private static final ModelMapper modelMapper = new ModelMapper();

    @Transactional(readOnly = true)
    public List<Lecture> getLectureList(String registerDate) {
        // 문자열 -> 날짜 데이터 변환
        LocalDate parseRegisterDate = getDate(registerDate);

        List<Lecture> result = lectureRepository.getLectureList(parseRegisterDate);

        // ModelMapper 로 자동 변환
        return result.stream()
                .map(entity -> modelMapper.map(entity, Lecture.class))
                .collect(Collectors.toList());
    }

    /**
     * String -> LocalDate 변환
     * @param registerDate
     * @return
     */
    private static LocalDate getDate(String registerDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate parseDate = LocalDate.parse(registerDate, formatter);
        return parseDate;
    }
    @Transactional
    public ResponseVO applyToLecture(RequestVO req) {
        // 응답 객체
        ResponseVO result = new ResponseVO();
        // 특강 인원 수 체크, 30 미만 이여야 신청 가능
        Optional<Lecture> lectureInfo = lectureRepository.findByIdForUpdate(req.getLectureId());

        if(lectureInfo.isPresent()) {
            if(lectureInfo.get().getAttendeeCount() == 30) {
                result.setCode(ErrorCode.FAIL.getCode());
                result.setMessage(ErrorCode.FAIL.getMessage());
                return result;
            }
            // 강의 참여 수 증가
            lectureInfo.get().setAttendeeCount(lectureInfo.get().getAttendeeCount() + 1);

            lectureRepository.save(lectureInfo.get());

            // 사용자 강의 신청 내역 등록
            UserLectureRecord userLectureRecord = new UserLectureRecord();
            userLectureRecord.setUserId(req.getUserId());
            userLectureRecord.setLectureId(req.getLectureId());
            userLectureRecordRepository.save(userLectureRecord);

            result.setCode(ErrorCode.SUCCES.getCode());
            result.setMessage(ErrorCode.SUCCES.getMessage());
        }
        return result;
    }
}
