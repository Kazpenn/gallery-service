package edu.cnm.deepdive.galleryservice.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.cnm.deepdive.galleryservice.model.entity.Image;
import edu.cnm.deepdive.galleryservice.model.entity.User;
import edu.cnm.deepdive.galleryservice.service.ImageService;
import edu.cnm.deepdive.galleryservice.view.ImageViews;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@ExposesResourceFor(Image.class)
public class ImageController {

  private final ImageService imageService;

  @Autowired
  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  @JsonView(ImageViews.Hierarchical.class)
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Image> post(@RequestParam MultipartFile file,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String description, Authentication auth)
      throws IOException, HttpMediaTypeException {
    Image image = imageService.store(file, title, description, (User) auth.getPrincipal());
    return ResponseEntity.created(image.getHref()).body(image);
  }

  @JsonView(ImageViews.Hierarchical.class)
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Image get(@PathVariable UUID id, Authentication auth) {
    return imageService
        .get(id)
        .orElseThrow();
  }

  @GetMapping("/{id}/content")
  public ResponseEntity<Resource> getContent(@PathVariable UUID id, Authentication auth) {
    return imageService
        .get(id)
        .map((image) -> {
          try {
            Resource resource = imageService.getContent(image);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    String.format("attachment: filename=\"%s\"", image.getName()))
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()))
                .header(HttpHeaders.CONTENT_TYPE, image.getContentType())
                .body(resource);
          } catch (IOException e) {
            throw new RuntimeException(e); // FIXME
          }
        })
        .orElseThrow();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Image> list(Authentication auth) {
    return imageService.list();
  }

}
