package edu.cnm.deepdive.galleryservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cnm.deepdive.galleryservice.model.entity.Gallery;
import edu.cnm.deepdive.galleryservice.model.entity.User;
import edu.cnm.deepdive.galleryservice.service.GalleryService;
import edu.cnm.deepdive.galleryservice.service.UserService;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Profile("preload")
public class Preloader implements CommandLineRunner {

  private static final String PRELOADER_USER_NAME = "system";
  private static final String PRELOADER_OAUTH_KEY = "";
  private static final String PRELOADER_DATA = "preload/galleries.json";

  private final UserService userService;
  private final GalleryService galleryService;

  @Autowired
  public Preloader(UserService userService, GalleryService galleryService) {
    this.userService = userService;
    this.galleryService = galleryService;
  }

  @Override
  public void run(String... args) throws Exception {
    User user = userService.getOrCreate(PRELOADER_OAUTH_KEY, PRELOADER_USER_NAME);
    ClassPathResource resource = new ClassPathResource(PRELOADER_DATA);
    try (InputStream input = resource.getInputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      galleryService.save(
          Stream
          .of(mapper.readValue(input, Gallery[].class))
          .peek((gallery) -> gallery.setCreator(user))
          .collect(Collectors.toList())
      );
    }
  }

}
