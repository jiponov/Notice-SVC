package app.web.mapper;

import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;
import app.model.Notice;
import app.web.dto.NoticeRequest;
import app.web.dto.NoticeResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void givenNoticeRequest_whenToEntity_thenCorrectMapping() {
        // Given
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        NoticeRequest request = NoticeRequest.builder()
                .gameId(gameId)
                .userId(userId)
                .title("Test Game")
                .description("Test Desc")
                .username("lubaka")
                .gameUrl("http://game.url")
                .publisher("admin")
                .build();

        // When
        Notice result = DtoMapper.toEntity(request);

        // Then
        assertEquals("Test Game", result.getTitle());
        assertEquals("Test Desc", result.getDescription());
        assertEquals("lubaka", result.getUsername());
        assertEquals("admin", result.getPublisher());
        assertEquals(gameId, result.getGameId());
        assertEquals(userId, result.getUserId());
        assertNotNull(result.getTimestamp());
    }


    @Test
    void givenNotice_whenToResponse_thenCorrectMapping() {
        // Given
        UUID id = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        Notice notice = Notice.builder()
                .id(id)
                .gameId(gameId)
                .userId(userId)
                .title("Test Game")
                .description("Test Desc")
                .username("lubaka")
                .gameUrl("http://game.url")
                .publisher("admin")
                .timestamp(timestamp)
                .build();

        // When
        NoticeResponse response = DtoMapper.toResponse(notice);

        // Then
        assertEquals(id, response.getId());
        assertEquals("Test Game", response.getTitle());
        assertEquals("Test Desc", response.getDescription());
        assertEquals("lubaka", response.getUsername());
        assertEquals("admin", response.getPublisher());
        assertEquals(gameId, response.getGameId());
        assertEquals(userId, response.getUserId());
        assertEquals(timestamp, response.getTimestamp());
    }
}