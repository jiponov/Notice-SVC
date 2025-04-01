package app.service;

import app.model.*;
import app.repository.*;
import app.web.dto.*;
import app.web.mapper.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;

import java.nio.charset.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;


@Slf4j
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;


    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }


    public NoticeResponse createNotice(NoticeRequest request) {

        Notice notice = DtoMapper.toEntity(request);

        notice = noticeRepository.save(notice);

        NoticeResponse response = DtoMapper.toResponse(notice);

        return response;
    }


    public ByteArrayResource generateNoticeFile(UUID gameId, UUID userId) {

        //  всеки USER ще получава само своя собствен NOTICE
        Notice notice = noticeRepository
                .findByGameIdAndUserId(gameId, userId)
                .orElseThrow(() -> new RuntimeException("No notice found for gameId: " + gameId + " and userId: " + userId));

        if (!notice.getUserId().equals(userId)) {
            throw new RuntimeException("User does not have permission to download this notice.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = notice.getTimestamp().format(formatter);


        String content = "LICENSE CERTIFICATE : Jubbisoft Games Store\n"
                + "==============================================\n\n"
                + "This document certifies the purchase of:\n"
                + "ONE REGULAR LICENSE\n"
                + "as defined in the standard terms and conditions on Jubbisoft.\n\n"
                + "Licensee Notice ID:\n" + notice.getId() + "\n\n"
                + "Licensee Username:\n" + notice.getUsername() + "\n\n"
                + "Licensor's Author Username:\n" + notice.getPublisher() + "\n\n"
                + "Notice Item:\n" + notice.getTitle() + "\n\n"
                + "Purchase Content:\n" + notice.getDescription() + "\n\n"
                + "URL Item:\n" + notice.getGameUrl() + "\n\n"
                + "Purchase Notice Date:\n" + formattedTimestamp + "\n\n"
                + "For any queries related to this document or license please contact Help Team via https://jubbisoft.com\n\n"
                + "Jubbisoft Ltd.\n"
                + "Jubbisoft Tower, 1987 Silicon Heights, Hollywood Blvd, CA 90210, USA\n\n\n"
                + "==============================================\n"
                + "**DISCLAIMER:** This document is generated for educational purposes only.\n"
                + "It does not represent a real purchase, license, or agreement.\n"
                + "Jubbisoft Games Store is a fictional project created for learning and development.\n"
                + "No real transactions or legal obligations are associated with this document.\n"
                + "==============================================";


        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));

        return resource;
    }


    public List<NoticeResponse> getNoticesByUserId(UUID userId) {

        List<NoticeResponse> responsesList = noticeRepository
                .findByUserId(userId)
                .stream()
                .map(DtoMapper::toResponse)
                .collect(Collectors.toList());

        return responsesList;
    }
}