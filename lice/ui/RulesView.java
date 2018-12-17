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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import lice.common.RuleEngine;
import lice.licenses.LicenseChecker;
import lice.licenses.LicenseRules;
import lice.licenses.Rule;
import lice.objects.Dependency;

	public class RulesView extends JPanel implements ActionListener
{
	
	public RulesView () {	
		super(new BorderLayout());
		
		licenseRules = RuleEngine.readRules();
		selectedLicense = licenseRules[0]; // here should be error checkin
		System.out.println("Rules length " + licenseRules.length );
		
		JPanel leftPanel = new JPanel( new BorderLayout() );
		leftPanel.setBorder( BorderFactory.createTitledBorder( "Define rules for license:") );
        String [] licenseNames = LicenseChecker.getLicenseNames();
        licenseNameCombo = new JComboBox( licenseNames );
        leftPanel.add( licenseNameCombo, BorderLayout.NORTH );
        licenseNameCombo.addActionListener( this );
        
        add(leftPanel, BorderLayout.WEST );
        
		tablePane = new JPanel( new BorderLayout() );
		tablePane.setBorder( BorderFactory.createTitledBorder( 
				"Rules when " + selectedLicense.getLicenseName() + " is dependend on") );
		tableModel = new LicenseTableModel();
		JTable table = new JTable( tableModel );
		table.setPreferredScrollableViewportSize(new Dimension(500, 250));

		JScrollPane scrollPane = new JScrollPane(table);
/*		pane.setLayout(new BorderLayout());
		pane.add(table.getTableHeader(), BorderLayout.PAGE_START);
		pane.add(table, BorderLayout.CENTER); */
		tablePane.add( scrollPane );
		
        add(tablePane, BorderLayout.CENTER);
	
		JComboBox comboBox = new JComboBox();
		comboBox.addItem(Rule.RESULT_UNDEFINED);
		comboBox.addItem(Rule.RESULT_OK);
		comboBox.addItem(Rule.RESULT_WARNING);
		comboBox.addItem(Rule.RESULT_NOK);

		int count = table.getColumnCount();
		
		for (int i = 0; i < count; i++) {
			if ( i == 0 ) continue;
			TableColumn col = table.getColumnModel().getColumn(i);
			col.setCellEditor(new DefaultCellEditor(comboBox));			
		}
		
		JPanel southPanel = new JPanel( new FlowLayout() );
		buttonSave = new JButton( "Save" );
		buttonSave.setActionCommand("Save");
		buttonSave.addActionListener(this);
		buttonCancel = new JButton( "Cancel" );
		buttonCancel.setActionCommand("Cancel");
		buttonCancel.addActionListener(this);
		southPanel.add( buttonSave );
		southPanel.add( buttonCancel );
		add( southPanel, BorderLayout.SOUTH );
		
        frame = new JFrame("License rules ");
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

		if ( command.equalsIgnoreCase("comboBoxChanged") ) {
			String licenseName = licenseNameCombo.getSelectedItem().toString();
			for (int i = 0; i < licenseRules.length; i++) {
				LicenseRules selected = licenseRules[i];
				if ( selected.getLicenseName().equalsIgnoreCase(licenseName) ) {
					selectedLicense = selected;
					break;
				}
			}

			tableModel.fireTableDataChanged();
			System.out.println("Selected license: " + selectedLicense.getLicenseName() );
			tablePane.setBorder( BorderFactory.createTitledBorder( 
					"Rules when " + selectedLicense.getLicenseName() + " is dependend on") );
		}
		else if ( command.equalsIgnoreCase("Save") ) {
			RuleEngine.saveRules( licenseRules );
			JOptionPane.showMessageDialog(this, 
					"Changes saved succesfully.",
					"Save",
					JOptionPane.INFORMATION_MESSAGE);
		}
		else if ( command.equalsIgnoreCase("Cancel") ) {
			System.out.println("Cancel");
			frame.setVisible(false);
		}
	}

	
	class LicenseTableModel extends AbstractTableModel {

		public String getColumnName(int col) {
			if ( col == 0 ) return "";
			else if ( col == 1 ) return Rule.TYPE_STATIC;
			else if ( col == 2 ) return Rule.TYPE_DYNAMIC;
			else if ( col == 3 ) return Rule.TYPE_RPC;
			return "";
		}
	    public int getRowCount() { 
	    	return licenseRules.length; 
	    }
	    public int getColumnCount() { 
	    	return 3; 
	    }
	    public Object getValueAt(int row, int col) {
	        if ( col == 0 )
	        	return licenseRules[row].getLicenseName();
	        else {
	        	LicenseRules rule = licenseRules[row];
	        	if ( col == 1 ) { 
	        		Rule r = selectedLicense.getRule( rule.getLicenseName() );
	        		return r.getRule( new Dependency( null, Dependency.DEP_TYPE_STATIC_LINK )  );
	        	}
	        	else if ( col == 2 ) {
	        		Rule r = selectedLicense.getRule( rule.getLicenseName() );
	        		return r.getRule( new Dependency( null, Dependency.DEP_TYPE_DYNAMIC_LINK )  );
	        	}
	    		else if ( col == 3 ) {
	        		Rule r = selectedLicense.getRule( rule.getLicenseName() );
	        		return r.getRule( new Dependency( null, Dependency.DEP_TYPE_RPC )  );
	    		}
	        }
	        
	        return "Error";
	    }
	    public boolean isCellEditable(int row, int col) { 
	    	if ( col == 0 ) return false;
	    	return true; 
	    }
	    public void setValueAt(Object value, int row, int col) {

        	LicenseRules rule = licenseRules[row];
        	if ( col == 1 ) { 
        		Rule r = selectedLicense.getRule( rule.getLicenseName() );
        		r.setRule( new Dependency( null, Dependency.DEP_TYPE_STATIC_LINK ), (String)value );
        		selectedLicense.setRule( rule.getLicenseName(), r );
        		System.out.println("Setting rule for " + selectedLicense.getLicenseName() + " for license " + rule.getLicenseName());
        	}
        	else if ( col == 2 ) {
        		Rule r = selectedLicense.getRule( rule.getLicenseName() );
        		r.setRule( new Dependency( null, Dependency.DEP_TYPE_DYNAMIC_LINK ), (String)value );
        		selectedLicense.setRule( rule.getLicenseName(), r );
        	}
    		else if ( col == 3 ) {
        		Rule r = selectedLicense.getRule( rule.getLicenseName() );
        		r.setRule( new Dependency( null, Dependency.DEP_TYPE_RPC ), (String)value );
        		selectedLicense.setRule( rule.getLicenseName(), r );
    		}
        	
        	fireTableCellUpdated( row, col );
        	modified = true;
	    }
	
	}

	
    private LicenseRules [] licenseRules; 
    private LicenseRules selectedLicense = null;
 
    private boolean modified = false;

    private JPanel tablePane = null;
    private JFrame frame;
    private JButton buttonSave = null;
    private JButton buttonCancel = null;
	private JComboBox licenseNameCombo = null;
	private LicenseTableModel tableModel = null;

}
