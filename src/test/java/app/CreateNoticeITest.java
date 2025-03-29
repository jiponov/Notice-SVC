package app;

import app.model.Notice;
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
public class CreateNoticeITest {

    // Използва реална имплементация на NoticeService + NoticeRepository
    // Тества целия flow: DTO ➝ Service ➝ Repository ➝ DB

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    // дали NoticeService - createNotice() записва правилно в базата и връща очаквания отговор.
    @Test
    void createNotice_shouldSaveNoticeAndReturnResponse() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();

        NoticeRequest request = NoticeRequest.builder()
                .userId(userId)
                .gameId(gameId)
                .title("Интеграционен Тест")
                .description("Описание на играта")
                .username("tester_user")
                .gameUrl("https://example.com/game")
                .publisher("TestPublisher")
                .build();

        // When
        NoticeResponse response = noticeService.createNotice(request);

        // Then
        List<Notice> allNotices = noticeRepository.findAll();
        assertThat(allNotices).hasSize(1);

        Notice saved = allNotices.get(0);
        assertThat(saved.getTitle()).isEqualTo(request.getTitle());
        assertThat(saved.getDescription()).isEqualTo(request.getDescription());
        assertThat(saved.getUserId()).isEqualTo(request.getUserId());
        assertThat(saved.getGameId()).isEqualTo(request.getGameId());

        // Проверяваме и отговора
        assertThat(response.getId()).isNotNull();
        assertThat(response.getUsername()).isEqualTo(request.getUsername());
        assertThat(response.getTimestamp()).isNotNull();
    }
}