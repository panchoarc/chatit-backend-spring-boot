package com.devit.chatapp.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseAPI<T> {
    private Status status;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    private Map<String, String> errors;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();


    private Pagination pagination;

    public enum Status {
        SUCCESS, ERROR
    }
}
