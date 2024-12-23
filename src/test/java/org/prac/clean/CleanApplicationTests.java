package org.prac.clean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.prac.clean.application.lecture.repository.impl.LectureRepositoryImpl;
import org.prac.clean.application.lecture.service.LectureService;
import org.prac.clean.domain.Lecture;
import org.prac.clean.infrastructure.lecture.JPALectureRepository;
import org.prac.clean.infrastructure.lecture.entity.LectureEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DataJpaTest
class LectureTest {

    @Autowired
    private JPALectureRepository jpaLectureRepository;

    @InjectMocks
    private LectureService lectureService;

    @Mock
    private LectureRepositoryImpl lectureRepository;

    private String[] name = {"Math", "Science", "Music"};  // 강의명
    private String[] speaker = {"John", "Tom", "June"};  // 강의자

    private String[] date = {"20241231", "20241230", "20241229"};  // 신청 마감일자

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @BeforeEach
    void setup() {
        // 초기 데이터 생성
        for(int i=0; i<3; i++) {
            jpaLectureRepository.save(
                    LectureEntity.builder()
                            .name(name[i])
                            .speaker(speaker[i])
                            .attendeeCount(0)
                            .deadLineDate(LocalDate.parse(date[i], formatter))
                            .build()
            );
        }
    }

    @Test
    @DisplayName("특강 신청 목록을 조회한다.")
    public void 특강_신청_목록_조회_SERVICE_TEST() throws Exception {
        // given
        String registerDate = "20241223";

        when(lectureRepository.getLectureList(LocalDate.parse(registerDate, formatter)))
                .thenReturn(jpaLectureRepository.getLectureList(LocalDate.parse(registerDate, formatter)));
        // when
        List<Lecture> result =
                lectureService.getLectureList(registerDate);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting(Lecture::getName).contains("Math", "Music", "Science");
        assertThat(result).extracting(Lecture::getSpeaker).contains("John", "Tom", "June");

        verify(lectureRepository, times(1)).getLectureList(LocalDate.parse(registerDate, formatter));

    }

    @Test
    @DisplayName("특강 신청 목록을 조회한다.")
    public void 특강_신청_목록_조회_REPOSITORY_TEST() throws Exception {
        // given
        String registerDate = "20241223";

        // when
        List<LectureEntity> result =
                jpaLectureRepository.getLectureList(LocalDate.parse(registerDate, formatter));

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting(LectureEntity::getName).contains("Math", "Music", "Science");
        assertThat(result).extracting(LectureEntity::getSpeaker).contains("John", "Tom", "June");
    }

}
