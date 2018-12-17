package lice.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lice.common.CommonFuctions;
import lice.licenses.LicenseStatus;
import lice.objects.Dependency;
import lice.objects.TargetFile;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.Graph;
//import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.VertexFontFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.utils.UserDataContainer;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphMouseListener;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;


public class ArchitectureView extends JFrame {

	public ArchitectureView( TargetFile target ) {
		addSingleObject(target);
		createView();
	}

	public ArchitectureView() {
		
		Vector v = CommonFuctions.getTopLevelObjects();

		if ( v.size() == 1 ) {
			addSingleObject((TargetFile)v.get(0));
		} else {
			addObjects( v );	
		}
		createView();
		
	}

	private void addSingleObject(TargetFile target ) {

		Vertex v1 = null;
		Vector<Dependency> v = target.getDependencies();

		v1 = (Vertex) graph.addVertex(new SimpleDirectedSparseVertex());
		v1.setUserDatum("Name", target.toString() , new UserDataContainer.CopyAction.Clone() );
		v1.setUserDatum("Object", target, new UserDataContainer.CopyAction.Shared() );
		
		if ( target.getLicenseStatus().equals(LicenseStatus.LICENCE_OK) )
			v1.setUserDatum("Status", "Ok", new UserDataContainer.CopyAction.Clone() );

		
		for (int i = 0; i < v.size(); i++) {
			Dependency d = (Dependency)v.get(i);
			TargetFile t = d.getTargetFile();
			Vertex v2 = (Vertex) graph.addVertex(new SimpleDirectedSparseVertex()); 
			v2.setUserDatum("Name", t.toString() , new UserDataContainer.CopyAction.Clone() );
			v2.setUserDatum("Object", t, new UserDataContainer.CopyAction.Shared() );
			
			if ( t.getLicenseStatus().equals(LicenseStatus.LICENCE_OK) ) {
				v2.setUserDatum("Status", "Ok", new UserDataContainer.CopyAction.Clone() );
			}

			DirectedEdge e1 = (DirectedEdge) graph.addEdge(new DirectedSparseEdge(v1, v2));
			
		}
	}
	
	private void addObjects ( Vector<TargetFile> objects) {
		
		
		Vertex v1 = null;
		for (int i = 0; i < objects.size(); i++) {
			TargetFile tf = (TargetFile)objects.get(i);
			if ( tf.getFileType().equals(TargetFile.FILE_TYPE_LICENSE) ) { 
				continue;
			}
			v1 = (Vertex) graph.addVertex(new SimpleDirectedSparseVertex());
			v1.setUserDatum("Name", tf.toString() , new UserDataContainer.CopyAction.Clone() );
			v1.setUserDatum("Object", tf, new UserDataContainer.CopyAction.Shared() );
			
			if ( tf.getLicenseStatus().equals(LicenseStatus.LICENCE_OK) )
				v1.setUserDatum("Status", "Ok", new UserDataContainer.CopyAction.Clone() );
		}
	}

	private void createView() {
		
		Layout lay = new FRLayout( graph );
		PluggableRenderer rend = new PluggableRenderer();
		rend.setVertexFontFunction(new FontFunc());
		rend.setVertexStringer(new Stringer());
		rend.setVertexPaintFunction(new VertexColor());

		VisualizationViewer view = new VisualizationViewer( lay, rend );
        //final EditingModalGraphMouse graphMouse = new EditingModalGraphMouse();
        //graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        //view.setGraphMouse(graphMouse);
        view.addGraphMouseListener(new GMouseListener());

        add( view );
        pack();
        setLocationRelativeTo(null);
        setVisible(true);	
	}
	

	private static class FontFunc implements VertexFontFunction {
		
		public Font getFont(Vertex v) {
			 Font f = new Font("Helvetica", Font.PLAIN, 12);
			 return f;
		 }
	}

	private static class Stringer implements VertexStringer {

		public String getLabel(ArchetypeVertex v) {
			// TODO Auto-generated method stub
			if ( v.getUserDatum("Name") != null ) {
				return (String)v.getUserDatum("Name");
			}
			return v.toString();
		}
	}

	private static class VertexColor implements VertexPaintFunction {

		public Paint getDrawPaint(Vertex v) {
			// TODO Auto-generated method stub
			return Color.BLACK;
		}

		public Paint getFillPaint(Vertex v) {
			
			if ( v.getUserDatum("Status") != null ) {
				if( ((String)v.getUserDatum("Status")).equals("Ok") ) {
					return Color.GREEN;
				}
			}
			return Color.RED;
		}
		
	}

	private static class GMouseListener implements GraphMouseListener {

		public void graphClicked(Vertex v, MouseEvent me) {
			System.out.println(v.toString());
			if ( me.getClickCount() == 2 ) {
				TargetFile tf = (TargetFile)v.getUserDatum("Object");
				System.out.println(tf.getFileName());
				ArchitectureView ar = new ArchitectureView( tf );
			}
		}

		public void graphPressed(Vertex v, MouseEvent me) {
			// TODO Auto-generated method stub
			
		}

		public void graphReleased(Vertex v, MouseEvent me) {
			// TODO Auto-generated method stub
			
		}
	
	}

    private JFrame frame;
    private Graph graph = new DirectedSparseGraph();

}
