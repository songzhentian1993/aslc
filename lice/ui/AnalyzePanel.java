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
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import lice.analyzator.Analyzer;
import lice.analyzator.DependencyAndLicenseAnalyzator;
import lice.analyzator.LicenseAnalyzer;
import lice.analyzator.LicenseReanalyzer;
import lice.common.GlobalStatistics;
import lice.licenses.LicenseTemplate;

/**
 * @author timtuun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AnalyzePanel extends JPanel implements ActionListener {

    public final static int ONE_SECOND = 1000;

    private JProgressBar progressBar;
    private Timer timer;
    private JButton cancelButton;	
    private Analyzer reader;
    private JFrame frame;
    private JLabel label;
    private MainView main;

   
	/**
	 * 
	 */
	public AnalyzePanel( MainView mv, String packageName, boolean dependencyAnalysis ) {
		super(new BorderLayout());	
		createUI( mv, packageName );
		if ( dependencyAnalysis ) {
			reader = new DependencyAndLicenseAnalyzator( packageName );			
		} else {
			reader = new LicenseAnalyzer( packageName );
		}
		progressBar.setMaximum(reader.getNumberOfFiles());
		timer = new Timer( 1 , new TimerListener());
        
		Date time = new Date();
		GlobalStatistics.analysisstarttime = time.getTime();
		GlobalStatistics.filesearchtime = 0;
        timer.start();
        reader.go();

	}

	public AnalyzePanel( MainView mv, 
						boolean reAnalyze,
						boolean dependencyAnalysis,
						LicenseTemplate l ) {
		super(new BorderLayout());	
		createUI( mv, "Reanalyzing objects" );
		reader = new LicenseReanalyzer( l, dependencyAnalysis, reAnalyze );
		progressBar.setMaximum( reader.getNumberOfFiles() );
		timer = new Timer( 1 , new TimerListener());

        timer.start();
        reader.go();

	}
	
	private void createUI ( MainView mv, String packageName ) {
		main = mv;
        //Create the demo's UI.
        cancelButton = new JButton("cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);

        progressBar = new JProgressBar(0, 0);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        JPanel top = new JPanel();
        label = new JLabel( packageName );
        top.add(label);
        
        JPanel panel = new JPanel();
        panel.add(cancelButton);
        panel.add(progressBar);

        setLayout(new GridLayout( 0,1 ) );
        add(top);
        add(panel);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        createAndShowGUI();
		
	}
		
    private void createAndShowGUI() {

    	//Create and set up the window.
        frame = new JFrame("Analyzing ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = this;
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void finished() {
    	System.out.println("Finished!!");
    	Date time = new Date();
    	System.out.println("Total time " + (time.getTime() - GlobalStatistics.analysisstarttime)/1000 + "s" );
    	System.out.println("Time spent searching " + GlobalStatistics.filesearchtime/1000 + "s" );
    	
    	timer.stop();
    	frame.setVisible(false);
    	main.updateTree();
    }
    
    public void actionPerformed(ActionEvent evt) {
    	reader.cancel( true );
    }
    	/**
     * The actionPerformed method in this class
     * is called each time the Timer "goes off".
     */
    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
//        	System.out.println("Action!");
            progressBar.setValue(reader.getProgress()); 

            if ( reader.getCancel() ) {
            	timer.stop();
            	frame.setVisible(false);            	
            }
            
            if ( reader.isDone() ) {
            	finished();
            }
        }
    }	 
}
