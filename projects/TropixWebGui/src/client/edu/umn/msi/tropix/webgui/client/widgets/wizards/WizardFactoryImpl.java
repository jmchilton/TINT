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

package edu.umn.msi.tropix.webgui.client.widgets.wizards;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;

public class WizardFactoryImpl implements WizardFactory {
  public static final int MAX_TITLED_PAGES = 4;
  private static WizardFactoryImpl factory;

  public static WizardFactory getInstance() {
    if(WizardFactoryImpl.factory == null) {
      WizardFactoryImpl.factory = new WizardFactoryImpl();
    }
    return WizardFactoryImpl.factory;
  }

  static class Wizard extends WizardWindow implements WizardStateListener {
    private final List<WizardPage> wizardPages;
    private final List<Label> titleLabels, descriptionLabels;
    private final Button nextButton = SmartUtils.getButton("Next", null, 70);
    private final Button previousButton = SmartUtils.getButton("Previous", null, 70);
    private final Button finishButton = SmartUtils.getButton("Finish", null, 70);
    private final Button cancelButton = SmartUtils.getButton("Cancel", null, 70);
    private int index = 0;
    private Canvas displayedCanvas = null;
    private Label displayedTitleLabel = null, displayedDescriptionLabel = null;
    private final HLayout pageLayout, descriptionLayout, titleLayout;
    private final WizardOptions options;

    protected void onDestroy() {
      for(final WizardPage wizardPage : this.wizardPages) {
        wizardPage.getCanvas().markForDestroy();
      }
    };

