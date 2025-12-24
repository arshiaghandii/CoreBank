package ir.tejaratBank.core.CoreBank.data.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private int status;

    public ErrorDetails(int status,String message,String details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.details = details;
    }
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public String getDetails() { return details; }
}
