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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.InputReport;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportParser.ReportType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.training.QuantificationTrainingOptions;
import edu.umn.msi.tropix.proteomics.tools.ITraqQuantificationGuiHelpers.FileChooserButton;

// TODO: Fix window size on type switch.
public class ITraqQuantificationTrainingGui {

  private static final Map<Integer, String> RATIO_LABEL_MAP = ImmutableMap.<Integer, String>builder().put(0, "i113").put(1, "i114").put(2, "i115")
      .put(3, "i116").put(4, "i117").put(5, "i118").put(6, "i119").put(7, "i121").build();

  public static final Runnable GUI_RUNNABLE = new Runnable() {
    private String type;
    private int numBins = 100;
    private final List<JTextField> ratioFields = Lists.newArrayListWithExpectedSize(8);
    private final List<JPanel> ratioPanels = Lists.newArrayListWithExpectedSize(8);
    private final JPanel ratioPanel = new JPanel();
    private FileChooserButton mzxmlChooser, xlsChooser, outputChooser;
    private JButton executeButton;
    private JFrame frame;

    private void validate() {
      boolean validated = true;
      if(mzxmlChooser.getFiles().isEmpty() || xlsChooser.getFiles().isEmpty() || outputChooser.getFiles().isEmpty()) {
        validated = false;
      }
      executeButton.setEnabled(validated);
    }

    private final Closure<Void> validater = new Closure<Void>() {
      public void apply(final Void input) {
        validate();
      }
    };

    JTextField getNumBinsField() {
      @SuppressWarnings("serial")
      final JTextField numBinsField = new JTextField(15) {
        public Dimension getMaximumSize() {
          return getPreferredSize();
        }
      };
      numBinsField.setText(Integer.toString(numBins));
      numBinsField.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent event) {
          numBins = Integer.parseInt(numBinsField.getText());
        }
      });
      return numBinsField;
    }

    public void updateRatios(final int numRatios) {
      getRatioPanel().removeAll();
      final GridLayout ratioLayout = new GridLayout(0, numRatios / 4);
      getRatioPanel().setLayout(ratioLayout);
      final int start = numRatios == 4 ? 1 : 0;
      for(int i = 0, index = start; i < numRatios; i++, index++) {
        final JPanel ratioLinePanel = ratioPanels.get(index);
        getRatioPanel().add(ratioLinePanel);
      }
      getRatioPanel().revalidate();
    }

    JPanel initRatios() {
      getRatioPanel().setLayout(new BoxLayout(getRatioPanel(), BoxLayout.PAGE_AXIS));
      final JLabel label = new JLabel("Expected Ratio:");
      label.setAlignmentX(Component.LEFT_ALIGNMENT);
      getRatioPanel().add(label);

      for(int i = 0; i < 8; i++) {
        final JPanel ratioLinePanel = new JPanel();
        ratioLinePanel.setLayout(new BoxLayout(ratioLinePanel, BoxLayout.LINE_AXIS));
        @SuppressWarnings("serial")
        final JTextField field = new JTextField(5) {
          public Dimension getMaximumSize() {
            return getPreferredSize();
          }
        };
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        final JLabel ratioLabelPanel = new JLabel(RATIO_LABEL_MAP.get(i) + "  ");
        ratioLinePanel.add(ratioLabelPanel);
        ratioLinePanel.add(field);
        ratioPanels.add(ratioLinePanel);
        ratioFields.add(field);
      }

      return getRatioPanel();
    }

    private double[] getRatios() {
      final int numLabels = Integer.parseInt(type);
      final double[] ratios = new double[numLabels];
      final int start = numLabels == 4 ? 1 : 0;
      for(int i = 0, index = start; i < numLabels; i++, index++) {
        ratios[i] = Double.parseDouble(ratioFields.get(index).getText().trim());
      }
      return ratios;
    }

    private void updateType(final String type) {
      this.type = type;
      final int numLabels = Integer.parseInt(type);
      updateRatios(numLabels);
    }

    public void run() {
      // JFrame.setDefaultLookAndFeelDecorated(true);
      // JDialog.setDefaultLookAndFeelDecorated(true);
      // frame.setSize(420, 450);

      final JPanel listPane = new JPanel();
      listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
      listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
      listPane.setAlignmentX(Component.LEFT_ALIGNMENT);

      final ActionListener typeListener = new ActionListener() {
        public void actionPerformed(final ActionEvent event) {
          updateType(event.getActionCommand());
        }
      };
      final JPanel typePanel = ITraqQuantificationGuiHelpers.getTypePanel(typeListener);

      final JPanel numBinsPanel = new JPanel();
      numBinsPanel.setLayout(new BoxLayout(numBinsPanel, BoxLayout.LINE_AXIS));
      numBinsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      // numBinsPanel.add(new JLabel("Number of Bins For Training:"));
      numBinsPanel.add(new JLabel("Bin Size:"));
      numBinsPanel.add(getNumBinsField());

      final JPanel inputPanel = new JPanel();
      inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
      inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      inputPanel.add(typePanel);
      inputPanel.add(numBinsPanel);

      final JPanel ratioPanel = initRatios();
      ratioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      inputPanel.add(ratioPanel);
      inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      mzxmlChooser = ITraqQuantificationGuiHelpers.getMzxmlChooser(inputPanel, validater);
      inputPanel.add(mzxmlChooser.getPanel());
      xlsChooser = ITraqQuantificationGuiHelpers.getScaffoldReportChooser(inputPanel, validater);
      inputPanel.add(xlsChooser.getPanel());

      inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
      listPane.add(inputPanel);

      final JPanel outputPanel = new JPanel();
      outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));
      outputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      outputChooser = ITraqQuantificationGuiHelpers.getOutputChooser(outputPanel, validater, "xml", "Quantification Training File (.xml)");
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

          final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
              "edu/umn/msi/tropix/proteomics/itraqquantitation/applicationContext.xml");
          @SuppressWarnings("unchecked")
          final Closure<QuantitationTrainingOptions> trainingClosure = (Closure<QuantitationTrainingOptions>) context
              .getBean("quantitationTrainingClosure");

          final List<File> trainingScanFiles = scanFiles;
          final File trainingReportFile = reportFile;
          final double[] ratios = getRatios();

          final QuantificationTrainingOptions trainingOptions = new QuantificationTrainingOptions();
          if(type.equals("4")) {
            trainingOptions.setI114Proportion(ratios[0]);
            trainingOptions.setI115Proportion(ratios[1]);
            trainingOptions.setI116Proportion(ratios[2]);
            trainingOptions.setI117Proportion(ratios[3]);
          } else {
            trainingOptions.setI113Proportion(ratios[0]);
            trainingOptions.setI114Proportion(ratios[1]);
            trainingOptions.setI115Proportion(ratios[2]);
            trainingOptions.setI116Proportion(ratios[3]);
            trainingOptions.setI117Proportion(ratios[4]);
            trainingOptions.setI118Proportion(ratios[5]);
            trainingOptions.setI119Proportion(ratios[6]);
            trainingOptions.setI121Proportion(ratios[7]);
          }
          trainingOptions.setNumBins(numBins);
          final QuantitationTrainingOptions options = QuantitationTrainingOptions
              .forInput(trainingScanFiles, new InputReport(reportFile, ReportType.SCAFFOLD)).withOutput(outputFile)
              .withTrainingOptions(trainingOptions).ofType(quantificationType).get();
          try {
            System.out.println("Executing quantiation with options " + options);
            trainingClosure.apply(options);
            JOptionPane.showMessageDialog(frame, "File " + outputFile.getPath() + " saved.");
          } catch(final RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error running training - " + e.getMessage());
          }
        }
      });
      buttonPane.add(executeButton);
      listPane.add(buttonPane);

      updateType("4");

      frame = getFrame("New Quantification Training", listPane);
      validate();
    }

    public JPanel getRatioPanel() {
      return ratioPanel;
    }
  };

  public static void main(final String[] args) {
    SwingUtilities.invokeLater(GUI_RUNNABLE);
  }
}
