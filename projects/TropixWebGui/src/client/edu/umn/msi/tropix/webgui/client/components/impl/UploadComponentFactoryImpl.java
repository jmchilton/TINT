/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.swfupload.client.SWFUpload;
import org.swfupload.client.SWFUpload.ButtonAction;
import org.swfupload.client.UploadBuilder;
import org.swfupload.client.event.FileQueuedHandler;
import org.swfupload.client.event.UploadErrorHandler;
import org.swfupload.client.event.UploadSuccessHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.models.locations.LocationPredicates;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.client.components.DynamicUploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.utils.FlashUtils;
import edu.umn.msi.tropix.webgui.client.utils.JObject;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;

public class UploadComponentFactoryImpl implements UploadComponentFactory<DynamicUploadComponent> {
  private static boolean debug = false;
  private static int componentCount = 0;
  private Session session;
  private LocationFactory locationFactory;
  private TreeComponentFactory treeComponentFactory;
  
  @Inject
  public void setSession(final Session session) {
    this.session = session;
  }
  
  @Inject
  public void setLocationFactory(final LocationFactory locationFactory) {
    this.locationFactory = locationFactory;
  }
  
  @Inject
  public void setTreeComponentFactory(final TreeComponentFactory treeComponentFactory) {
    this.treeComponentFactory = treeComponentFactory;
  }

  private static String getUniqueId() {
    return "XYZuploadcomponentXYZ" + componentCount++;
  }

  private static Layout getParentCanvas(final UploadComponentOptions options) {
    final Layout canvas = new Layout();
    canvas.setWidth100();
    canvas.setHeight100();
    return canvas;
  }
    
  private class FileLocationComponentImpl extends WidgetSupplierImpl<Canvas> implements UploadComponent {
    private TreeComponent treeComponent;
    private UploadComponentOptions options;
    
    FileLocationComponentImpl(final UploadComponentOptions uploadOptions) {
      this.options = uploadOptions;
      final TreeOptions treeOptions = new TreeOptions();
      final String extension = uploadOptions.getExtension();
      treeOptions.setSelectionType(uploadOptions.isAllowMultiple() ? SelectionType.MULTIPlE : SelectionType.SINGLE);
      treeOptions.setInitialItems(locationFactory.getTropixObjectSourceRootItems(null));
      treeOptions.setShowPredicate(LocationPredicates.getTropixFileTreeItemPredicate(extension, true));
      treeOptions.setSelectionPredicate(LocationPredicates.getTropixFileTreeItemPredicate(extension, false));
      treeComponent = treeComponentFactory.get(treeOptions);
      treeComponent.addSelectionListener(new Listener<TreeItem>() {
        public void onEvent(final TreeItem location) {
        }
      });
      final TreeGrid treeGrid = treeComponent.get();
      treeGrid.setWidth100();
      treeGrid.setHeight100();
      super.setWidget(treeGrid);
    }
    
    public CanUpload canUpload() {
      final CanUpload canUpload;
      if(treeComponent.getMultiSelection().isEmpty()) {
        canUpload = CanUpload.cannotUpload("No files selected");
      } else {
        canUpload = CanUpload.canUpload();
      }
      return canUpload;
    }

    public void startUpload() {
      final List<FileSource> fileSources = Lists.newArrayList();
      for(final TreeItem treeItem : getTreeItems()) {
        final FileSource fileSource = new FileSource(treeItem.getId(), treeItem.getName(), false);
        fileSources.add(fileSource);
      }
      options.getCompletionCallback().onSuccess(fileSources);   
    }
    
    private Collection<TreeItem> getTreeItems() {
      return treeComponent.getMultiSelection();
    }

    public int getNumSelectedFiles() {
      return treeComponent.getMultiSelection().size();
    }

    public boolean isZip() {
      return false;
    }

    public boolean hasNames() {
      return true;
    }

    public List<String> getNames() {
      final List<String> names = Lists.newArrayList();      
      for(final TreeItem treeItem : getTreeItems()) {
        names.add(treeItem.getName());
      }
      return names;
    }
    
  }

