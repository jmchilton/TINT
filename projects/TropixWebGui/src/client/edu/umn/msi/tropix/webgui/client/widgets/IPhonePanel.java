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

package edu.umn.msi.tropix.webgui.client.widgets;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Based on WallToWallPanel of gwtfeedreader project.
 */
public class IPhonePanel extends Composite implements HasHTML {
  private static IPhonePanel activePanel;

  private final ClickListener parentClickListener = new ClickListener() {
    public void onClick(final Widget w) {
      IPhonePanel.this.exit();
    }
  };

  private final IPhonePanel parent;
  private final FlowPanel contents = new FlowPanel();
  private final HorizontalPanel header = new HorizontalPanel();
  private final PanelLabel panelLabel;
  private final UnsunkLabel titleLabel = new UnsunkLabel("");

  private HasText hasText;
  private HasHTML hasHtml;
  @SuppressWarnings("unused")
  // TODO: Do something with this label
  private PanelLabel lastSelectedLabel;

  public IPhonePanel(final String title, final IPhonePanel parent) {
    this.parent = parent;
    this.panelLabel = new PanelLabel("", new Command() {
      public void execute() {
        IPhonePanel.this.enter();
      }
    }) {
      public void setText(final String title) {
        IPhonePanel.this.titleLabel.setText(title);
        super.setText(title);
      }
    };
    this.panelLabel.setText(title);

    this.header.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
    this.header.addStyleName("header");

    if(parent != null) {
      final Label l = new Label(parent.getShortTitle());
      l.addClickListener(this.parentClickListener);
      l.addStyleName("button");
      l.addStyleName("backButton");
      this.header.add(l);
    }

    this.titleLabel.addStyleName("titleLabel");
    this.header.add(this.titleLabel);
    this.header.setCellWidth(this.titleLabel, "100%");
    this.header.setCellHorizontalAlignment(this.titleLabel, HasHorizontalAlignment.ALIGN_CENTER);

    final FlowPanel vp = new FlowPanel();
    vp.add(this.header);

    this.contents.addStyleName("contents");
    vp.add(this.contents);

    this.initWidget(vp);
    this.addStyleName("SliderPanel");
  }

  public void add(final Widget widget) {
    if((this.hasText != null) || (this.hasHtml != null)) {
      this.hasText = null;
      this.hasHtml = null;
      this.contents.clear();
    }

    this.contents.add(widget);
  }

  public void clear() {
    this.contents.clear();
  }

  public String getHTML() {
    return this.hasHtml == null ? null : this.hasHtml.getHTML();
  }

  public PanelLabel getLabel() {
    return this.panelLabel;
  }

  public String getText() {
    return this.hasText == null ? null : this.hasText.getText();
  }

  public void remove(final Widget widget) {
    this.contents.remove(widget);
  }

  /**
   * Set the command to be executed by a right-justified button in the title bar.
   * 
   * @param label
   *          the label for the button
   * @param title
   *          the title or alt-text for the button
   * @param command
   *          the Command to execute when the button is pressed.
   */
  public void addCommand(final String label, final String title, final Command command) {
    final Label l = new Label(label);
    l.addStyleName("button");
    l.addStyleName("goButton");
    l.setTitle(title);
    l.addClickListener(new ClickListener() {
      public void onClick(final Widget w) {
        command.execute();
      }
    });
    this.header.add(l);
    this.header.setCellHorizontalAlignment(l, HasHorizontalAlignment.ALIGN_RIGHT);
  }

  public void setHTML(final String html) {
    HTML h = new HTML(html);
    this.hasText = h;
    this.hasHtml = h;

    this.contents.clear();
    this.contents.add(h);
  }

  public void setText(final String text) {
    UnsunkLabel l = new UnsunkLabel(text);
    this.hasText = l; 
    this.hasHtml = null;

    this.contents.clear();
    this.contents.add(l);
  }

