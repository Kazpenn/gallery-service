package edu.cnm.deepdive.galleryservice.service;

import edu.cnm.deepdive.galleryservice.configuration.UploadConfiguration;
import edu.cnm.deepdive.galleryservice.configuration.UploadConfiguration.FilenameProperties;
import edu.cnm.deepdive.galleryservice.configuration.UploadConfiguration.FilenameProperties.TimestampProperties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("service")
public class LocalFilesystemStorageService implements StorageService {

  private static final String KEY_PATH_DELIMITER = "/";
  private static final String KEY_PATH_FORMAT = "%s" + KEY_PATH_DELIMITER + "%s";
  private static final String INVALID_MEDIA_FORMAT = "%s is not allowed in this storage service.";

  private final Random rng;
  private final Path uploadDirectory;
  private final Pattern subdirectoryPattern;
  private final Set<String> whitelist;
  private final DateFormat formatter;
  private final String filenameFormat;
  private final int randomizerLimit;
  private final List<MediaType> contentTypes;

  @Autowired
  public LocalFilesystemStorageService(
      Random rng, UploadConfiguration uploadConfiguration, ApplicationHome applicationHome) {
    this.rng = rng;
    FilenameProperties filenameProperties = uploadConfiguration.getFilename();
    TimestampProperties timestampProperties = filenameProperties.getTimestamp();
    String uploadPath = uploadConfiguration.getDirectory();
    uploadDirectory = uploadConfiguration.isApplicationHome()
        ? applicationHome.getDir().toPath().resolve(uploadPath)
        : Path.of(uploadPath);
    //noinspection ResultOfMethodCallIgnored
    uploadDirectory.toFile().mkdirs();
    subdirectoryPattern = uploadConfiguration.getSubdirectoryPattern();
    whitelist = uploadConfiguration.getWhitelist();
    contentTypes = whitelist
        .stream()
        .map(MediaType::valueOf)
        .collect(Collectors.toList());
    filenameFormat = filenameProperties.getFormat();
    randomizerLimit = filenameProperties.getRandomizerLimit();
    formatter = new SimpleDateFormat(timestampProperties.getFormat());
    formatter.setTimeZone(timestampProperties.getTimeZone());
  }

  @Override
  public String store(MultipartFile file) throws IOException, HttpMediaTypeException {
    if (!whitelist.contains(file.getContentType())) {
      throw new HttpMediaTypeException(
          String.format(INVALID_MEDIA_FORMAT, file.getContentType()), contentTypes) {
      };
    }
    String originalFilename = file.getOriginalFilename();
    String newFilename = String.format(filenameFormat,
        formatter.format(new Date()), rng.nextInt(randomizerLimit),
        (originalFilename != null) ? getExtension(originalFilename) : "");
    String subdirectory = getSubdirectory(newFilename);
    Path resolvedPath = uploadDirectory.resolve(subdirectory);
    //noinspection ResultOfMethodCallIgnored
    resolvedPath.toFile().mkdirs();
    Files.copy(file.getInputStream(), resolvedPath.resolve(newFilename));
    return String.format(KEY_PATH_FORMAT, subdirectory, newFilename);
  }

  @Override
  public Resource retrieve(String key) throws IOException {
    Path path = uploadDirectory.resolve(key);
    return new UrlResource(path.toUri());
  }

  @Override
  public boolean delete(String key)
      throws IOException, UnsupportedOperationException, SecurityException {
    try {
      File file = uploadDirectory.resolve(key).toFile();
      return file.delete();
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  @NonNull
  private String getExtension(@NonNull String filename) {
    int position;
    position = filename.lastIndexOf('.');
    return (position >= 0) ? filename.substring(position + 1) : "";
  }

  @NonNull
  private String getSubdirectory(@NonNull String filename) {
    String path;
    Matcher matcher = subdirectoryPattern.matcher(filename);
    if (matcher.matches()) {
      path = IntStream
          .rangeClosed(1, matcher.groupCount())
          .mapToObj(matcher::group)
          .collect(Collectors.joining(KEY_PATH_DELIMITER));
    } else {
      path = "";
    }
    return path;
  }

}
