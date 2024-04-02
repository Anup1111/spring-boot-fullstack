package com.amigoscode.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RequestValidationException extends RuntimeException {

   public RequestValidationException() {
      this("No changes found");
   }

   public RequestValidationException(String message) {
      super(message);
   }
}