  /**
   * Display the panel, removing any currently-displayed panel from the screen. If the panel is already displayed, calling this method again will produce no result.
   */
  public void enter() {
    if(this.isAttached()) {
      return;
    }
    if(IPhonePanel.activePanel != null) {
      // Save the label to scroll to when backing into the parent panel.
      if(IPhonePanel.activePanel == this.parent) {
        this.parent.setLastSelectedLabel(this.getLabel());
      }
      RootPanel.get().remove(IPhonePanel.activePanel);
    }

    IPhonePanel.activePanel = IPhonePanel.this;
    RootPanel.get().add(IPhonePanel.this, 0, 0);
  }

  /**
   * Return to the parent panel.
   */
  protected void exit() {
    if(this.parent == null) {
      throw new RuntimeException("SliderPanel has no parent");
    }
    this.setLastSelectedLabel(null);
    this.parent.enter();
  }

  /**
   * A short title to be used as the label of the back button.
   */
  protected String getShortTitle() {
    return "Previous";
  }

  /**
   * Remember the last PanelLabel that was selected on the current panel. This is used to scroll the viewport down to the last selected panel when the IPhonePanel is backed into.
   */
  private void setLastSelectedLabel(final PanelLabel label) {
    this.lastSelectedLabel = label;
  }

  public static class PanelLabel extends SimplePanel implements HasText {

    private final Command primary;
    private final Widget widget;

    // final UnsunkImage enterImage = new
    // UnsunkImage(Resources.INSTANCE.enter());

    public PanelLabel(final String text) {
      this(text, null, false);
    }

    public PanelLabel(final String text, final Command primary) {
      this(text, primary, false);
    }

    public PanelLabel(final String text, final Command primary, final boolean html) {
      this(html ? new UnsunkLabel(text, true) : new UnsunkLabel(text), primary);
    }

    public PanelLabel(final Widget widget, final Command primary) {
      this.primary = primary;
      this.widget = widget;

      this.add(widget);
      this.addStyleName("PanelLabel");

      if(primary != null) {
        this.sinkEvents(Event.ONCLICK);
        this.addStyleName("hasCommand");
      }
    }

    public String getText() {
      if(this.widget instanceof HasText) {
        return ((HasText) this.widget).getText();
      } else {
        return this.widget.toString();
      }
    }

    public void onBrowserEvent(final Event e) {
      switch(DOM.eventGetType(e)) {
      case Event.ONCLICK:
        this.primary.execute();
      }
    }

    public void setBusy(final boolean busy) {
      if(busy) {
        this.addStyleName("busy");
      } else {
        this.removeStyleName("busy");
      }
    }

    public void setText(final String text) {
      if(this.widget instanceof HasText) {
        ((HasText) this.widget).setText(text);
      }
    }
  }

  public static class UnsunkLabel extends Widget implements HasText, HasHTML {

    public UnsunkLabel() {
      this.setElement(DOM.createDiv());
    }

    public UnsunkLabel(final String contents) {
      this();
      this.setText(contents);
    }

    public UnsunkLabel(final String contents, final boolean asHTML) {
      this();
      if(asHTML) {
        this.setHTML(contents);
      } else {
        this.setText(contents);
      }
    }

    public String getHTML() {
      return DOM.getInnerHTML(this.getElement());
    }

    public String getText() {
      return DOM.getInnerText(this.getElement());
    }

    public void setHTML(final String html) {
      DOM.setInnerHTML(this.getElement(), html);
    }

    public void setText(final String text) {
      DOM.setInnerText(this.getElement(), text);
    }
  }

  public static class UnsunkImage extends Widget {
    public UnsunkImage() {
      this.setElement(DOM.createImg());
    }

    public UnsunkImage(final String src) {
      this();
      this.setUrl(src);
    }

    public void setUrl(final String src) {
      DOM.setElementProperty(this.getElement(), "src", src);
    }
  }
}
