package edu.cnm.deepdive.galleryservice.model.dao;

import edu.cnm.deepdive.galleryservice.model.entity.Image;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, UUID> {

  //TODO Add search methods.

}
