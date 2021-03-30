package edu.cnm.deepdive.galleryservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ImageNotFoundException extends ResponseStatusException {

  private final static String NOT_FOUND_REASON = "Image not found";

  public ImageNotFoundException() {
    super(HttpStatus.NOT_FOUND, NOT_FOUND_REASON);
  }
}
