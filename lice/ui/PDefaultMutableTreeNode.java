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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * Modified version of standard java DefaultMutableTreeNode
 * remove() -method removes all children also
 */
public class PDefaultMutableTreeNode extends DefaultMutableTreeNode {

	public PDefaultMutableTreeNode() {
		this(null);
	}

	public PDefaultMutableTreeNode(Object userObject) {
		this(userObject, true);
	}

	public PDefaultMutableTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}

	public void remove(int childIndex) {
		MutableTreeNode child = (MutableTreeNode)getChildAt(childIndex);

		children.removeElementAt(childIndex);
		/* start of new stuff */
		if (child.getChildCount() > 0) {
			((DefaultMutableTreeNode)child).removeAllChildren();
		}
		child.setUserObject(null);
		/* end of new stuff */
		child.setParent(null);
	}	
}
