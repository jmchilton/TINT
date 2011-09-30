/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.proteomics.tools;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;

class ITraqQuantificationGuiHelpers {

  static JFrame getFrame(final String name, final JPanel contentPane) {
    final JFrame frame = new JFrame(name);
    final JScrollPane scrollPane = new JScrollPane(contentPane);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setContentPane(scrollPane);
    frame.setVisible(true);
    frame.pack();
    frame.setResizable(false);
    return frame;
  }

  static JPanel getButtonPane() {
    final JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    buttonPane.add(Box.createHorizontalGlue());
    buttonPane.setAlignmentX(Component.LEFT_ALIGNMENT);
    return buttonPane;
  }

  static class FileChooserButton implements ActionListener {
    private final JPanel panel;
    private final JButton button;
    private final JTextField selectionBox;
    private final JLabel filesLabel;
    private final JFileChooser fc;
    private final Component parent;
    private final boolean multipleSelect;
    private final boolean open;
    private final Closure<?> validater; 
    private List<File> files = Lists.newArrayList();

    private String emptyLabel() {
      return "<No File" + (multipleSelect ? "s" : "") + " Selected>";
    }

    FileChooserButton(final Component parent, final boolean multipleSelect, final boolean open, final String title, final Closure<?> validater) {
      this(parent, multipleSelect, open, title, validater, null, null);
    }

    FileChooserButton(final Component parent, final boolean multipleSelect, final boolean open, final String title, final Closure<?> validater, @Nullable final String extension, @Nullable final String description, final File[] initialFiles) {
      this.validater = validater;
      this.multipleSelect = multipleSelect;
      this.open = open;

      button = new JButton("Select...");
      button.setAlignmentX(Component.LEFT_ALIGNMENT);

      selectionBox = new JTextField(emptyLabel());
      selectionBox.setAlignmentX(Component.LEFT_ALIGNMENT);

      fc = new JFileChooser();
      if(extension != null) {
        fc.setFileFilter(new FileFilter() {
          public boolean accept(final File file) {
            return file.isDirectory() || file.getName().toLowerCase().matches(".*" + extension.toLowerCase());
          }

          public String getDescription() {
            return description;
          }
        });
      }
      if(initialFiles != null) {
        getFiles().addAll(Arrays.asList(initialFiles));
        fc.setSelectedFiles(initialFiles);
        updateSelectionBox();
      }
      fc.setMultiSelectionEnabled(multipleSelect);

      filesLabel = new JLabel("Selection");
      filesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      this.parent = parent;
      button.addActionListener(this);

      panel = new JPanel();
      getPanel().setLayout(new BoxLayout(getPanel(), BoxLayout.PAGE_AXIS));
      getPanel().setAlignmentX(Component.LEFT_ALIGNMENT);

      final JLabel titleLabel = new JLabel(title);
      titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      final JPanel selectionPanel = new JPanel();
      selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.LINE_AXIS));
      selectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      selectionPanel.add(filesLabel);
      selectionPanel.add(selectionBox);
      selectionPanel.add(button);
      getPanel().add(titleLabel);
      getPanel().add(selectionPanel);
    }

    FileChooserButton(final Component parent, final boolean multipleSelect, final boolean open, final String title, final Closure<?> validater, @Nullable final String extension, @Nullable final String description) {
      this(parent, multipleSelect, open, title, validater, extension, description, null);
    }

    private int getDialogResult() {
      int retValue;
      if(open) {
        retValue = fc.showOpenDialog(parent);
      } else {
        retValue = fc.showSaveDialog(parent);
        if(fc.getSelectedFile().exists()) {
          final int confirm = JOptionPane.showConfirmDialog(parent, "A file with this name already exists, are you sure you wish to over write this file?", "File Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
          if(confirm == JOptionPane.NO_OPTION) {
            return getDialogResult();
          }
        }
      }
      return retValue;
    }

    private void updateSelectionBox() {
      if(getFiles().size() > 1) {
        selectionBox.setText(Joiner.on(",").join(getFiles()));
      } else if(getFiles().isEmpty()) {
        selectionBox.setText(emptyLabel());
      } else {
        selectionBox.setText(Iterables.getOnlyElement(getFiles()).toString());
      }
    }

    public void actionPerformed(final ActionEvent arg0) {
      final int retValue = getDialogResult();
      if(retValue == JFileChooser.APPROVE_OPTION) {
        if(multipleSelect) {
          setFiles(Arrays.asList(fc.getSelectedFiles()));
        } else {
          setFiles(Lists.newArrayList(fc.getSelectedFile()));
        }
      } else {
        setFiles(Lists.<File>newArrayList());
      }
      updateSelectionBox();
      validater.apply(null);
    }

    public JPanel getPanel() {
      return panel;
    }

    public void setFiles(final List<File> files) {
      this.files = files;
    }

    public List<File> getFiles() {
      return files;
    }
  }

  static FileChooserButton getMzxmlChooser(final JPanel parent, final Closure<?> validater) {
    return new FileChooserButton(parent, true, true, "MzXML Files(s):", validater, "mzxml", "MzXML File(s) (.mzXML)");
  }

  static FileChooserButton getScaffoldReportChooser(final JPanel parent, final Closure<?> validater) {
    return new FileChooserButton(parent, true, true, "Scaffold Report File:", validater, "xls", "Scaffold Spectrum Report File (.xls)");
  }

  static FileChooserButton getOutputChooser(final JPanel parent, final Closure<?> validater, final String extension, final String description) {
    File initialFile = new File("output." + extension);
    int i = 0;
    while(initialFile.exists()) {
      i++;
      initialFile = new File("output (" + i + ")." + extension);
    }
    initialFile = initialFile.getAbsoluteFile();
    return new FileChooserButton(parent, false, false, "Results File:", validater, extension, description, new File[] {initialFile});
  }

  static JPanel getTypePanel(final ActionListener typeListener) {
    final JRadioButton fourPlexButton = new JRadioButton("4-plex");
    fourPlexButton.setMnemonic(KeyEvent.VK_4);
    fourPlexButton.setSelected(true);
    fourPlexButton.setActionCommand("4");

    final JRadioButton eightPlexButton = new JRadioButton("8-plex");
    eightPlexButton.setMnemonic(KeyEvent.VK_8);
    eightPlexButton.setActionCommand("8");

    final ButtonGroup typeGroup = new ButtonGroup();
    typeGroup.add(fourPlexButton);
    typeGroup.add(eightPlexButton);

    final JPanel typePanel = new JPanel();

    typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.LINE_AXIS));
    typePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    typePanel.add(new JLabel("Label Type:"));
    typePanel.add(fourPlexButton);
    typePanel.add(eightPlexButton);

    fourPlexButton.addActionListener(typeListener);
    eightPlexButton.addActionListener(typeListener);

    return typePanel;
  }

}
