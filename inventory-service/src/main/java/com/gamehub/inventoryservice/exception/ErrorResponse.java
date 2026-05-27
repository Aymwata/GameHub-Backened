// Estructura limpia en JSON (molde) que se envía al cliente cuando ocurre un error.
package com.gamehub.inventoryservice.exception;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private String title;
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
}