package app.web;

import app.service.*;
import app.web.dto.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {

    private final NoticeService noticeService;


    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }


    // TEST
    @GetMapping("/test")
    public ResponseEntity<String> getHelloWorld(@RequestParam(name = "name") String name) {
        // throw new RuntimeException();
        return ResponseEntity.ok("Hello, " + name + " user!");
    }


    // /api/v1/notices/download/{gameId}/{userId}
    @GetMapping("/download/{gameId}/{userId}")
    public ResponseEntity<Resource> downloadNotice(@RequestParam(name = "gameId") UUID gameId, @RequestParam(name = "userId") UUID userId) {

        Resource file = noticeService.generateNoticeFile(gameId, userId);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<Resource> resource = ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=game-purchase.txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(file);

        return resource;
    }


    // /api/v1/notices
    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(@RequestBody NoticeRequest request) {

        NoticeResponse response = noticeService.createNotice(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // /api/v1/notices/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<List<NoticeResponse>> getNotices(@RequestParam(name = "userId") UUID userId) {

        List<NoticeResponse> notices = noticeService.getNoticesByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notices);
    }
}