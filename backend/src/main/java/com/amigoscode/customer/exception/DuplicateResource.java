package com.amigoscode.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicateResource extends RuntimeException {

   public DuplicateResource() {
      this("Resource already present");
   }

   public DuplicateResource(String message) {
      super(message);
   }
}
