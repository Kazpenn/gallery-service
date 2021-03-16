package edu.cnm.deepdive.galleryservice.model.dao;

import edu.cnm.deepdive.galleryservice.model.entity.Gallery;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends CrudRepository<Gallery, UUID> {

  Iterable<Gallery> getAllByOrderByTitleAsc();

}
