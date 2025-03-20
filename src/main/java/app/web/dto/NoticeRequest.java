package app.web.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
public class NoticeRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID gameId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String username;

    @NotBlank
    private String gameUrl;

    @NotBlank
    private String publisher;

}