    Wizard(final List<WizardPage> pages, final WizardCompletionHandler finishHandler, final WizardOptions options) {
      this.setShowToolbar(true);
      SmartUtils.destroyOnClose(this);
      this.options = options == null ? new WizardOptions() : options;
      this.setToolbarButtons(this.previousButton, this.nextButton, this.finishButton, this.cancelButton);
      this.wizardPages = pages;
      titleLayout = new HLayout();
      this.setOverflow(Overflow.HIDDEN);
      titleLayout.setAlign(Alignment.CENTER);
      titleLayout.setHeight(20);
      titleLayout.setWidth100();
      titleLayout.setAlign(VerticalAlignment.CENTER);
      titleLayout.setOverflow(Overflow.HIDDEN);
      this.titleLabels = new ArrayList<Label>(pages.size());
      this.descriptionLabels = new ArrayList<Label>(pages.size());

      this.pageLayout = new HLayout();
      this.pageLayout.setWidth100();
      this.pageLayout.setHeight("*");
      this.pageLayout.setOverflow(Overflow.HIDDEN);
      this.pageLayout.setMargin(5);
      this.pageLayout.setLayoutMargin(5);
      this.pageLayout.setAlign(Alignment.CENTER);
      this.pageLayout.setAlign(VerticalAlignment.CENTER);
      for(final WizardPage wizardPage : pages) {
        wizardPage.registerWizardStateListener(this);

        final String title = wizardPage.getTitle();
        final Label titleLabel = new Label("<b>-" + title + "-</b>");
        titleLabel.setAlign(Alignment.CENTER);
        titleLabel.setOverflow(Overflow.HIDDEN);
        titleLabel.setOpacity(40);
        this.titleLabels.add(titleLabel);

        final String description = wizardPage.getDescription();
        final Label descriptionLabel = SmartUtils.smartParagraph("<i>" + description + "</i>");
        descriptionLabel.setOpacity(70);
        descriptionLabel.setAutoFit(true);
        this.descriptionLabels.add(descriptionLabel);

        final Canvas canvas = wizardPage.getCanvas();
        canvas.setWidth100();
        canvas.setHeight100();
        canvas.hide();
        this.pageLayout.addChild(canvas);
      }
      if(pages.size() <= MAX_TITLED_PAGES) {
        for(Label titleLabel : titleLabels) {
          titleLayout.addMember(titleLabel);
        }
      }

      this.descriptionLayout = new HLayout();
      this.descriptionLayout.setAlign(Alignment.LEFT);
      this.descriptionLayout.setWidth100();
      this.descriptionLayout.setHeight(15);
      this.descriptionLayout.setOverflow(Overflow.HIDDEN);

      this.setAlign(Alignment.CENTER);
      this.setWidth(475);
      this.setHeight(425);
      this.setCanDragResize(true);
      this.addItem(titleLayout);
      this.addItem(this.descriptionLayout);
      this.addItem(this.pageLayout);
      this.setCanDragReposition(true);
      this.setShowMinimizeButton(true);
      this.setShowMaximizeButton(true);
      this.setTitle(this.options.getTitle());
      this.cancelButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          destroy();
        }
      });
      this.previousButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          handlePrevious();
        }
      });
      this.nextButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          handleNext();
        }
      });
      this.finishButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          if(!getWizardPage(index).isValid()) {
            String error = getWizardPage(index).readAndResetError();
            if(error == null) {
              error = "You must resolve invalid or missing fields before finishing.";
            }
            SC.say(error);
            return;
          }
          hide();
          finishHandler.onCompletion(Wizard.this);
        }
      });
      this.handleIndexChange();
    }

    public void handleNext() {
      if(!getWizardPage(index).isValid()) {
        SC.say("Please address invalid or missing fields before advancing.");
        return;
      }
      do {
        index++;
      } while(index < wizardPages.size() && !getWizardPage(index).isEnabled());
      this.handleIndexChange();
    }

    private boolean atFirstEnabledIndex() {
      if(index == 0) {
        return true;
      }
      boolean foundEnabledBefore = false;
      for(int i = index; i >= 0; i--) {
        if(getWizardPage(i).isEnabled()) {
          foundEnabledBefore = true;
          break;
        }
      }
      return !foundEnabledBefore;
    }

    private boolean atLastEnabledIndex() {
      final int numPages = getNumPages();
      if(index >= numPages) {
        return true;
      }
      boolean foundEnabledPast = false;
      for(int i = index + 1; i < numPages; i++) {
        if(getWizardPage(i).isEnabled()) {
          foundEnabledPast = true;
          break;
        }
      }
      return !foundEnabledPast;
    }

    private WizardPage getWizardPage(final int index) {
      return wizardPages.get(index);
    }

    private int getNumPages() {
      return wizardPages.size();
    }

    public void handleIndexChange() {
      if(displayedCanvas != null) {
        displayedCanvas.hide();
        displayedTitleLabel.setOpacity(40);
        if(wizardPages.size() > MAX_TITLED_PAGES) {
          titleLayout.removeMember(displayedTitleLabel);
        }
        descriptionLayout.removeMember(displayedDescriptionLabel);
      }
      final WizardPage selectedPage = wizardPages.get(index);
      if(atFirstEnabledIndex()) {
        previousButton.disable();
      } else {
        previousButton.enable();
      }
      onStateChange(null);
      displayedCanvas = selectedPage.getCanvas();
      selectedPage.onDisplay();
      displayedTitleLabel = titleLabels.get(index);
      displayedDescriptionLabel = descriptionLabels.get(index);
      displayedCanvas.show();
      displayedTitleLabel.setOpacity(100);
      if(wizardPages.size() > MAX_TITLED_PAGES) {
        titleLayout.addMember(displayedTitleLabel);
      }
      descriptionLayout.addMember(displayedDescriptionLabel);
    }

    public void handlePrevious() {
      do {
        index--;
      } while(index > 0 && !getWizardPage(index).isEnabled());
      this.handleIndexChange();
    }

    public void onStateChange(@Nullable final WizardPage wizardPage) {
      final WizardPage selectedPage = getWizardPage(index);
      if(!atLastEnabledIndex() && selectedPage.allowNext()) {
        this.nextButton.enable();
      } else {
        this.nextButton.disable();
      }
      if(selectedPage.allowNext() && (selectedPage.canFinishEarly() || atLastEnabledIndex())) {
        this.finishButton.enable();
      } else {
        this.finishButton.disable();
      }
    }

    public void goToPage(final int index) {
      this.index = index;
    }

  }

  public WizardWindow getWizard(final List<WizardPage> pages, final WizardOptions options, final WizardCompletionHandler finishHandler) {
    final Wizard wizard = new Wizard(pages, finishHandler, options);
    wizard.draw();
    return wizard;
  }
}
