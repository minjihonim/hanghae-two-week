package org.prac.clean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prac.clean.application.lecture.LectureFacade;
import org.prac.clean.common.code.ErrorCode;
import org.prac.clean.common.vo.RequestVO;
import org.prac.clean.common.vo.ResponseVO;
import org.prac.clean.domain.lecture.entity.Lecture;
import org.prac.clean.domain.lecture.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class LectureH2DBTest {

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LectureFacade lectureFacade;

    private String[] name = {"Math", "Science", "Music"};  // 강의명
    private String[] speaker = {"John", "Tom", "June"};  // 강의자

    private String[] date = {"20241231", "20241230", "20241229"};  // 신청 마감일자

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @BeforeEach
    void setup() {
        // 초기 데이터 생성
        for(int i=0; i<3; i++) {
            lectureRepository.save(
                    Lecture.builder()
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
    public void 특강_신청_목록_조회_REPOSITORY_TEST() throws Exception {
        // given
        String registerDate = "20241223";

        // when
        List<Lecture> result =
                lectureFacade.getLectureList(registerDate);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting(Lecture::getName).contains("Math", "Music", "Science");
        assertThat(result).extracting(Lecture::getSpeaker).contains("John", "Tom", "June");
    }

    @Test
    @DisplayName("특강 신청 단일 테스트")
    public void 특강_신청_성공_테스트_동시성_고려_X() throws Exception {
        // given
        long lectureId = 1;
        long userId = 1;
        RequestVO vo = new RequestVO(lectureId, "Math", userId);

        // when
        ResponseVO result = lectureFacade.applyToLecture(vo); // 특강신청 Facade

        Optional<Lecture> lecture = lectureRepository.findById(lectureId);

        // then
        assertEquals(ErrorCode.SUCCES.getCode(), result.getCode());
        assertTrue(lecture.isPresent());
        assertEquals(1, lecture.get().getAttendeeCount());
    }

    @Test
    @DisplayName("특강 신청 동시 신청 테스트")
    public void 특강_신청_동시_신청_테스트_동시성_제어_성공_비관적_잠금() throws Exception {
        // given
        int threadCount = 31;   // 생성 쓰레드 개수
        long lectureId = 1;  // 강의 식별번호

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);    // 멀티쓰레드 생성

        // when
        // 동시 신청 진행
        for (int i =0; i<threadCount; i++) {
            int finalI = i; // userId
            executorService.submit(() -> {
                try {
                    RequestVO vo = new RequestVO(lectureId, "Math", finalI +1);
                    lectureFacade.applyToLecture(vo); // 특강신청 Facade
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        boolean completed = executorService.awaitTermination(20, TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Test timed out");
        }

        Optional<Lecture> lecture = lectureRepository.findById(lectureId);

        // then
        assertTrue(lecture.isPresent());
        assertEquals(30, lecture.get().getAttendeeCount());

    }

    @Test
    @DisplayName("특강 신청 동시 신청 테스트")
    public void 특강_신청_동시_신청_테스트_동시성_제어_실패() throws Exception {
        // given
        int threadCount = 31;   // 생성 쓰레드 개수
        long lectureId = 1;  // 강의 식별번호

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);    // 멀티쓰레드 생성

        // when
        // 동시 신청 진행
        for (int i =0; i<threadCount; i++) {
            int finalI = i; // userId
            executorService.submit(() -> {
                try {
                    RequestVO vo = new RequestVO(lectureId, "Math", finalI +1);
                    Optional<Lecture> lectureInfo = lectureRepository.findById(vo.getLectureId());
                    if(lectureInfo.isPresent()) {
                        lectureInfo.get().setAttendeeCount(lectureInfo.get().getAttendeeCount() + 1);
                        lectureRepository.save(lectureInfo.get());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        boolean completed = executorService.awaitTermination(20, TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Test timed out");
        }

        Optional<Lecture> lecture = lectureRepository.findById(lectureId);

        // then
        assertTrue(lecture.isPresent());
        assertNotEquals(30, lecture.get().getAttendeeCount());

    }

    @Test
    @DisplayName("특강 신청 및 조회 동시 테스트")
    public void 특강_신청_및_조회_동시_테스트_비관적_락_적용_상태() throws Exception {
        // given
        int threadCount = 20;   // 생성 쓰레드 개수
        long lectureId = 1;  // 강의 식별번호
        long waitTime = 0;  // 읽기 작업 지연 시간 측정 변수
        
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);    // 멀티쓰레드 생성
    
        // 1. 특강 신청 작업(쓰기 잠금)을 위한 쓰레드
        Callable<Void> writeTask = () -> {
            try {
                long userId = new AtomicLong(0).getAndIncrement();
                RequestVO vo = new RequestVO(lectureId, "Math", userId);
                lectureFacade.applyToLecture(vo); // 특강신청 Facade
                System.out.println("update 완료시간 = " + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };

        // 2. 조회 작업(읽기 잠금)을 위한 쓰레드
        Callable<Long> readTask = () -> {
            long startTime = System.currentTimeMillis();
            String registerDate = "20241223";
            List<Lecture> result =
                    lectureFacade.getLectureList(registerDate);
            result.stream().filter(v -> v.getId()==1).toList()
                    .forEach(lecture -> System.out.println("참석자 수 =" + lecture.getAttendeeCount()
                    + " / 소요시간: " + System.currentTimeMillis()));
            long endTime = System.currentTimeMillis();
            return endTime - startTime; // 대기 시간
        };
        
        // 3. 쓰레드 풀을 사용하여 동시에 작업 실행
        List<Future<Void>> writeFutures = new ArrayList<>();
        List<Future<Long>> readFutures = new ArrayList<>();

        for(int i =0; i< threadCount; i++) {
            // 쓰기 작업 제출
            writeFutures.add(executorService.submit(writeTask));
            // 읽기 작업 제출
            readFutures.add(executorService.submit(readTask));
        }

        // when
        // 4. 두 작업이 완료될 때까지 기다림
        for (Future<Void> write : writeFutures) {
            write.get();
        }

        for(Future<Long> read : readFutures) {
            waitTime += read.get();
        }

        executorService.shutdown();
        boolean completed = executorService.awaitTermination(20, TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Test timed out");
        }
        // then
        // 5. 대기 시간이 일정 시간 이상이면 대기 발생을 확인한 것으로 간주
        System.out.println("Read Task waitTime = " + waitTime + " milliseconds");

        assertTrue(waitTime > 1000, "Read task should wait due to pessimistic lock");

        /** JPA 비관적락 (@Lock(LockModeType.PESSIMISTIC_WRITE) 적용 후
         * 강의 목록을 조회할 때에도 특강 ROW 에 쓰기 작업이 완료될 때 까지 대기한 후 데이터를 불러오는 것을 확인.
         * 강의 목록 조회 시 참여 인원 수의 데이터 무결성을 보장할 수 있음 확인
         * PESSIMISTIC_WRITE 락을 사용할 경우, 동시에 실행되는 쓰레드의 수(n)에 따라
         * 조회 대기 시간이 O(n)으로 증가
         */

    }
}
