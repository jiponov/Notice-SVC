package app.web;

import app.service.*;
import app.web.dto.*;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.*;
import org.springframework.test.web.servlet.*;

import java.time.*;
import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@WebMvcTest(NoticeController.class)
public class NoticeControllerApiTest {

    @MockitoBean
    private NoticeService noticeService;

    @Autowired
    private MockMvc mockMvc;


    // GET /api/v1/notices/test?name=...
    @Test
    void getHelloWorld_withNameParam_shouldReturnGreeting() throws Exception {
        mockMvc.perform(get("/api/v1/notices/test")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, John user!"));
    }


    // GET /api/v1/notices/download/{gameId}/{userId}
    @Test
    void downloadNotice_withValidIds_shouldReturnFile() throws Exception {
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String fileContent = "Purchase confirmation.";

        ByteArrayResource resource = new ByteArrayResource(fileContent.getBytes());

        when(noticeService.generateNoticeFile(gameId, userId)).thenReturn(resource);

        mockMvc.perform(get("/api/v1/notices/download/{gameId}/{userId}", gameId, userId)
                        .param("gameId", gameId.toString())  // <-- важно, защото @RequestParam, а не @PathVariable
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=game-purchase.txt"))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string(fileContent));
    }


    @Test
    void downloadNotice_fileNotFound_shouldReturn404() throws Exception {
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(noticeService.generateNoticeFile(gameId, userId)).thenReturn(null);

        mockMvc.perform(get("/api/v1/notices/download/{gameId}/{userId}", gameId, userId)
                        .param("gameId", gameId.toString())
                        .param("userId", userId.toString()))
                .andExpect(status().isNotFound());
    }


    @Test
    void createNotice_withValidRequest_shouldReturnCreatedNotice() throws Exception {
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID noticeId = UUID.randomUUID();

        NoticeRequest request = NoticeRequest.builder()
                .gameId(gameId)
                .userId(userId)
                .title("Elden Ring")
                .description("Purchase completed successfully.")
                .username("john_doe")
                .gameUrl("http://example.com/elden-ring")
                .publisher("FromSoftware")
                .build();

        NoticeResponse response = NoticeResponse.builder()
                .id(noticeId)
                .gameId(gameId)
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .timestamp(LocalDateTime.now())
                .username(request.getUsername())
                .gameUrl(request.getGameUrl())
                .publisher(request.getPublisher())
                .build();

        when(noticeService.createNotice(any(NoticeRequest.class))).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/v1/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(noticeId.toString()))
                .andExpect(jsonPath("$.gameId").value(gameId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.username").value(request.getUsername()))
                .andExpect(jsonPath("$.gameUrl").value(request.getGameUrl()))
                .andExpect(jsonPath("$.publisher").value(request.getPublisher()));
    }


    @Test
    void getNotices_withValidUserId_shouldReturnListOfNotices() throws Exception {
        UUID userId = UUID.randomUUID();

        List<NoticeResponse> notices = List.of(
                NoticeResponse.builder()
                        .id(UUID.randomUUID())
                        .gameId(UUID.randomUUID())
                        .userId(userId)
                        .title("Notice 1")
                        .description("Test description 1")
                        .timestamp(LocalDateTime.now())
                        .username("john_doe")
                        .gameUrl("http://example.com/game1")
                        .publisher("Publisher 1")
                        .build(),

                NoticeResponse.builder()
                        .id(UUID.randomUUID())
                        .gameId(UUID.randomUUID())
                        .userId(userId)
                        .title("Notice 2")
                        .description("Test description 2")
                        .timestamp(LocalDateTime.now())
                        .username("john_doe")
                        .gameUrl("http://example.com/game2")
                        .publisher("Publisher 2")
                        .build()
        );

        when(noticeService.getNoticesByUserId(userId)).thenReturn(notices);

        mockMvc.perform(get("/api/v1/notices/{userId}", userId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(notices.size()))
                .andExpect(jsonPath("$[0].title").value("Notice 1"))
                .andExpect(jsonPath("$[1].title").value("Notice 2"));
    }


}