package edu.cnm.deepdive.galleryservice.configuration;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "upload")
public class UploadConfiguration {

  private boolean applicationHome = true;
  private String directory = "uploads";
  private Pattern subdirectoryPattern = Pattern.compile("^(.{4})(.{2})(.{2}).*$");
  private Set<String> whitelist = new LinkedHashSet<>(Set.of(
          "- image/bmp",
          "image/gif",
          "image/jpeg",
          "image/pjpeg",
          "image/png",
          "image/tiff"
      ));
  private FilenameProperties filename;

  public boolean isApplicationHome() {
    return applicationHome;
  }

  public void setApplicationHome(boolean applicationHome) {
    this.applicationHome = applicationHome;
  }

  public String getDirectory() {
    return directory;
  }

  public void setDirectory(String directory) {
    this.directory = directory;
  }

  public Pattern getSubdirectoryPattern() {
    return subdirectoryPattern;
  }

  public void setSubdirectoryPattern(Pattern subdirectoryPattern) {
    this.subdirectoryPattern = subdirectoryPattern;
  }

  public Set<String> getWhitelist() {
    return whitelist;
  }

  public void setWhitelist(Set<String> whitelist) {
    this.whitelist = whitelist;
  }

  public FilenameProperties getFilename() {
    return filename;
  }

  public void setFilename(FilenameProperties filename) {
    this.filename = filename;
  }

  public static class FilenameProperties {

    /**
     * Specifies whether the upload directory should be interpreted relative to the application home
     * directory
     */

    private String format = "%1$s=%2$d.%3$s";
    private int randomizerLimit = 1_000_000;
    private TimestampProperties timestamp;

    /**
     * Specifies whether the upload directory should be interpreted relative to the application home
     * directory
     */

    public String getFormat() {
      return format;
    }

    public void setFormat(String format) {
      this.format = format;
    }

    public int getRandomizerLimit() {
      return randomizerLimit;
    }

    public void setRandomizerLimit(int randomizerLimit) {
      this.randomizerLimit = randomizerLimit;
    }

    public TimestampProperties getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(TimestampProperties timestamp) {
      this.timestamp = timestamp;
    }

    public static class TimestampProperties {

      private String format = "yyyyMMddHHmmssSSS";
      private TimeZone timeZone = TimeZone.getTimeZone("UTC");

      public String getFormat() {
        return format;
      }

      public void setFormat(String format) {
        this.format = format;
      }

      public TimeZone getTimeZone() {
        return timeZone;
      }

      public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
      }
    }

  }

}
