package app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import app.model.Notice;
import app.repository.NoticeRepository;
import app.service.NoticeService;
import app.web.dto.NoticeRequest;
import app.web.dto.NoticeResponse;
import app.web.mapper.DtoMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.*;

import java.time.LocalDateTime;
import java.util.*;


@ExtendWith(MockitoExtension.class)
public class NoticeServiceUTest {

    @Mock
    private NoticeRepository noticeRepository;


    @InjectMocks
    private NoticeService noticeService;


    // createNotice
    // Валиден NoticeRequest води до коректно повикване на save(...)
    // Коректен NoticeResponse се връща
    // Проверени са ключови стойности (title, username, id)
    @Test
    void givenValidRequest_whenCreateNotice_thenReturnCorrectResponse() {
        // Given
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        NoticeRequest request = NoticeRequest.builder()
                .gameId(gameId)
                .userId(userId)
                .title("Cool Game")
                .description("Description of game")
                .username("lubaka")
                .gameUrl("http://game.url")
                .publisher("admin")
                .build();

        Notice noticeBeforeSave = DtoMapper.toEntity(request);
        Notice savedNotice = Notice.builder()
                .id(UUID.randomUUID())
                .gameId(gameId)
                .userId(userId)
                .title("Cool Game")
                .description("Description of game")
                .username("lubaka")
                .gameUrl("http://game.url")
                .publisher("admin")
                .timestamp(LocalDateTime.now())
                .build();

        when(noticeRepository.save(any(Notice.class))).thenReturn(savedNotice);

        // When
        NoticeResponse response = noticeService.createNotice(request);

        // Then
        assertNotNull(response);
        assertEquals(savedNotice.getId(), response.getId());
        assertEquals("Cool Game", response.getTitle());
        assertEquals("lubaka", response.getUsername());

        ArgumentCaptor<Notice> captor = ArgumentCaptor.forClass(Notice.class);
        verify(noticeRepository).save(captor.capture());

        Notice captured = captor.getValue();
        assertEquals("Cool Game", captured.getTitle());
        assertEquals("lubaka", captured.getUsername());
    }


    // createNotice
    @Test
    void givenNullRequest_whenCreateNotice_thenThrowException() {
        assertThrows(NullPointerException.class, () -> {
            noticeService.createNotice(null);
        });
    }


    // createNotice
    @Test
    void givenIncompleteRequest_whenCreateNotice_thenStillSaves() {
        NoticeRequest request = NoticeRequest.builder()
                .gameId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .title("Only Title") // пропуснати някои полета
                .build();

        Notice dummyNotice = Notice.builder()
                .id(UUID.randomUUID())
                .title("Only Title")
                .gameId(request.getGameId())
                .userId(request.getUserId())
                .build();

        when(noticeRepository.save(any())).thenReturn(dummyNotice);

        NoticeResponse response = noticeService.createNotice(request);

        assertNotNull(response);
        assertEquals("Only Title", response.getTitle());
    }


    // generateNoticeFile()
    // Успешен случай – връща ByteArrayResource
    @Test
    void givenValidGameAndUserId_whenGenerateNoticeFile_thenReturnByteArrayResource() {
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Notice mockNotice = Notice.builder()
                .id(UUID.randomUUID())
                .gameId(gameId)
                .userId(userId)
                .title("Test Game")
                .description("Test Description")
                .username("lubo")
                .gameUrl("http://game.url")
                .publisher("admin")
                .timestamp(LocalDateTime.now())
                .build();

        when(noticeRepository.findByGameIdAndUserId(gameId, userId)).thenReturn(Optional.of(mockNotice));

        ByteArrayResource result = noticeService.generateNoticeFile(gameId, userId);

        assertNotNull(result);
        String content = new String(result.getByteArray());
        assertTrue(content.contains("LICENSE CERTIFICATE"));
        assertTrue(content.contains("Test Game"));
        assertTrue(content.contains("lubo"));
    }


    // generateNoticeFile()
    // Няма Notice – хвърля RuntimeException
    @Test
    void givenNoNoticeFound_whenGenerateNoticeFile_thenThrowException() {
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(noticeRepository.findByGameIdAndUserId(gameId, userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                noticeService.generateNoticeFile(gameId, userId));

        assertTrue(ex.getMessage().contains("No notice found"));
    }


    // generateNoticeFile()
    // Notice.userId различен от подадения → грешка
    @Test
    void givenDifferentUserIdInNotice_whenGenerateNoticeFile_thenThrowException() {
        UUID gameId = UUID.randomUUID();
        UUID actualUserId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();

        Notice notice = Notice.builder()
                .id(UUID.randomUUID())
                .gameId(gameId)
                .userId(anotherUserId) // Различен!
                .title("Test")
                .description("...")
                .username("wrong-user")
                .gameUrl("http://game.url")
                .publisher("admin")
                .timestamp(LocalDateTime.now())
                .build();

        when(noticeRepository.findByGameIdAndUserId(gameId, actualUserId)).thenReturn(Optional.of(notice));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                noticeService.generateNoticeFile(gameId, actualUserId));

        assertTrue(ex.getMessage().contains("User does not have permission"));
    }


    // getNoticesByUserId()
    // Връща списък с данни, Потребител има няколко Notice-а
    @Test
    void givenUserIdWithNotices_whenGetNoticesByUserId_thenReturnListOfResponses() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();

        Notice notice1 = Notice.builder()
                .id(UUID.randomUUID())
                .gameId(gameId)
                .userId(userId)
                .title("Game 1")
                .description("Description 1")
                .username("lubaka")
                .gameUrl("url1")
                .publisher("admin")
                .timestamp(LocalDateTime.now())
                .build();

        Notice notice2 = Notice.builder()
                .id(UUID.randomUUID())
                .gameId(gameId)
                .userId(userId)
                .title("Game 2")
                .description("Description 2")
                .username("lubaka")
                .gameUrl("url2")
                .publisher("admin")
                .timestamp(LocalDateTime.now())
                .build();

        when(noticeRepository.findByUserId(userId)).thenReturn(List.of(notice1, notice2));

        // When
        List<NoticeResponse> result = noticeService.getNoticesByUserId(userId);

        // Then
        assertEquals(2, result.size());
        assertEquals("Game 1", result.get(0).getTitle());
        assertEquals("Game 2", result.get(1).getTitle());
        verify(noticeRepository).findByUserId(userId);
    }


    // getNoticesByUserId()
    // Потребител няма никакви notices → връща празен списък
    @Test
    void givenUserIdWithNoNotices_whenGetNoticesByUserId_thenReturnEmptyList() {
        // Given
        UUID userId = UUID.randomUUID();
        when(noticeRepository.findByUserId(userId)).thenReturn(List.of());

        // When
        List<NoticeResponse> result = noticeService.getNoticesByUserId(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(noticeRepository).findByUserId(userId);
    }


}