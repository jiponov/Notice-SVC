package app.web.mapper;

import app.model.*;
import app.web.dto.*;
import lombok.experimental.*;

import java.time.*;
import java.util.*;


@UtilityClass
public class DtoMapper {

    public Notice toEntity(NoticeRequest request) {

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .timestamp(LocalDateTime.now())
                .userId(request.getUserId())
                .gameId(request.getGameId())
                .username(request.getUsername())
                .gameUrl(request.getGameUrl())
                .publisher(request.getPublisher())
                .build();

        return notice;
    }


    public NoticeResponse toResponse(Notice notice) {

        NoticeResponse response = NoticeResponse.builder()
                .id(notice.getId())
                .userId(notice.getUserId())
                .gameId(notice.getGameId())
                .title(notice.getTitle())
                .description(notice.getDescription())
                .timestamp(notice.getTimestamp())
                .username(notice.getUsername())
                .gameUrl(notice.getGameUrl())
                .publisher(notice.getPublisher())
                .build();

        return response;
    }

}