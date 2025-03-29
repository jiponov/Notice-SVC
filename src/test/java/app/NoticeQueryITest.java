package app;

import app.repository.NoticeRepository;
import app.service.NoticeService;
import app.web.dto.NoticeRequest;
import app.web.dto.NoticeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class NoticeQueryITest {

    // Същото като горното: прави реални записи в базата, после ги чете
    // Тества бизнес логика и реална интеграция със слоя за достъп до данни

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    // getNoticesByUserId(UUID userId)  -  Връща списък от notices за даден user.
    @Test
    void getNoticesByUserId_shouldReturnCorrectList() {
        UUID userId = UUID.randomUUID();

        for (int i = 0; i < 3; i++) {
            NoticeRequest req = NoticeRequest.builder()
                    .userId(userId)
                    .gameId(UUID.randomUUID())
                    .title("Game " + i)
                    .description("Desc " + i)
                    .username("user")
                    .gameUrl("http://game" + i + ".com")
                    .publisher("Pub")
                    .build();
            noticeService.createNotice(req);
        }

        List<NoticeResponse> notices = noticeService.getNoticesByUserId(userId);
        assertThat(notices).hasSize(3);
    }

    // Edge Case – Празен резултат от getNoticesByUserId (Вика метода с userId, който няма записи)
    @Test
    void getNoticesByUserId_shouldReturnEmptyList_whenNoNotices() {
        UUID userId = UUID.randomUUID();

        List<NoticeResponse> notices = noticeService.getNoticesByUserId(userId);
        assertThat(notices).isEmpty();
    }
}