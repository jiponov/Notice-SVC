package app.web.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
public class NoticeResponse {

    private UUID id;

    private UUID userId;

    private UUID gameId;

    private String title;

    private String description;

    private LocalDateTime timestamp;

    private String username;

    private String gameUrl;

    private String publisher;
}