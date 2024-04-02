package com.amigoscode.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotFound extends RuntimeException {

   public ResourceNotFound() {
      this("Resource not found");
   }

   public ResourceNotFound(String message) {
      super(message);
   }
}
