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

import java.awt.Component;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import lice.licenses.LicenseStatus;
import lice.objects.Dependency;
import lice.objects.TargetFile;

/**
 * @author timot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MyTreeRenderer extends DefaultTreeCellRenderer {

	public MyTreeRenderer() {
		sourceIconOk = createImageIcon("images/ok.gif");
		sourceIconNok = createImageIcon("images/nok.gif");
		sourceIconIgnore = createImageIcon("images/ignore.gif");
	    objectIconNok = createImageIcon("images/objectnok.gif");
	    objectIconOk = createImageIcon("images/objectok.gif");
	    sharedObjectIconNok = createImageIcon("images/sharedobjectnok.gif");
	    sharedObjectIconOk = createImageIcon("images/sharedobjectok.gif");
	    root = createImageIcon("images/root.gif");
	}
	
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
        		tree, value, sel,
				expanded, leaf, row,
				hasFocus);
        if ( row == 0 ) {
    		setIcon(root);        	
        }

        String state = getState( value );
        
        if ( state.equalsIgnoreCase("") ) return this;
                
        if ( state.equalsIgnoreCase(sourceStateOk) )
        	setIcon(sourceIconOk);	
       	else if ( state.equalsIgnoreCase(sourceStateNok ))
        	setIcon(sourceIconNok);
       	else if ( state.equalsIgnoreCase(objectStateOk ))
        	setIcon(objectIconOk);
       	else if ( state.equalsIgnoreCase(objectStateNok ))
        	setIcon(objectIconNok);
       	else if ( state.equalsIgnoreCase(dynamicStateOk ))
        	setIcon(sharedObjectIconOk);
       	else if ( state.equalsIgnoreCase(dynamicStateNok ))
        	setIcon(sharedObjectIconNok);

        return this;
    }

    private String getState( Object value ) {
    	DefaultMutableTreeNode node =
            (DefaultMutableTreeNode)value;
    	
    	DefaultMutableTreeNode parent =
    		(DefaultMutableTreeNode)node.getParent();
    	
    	String state = "";
    	if ( (node.getUserObject() instanceof TargetFile ) ) {
    		TargetFile targetFile =
    			(TargetFile)(node.getUserObject());

    		if ( targetFile.getFileType().equalsIgnoreCase(TargetFile.FILE_TYPE_SOURCE) || 
    				targetFile.getFileType().equalsIgnoreCase(TargetFile.FILE_TYPE_LICENSE) ) {
    			
        		String licenseStatus = targetFile.getLicenseStatus();

        		if ( licenseStatus.equalsIgnoreCase(LicenseStatus.LICENCE_OK ) ) {
    				state = sourceStateOk;
    			} else {
    				state = sourceStateNok;
    			}
    			
    		} else {

    			if ( parent == null ) return state;
    			
				TargetFile parentFile = null;
    			if ( parent.getUserObject() instanceof TargetFile ) {
    				parentFile = (TargetFile)(parent.getUserObject());
    			}

    			state = binaryObjectState(parentFile, targetFile);
    		}

    		
    	} else if ( node.getUserObject() instanceof TreeDirectory ) {
    		TreeDirectory dir = (TreeDirectory)node.getUserObject();
    	}

    	return state;
    }

    private String binaryObjectState( TargetFile parent, TargetFile child ) {
    	String status = "";
		String licenseStatus = child.getLicenseStatus();
    	
		if ( parent != null ) {
		if ( parent.getFileName().endsWith( "liboscar.so" ) && 
				child.getFileName().endsWith("libgcc.a")) {
			System.out.println("‰‰‰‰hh");
		}
		}
		
		if ( parent == null ) {
    		if ( licenseStatus.equalsIgnoreCase(LicenseStatus.LICENCE_OK ) ) {
				status = objectStateOk;
			} else {
				status = objectStateNok;
			}
			
		} else {
			
			Vector deps = parent.getDependencies();
			Dependency childDep = null;
			for (int i = 0; i < deps.size(); i++) {
				Dependency dep = (Dependency)deps.get(i);
				if ( dep.getTargetFile().getFileName().equalsIgnoreCase(child.getFileName()) ) {
					childDep = dep;
					break;
				}
			}
			
			if ( childDep == null ) return status; // Something weird has happened
			
    		if ( licenseStatus.equalsIgnoreCase(LicenseStatus.LICENCE_OK ) ) {
    			if ( childDep.getDependencyType().equalsIgnoreCase(Dependency.DEP_TYPE_DYNAMIC_LINK) ) {
    				status = dynamicStateOk;
    			} else {
    				status = objectStateOk;
    			}
			} else { // license NOK
    			if ( childDep.getDependencyType().equalsIgnoreCase(Dependency.DEP_TYPE_DYNAMIC_LINK) ) {
    				status = dynamicStateNok;
    			} else {
    				status = objectStateNok;
    			}
			}
		
		}
		
    	return status;
    }
    
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
    	return new ImageIcon(path);
    }

    private String sourceStateOk = "SourceOK";
    private String sourceStateNok = "SourceNOK";
    private String objectStateOk = "ObjectOK";
    private String objectStateNok = "ObjectNOK";
    private String dynamicStateOk = "DynamicOK";
    private String dynamicStateNok = "DynamicNOK";
    
    private ImageIcon objectIconOk = null;
    private ImageIcon objectIconWarning = null;
    private ImageIcon objectIconNok = null;
    private ImageIcon sharedObjectIconOk = null;
    private ImageIcon sharedObjectIconNok = null;
    private ImageIcon sourceIconOk = null;
    private ImageIcon sourceIconNok = null;
    private ImageIcon sourceIconIgnore = null;
    private ImageIcon root = null;
	
}
