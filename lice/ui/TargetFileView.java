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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.StyledDocument;

import lice.objects.TargetFile;

/**
 * @author timot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TargetFileView extends JPanel implements ActionListener {

	/**
	 * 
	 */
	public TargetFileView( MainView mv ) {

		this.setLayout( new BorderLayout() );
		mainView = mv;
        this.setBorder( BorderFactory.createLineBorder(Color.black) );

        JPanel topTop = new JPanel();
        topTop.setLayout( new BoxLayout( topTop, BoxLayout.Y_AXIS) );
        topTop.setBorder(BorderFactory.createTitledBorder("File information"));
        
        JLabel licenseStatusLabel = new JLabel("License status:");
        Dimension dim = licenseStatusLabel.getPreferredSize();
        dim.setSize( dim.getWidth() + 10, dim.getHeight() );
        
        JPanel p1 = new JPanel();
        p1.setLayout( new BoxLayout( p1, BoxLayout.X_AXIS) );
        JLabel fileNameLabel = new JLabel("File name:");
        fileNameLabel.setPreferredSize( dim );
        p1.add( fileNameLabel );
        fileName = new JTextField("File name ", 50);
        p1.add(fileName);
        
        JPanel p2 = new JPanel();
        p2.setLayout( new BoxLayout( p2, BoxLayout.X_AXIS) );
        JLabel fileTypeLabel = new JLabel("File type:");
        fileTypeLabel.setPreferredSize( dim );
        p2.add( fileTypeLabel );
        fileType = new JTextField("File type ", 50);
        p2.add( fileType );
        
        JPanel p3 = new JPanel();
        p3.setLayout( new BoxLayout( p3, BoxLayout.X_AXIS) );
        JLabel licenseLabel = new JLabel("License:");
        licenseLabel.setPreferredSize( dim );
        p3.add( licenseLabel );
        license = new JTextField("License ", 50);
        p3.add(license);
        
        JPanel p4 = new JPanel();
        p4.setLayout( new BoxLayout( p4, BoxLayout.X_AXIS) );
        licenseStatusLabel.setPreferredSize( dim );
        p4.add( licenseStatusLabel );
        licenseStatus = new JTextField("License status ", 50);
        p4.add( licenseStatus );
        
        JPanel p5 = new JPanel();
        p5.setLayout( new BoxLayout( p5, BoxLayout.X_AXIS) );
        JLabel childLabel = new JLabel("Child licenses:"); 
        childLabel.setPreferredSize( dim );
        p5.add( childLabel );
        childLicenses = new JTextField("Child licenses ", 50);
        p5.add(childLicenses);

        
        topTop.add( p1 );
        topTop.add( p2 );
        topTop.add( p3 );
        topTop.add( p4 ); 
        topTop.add( p5 );

//        JLabel licenseLabel = new JLabel("License:");
//        topTop.add(licenseLabel, BorderLayout.NORTH );

//		JPanel topHalf = new JPanel( new BorderLayout() );
//		topHalf.add( topTop, BorderLayout.CENTER );
//		this.add( topHalf, BorderLayout.NORTH );
		this.add( topTop, BorderLayout.NORTH );

		fileText = new JTextPane();
		fileText.setEditable(false);
		
        JScrollPane scrollPane = new JScrollPane(fileText,
                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add( scrollPane, BorderLayout.CENTER);
        
        popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Use text as a license template");
        menuItem.addActionListener(this);
        popup.add(menuItem);

        //Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        fileText.addMouseListener(popupListener);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
    }
	
    public void actionPerformed(ActionEvent e) {
    	
    	JMenuItem source = (JMenuItem)(e.getSource());
        if ( source.getText().equalsIgnoreCase("Use text as a license template") ) {
        	LicenseTemplatePanel panel = new LicenseTemplatePanel( licenseTextTemplate, mainView );
        }
    }
    
    
	public void updateInfo( TargetFile file ) {
//		model.addModel( file.getDependencies() );
		fileName.setText( file.getFileName() );
		fileType.setText( file.getFileType() );
		license.setText( file.getLicense().getLicenseName() );
		licenseStatus.setText( file.getLicenseStatus() );
		childLicenses.setText( file.getChildLicensesString().toString() );
				
//		fileText.setText( file.getFileContents() );

		StyledDocument doc = file.getFileContents();
		fileText.setStyledDocument( doc );
		fileText.setCaretPosition( 0 );
	}

	class PopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	            popup.show(e.getComponent(),
	                       e.getX(), e.getY());
	            if ( e.getButton() == 3 ) {
	    			String text = fileText.getSelectedText();
	    			if ( text != null ) {
	    				licenseTextTemplate = text.trim();
	    			}
	    		}
	        }
	    }	

	}
	
	private JTextField fileName = null;
	private JTextField license = null;
	private JTextField childLicenses = null;
	private JTextField fileType = null;
	private JTextField licenseStatus = null;
	private JTextPane fileText = null;
	private JPopupMenu popup = null;
	private String licenseTextTemplate = "";
	private MainView mainView = null;
	//	private int selectedRow = -1;
//	private JTable table = null;
//    private FileViewDependenciesModel model = null;	
}
