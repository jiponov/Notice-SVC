package app;

import app.model.Notice;
import app.repository.NoticeRepository;
import app.service.NoticeService;
import app.web.dto.NoticeRequest;
import app.web.dto.NoticeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class GenerateNoticeFileITest {

    // Извиква generateNoticeFile(), който взима реални данни от БД и генерира съдържание
    // Проверява дали текстовият файл е валиден

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    // създава запис, после вика generateNoticeFile и проверява съдържанието на ресурса.
    // Happy path
    @Test
    void generateNoticeFile_shouldReturnValidTextFile() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();

        NoticeRequest request = NoticeRequest.builder()
                .userId(userId)
                .gameId(gameId)
                .title("Игра Тест")
                .description("Описание на играта")
                .username("file_user")
                .gameUrl("https://example.com/filegame")
                .publisher("TestPublisher")
                .build();

        // Създаваме notice
        NoticeResponse response = noticeService.createNotice(request);

        // When
        ByteArrayResource file = noticeService.generateNoticeFile(gameId, userId);

        // Then
        assertThat(file).isNotNull();
        String content = new String(file.getByteArray(), StandardCharsets.UTF_8);

        assertThat(content).contains("LICENSE CERTIFICATE");
        assertThat(content).contains("file_user");
        assertThat(content).contains("Игра Тест");
    }

    // симулира липсващ запис и очаква изключение RuntimeException
    @Test
    void generateNoticeFile_shouldThrowException_whenNotFound() {
        // When & Then
        UUID fakeGameId = UUID.randomUUID();
        UUID fakeUserId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () -> {
            noticeService.generateNoticeFile(fakeGameId, fakeUserId);
        });
    }

    // Ако gameId съществува, но userId не съвпада → трябва да хвърли грешка.
    @Test
    void generateNoticeFile_shouldThrow_whenUserIdDoesNotMatch() {
        UUID correctUserId = UUID.randomUUID();
        UUID wrongUserId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();

        NoticeRequest request = NoticeRequest.builder()
                .userId(correctUserId)
                .gameId(gameId)
                .title("Test")
                .description("Desc")
                .username("correct")
                .gameUrl("url")
                .publisher("pub")
                .build();

        noticeService.createNotice(request);

        assertThrows(RuntimeException.class, () -> {
            noticeService.generateNoticeFile(gameId, wrongUserId);
        });
    }
}