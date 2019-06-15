package graphics;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Resizehandler implements ComponentListener {

              
		@Override
		public void componentHidden(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void componentMoved(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void componentResized(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			Dimension newSize = arg0.getComponent().getBounds().getSize();       
			Frame_functions.width=newSize.width;
			Frame_functions.height=newSize.height;
			Frame_functions.ctrw=newSize.width/2;
			Frame_functions.ctrh=newSize.height/2;

		}
		@Override
		public void componentShown(ComponentEvent arg0) {
			// TODO Auto-generated method stub			
		}   


}
