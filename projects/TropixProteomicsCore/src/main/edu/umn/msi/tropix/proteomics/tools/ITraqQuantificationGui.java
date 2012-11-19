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

import static edu.umn.msi.tropix.proteomics.tools.ITraqQuantificationGuiHelpers.getFrame;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.xml.AxisSerializationUtilsFactory;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.QuantitationOptionsBuilder;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.InputReport;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportParser.ReportType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;
import edu.umn.msi.tropix.proteomics.tools.ITraqQuantificationGuiHelpers.FileChooserButton;

// TODO: Validate weights file
public class ITraqQuantificationGui {

  public static class GuiRunnable implements Runnable {
    private String type;
    private boolean useTraining = false;
    private FileChooserButton mzxmlChooser, xlsChooser, trainingChooser, outputChooser;
    private JButton executeButton;
    private JFrame frame;

    private void validate() {
      boolean validated = true;
      if(mzxmlChooser.getFiles().isEmpty() || xlsChooser.getFiles().isEmpty() || outputChooser.getFiles().isEmpty()) {
        validated = false;
      }
      if(useTraining && trainingChooser.getFiles().isEmpty()) {
        validated = false;
      }
      executeButton.setEnabled(validated);
    }

    private final Closure<Void> validater = new Closure<Void>() {
      public void apply(final Void input) {
        validate();
      }
    };

    public GuiRunnable(final boolean useTraining) {
      this.useTraining = useTraining;
    }

    private void updateType(final String type) {
      this.type = type;
    }

    public void run() {
      final JPanel listPane = new JPanel();

      listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
      listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
      listPane.setAlignmentX(Component.LEFT_ALIGNMENT);

      final ActionListener typeListener = new ActionListener() {
        public void actionPerformed(final ActionEvent event) {
          updateType(event.getActionCommand());
        }
      };
      final JPanel typePanel = ITraqQuantificationGuiHelpers.getTypePanel(typeListener);

      final JPanel inputPanel = new JPanel();
      inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
      inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      inputPanel.add(typePanel);
      mzxmlChooser = ITraqQuantificationGuiHelpers.getMzxmlChooser(inputPanel, validater);
      inputPanel.add(mzxmlChooser.getPanel());
      xlsChooser = ITraqQuantificationGuiHelpers.getScaffoldReportChooser(inputPanel, validater);
      inputPanel.add(xlsChooser.getPanel());

      inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
      listPane.add(inputPanel);

      if(useTraining) {
        final JPanel trainingPanel = new JPanel();
        trainingPanel.setLayout(new BoxLayout(trainingPanel, BoxLayout.PAGE_AXIS));
        trainingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        trainingChooser = new FileChooserButton(trainingPanel, false, true, "Training File:", validater);
        trainingPanel.add(trainingChooser.getPanel());
        trainingPanel.setBorder(BorderFactory.createTitledBorder("Training"));

        listPane.add(trainingPanel);
      }

      final JPanel outputPanel = new JPanel();
      outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));
      outputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      outputChooser = ITraqQuantificationGuiHelpers.getOutputChooser(outputPanel, validater, "xls", "Quantification Results File (.xsl)");
      outputPanel.add(outputChooser.getPanel());

      outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
      listPane.add(outputPanel);

      final JPanel buttonPane = ITraqQuantificationGuiHelpers.getButtonPane();
      executeButton = new JButton("Execute");
      executeButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent event) {
          final List<File> scanFiles = mzxmlChooser.getFiles();
          final File reportFile = Iterables.getOnlyElement(xlsChooser.getFiles());
          final File outputFile = Iterables.getOnlyElement(outputChooser.getFiles());
          final QuantificationType quantificationType = type.equals("4") ? QuantificationType.FOUR_PLEX : QuantificationType.EIGHT_PLEX;
          final QuantitationOptionsBuilder optionBuilder = QuantitationOptions
              .forInput(scanFiles, new InputReport(reportFile, ReportType.SCAFFOLD)).withOutput(outputFile).ofType(quantificationType);

          final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
              "edu/umn/msi/tropix/proteomics/itraqquantitation/applicationContext.xml");
          if(useTraining) {
            final File weightFile = Iterables.getOnlyElement(trainingChooser.getFiles());
            optionBuilder.withWeights(AxisSerializationUtilsFactory.getInstance().deserialize(weightFile, QuantificationWeights.class));
          }

          final QuantitationOptions options = optionBuilder.get();
          @SuppressWarnings("unchecked")
          final Closure<QuantitationOptions> closure = (Closure<QuantitationOptions>) context.getBean("quantitationClosure");
          try {
            System.out.println("Executing quantiation with options " + options);
            closure.apply(options);
            JOptionPane.showMessageDialog(frame, "File " + outputFile.getPath() + " saved.");
          } catch(final RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error running analysis - " + e.getMessage());
          }
        }
      });
      buttonPane.add(executeButton);
      listPane.add(buttonPane);

      updateType("4");
      frame = getFrame("New Quantification", listPane);
      validate();
    }
  };

  public static void main(final String[] args) {
    SwingUtilities.invokeLater(new GuiRunnable(false));
  }

}
