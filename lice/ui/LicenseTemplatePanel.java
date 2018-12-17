/*
Copyright (C) 2006 Timo Tuunanen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package lice.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import lice.licenses.LicenseChecker;
import lice.licenses.LicenseTemplate;

/**
 * @author timtuun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LicenseTemplatePanel extends JPanel implements ActionListener {

	public LicenseTemplatePanel( String template, MainView mv ) {
		super(new BorderLayout());	

		mainView = mv;
		
        template = template.replaceAll("/", "");
        template = template.replaceAll("\\*", "");
        String lines [] = template.split("\n");
        StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < lines.length; i++) {
			lines[i] = lines[i].trim();
			buf.append( lines[i] + "\n" );
		}

        licenseText = new JTextArea( buf.toString() );
        licenseText.setBorder(BorderFactory.createLoweredBevelBorder() );
        JScrollPane scrollPane = new JScrollPane(licenseText,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder( BorderFactory.createTitledBorder( "License Template Text") );
        add(scrollPane, BorderLayout.NORTH );
        
        JPanel centerPanel = new JPanel( new BorderLayout() );
        centerPanel.setBorder( BorderFactory.createTitledBorder( "License Name") );
        
        String [] licenseNames = LicenseChecker.getLicenseNames();
        licenses = new JComboBox( licenseNames );

        JRadioButton existingLicenseButton = new JRadioButton("Use existing license name");
        existingLicenseButton.setActionCommand("Use existing license");
        existingLicenseButton.setSelected( true );
        existingLicenseButton.addActionListener( this);
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(existingLicenseButton);        

        JPanel existingLicensePanel = new JPanel();
        existingLicensePanel.setLayout( new BoxLayout( existingLicensePanel, BoxLayout.X_AXIS ) );
        existingLicensePanel.add( existingLicenseButton );
        existingLicensePanel.add( Box.createRigidArea(new Dimension(10,0)) );
        existingLicensePanel.add( licenses );
        //existingLicensePanel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
		
        JPanel newLicensePanel = new JPanel();
        newLicensePanel.setLayout( new BoxLayout( newLicensePanel, BoxLayout.X_AXIS ) );

        JRadioButton newLicenseButton = new JRadioButton("New license");
        newLicenseButton.setActionCommand("New license");
        newLicenseButton.addActionListener( this);
        
        group.add(newLicenseButton);        
        //newLicensePanel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        
        licenseName = new JTextField("LICENSE_NAME");
		licenseName.setEditable( false );
		licenseName.setEnabled( false );
        
        newLicensePanel.add(newLicenseButton);
        newLicensePanel.add( Box.createRigidArea(new Dimension(98 ,0) ) );
        newLicensePanel.add(licenseName);

        
        centerPanel.add(existingLicensePanel, BorderLayout.NORTH );
        centerPanel.add(newLicensePanel, BorderLayout.SOUTH);
        //centerPanel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        
        add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel( new BorderLayout() );
        JPanel doAnalyzePanel = new JPanel( new BorderLayout() );
        doAnalyzePanel.setBorder( BorderFactory.createTitledBorder( "License Analysis") );
        doAnalyzeButton = new JRadioButton("Analyze package with this template");
        doAnalyzeButton.setActionCommand("Analyze package with this template");
        doAnalyzeButton.setSelected( true );
        doAnalyzePanel.add( doAnalyzeButton, BorderLayout.WEST );
        southPanel.add( doAnalyzePanel, BorderLayout.NORTH );

        JPanel saveForFuturePanel = new JPanel( new BorderLayout() );
        saveForFuturePanel.setBorder( BorderFactory.createTitledBorder( "Save template") );
        JRadioButton saveFutureButton = new JRadioButton("Save template for future use");
        saveFutureButton.setActionCommand("Save template for future use");
        saveFutureButton.setSelected( false );
        saveForFuturePanel.add( saveFutureButton, BorderLayout.WEST );
        southPanel.add( saveForFuturePanel, BorderLayout.CENTER );
        
        
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		frame.hide();
	    	}
	    });

        okButton = new JButton("Ok");
        okButton.setActionCommand("Ok");
        okButton.addActionListener(this);
        okButton.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		String text = licenseText.getText();
	    		text = text.replaceAll("\\.", " ");
	    		text = text.replaceAll("\\s", "\\\\s*");
	    		text = text.replaceAll("\\(", "\\\\(");
	    		text = text.replaceAll("\\)", "\\\\)");
	    		System.out.println( text );
	            //System.out.println(text);
	            String licenseNameString = "";
	            if ( existingLicenseSelected ) {
	            	licenseNameString = (licenses.getSelectedItem()).toString();
	            } else {
	            	licenseNameString = licenseName.getText();
	            }
	    		
	            if ( doAnalyzeButton.isSelected() ) {
	            	frame.hide();
	            
					LicenseTemplate l = new LicenseTemplate( licenseNameString );
					l.setLicenseTemplate( text );
					AnalyzePanel aPanel = 
						new AnalyzePanel( mainView, false, mainView.getAnalyzeDependencies(), l );
	            }
			}
	    });

        JPanel okCancelPanel = new JPanel();
        okCancelPanel.add(okButton);
        okCancelPanel.add(cancelButton);
        southPanel.add( okCancelPanel, BorderLayout.SOUTH );
        add(southPanel, BorderLayout.SOUTH);
        
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
    	//Create and set up the window.
        frame = new JFrame("New license template");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = this;
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		if ( command.equals("Use existing license") ) {
			licenses.setEnabled( true );
			licenseName.setEditable( false );
			licenseName.setEnabled( false );
			licenseName.select( 0, 0 );
			existingLicenseSelected = true;
		} else if ( command.equals("New license") ) {
			licenses.setEnabled( false );
			licenseName.setEnabled( true );
			licenseName.setEditable( true );			
			licenseName.requestFocus();
			licenseName.selectAll();
			existingLicenseSelected = false;
		}
	}	

	private MainView mainView = null;
	private boolean existingLicenseSelected = true;
	private JComboBox licenses = null;
	private JTextField licenseName = null; 
    private JFrame frame;
	private JTextArea licenseText = null;
	private String template;
	private JButton cancelButton;	
    private JButton okButton;
    private JRadioButton doAnalyzeButton;
}
