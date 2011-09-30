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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;

public class LtqIQuantGui {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  public static final Runnable GUI_RUNNABLE = new Runnable() {

    private void launchQuantification(final boolean trained) {
      SwingUtilities.invokeLater(new ITraqQuantificationGui.GuiRunnable(trained));
    }

    private void launchTraining() {
      SwingUtilities.invokeLater(ITraqQuantificationTrainingGui.GUI_RUNNABLE);
    }

    private void launchAbout() {
      final JFrame frame = new JFrame("LTQ-iQuant");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      final JPanel mainPane = new JPanel();
      mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));
      mainPane.add(new JLabel("<html><center><h1>LTQ-iQuant</h1></center><p>Developed by Getiria Onsongo and Susan Van Riper</p><p>Java implementation by John Chilton</p><p>Powered by ProTIP.</html>"));
      mainPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      final JScrollPane contentPane = new JScrollPane(mainPane);
      frame.setContentPane(contentPane);
      frame.pack();
      frame.setVisible(true);
    }

    public void run() {
      final JFrame frame = new JFrame("LTQ-iQuant");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      final JPanel mainPane = new JPanel();
      final JLabel content = new JLabel(IO_UTILS.toString(getClass().getResourceAsStream("ltq-iquant-main.html")));
      mainPane.add(content);

      final JMenuBar menuBar = new JMenuBar();
      final JMenu fileMenu = new JMenu("File"), helpMenu = new JMenu("Help");

      final JMenuItem newQuantItem = new JMenuItem("New Untrianed Quantification", KeyEvent.VK_U);
      newQuantItem.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent event) {
          launchQuantification(false);
        }
      });
      final JMenuItem newTrainedQuanttItem = new JMenuItem("New Trained Quantification", KeyEvent.VK_Q);
      newTrainedQuanttItem.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent event) {
          launchQuantification(true);
        }
      });

      final JMenuItem newTrainingItem = new JMenuItem("New Training", KeyEvent.VK_T);
      newTrainingItem.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent event) {
          launchTraining();
        }
      });

      final JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);

      fileMenu.add(newQuantItem);
      fileMenu.add(newTrainedQuanttItem);
      fileMenu.addSeparator();
      fileMenu.add(newTrainingItem);
      fileMenu.addSeparator();
      fileMenu.add(exitItem);
      exitItem.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent event) {
          System.exit(0);
        }
      });

      final JMenuItem aboutItem = new JMenuItem("About", KeyEvent.VK_A);
      aboutItem.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent arg0) {
          launchAbout();
        }
      });
      helpMenu.add(aboutItem);

      menuBar.add(fileMenu);
      menuBar.add(helpMenu);

      final JScrollPane contentPane = new JScrollPane(mainPane);
      frame.setJMenuBar(menuBar);
      frame.setContentPane(contentPane);
      frame.setVisible(true);
      frame.pack();
      frame.setResizable(false);
    }

  };

  public static void main(final String[] args) {
    SwingUtilities.invokeLater(GUI_RUNNABLE);
  }
}
