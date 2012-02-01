package edu.umn.msi.tropix.jobs.activities.descriptions;

import javax.annotation.Nonnull;

import com.google.common.base.Optional;

public class FileSourceHolder {
  private final Optional<String> tropixFileId;
  private final Optional<CreateTropixFileDescription> createTropixFileDescription;

  public FileSourceHolder(@Nonnull final String tropixFileId) {
    this.tropixFileId = Optional.of(tropixFileId);
    this.createTropixFileDescription = Optional.absent();
  }

  public FileSourceHolder(@Nonnull final CreateTropixFileDescription createTropixFileDescription) {
    this.createTropixFileDescription = Optional.of(createTropixFileDescription);
    this.tropixFileId = Optional.absent();
  }

  public boolean hasExistingId() {
    return tropixFileId.isPresent();
  }

  public String getTropixFileObjectId() {
    return tropixFileId.get();
  }

  public CreateTropixFileDescription getCreateTropixFileDescription() {
    return createTropixFileDescription.get();
  }

}
