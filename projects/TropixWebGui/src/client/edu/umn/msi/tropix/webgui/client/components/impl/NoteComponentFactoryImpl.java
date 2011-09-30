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

import com.google.gwt.user.client.Command;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

import edu.umn.msi.tropix.models.Note;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.SmartTextBox;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;
import edu.umn.msi.tropix.webgui.services.object.NoteService;

public class NoteComponentFactoryImpl implements ComponentFactory<Note, CanvasComponent<TabSet>> {

  public CanvasComponent<TabSet> get(final Note note) {
    return new NoteComponentImpl(note);
  }

  static class NoteComponentImpl extends WidgetSupplierImpl<TabSet> implements CanvasComponent<TabSet> {
    private final Note note;
    private final Tab viewTab, editTab;
    private final VLayout sourceLayout = new VLayout();
    private final VLayout sourceTextLayout;
    private final SmartTextBox sourceTextBox = new SmartTextBox();

    private final HTMLPane htmlPanel = new HTMLPane();
    private final Button editButton = SmartUtils.getButton("Edit", Resources.EDIT);

    public void initContents() {
      htmlPanel.setContents("Loading note contents...");
      NoteService.Util.getInstance().getNoteHTML(this.note.getId(), new AsyncCallbackImpl<String>() {
        @Override
        public void onSuccess(final String html) {
          final String contents = "<span class=\"wikitext\">" + html + "</span>";
          htmlPanel.setContents(contents);
          htmlPanel.redraw();
        }
      });
      NoteService.Util.getInstance().getNoteContents(this.note.getId(), new AsyncCallbackImpl<String>() {
        @Override
        public void onSuccess(final String contents) {
          sourceTextBox.setContents(contents);
        }
      });
      SmartUtils.removeAndDestroyAllMembers(sourceLayout);
      sourceLayout.addMember(sourceTextLayout);
    }

    // Add link http://en.wikipedia.org/wiki/Help:Wikitext_examples
    NoteComponentImpl(final Note note) {
      this.setWidget(new TabSet());
      this.note = note;

      this.editTab = new Tab("Source");
      this.viewTab = new Tab("Note");

      NoteService.Util.getInstance().canEditContents(this.note.getId(), new AsyncCallbackImpl<Boolean>() {
        @Override
        public void onSuccess(final Boolean canEdit) {
          editButton.setDisabled(false);
        }
      });

      this.get().setTabs(viewTab, editTab);

      final VLayout viewLayout = new VLayout();
      viewLayout.setWidth100();
      viewLayout.setHeight100();
      this.viewTab.setPane(viewLayout);

      final Command reloadHtml = new Command() {
        public void execute() {
        }
      };
      reloadHtml.execute();
      viewLayout.addMember(htmlPanel);

      sourceTextBox.setWidth100();
      sourceTextBox.setHeight100();
      sourceTextBox.setContents("Loading note contents...");
      sourceTextBox.setDisabled(true);

      sourceLayout.setHeight100();
      sourceLayout.setWidth100();

      final Label wikitextLabel = new Label("This note can be formatted using <a href=\"http://en.wikipedia.org/wiki/Help:Wikitext_examples\">Wikitext</a>.");
      wikitextLabel.setHeight(20);
      wikitextLabel.setWrap(false);
      final VLayout editLayout = new VLayout();
      editLayout.addMember(wikitextLabel);
      editLayout.addMember(sourceLayout);
      editLayout.setHeight100();
      editLayout.setWidth100();
      this.editTab.setPane(editLayout);

      editButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          NoteService.Util.getInstance().attemptEdit(NoteComponentImpl.this.note.getId(), new AsyncCallbackImpl<String>() {
            @Override
            public void onSuccess(final String wikiText) {
              if(wikiText == null) {
                com.smartgwt.client.util.SC.warn("Someone else is editing this note, try back later", new BooleanCallback() {
                  public void execute(final Boolean value) {
                  }
                });
                return;
              }
              final Button saveButton = SmartUtils.getButton("Save", Resources.SAVE);
              saveButton.setDisabled(false);
              final Button cancelButton = SmartUtils.getButton("Cancel", Resources.CROSS);

              final SmartTextBox editTextBox = new SmartTextBox();
              editTextBox.setWidth100();
              editTextBox.setHeight100();
              editTextBox.setContents(wikiText);
              final CanvasWithOpsLayout<SmartTextBox> editLayout = new CanvasWithOpsLayout<SmartTextBox>(editTextBox, saveButton, cancelButton);
              editLayout.setWidth100();
              editLayout.setHeight100();
              final Command endEdit = new Command() {
                public void execute() {
                  NoteComponentImpl.this.initContents();
                }
              };
              cancelButton.addClickHandler(new ClickHandler() {
                public void onClick(final ClickEvent event) {
                  NoteService.Util.getInstance().updateContents(NoteComponentImpl.this.note.getId(), wikiText, new AsyncCallbackImpl<Void>() {
                    @Override
                    public void onFailure(final Throwable failure) {
                      endEdit.execute();
                      super.onFailure(failure);
                    }

                    @Override
                    public void onSuccess(final Void result) {
                      endEdit.execute();
                    }
                  });
                }
              });

              saveButton.addClickHandler(new ClickHandler() {
                public void onClick(final ClickEvent event) {
                  NoteService.Util.getInstance().updateContents(NoteComponentImpl.this.note.getId(), editTextBox.getContents(), new AsyncCallbackImpl<Void>() {
                    @Override
                    public void onFailure(final Throwable failure) {
                      endEdit.execute();
                      super.onFailure(failure);
                    }

                    @Override
                    public void onSuccess(final Void result) {
                      endEdit.execute();
                    }
                  });
                }
              });
              SmartUtils.removeAllMembers(sourceLayout);
              sourceLayout.setMembers(editLayout);

            }
          });

        }

      });

      sourceTextLayout = new CanvasWithOpsLayout<SmartTextBox>(sourceTextBox, editButton);
      sourceTextLayout.setWidth100();
      sourceTextLayout.setHeight100();

      initContents();
    }

  }

}
