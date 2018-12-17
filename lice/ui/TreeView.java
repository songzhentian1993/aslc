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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.pf.util.SysUtil;

import lice.common.CommonFuctions;
import lice.licenses.LicenseChecker;
import lice.licenses.LicenseOfAFile;
import lice.licenses.LicenseStatus;
import lice.objects.Dependency;
import lice.objects.DependencyObjects;
import lice.objects.TargetFile;

/**
 * @author timot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeView extends JPanel implements ActionListener, TreeSelectionListener, TreeExpansionListener {

	public TreeView( TargetFileView tv ) {
		super();

		tfView = tv;
		setLayout(new BorderLayout());
		
		myRootNode = new PDefaultMutableTreeNode("ASLA");
		DefaultTreeModel treeModel = new DefaultTreeModel( myRootNode );
	    myTree = new JTree();
	    myTree.setModel( treeModel );
	    myTree.setCellRenderer(new MyTreeRenderer());
	    
	    applyPopup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Use this license for the package");
        menuItem.addActionListener(this);
        applyPopup.add(menuItem);

	    searchPopup = new JPopupMenu();
        JMenuItem searchMenuItem = new JMenuItem("Search item");
        searchMenuItem.addActionListener(this);
        searchPopup.add(searchMenuItem);

        
	    licensesPopup = new JPopupMenu();
        JMenuItem mItem = new JMenuItem("Set license for this file");
        mItem.addActionListener(this);
        licensesPopup.add(mItem);
        JMenuItem excludeItem = new JMenuItem("Exclude this file from analysis");
        excludeItem.addActionListener(this);
        licensesPopup.add(excludeItem);
        JMenuItem excludePathItem = new JMenuItem("Exclude this path from analysis");
        excludePathItem.addActionListener(this);
        licensesPopup.add(excludePathItem);

        
	    myTree.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		if ( e.getButton() == 3 ) {
	    			if (selected != null) {
	    				if ( selected.getFileType().equals( TargetFile.FILE_TYPE_LICENSE ) ) {
	    					applyPopup.show(myTree, e.getX(), e.getY());
	    				} else if ( selected.getFileType().equals( TargetFile.FILE_TYPE_SOURCE ) ) {
	    					licensesPopup.show( myTree, e.getX(), e.getY() );
	    					//System.out.println( selected.getFileName() );
	    				}
	    			} else { // Some directory is selected
	    				if ( mySelectedNode != null ) {
	    					searchPopup.show( myTree, e.getX(), e.getY() );
	    				} 
	    				
	    			}
	    		}
	    	}
	    });
	    
	    myTree.addTreeSelectionListener( this );
	    myTree.addTreeExpansionListener( this );
	    
	    JScrollPane scrollPane = new JScrollPane(myTree);
	    scrollPane.setPreferredSize( new Dimension( 300, 800) );
	    //Add the scroll pane to this window.
	    add(scrollPane, BorderLayout.CENTER);	    
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	}

    public void actionPerformed(ActionEvent e) {
    	
    	JMenuItem source = (JMenuItem)(e.getSource());
        if ( source.getText().equalsIgnoreCase("Use this license for the package") ) {
			LicenseChecker.applyLicenseToAllInPath( selected.getFilePath(), selected.getLicense() );
			PDefaultMutableTreeNode parent = (PDefaultMutableTreeNode)mySelectedNode.getParent();
			Vector removable = new Vector();
			for (int j = 0; j < ((DefaultTreeModel)myTree.getModel()).getChildCount( myMissingLicenseNode ); j++) {
				PDefaultMutableTreeNode node = (PDefaultMutableTreeNode) ((DefaultTreeModel)myTree.getModel()).getChild( myMissingLicenseNode, j );
				//System.out.println(node.toString() + " " + nodeName);
				Object o = node.getUserObject();
				if ( o instanceof TargetFile ) {
					TargetFile f = (TargetFile)o;
					if ( f.getLicenseStatus().equals( LicenseStatus.LICENCE_OK ) ) {
						removable.add( node );
					}
				}
			}
			for (Iterator iter = removable.iterator(); iter.hasNext();) {
				PDefaultMutableTreeNode element = (PDefaultMutableTreeNode) iter.next();
				((DefaultTreeModel)myTree.getModel()).removeNodeFromParent( element );
				
			}
			((DefaultTreeModel)myTree.getModel()).reload( parent );
			((DefaultTreeModel)myTree.getModel()).reload( myMissingLicenseNode );
			//addObjectsToTree( objects, myDependencies );
        }

        if ( source.getText().equalsIgnoreCase("Set license for this file") ) {

        	String [] licenseNames = LicenseChecker.getLicenseNames();

        	String s = (String)JOptionPane.showInputDialog(
                    licensesPopup,
                    "Select the license for the file:\n"
                    + selected.getDisplayFileName(),
                    "Customized Dialog",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    licenseNames, 
                    null );
        	
        	selected.setLicense( new LicenseOfAFile( s ) );
        	PDefaultMutableTreeNode nodeToBeRemovedFromMissing =
        		findNode(myMissingLicenseNode, selected.getDisplayFileName());
			((DefaultTreeModel)myTree.getModel()).removeNodeFromParent( nodeToBeRemovedFromMissing );			
			((DefaultTreeModel)myTree.getModel()).reload( myMissingLicenseNode );
        	LicenseChecker.checkLicenseStatuses();
        }

        if ( source.getText().equalsIgnoreCase("Exclude this file from analysis") ) {
        	System.out.println("Removing " + selected.getFileName());
        	DependencyObjects.removeObject(selected.getFileName());
        	addObjectsToTree(myDependencies);
        }

        if ( source.getText().equalsIgnoreCase("Exclude this path from analysis") ) {
			String s = (String)JOptionPane.showInputDialog(
                    "Exclude path from analysis", selected.getFilePath() );
			DependencyObjects.removeObjectsWithPath(s);
        	addObjectsToTree(myDependencies);        
        }
        
        if ( source.getText().equalsIgnoreCase("Search item") ) {
			String s = (String)JOptionPane.showInputDialog(
                    "Search item from tree", "");
        	PDefaultMutableTreeNode node = findNode(mySelectedNode, s);
        	if ( node != null ) { 
        		myTree.setSelectionPath( new TreePath( node.getPath() ) );
        	}
        }
        
    }

    public void addObjectsToTree( boolean dependencies ) {
		myDependencies = dependencies;
    	clearTree();
		PDefaultMutableTreeNode node = null;
		myFilesWithoutLicense = new TreeMap();
		
		if ( myDependencies ) {
			TreeDirectory basePathNode = new TreeDirectory( basePath );
			myPackageNode = new PDefaultMutableTreeNode(basePathNode);
		}
		else {
			myPackageNode = new PDefaultMutableTreeNode("Licenses");			
		}
		TreeDirectory missing = new TreeDirectory( "Files with missing licenses" );
		missing.setLicenseStatus( false );
		myMissingLicenseNode = new PDefaultMutableTreeNode( missing );

		((DefaultTreeModel)myTree.getModel()).insertNodeInto( myPackageNode, myRootNode, myRootNode.getChildCount() );			
		
//		if ( myDependencies ) {
			((DefaultTreeModel)myTree.getModel()).insertNodeInto( myMissingLicenseNode, myRootNode, myRootNode.getChildCount() );			
//		}
		
    	Vector vec = null;
		if ( !myDependencies ) {
			vec = CommonFuctions.getTargetFileVector();
			Vector vec2 = CommonFuctions.getLicenses( );
			for (Iterator iter = vec2.iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				((DefaultTreeModel)myTree.getModel()).insertNodeInto( new PDefaultMutableTreeNode(element), myPackageNode, myPackageNode.getChildCount() );				
			}
		} else {

			vec = CommonFuctions.getTopLevelObjects();
			System.out.println("** " + vec.size() + " of top level objects **");
			createPackageNodes( vec, myPackageNode );
			System.out.println("** Create packages ok **");
		}
		
	    for (int i = 0; i < vec.size(); i++) {
			TargetFile target = (TargetFile)vec.get( i );
	    	node = new PDefaultMutableTreeNode( target );

	    	String filePath = target.getFilePath();
	    	filePath = filePath.replace( basePath, "" );
	    	PDefaultMutableTreeNode parent = null;
	    	if ( myDependencies ) {
	    		parent = findNodeRecurse( myPackageNode, filePath );
	    	} else { 
	    		if ( target.getLicense().getLicenseType().equals( LicenseOfAFile.UNKNOWN )) {
	    			parent = myMissingLicenseNode;
	    		} else {
	    			parent = findNodeRecurse( myPackageNode, target.getLicense().getLicenseName() );
	    		}
	    	}

	    	if ( myDependencies && target.getFileType().equals( TargetFile.FILE_TYPE_LICENSE ) ) {
	    		((DefaultTreeModel)myTree.getModel()).insertNodeInto( node, parent, 0 );
	    		TreePath path = new TreePath(parent.getPath() );
	    		copyingPaths.add( path );
	    	} else {
	    		((DefaultTreeModel)myTree.getModel()).insertNodeInto( node, parent, parent.getChildCount() );
	    	}
	    	addChildren( node, target );	    	
		}

	    for (Iterator iter = myFilesWithoutLicense.keySet().iterator(); iter.hasNext();) {
	    	String key = (String) iter.next();
			TargetFile tr = 
				(TargetFile) myFilesWithoutLicense.get( key );
			PDefaultMutableTreeNode childNode = 
				new PDefaultMutableTreeNode( tr );

			((DefaultTreeModel)myTree.getModel()).insertNodeInto( childNode, myMissingLicenseNode, myMissingLicenseNode.getChildCount() );	    		
			
		}

		System.out.println("** Trying to show! **");
	    
	    myRootNode.setUserObject("");
	    ((DefaultTreeModel)myTree.getModel()).nodeStructureChanged( myRootNode );
	    
	    // Expand all COPYING files
	    /* for (Iterator iter = copyingPaths.iterator(); iter.hasNext();) {
	    	TreePath path = (TreePath) iter.next();
	    	tree.expandPath( path );
	    	System.out.println("Expanding path " + path.toString() );
		} */
	    //myTreeModel.getIndexOfChild()
	    if ( !myDependencies ) {
    		TreePath path = new TreePath( myPackageNode.getPath() );
	    	myTree.expandPath( path );
	    }
	}

	public void setBasePath ( String path ) {
		basePath = path;
	}

	public String getBasePath ( ) {
		return basePath;
	}

	
	private void createPackageNodes( Vector vec, PDefaultMutableTreeNode node ) {
		TreeMap packages = getPackages( vec );
		
		for (Iterator iter = packages.keySet().iterator(); iter.hasNext();) {
			String packageName = (String) iter.next();
			if ( packageName.equals("")) continue;
			String [] paths = packageName.split("/");
            PDefaultMutableTreeNode parent = node;
            for (int i = 0; i < paths.length; i++) {
            	if ( paths[i].equals("") ) continue;
            	PDefaultMutableTreeNode child = findNode( parent, paths[i] );
            	if ( child == null ) {
            		child = new PDefaultMutableTreeNode( new TreeDirectory( paths[i] ) );
            		((DefaultTreeModel)myTree.getModel()).insertNodeInto( child, parent, parent.getChildCount() );
            	} 
            	parent = child;
			}
			
//			myTreeModel.insertNodeInto( node, packageNode, packageNode.getChildCount() );			
		}
		
	}
	
	
	private TreeMap getPackages ( Vector topLevelObjects ) 
	{
		TreeMap packages = new TreeMap();
		//PDefaultMutableTreeNode node = null;
	    for (int i = 0; i < topLevelObjects.size(); i++) {
			TargetFile target = (TargetFile)topLevelObjects.get( i );
	    	String packageName = target.getFilePath();

	    	packageName = packageName.replace(basePath, "");
	   		//node = new PDefaultMutableTreeNode( target );
	    	packages.put( packageName, packageName );
	    }
		return packages;
	}
	
	private void clearTree () {
		myRootNode.removeAllChildren();
		myRootNode = null;
		myPackageNode = null;
		myMissingLicenseNode = null;
		mySelectedNode = null;
		selected = null;
		myTree.removeAll();
//		tree.setModel( null );
		myRootNode = new PDefaultMutableTreeNode("ASLA");
	    DefaultTreeModel treeModel = new DefaultTreeModel( myRootNode );
		myTree.setModel( treeModel );
		tfView.updateInfo( new TargetFile("", 0) );
		((DefaultTreeModel)myTree.getModel()).setRoot(myRootNode);
	}
		
	private PDefaultMutableTreeNode findNode( PDefaultMutableTreeNode parent, String nodeName ) {
		
		PDefaultMutableTreeNode node = null;
		
		for (int j = 0; j < ((DefaultTreeModel)myTree.getModel()).getChildCount( parent ); j++) {
			node = (PDefaultMutableTreeNode) ((DefaultTreeModel)myTree.getModel()).getChild( parent, j );
			//System.out.println(node.toString() + " " + nodeName);
			if ( node.toString().equals( nodeName ) ) {
				//System.out.println(node.toString() + " " + nodeName);
				break;
			} else {
				node = null;
			}
		}
		
		return node;
	}	
	
	private PDefaultMutableTreeNode findNodeRecurse( PDefaultMutableTreeNode parent, String nodeName ) {
		PDefaultMutableTreeNode node = null;
		
		if ( nodeName.equals("") ) return parent;
		
		String [] nodeNames = nodeName.split("/");
		for (int i = 0; i < nodeNames.length; i++) {
			if ( nodeNames[i].equals("") ) continue;
			node = findNode( parent, nodeNames[i] );
			parent = node;
		}		
		return node;
	}
	
	private void addChildren( PDefaultMutableTreeNode node, TargetFile target ) {
		for (int i = 0; i < target.getDependencies().size(); i++) {
			Dependency dep = (Dependency)target.getDependencies().get( i );
			TargetFile child = dep.getTargetFile();

			//PDefaultMutableTreeNode childNode = new PDefaultMutableTreeNode( child );
			//((DefaultTreeModel)myTree.getModel()).insertNodeInto( childNode, node, node.getChildCount() );

			if ( child.getLicense().getLicenseType().equalsIgnoreCase( LicenseOfAFile.UNKNOWN ) && 
					child.getFileType().equalsIgnoreCase( TargetFile.FILE_TYPE_SOURCE ) ) {
				myFilesWithoutLicense.put( child.getFileName(), child );
		    } 
			
			addChildren( null, child );
			//addChildren( childNode, child );
		}
	}

	private void addChildsToTree( PDefaultMutableTreeNode node, TargetFile target ) {
		for (int i = 0; i < target.getDependencies().size(); i++) {
			Dependency dep = (Dependency)target.getDependencies().get( i );
			TargetFile child = dep.getTargetFile();

			PDefaultMutableTreeNode childNode = new PDefaultMutableTreeNode( child );
			((DefaultTreeModel)myTree.getModel()).insertNodeInto( childNode, node, node.getChildCount() );

		}
	}
	
	

	public void valueChanged(TreeSelectionEvent e) {
		mySelectedNode = (PDefaultMutableTreeNode)
    	myTree.getLastSelectedPathComponent();
		
		if (mySelectedNode == null) 
			return;
	
		Object nodeInfo = mySelectedNode.getUserObject();
		if (nodeInfo instanceof TargetFile) {
			selected = (TargetFile)nodeInfo;
			tfView.updateInfo( selected );
		} else {
			selected = null;
		}
	}	

	public void treeCollapsed(TreeExpansionEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void treeExpanded(TreeExpansionEvent event) {
		// TODO Auto-generated method stub
		System.out.println(event.getPath().toString()); 
		TreePath expanded = event.getPath();
		myTree.setSelectionPath(expanded);
		
		if ( mySelectedNode.equals(myMissingLicenseNode) ) {
			//System.out.println("Missing node, no action");
			return;
		}

		Enumeration<DefaultMutableTreeNode> e = mySelectedNode.children();
		
		while ( e.hasMoreElements() ) {
			PDefaultMutableTreeNode child = (PDefaultMutableTreeNode)e.nextElement();

			Object nodeInfo = child.getUserObject();
			
			if (nodeInfo instanceof TargetFile) {
//				System.out.println("Oikee lapsi!! " + child.toString() );
				addChildsToTree(child, (TargetFile)nodeInfo);
			}

		}
		
	}


	private JTree myTree = null;
	private TreeMap myFilesWithoutLicense = null;
	private TargetFileView tfView = null;
	private PDefaultMutableTreeNode myRootNode = null;
	private PDefaultMutableTreeNode myPackageNode = null;
	private PDefaultMutableTreeNode myMissingLicenseNode = null;
	private PDefaultMutableTreeNode mySelectedNode = null;
	private JPopupMenu applyPopup = null;
	private JPopupMenu licensesPopup = null;
	private JPopupMenu searchPopup = null;
	private TargetFile selected = null;
	private String basePath = "";
	private boolean myDependencies = false;
	private Vector copyingPaths = new Vector();
//	private int alloc = 0;

}
