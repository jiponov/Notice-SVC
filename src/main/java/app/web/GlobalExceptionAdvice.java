package app.web;

import app.web.dto.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.*;


@RestControllerAdvice
public class GlobalExceptionAdvice {

    // @ExceptionHandler(RuntimeException.class)
    // public String handleException(){
    //     return "error-error";
    // }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundEndpoint() {

        // ErrorResponse:  private int status,  private String message,  private LocalDateTime time;
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not supported application endpoint.");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}