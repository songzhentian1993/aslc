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
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import lice.common.CommonFuctions;
import lice.common.DebugHelper;
import lice.common.StringFuncs;
import lice.file.ArchitectureWriter;
import lice.licenses.LicenseChecker;
import lice.licenses.LicenseOfAFile;
import lice.objects.DependencyObjects;
import lice.objects.TargetFile;

/**
 * @author timot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MainView extends JFrame implements ActionListener//, ComponentListener
{
    /**
	 * 
	 */
	public MainView() {
		super("ASLA");
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
	    //JPanel contentPane = new JPanel( new BorderLayout() );
		fileView = new TargetFileView( this ); 
		fileView.setMinimumSize( new Dimension(200, 300) );
	    //contentPane.add( f, BorderLayout.CENTER );
	    
	    treeView = new TreeView( fileView );
	    
	    JSplitPane split = 
    	new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, treeView, fileView );
	    
	    //split.setDividerLocation( 300 );
	    getContentPane().add(split, BorderLayout.CENTER);
	    
	    createMenuBar();
	    
	    //Display the window.
//	    setPreferredSize( new Dimension( 1200, 900 ));
//	    setSize( 1200, 900 );
	    pack();
        setVisible(true);
        
	}
	public void windowClosing(WindowEvent e)
    {
       System.exit(0);
    }

	public void updateTree() {
	    treeView.addObjectsToTree( analyzeDependencies );
	}
	
	/*public void reanalyzeObjects( LicenseTemplate newLicense, boolean recheckAll ) {
		TreeMap objects = aPanel.getReader().getAllObjects();
		LicenseChecker.checkFilesUsingLicense( objects, newLicense, analyzeDependencies, recheckAll );
		treeView.addObjectsToTree( objects, analyzeDependencies );
	}*/
	
	public void actionPerformed(ActionEvent ev) {
	    JMenuItem menuItem = (JMenuItem) ev.getSource();
	    String command = menuItem.getActionCommand();
	    System.out.println("Command: " + command);
	    
	    if ( command.equals("Analyze package" ) ) {
	    	System.out.println( "Analyze package");
		    fileChooser = new JFileChooser();
		    fileChooser = new JFileChooser( "/space/timtuun/" );
		    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Analyzing package: " + file.getPath() );
                analysisPath = file.getPath();
                treeView.setBasePath( file.getPath() );
                analyzeDependencies = true;
    		    aPanel = new AnalyzePanel( this, file.getPath(), true );
            } else {
            	System.out.println("Open command cancelled by user.");
            }
	    }
	    else if ( command.equals("Analyze only licenses of a package" ) ) {
	    	System.out.println( "Analyze licenses");

		    fileChooser = new JFileChooser( "/space/timtuun/" );
		    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    	int returnVal = fileChooser.showOpenDialog(this);
	    		    		    	
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Analyzing licenses: " + file.getPath() );
                analysisPath = file.getPath();
                treeView.setBasePath( file.getPath() );
                analyzeDependencies = false;
    		    aPanel = new AnalyzePanel( this, file.getPath(), false );
            } else {
            	System.out.println("Open command cancelled by user.");
            }
	    }
	    else if ( command.equals("Print statistics" ) ) {
	    	System.out.println("Print statistics");
	    	StringBuffer str = makeAnalysis();
	    	LicenseChecker.getLicenseSummary( str );
	    }
	    else if ( command.equals("Print debug statistics" ) ) {
	    	System.out.println("Print debug statistics");
	    	writeDebug();
	    }
	    else if ( command.equals("Exit") ) {
	    	System.exit(0);
	    }
	    else if ( command.equals("Re-Analyze licenses") ) {
	    	System.out.println("Analyze licenses");
	    	LicenseChecker.checkLicenseStatuses( );
	    	//treeView
	    }
	    else if ( command.equals( "Display architecture") ) {
//	    	ArchitectureWriter writer = new ArchitectureWriter( treeView.getBasePath() );
//	    	writer.writeArchitecture( "kukka" );
	    	ArchitectureView arcg = new ArchitectureView(); 
	    } 
	    else if ( command.equals( "License rules") ) {
	    	System.out.println("Rules");
	    	rulesView = new RulesView();
	    }
	    
	}

	
	  /**
	   *
	   */
	private void createMenuBar () {
	  	JMenuBar menuBar = new JMenuBar();
	    setJMenuBar( menuBar );

	    // Build file menu
	    JMenu fileMenu = new JMenu("File");
	    fileMenu.setMnemonic(KeyEvent.VK_F);
	    menuBar.add( fileMenu );	    
	    
	    fileMenu.addSeparator();

	    JMenuItem menuItemExit = new JMenuItem("Exit",
	                         KeyEvent.VK_X );
	    menuItemExit.setAccelerator( KeyStroke.getKeyStroke(
	        KeyEvent.VK_X, ActionEvent.CTRL_MASK ) );
	    menuItemExit.addActionListener( this );
	    menuItemExit.getAccessibleContext().setAccessibleDescription(
	        "Exit program");
	    fileMenu.add( menuItemExit );

	    // Build Analyze menu
	    JMenu licenseMenu = new JMenu("Analyze");
	    fileMenu.setMnemonic(KeyEvent.VK_A);
	    menuBar.add( licenseMenu );
	    
	    //a group of JMenuItems
	    JMenuItem analyzeLicenses = new JMenuItem("Re-Analyze licenses" );
	    analyzeLicenses.addActionListener( this );
	    analyzeLicenses.getAccessibleContext().setAccessibleDescription(
	        "Re-Analyze licenses");
	    licenseMenu.add( analyzeLicenses );

	    //a group of JMenuItems
	    JMenuItem analyzeComponent = new JMenuItem("Analyze package" );
	    analyzeComponent.addActionListener( this );
	    analyzeComponent.getAccessibleContext().setAccessibleDescription(
	        "Analyze source package");
	    licenseMenu.add( analyzeComponent );

	    //a group of JMenuItems
	    JMenuItem simpleLicenseAnalyzeComponent = new JMenuItem("Analyze only licenses of a package" );
	    simpleLicenseAnalyzeComponent.addActionListener( this );
	    simpleLicenseAnalyzeComponent.getAccessibleContext().setAccessibleDescription(
	        "Analyze only licenses of a package");
	    licenseMenu.add( simpleLicenseAnalyzeComponent  );

	    // Build Statistics menu
	    JMenu statisticsMenu = new JMenu("Statistics");
	    statisticsMenu.setMnemonic(KeyEvent.VK_S);
	    menuBar.add( statisticsMenu );

	    JMenuItem statisticsComponent = new JMenuItem("Print statistics" );
	    statisticsComponent.addActionListener( this );
	    statisticsComponent.getAccessibleContext().setAccessibleDescription(
	    	"Print statistics");
	    statisticsMenu.add( statisticsComponent  );

	    JMenuItem statisticsDebugComponent = new JMenuItem("Print debug statistics" );
	    statisticsDebugComponent.addActionListener( this );
	    statisticsDebugComponent.getAccessibleContext().setAccessibleDescription(
	    	"Print debug statistics");
	    statisticsMenu.add( statisticsDebugComponent  );

	    
	    // Build Statistics menu
	    JMenu actionsMenu = new JMenu("Actions");
//	    statisticsMenu.setMnemonic(KeyEvent.VK_S);
	    menuBar.add( actionsMenu );

	    JMenuItem architectureComponent = new JMenuItem("Display architecture" );
	    architectureComponent.addActionListener( this );
	    architectureComponent.getAccessibleContext().setAccessibleDescription(
	    		"Display architecture");
	    actionsMenu.add( architectureComponent );	    

	    JMenu rulesMenu = new JMenu("Rules");
//	    statisticsMenu.setMnemonic(KeyEvent.VK_S);
	    menuBar.add( rulesMenu );

	    JMenuItem rulesComponent = new JMenuItem("License rules" );
	    rulesComponent.addActionListener( this );
	    rulesComponent.getAccessibleContext().setAccessibleDescription(
	    		"License rules");
	    rulesMenu.add( rulesComponent );	    

	}

	private StringBuffer makeAnalysis() {
		StringBuffer results = new StringBuffer("");
		int sourceFiles = 0;
		int licenseOk = 0;
		int sourceFileUsedInPath = 0;
		int licenseOkInPath = 0;
		
		boolean okInPath = false;
		boolean usedInPath = false;
		
		int totalCount = CommonFuctions.countAllSourceFiles( analysisPath );
		//int totalCount = 0;
		Vector<String> allSourceFileNames = DebugHelper.allSourceFileNames(analysisPath);
		
		for (Iterator iter = DependencyObjects.objects.values().iterator(); iter.hasNext();) {
			okInPath = false;
			usedInPath = false;
			TargetFile element = (TargetFile) iter.next();			
			if ( element.getFileType().equals( TargetFile.FILE_TYPE_SOURCE ) ) {
				sourceFiles++;
				if ( element.getFileName().startsWith(analysisPath)) {
					sourceFileUsedInPath++;
					usedInPath = true;
					allSourceFileNames.remove(element.getFileName());
				}
				if( !element.getLicense().getLicenseType().equals( LicenseOfAFile.UNKNOWN ) ) {
					licenseOk++;
					if ( element.getFileName().startsWith(analysisPath)) {
						licenseOkInPath++;
						okInPath = true;
					}
				}
								
			}
		}
		
		System.out.println("All source filenames lenght " + allSourceFileNames.size());
	   	FileWriter outputStream1 = null;

        try {
            try {
				outputStream1 = new FileWriter("extraobjects.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    
			for (Iterator iter = allSourceFileNames.iterator(); iter.hasNext();) {
				String str= (String) iter.next();
				try {
					outputStream1.write(str + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

        }finally {
            if (outputStream1 != null) {
            	try {
					outputStream1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }



		results.append("####### SOURCE ANALYISIS ####### \n");
		results.append("Source files in path: " + totalCount + "\n" );
		results.append("Source used from path: " + sourceFileUsedInPath + "\n" );
		results.append("Total source files: " + sourceFiles + "\n" ); 
		results.append("License recognized: " + licenseOk + "\n" );
		results.append("License recognized from files in path: " + licenseOkInPath + "\n" );
		return results;
	}
	
	private void writeDebug() {
    	Vector <String> vec1 = DebugHelper.allSourceFileNames(analysisPath);
    	Collections.sort(vec1);
    	Vector <String> vec2 = new Vector<String>(); 
		for (Iterator iter = DependencyObjects.objects.values().iterator(); iter.hasNext();) {
			TargetFile element = (TargetFile) iter.next();			
			if ( element.getFileType().equals( TargetFile.FILE_TYPE_SOURCE ) ) {
				vec2.add(element.getFileName());
			}
		}
    	Collections.sort(vec2);
    	
    	FileWriter outputStream1 = null;
    	FileWriter outputStream2 = null;

        try {
            try {
				outputStream1 = new FileWriter("objects1.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    
            try {
				outputStream2 = new FileWriter("objects2.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    

			for (Iterator iter = vec1.iterator(); iter.hasNext();) {
				String str= (String) iter.next();
				try {
					outputStream1.write(str + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			for (Iterator iter = vec2.iterator(); iter.hasNext();) {
				String str= (String) iter.next();
				try {
					outputStream2.write(str + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			
        }finally {
            if (outputStream1 != null) {
            	try {
					outputStream1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if (outputStream2 != null) {
                try {
					outputStream2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
		
	}
	
	public boolean getAnalyzeDependencies() {
		return analyzeDependencies;
	}
	
	private AnalyzePanel aPanel = null;
	private JFileChooser fileChooser;
	private String fileName = "output.xml";
	private TreeView treeView = null;
	private TargetFileView fileView = null;
	private RulesView rulesView = null;
	private boolean analyzeDependencies = false;
	private String analysisPath = "";
	
}
