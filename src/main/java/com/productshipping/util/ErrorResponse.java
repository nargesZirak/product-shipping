package com.productshipping.util;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class ErrorResponse {

    private String errorMessage;

    public ErrorResponse(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static ResponseEntity returnErrorResponse(String message){
        return new ResponseEntity(new ErrorResponse("Error: " + message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
