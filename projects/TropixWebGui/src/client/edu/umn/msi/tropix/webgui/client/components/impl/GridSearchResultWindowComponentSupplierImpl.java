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
import java.util.List;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderOpenedEvent;
import com.smartgwt.client.widgets.tree.events.FolderOpenedHandler;

import edu.umn.msi.tropix.client.search.models.GridData;
import edu.umn.msi.tropix.client.search.models.GridFile;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.ResultComponent;
import edu.umn.msi.tropix.webgui.client.constants.GridSearchConstants;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.Frame;
import edu.umn.msi.tropix.webgui.client.widgets.GWTDownloadFormPanel;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.PropertyListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.tropix.GridSearchService;

public class GridSearchResultWindowComponentSupplierImpl implements Supplier<ResultComponent<List<GridData>, Window>> {

  public ResultComponent<List<GridData>, Window> get() {
    return new GridSearchResultWindowComponentImpl();
  }

  class GridSearchResultWindowComponentImpl extends WindowComponentImpl<Window> implements ResultComponent<List<GridData>, Window> {
    private List<GridData> topLevelItems;
    private final TreeGrid treeGrid;
    private final PropertyListGrid propertyListGrid;
    private final Tree tree;
    private final TreeNode root;
    private final Frame detailsPanel;
    private final Label selectObjectLabel;
    private final GWTDownloadFormPanel smartDownloadFormPanel;

    GridSearchResultWindowComponentImpl() {
      this.setWidget(PopOutWindowBuilder.titled("Grid Search Results").sized(600, 600).hideOnClose().get());
      this.treeGrid = new TreeGrid();
      this.tree = new Tree();
      this.tree.setIdField("id");
      this.tree.setParentIdField("parent");
      this.tree.setIsFolderProperty("isFolder");
      this.tree.setModelType(TreeModelType.PARENT);
      this.root = new TreeNode();
      this.root.setID("-1");
      this.tree.setRoot(this.root);
      this.tree.setShowRoot(false);
      this.treeGrid.setData(this.tree);
      this.treeGrid.setSelectionType(SelectionStyle.MULTIPLE);
      final TreeGridField idField = new TreeGridField("id");
      idField.setRequired(true);
      idField.setHidden(true);
      final TreeGridField isFileField = new TreeGridField("isFile");
      isFileField.setRequired(true);
      isFileField.setHidden(true);
      final TreeGridField parentField = new TreeGridField("parent");
      parentField.setRequired(true);
      parentField.setHidden(true);
      final TreeGridField nameField = new TreeGridField("name", "Name");
      this.treeGrid.setFields(idField, parentField, nameField);
      this.treeGrid.addFolderOpenedHandler(new FolderOpenedHandler() {
        public void onFolderOpened(final FolderOpenedEvent event) {
          final TreeNode treeNode = event.getNode();
          if(!treeNode.getAttributeAsBoolean("expanded")) {
            treeNode.setAttribute("expanded", true);
            final String id = treeNode.getAttribute("id");
            final String serviceUrl = treeNode.getAttribute("serviceUrl");
            GridSearchService.Util.getInstance().getChildItems(serviceUrl, id, new AsyncCallbackImpl<List<GridData>>() {
              @Override
              public void onSuccess(final List<GridData> childItems) {
                tree.addList(getItems(childItems, id), treeNode);
              }
            });
          }
        }
      });

      this.selectObjectLabel = SmartUtils.smartParagraph(GridSearchConstants.INSTANCE.selectObject());
      this.selectObjectLabel.setWidth100();

      detailsPanel = new Frame();
      detailsPanel.setTitle("Object Details");
      detailsPanel.setWidth100();
      detailsPanel.setHeight("50%");
      propertyListGrid = new PropertyListGrid();
      propertyListGrid.setHeight100();
      propertyListGrid.setWidth100();
      smartDownloadFormPanel = new GWTDownloadFormPanel("serviceUrl", "names", "isBatch");
      final Button downloadButton = SmartUtils.getButton(GridSearchConstants.INSTANCE.download(), Resources.DOWNLOAD);
      downloadButton.setLeft(10);
      downloadButton.setHeight(20);

      // this.smartDownloadFormPanel.addToPanel(downloadButton);

      this.smartDownloadFormPanel.setWidth("100%");
      this.smartDownloadFormPanel.setType("search");
      final Command downloadCommand = new Command() {
        public void execute() {
          try {
            final ListGridRecord[] records = treeGrid.getSelection();
            if(records.length == 1) {
              final ListGridRecord record = records[0];
              final String filename = record.getAttribute("name");
              smartDownloadFormPanel.setParameter("serviceUrl", record.getAttribute("serviceUrl"));
              smartDownloadFormPanel.setParameter("isBatch", false);
              smartDownloadFormPanel.setFilename(filename);
              smartDownloadFormPanel.setParameter("names", filename);
              final String id = record.getAttribute("fileIdentifier");
              smartDownloadFormPanel.setId(id);
              smartDownloadFormPanel.submit();
            } else {
              final String filename = "tint-export.zip";
              smartDownloadFormPanel.setFilename(filename);
              final ArrayList<String> serviceUrls = Lists.newArrayListWithCapacity(records.length);
              final ArrayList<String> ids = Lists.newArrayListWithCapacity(records.length);
              final ArrayList<String> names = Lists.newArrayListWithCapacity(records.length);
              for(ListGridRecord record : records) {
                serviceUrls.add(record.getAttribute("serviceUrl"));
                ids.add(record.getAttribute("fileIdentifier"));
                names.add(record.getAttribute("name").replace(',', '_'));
              }
              smartDownloadFormPanel.setParameter("isBatch", true);
              smartDownloadFormPanel.setParameter("serviceUrl", StringUtils.join(serviceUrls));
              smartDownloadFormPanel.setId(StringUtils.join(ids));
              smartDownloadFormPanel.setParameter("names", StringUtils.join(names));
              smartDownloadFormPanel.submit();
            }
          } catch(final RuntimeException e) {
            e.printStackTrace();
          }
        }
      };
      this.treeGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
        public void onSelectionChanged(final SelectionEvent event) {
          final ListGridRecord[] selectedRecords = treeGrid.getSelection();
          if(selectedRecords.length <= 1) {
            final ListGridRecord selectedRecord = selectedRecords.length == 0 ? null : selectedRecords[0];
            if(selectedRecord == null) {
              downloadButton.setVisibility(Visibility.HIDDEN);
              propertyListGrid.setVisibility(Visibility.HIDDEN);
              selectObjectLabel.setVisibility(Visibility.INHERIT);
            } else {
              detailsPanel.setVisibility(Visibility.INHERIT);
              downloadButton.setVisibility(selectedRecord.getAttributeAsBoolean("isFile") ? Visibility.INHERIT : Visibility.HIDDEN);
              propertyListGrid.setVisibility(Visibility.INHERIT);
              selectObjectLabel.setVisibility(Visibility.HIDDEN);
              setDetails((GridData) selectedRecord.getAttributeAsObject("object"));
            }
          } else {
            boolean allFiles = true;
            for(final ListGridRecord selectedRecord : selectedRecords) {
              if(!selectedRecord.getAttributeAsBoolean("isFile")) {
                allFiles = false;
                break;
              }
            }
            downloadButton.setVisibility(allFiles ? Visibility.INHERIT : Visibility.HIDDEN);
            propertyListGrid.setVisibility(Visibility.HIDDEN);
            selectObjectLabel.setVisibility(Visibility.HIDDEN);
          }
        }
      });
      downloadButton.setVisibility(Visibility.HIDDEN);
      this.propertyListGrid.setVisibility(Visibility.HIDDEN);

