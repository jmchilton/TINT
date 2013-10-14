package edu.umn.msi.tropix.client.galaxy.impl;

import java.io.File;
import java.util.Date;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryPermissions;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.sun.jersey.api.client.ClientResponse;

import edu.umn.msi.tropix.client.galaxy.GalaxyExportOptions;
import edu.umn.msi.tropix.client.galaxy.GalaxyExporter;
import edu.umn.msi.tropix.files.export.ExportStager;

@ManagedBean
public class GalaxyExporterImpl implements GalaxyExporter {

  @Inject
  public GalaxyExporterImpl(@Named("galaxyInstanceSupplier") final Supplier<GalaxyInstance> galaxyInstanceSupplier,
      @Value("${galaxy.email.domain}") final String emailDomain,
      final ExportStager exportStager) {
    this.galaxyInstanceSupplier = galaxyInstanceSupplier;
    this.exportStager = exportStager;
    if(StringUtils.hasText(emailDomain) && !emailDomain.startsWith("$")) {
      this.emailDomain = emailDomain;
    } else {
      this.emailDomain = "";
    }
  }

  private Supplier<GalaxyInstance> galaxyInstanceSupplier;
  private final ExportStager exportStager;
  private String emailDomain;

  public void uploadFiles(final String userId, final GalaxyExportOptions exportOptions) {
    final File exportDirectory = exportStager.stageFilesForExport(userId, Iterables.toArray(exportOptions.getFileObjectIds(), String.class));
    try {
      Thread.sleep(10000);
    } catch(InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    final LibrariesClient client = galaxyInstanceSupplier.get().getLibrariesClient();

    final Library library = new Library();
    final String dateStr = DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date());
    library.setName("TINT_upload_" + exportOptions.getName() + "_" + dateStr);
    library.setDescription("Auto-generated data library for TINT upload on " + dateStr);

    final Library createdLibrary = client.createLibrary(library);
    final String libraryId = createdLibrary.getId();
    final LibraryContent rootFolder = client.getRootFolder(libraryId);

    final FilesystemPathsLibraryUpload upload = new FilesystemPathsLibraryUpload(exportOptions.isMultipleFileDataset());
    String fileType = exportOptions.getFileType();
    if(exportOptions.isMultipleFileDataset()) {
      upload.setName(exportOptions.getName());
    }
    if(StringUtils.hasText(fileType)) {
      upload.setFileType(fileType);
    }
    upload.setContent(exportDirectory.getAbsolutePath());
    upload.setFolderId(rootFolder.getId());
    final ClientResponse response = client.uploadFilesystemPathsRequest(libraryId, upload);
    if(response.getStatus() != 200) {
      throw new RuntimeException("Failed to upload file paths" + response.getEntity(String.class));
    }

    if(exportOptions.isMakePrivate()) {
      makeLibraryPrivate(userId, createdLibrary);
    }

  }

  private void makeLibraryPrivate(final String userId, final Library createdLibrary) {
    // Obtain user object
    User owner = null;
    final UsersClient usersClient = galaxyInstanceSupplier.get().getUsersClient();
    final String email = userId + emailDomain;
    for(final User user : usersClient.getUsers()) {
      if(user.getEmail().equals(email)) {
        owner = user;
        break;
      }
    }
    final LibrariesClient librariesClient = galaxyInstanceSupplier.get().getLibrariesClient();
    if(owner == null) {
      // In order to create users like this - use_remote_user must be enabled
      // in the Galaxy instance's universe_wsgi.ini options.
      owner = usersClient.createUser(email);
    }
    // Obtain user role
    Role ownersPrivateRole = null;
    final RolesClient rolesClient = galaxyInstanceSupplier.get().getRolesClient();
    for(final Role role : rolesClient.getRoles()) {
      if(role.getName().equals(email)) {
        ownersPrivateRole = role;
        break;
      }
    }
    final String ownersPrivateRoleId = ownersPrivateRole.getId();

    // Set data library permissions
    final LibraryPermissions permissions = new LibraryPermissions();
    permissions.getAccessInRoles().add(ownersPrivateRoleId);
    permissions.getAddInRoles().add(ownersPrivateRoleId);
    permissions.getManageInRoles().add(ownersPrivateRoleId);
    permissions.getModifyInRoles().add(ownersPrivateRoleId);
    librariesClient.setLibraryPermissions(createdLibrary.getId(), permissions);
  }

}