  private class FlashUploadComponentImpl extends WidgetSupplierImpl<Canvas> implements UploadComponent {
    private final HandlerRegistration reg;
    private final ClientListGrid filesGrid = new ClientListGrid("id");
    private SWFUpload upload;
    private long totalSize;
    private int filesUploaded = 0, numFiles;
    private long bytesUploaded;
    //private final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
    private final List<FileSource> fileSources = Lists.newArrayList();
    private final String clientId;

    private void updateUrl() {
      final String uploadUrl = GWT.getHostPageBaseURL() + "fileupload;jsessionid=" + session.getSessionId() + "?end=" + totalSize + "&start="
          + bytesUploaded + "&clientId=" + clientId + "&lastUpload=" + ((filesUploaded + 1) == numFiles);
      upload.setUploadURL(uploadUrl);
    }

    public boolean isZip() {
      return false;
    }

    FlashUploadComponentImpl(final UploadComponentOptions options) {
      filesGrid.setHeight(100);
      filesGrid.setWidth100();
      if(options.isAllowMultiple()) {
        filesGrid.setEmptyMessage("No files selected.");
      } else {
        filesGrid.setEmptyMessage("File not selected.");
      }
      final ListGridField nameField = new ListGridField("name", "File Name");
      nameField.setType(ListGridFieldType.TEXT);
      nameField.setWidth("*");

      filesGrid.setFields(nameField);

      final UploadBuilder builder = new UploadBuilder();
      // Configure which file types may be selected
      if(options.getTypes() != null) {
        builder.setFileTypes(options.getTypes());
      }
      if(options.getTypesDescription() != null) {
        builder.setFileTypesDescription(options.getTypesDescription());
      }
      if(options.getTypesDescription() != null) {
        builder.setFileTypesDescription(options.getTypesDescription());
      }
      clientId = getUniqueId();

      final HTMLPane pane = new HTMLPane();
      pane.setHeight(22);
      builder.setButtonPlaceholderID(clientId);
      builder.setButtonHeight(22);
      builder.setButtonWidth(100);
      pane.setWidth(100);
      if(options.isAllowMultiple()) {
        builder.setButtonImageURL(GWT.getHostPageBaseURL() + "images/uploadImageAddFiles.jpg");
      } else {
        builder.setButtonImageURL(GWT.getHostPageBaseURL() + "images/uploadImageChooseFile.jpg");
      }
      // Use ButtonAction.SELECT_FILE to only allow selection of a single file

      builder.setButtonAction(options.isAllowMultiple() ? ButtonAction.SELECT_FILES : ButtonAction.SELECT_FILE);
      builder.setFileQueuedHandler(new FileQueuedHandler() {
        public void onFileQueued(final FileQueuedEvent queuedEvent) {
          if(!options.isAllowMultiple()) {
            SmartUtils.removeAllRecords(filesGrid);
          }
          final ListGridRecord record = new ListGridRecord();
          record.setAttribute("id", queuedEvent.getFile().getId());
          record.setAttribute("name", queuedEvent.getFile().getName());
          filesGrid.addData(record);
        }
      });
      builder.setUploadSuccessHandler(new UploadSuccessHandler() {
        public void onUploadSuccess(final UploadSuccessEvent uploadEvent) {
          try {
            filesUploaded++;
            bytesUploaded += uploadEvent.getFile().getSize();
            final String serverResponse = uploadEvent.getServerData();
            fileSources.addAll(toFileSources(serverResponse));
            updateUrl();
            if(upload.getStats().getFilesQueued() > 0) {
              upload.startUpload();
            } else {
              options.getCompletionCallback().onSuccess(fileSources);
            }
          } catch(final Exception e) {
            options.getCompletionCallback().onFailure(e);
          }
        }
      });
      builder.setUploadErrorHandler(new UploadErrorHandler() {
        public void onUploadError(final UploadErrorEvent e) {
          options.getCompletionCallback().onFailure(new Exception("Upload failed " + e.getMessage()));
        }
      });

      // The button to start the transfer
      final Layout parent = getParentCanvas(options);
      setWidget(parent);

      // pane.setMargin(5);
      pane.setContents("<span id=\"" + clientId + "\" />");
      reg = get().addDrawHandler(new DrawHandler() {
        public void onDraw(final DrawEvent event) {
          upload = builder.build();
          reg.removeHandler();
        }
      });
      final Button removeButton = SmartUtils.getButton("Remove File", Resources.CROSS);
      removeButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          final ListGridRecord record = filesGrid.getSelectedRecord();
          final String id = record.getAttribute("id");
          filesGrid.removeData(record);
          upload.cancelUpload(id, false);
        }
      });
      SmartUtils.enabledWhenHasSelection(removeButton, filesGrid);
      removeButton.setDisabled(true);
      if(!options.isAllowMultiple()) {
        removeButton.hide();
      }

      final CanvasWithOpsLayout<ClientListGrid> layout = new CanvasWithOpsLayout<ClientListGrid>(filesGrid, pane, removeButton);
      if(debug) {
        layout.setBorder("1px solid red");
        parent.setBorder("1px solid orange");
      }
      parent.addMember(layout);
    }

    public void startUpload() {
      if(upload.getStats().isInProgress()) {
        return;
      }
      numFiles = upload.getStats().getFilesQueued();
      totalSize = 0L;
      bytesUploaded = 0L;
      for(int i = 0; i < numFiles; i++) {
        totalSize += upload.getFile(i).getSize();
      }
      updateUrl();
      upload.startUpload();
    }

    public CanUpload canUpload() {
      final CanUpload canUpload;
      if(filesGrid.getDataAsRecordList().isEmpty()) {
        canUpload = CanUpload.cannotUpload("No files selected");
      } else {
        canUpload = CanUpload.canUpload();
      }
      return canUpload;
    }

    public int getNumSelectedFiles() {
      final ListGridRecord[] records = filesGrid.getRecords();
      return records == null ? 0 : records.length;
    }

    public boolean hasNames() {
      return true;
    }
    
    public List<String> getNames() {
      final List<String> names = Lists.newArrayList();
      for(ListGridRecord record : filesGrid.getRecords()) {
        names.add(record.getAttributeAsString("name"));
      }
      return names;
    }
    
  }

  private class HtmlUploadComponentImpl extends WidgetSupplierImpl<Canvas> implements UploadComponent {
    private final FormPanel formPanel = new FormPanel();
    private final VerticalPanel panel = new VerticalPanel();
    private final ScrollPanel scrollPanel = new ScrollPanel(panel);
    private final ArrayList<FileUpload> uploadForms = new ArrayList<FileUpload>();
    private final UploadComponentOptions options;
    private final boolean zip;
    private final String types;
    private final String typesDescription;

    private boolean isMultiple() {
      return options.isAllowMultiple() && !zip;
    }

    private void addFileUploadWidget() {
      final FileUpload upload = new FileUpload();
      upload.setWidth("200px");
      upload.setName(getUniqueId());
      uploadForms.add(upload);
      final HorizontalPanel hPanel = new HorizontalPanel();
      hPanel.setHeight("30px");
      hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      // Don't display ability to remove form if expecting exactly 1 upload or if this is the first upload element
      if(isMultiple() && uploadForms.size() > 1) {
        com.google.gwt.user.client.ui.Image crossImage = new com.google.gwt.user.client.ui.Image();
        crossImage.setUrl(Resources.CROSS);
        crossImage.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
          public void onClick(final com.google.gwt.event.dom.client.ClickEvent clickEvent) {
            panel.remove(hPanel);
            uploadForms.remove(hPanel.getWidget(1));
          }
        });
        hPanel.add(crossImage);
      } else {
        com.google.gwt.user.client.ui.VerticalPanel space = new com.google.gwt.user.client.ui.VerticalPanel();
        space.setSize("16px", "1px");
        hPanel.add(space);
      }

      hPanel.add(upload);
      panel.add(hPanel);
      if(get() != null) {
        get().markForRedraw();
      }
    }

    HtmlUploadComponentImpl(final UploadComponentOptions options, final boolean zip) {
      this.zip = zip;
      if(zip) {
        types = "*.zip";
        typesDescription = "Zip";
      } else {
        types = options.getTypes() == null ? null : options.getTypes();
        typesDescription = options.getTypesDescription() == null ? null : options.getTypesDescription();
      }
      this.options = options;
      if(isMultiple()) {
        formPanel.setHeight("125px");
        scrollPanel.setHeight("125px");
        formPanel.setWidth("320px");
        scrollPanel.setWidth("320px");
      } else {
        formPanel.setHeight("30px");
        scrollPanel.setHeight("30px");
        formPanel.setWidth("310px");
        scrollPanel.setWidth("310px");
      }

      panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

      formPanel.setWidget(scrollPanel);
      formPanel.setAction(GWT.getHostPageBaseURL() + "fileupload?zip=" + zip);
      formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
      formPanel.setMethod(FormPanel.METHOD_POST);
      formPanel.addSubmitHandler(new SubmitHandler() {
        public void onSubmit(final SubmitEvent submitEvent) {

        }
      });
      formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
        public void onSubmitComplete(final SubmitCompleteEvent completeEvent) {
          System.out.println("Submit complete " + completeEvent.getResults());
          final String result = completeEvent.getResults();
          try {
            final List<FileSource> fileSources = toFileSources(result);  
            options.getCompletionCallback().onSuccess(fileSources);
          } catch(final Exception e) {
            options.getCompletionCallback().onFailure(e);
          }
        }
      });
      final Layout parent = getParentCanvas(options);
      parent.setWidth100();
      if(debug) {
        parent.setBorder("1px solid pink");
      }
      parent.setAlign(VerticalAlignment.CENTER);
      parent.setLayoutAlign(VerticalAlignment.CENTER);
      final Layout wrapperLayout = new VLayout();
      // wrapperLayout.setHeight100();
      wrapperLayout.setWidth100();
      if(isMultiple()) {
        wrapperLayout.setHeight("140px");
      } else {
        wrapperLayout.setHeight("30px");
      }
      if(debug) {
        wrapperLayout.setBorder("1px solid blue");
      }
      wrapperLayout.setAlign(VerticalAlignment.CENTER);
      wrapperLayout.setLayoutAlign(VerticalAlignment.CENTER);
      /*
       * wrapperLayout.setGroupTitle(isMultiple() ? "File(s)" : (zip ? "Zip File" : "File"));
       * wrapperLayout.setIsGroup(true);
       * wrapperLayout.setPadding(15);
       * wrapperLayout.setLayoutAlign(Alignment.CENTER);
       * wrapperLayout.setAlign(Alignment.CENTER);
       */
      if(isMultiple()) {
        final Label addLabel = SmartUtils.smartParagraph("<span style=\"color:blue;cursor:pointer; text-decoration: underline;\">Add Upload</span>");
        addLabel.addClickHandler(new ClickHandler() {
          public void onClick(final ClickEvent clickEvent) {
            addFileUploadWidget();
          }
        });
        wrapperLayout.addMember(addLabel);
      }
      wrapperLayout.addMember(formPanel);
      parent.addMember(wrapperLayout);
      // parent.setLayoutAlign(Alignment.CENTER);
      // parent.setAlign(Alignment.CENTER);
      addFileUploadWidget();
      setWidget(parent);
    }

    public void startUpload() {
      formPanel.submit();
    }

    public CanUpload canUpload() {
      for(FileUpload upload : uploadForms) {
        final String filename = upload.getFilename();
        if(filename == null || filename.trim().length() == 0) {
          return CanUpload.cannotUpload("File not specified.");
        }
        if(types != null) {
          if(!filename.matches(types.replace(";", "|").replace("*", ".*"))) {
            return CanUpload
                .cannotUpload("Selected file " + filename + " doesn't appear to be a valid " + typesDescription + "(" + types + ") file.");
          }
        }
      }
      return CanUpload.canUpload();
    }

    public int getNumSelectedFiles() {
      return uploadForms.size();
    }

    public boolean isZip() {
      return zip;
    }

    public boolean hasNames() {
      return !zip;
    }
    
    public List<String> getNames() {
      if(!hasNames()) {
        return null;
      }
      final List<String> names = Lists.newArrayList();
      for(final FileUpload upload : uploadForms) {
        names.add(upload.getFilename());
      }
      return names;
    }

  }

  private static int uploadComponentCount = 0;

  private class MultiUploadComponentImpl extends WidgetSupplierImpl<Canvas> implements DynamicUploadComponent {
    private int uploadComponentNumber = uploadComponentCount++;
    private final LinkedHashMap<String, UploadComponent> components = new LinkedHashMap<String, UploadComponent>();
    private final VLayout wrapperLayout = new VLayout();
    private VLayout layout;
    private Canvas parentCanvas;
    private UploadComponent currentComponent;
    private Canvas currentCanvas;
    private String currentType;

    MultiUploadComponentImpl(final UploadComponentOptions uploadOptions) {
      setWidget(wrapperLayout);
      reset(uploadOptions);
    }

    public void update(final UploadComponentOptions uploadOptions) {
      reset(uploadOptions);
    }

    private void clear() {
      SmartUtils.removeAndDestroyAllMembers(wrapperLayout);
      layout = null;
      parentCanvas = null;
    }

    private void reset(final UploadComponentOptions options) {
      resetComponents(options);
      clear();

      final Set<String> types = components.keySet();
      currentType = types.iterator().next();

      parentCanvas = new Canvas();
      layout = new VLayout();
      final SelectItem typeItem = new SelectItem("uploadType", "Upload Type");
      typeItem.setValueMap(types.toArray(new String[types.size()]));
      typeItem.setValue(currentType);
      typeItem.setWidth(250);
      typeItem.addChangedHandler(new ChangedHandler() {
        public void onChanged(final ChangedEvent event) {
          currentType = typeItem.getValue().toString();
          update();
        }
      });
      final Form form = new Form("UploadComponentType_" + uploadComponentNumber, typeItem);
      form.setPadding(0);
      form.setMargin(0);
      form.setCellSpacing(0);
      form.setCellPadding(0);

      form.setAlign(Alignment.RIGHT);
      form.setWidth(350);
      form.setHeight(20);
      final HLayout header = new HLayout();
      header.setAlign(Alignment.RIGHT);
      header.addMember(form);
      header.setHeight(20);

      layout.setAlign(Alignment.RIGHT);
      layout.addMember(header);
      layout.addMember(parentCanvas);
      layout.setGroupTitle("File Chooser");
      layout.setIsGroup(true);
      layout.setHeight("185px");
      layout.setPadding(2);
      layout.setLayoutAlign(Alignment.CENTER);
      layout.setAlign(Alignment.CENTER);

      for(final UploadComponent uploadComponent : components.values()) {
        final Canvas childCanvas = uploadComponent.get();
        childCanvas.hide();
        parentCanvas.addChild(childCanvas);
      }
      update();
      wrapperLayout.addMember(layout);
    }

    private void resetComponents(final UploadComponentOptions options) {
      components.clear();
      if(FlashUtils.flashMajorVersion() > 8) {
        components.put("Flash Upload", new FlashUploadComponentImpl(options));
      }
      if(options.isAllowMultiple()) {
        components.put("Traditional Upload (select files as zip)", new HtmlUploadComponentImpl(options, true));
        components.put("Traditional Upload (select individual files)", new HtmlUploadComponentImpl(options, false));
      } else {
        components.put("Traditional Upload", new HtmlUploadComponentImpl(options, false));
      }
      if(StringUtils.hasText(options.getExtension())) {
        components.put("Select Existing TINT Files", new FileLocationComponentImpl(options));
      }
    }

    private void update() {
      if(currentCanvas != null) {
        currentCanvas.hide();
      }
      currentComponent = components.get(currentType);
      currentCanvas = currentComponent.get();
      currentCanvas.show();
    }

    public CanUpload canUpload() {
      return currentComponent.canUpload();
    }

    public int getNumSelectedFiles() {
      return currentComponent.getNumSelectedFiles();
    }

    public void startUpload() {
      currentComponent.startUpload();
    }

    public boolean isZip() {
      return currentComponent.isZip();
    }

    public boolean hasNames() {
      return currentComponent.hasNames();
    }
    
    public List<String> getNames() {
      return currentComponent.getNames();
    }

  }
  
  private static List<FileSource> toFileSources(final String serverResponse) {
    final List<FileSource> fileSources = Lists.newArrayList();
    final JObject jObject = JObject.Factory.create(serverResponse);
    for(final JObject fileObject : jObject.getJArray("result").asJObjectIterable()) {
      final String fileName = fileObject.getString("fileName");
      final String id = fileObject.getString("id");
      final FileSource fileSource = new FileSource(id, fileName, true);
      fileSources.add(fileSource);
    }
    return fileSources;
  }

  public DynamicUploadComponent get(final UploadComponentOptions options) {
    return new MultiUploadComponentImpl(options);
  }

}