      downloadButton.addClickHandler(new CommandClickHandlerImpl(downloadCommand));
      final VLayout detailsLayout = new VLayout();
      detailsLayout.setWidth100();
      detailsLayout.setHeight100();
      detailsLayout.addMember(selectObjectLabel);
      detailsLayout.addMember(propertyListGrid);
      detailsLayout.setMembersMargin(10);
      detailsLayout.setMargin(10);
      detailsLayout.addMember(smartDownloadFormPanel);
      detailsLayout.addMember(downloadButton);
      this.detailsPanel.addItem(detailsLayout);
      final VLayout layout = new VLayout();
      this.treeGrid.setWidth("100%");
      this.treeGrid.setHeight("50%");
      layout.addMember(this.treeGrid);
      layout.setMembersMargin(10);
      layout.setWidth100();
      layout.setHeight100();
      layout.addMember(this.detailsPanel);
      this.get().addItem(layout);
    }

    private TreeNode[] getItems(final List<GridData> items, final String parentId) {
      final TreeNode[] childNodes = new TreeNode[items.size()];
      int i = 0;
      for(final GridData childItem : items) {
        childNodes[i++] = this.getRecord(childItem, parentId);
      }
      return childNodes;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "BC", justification = "There is an instanceof check first.")
    private void setDetails(final GridData gridData) {
      this.propertyListGrid.clearProperties();
      this.propertyListGrid.set("Name", StringUtils.sanitize(gridData.getName()));
      this.propertyListGrid.set("Description", StringUtils.sanitize(gridData.getDescription()));
      if(gridData.getCreationDate() != null) {
        this.propertyListGrid.set("Creation Date", gridData.getCreationDate());
      }
      if(StringUtils.hasText(gridData.getUserName())) {
        this.propertyListGrid.set("Owner ", StringUtils.sanitize(gridData.getUserName()));
      }
      this.propertyListGrid.set("Owner's Grid Identity", StringUtils.sanitize(gridData.getOwnerId()));
      if(gridData instanceof GridFile) {
        final GridFile gridFile = (GridFile) gridData;
        this.propertyListGrid.set("File Type", StringUtils.sanitize(gridFile.getType()));
        this.propertyListGrid.set("File Type Description", StringUtils.sanitize(gridFile.getTypeDescription()));
      }
    }

    private TreeNode getRecord(final GridData gridData, final String parentId) {
      final TreeNode treeNode = new TreeNode();
      treeNode.setAttribute("id", gridData.getId());
      treeNode.setAttribute("parent", parentId);
      treeNode.setAttribute("name", StringUtils.sanitize(gridData.getName()));
      treeNode.setAttribute("serviceUrl", StringUtils.sanitize(gridData.getServiceUrl()));
      treeNode.setAttribute("isFolder", gridData.isDataHasChildren());
      treeNode.setAttribute("expanded", false);
      if(gridData instanceof GridFile) {
        final GridFile gridFile = (GridFile) gridData;
        treeNode.setAttribute("isFile", true);
        treeNode.setAttribute("fileIdentifier", gridFile.getFileIdentifier());
      } else {
        treeNode.setAttribute("isFile", false);
      }
      treeNode.setAttribute("object", gridData);
      return treeNode;
    }

    public void setResults(final List<GridData> results) {
      this.topLevelItems = results;
      this.tree.addList(this.getItems(this.topLevelItems, "-1"), this.root);
    }
  }

}
