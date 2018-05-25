package sidnet.core.gui.staticdrawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import jist.runtime.JistAPI;

import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;

public class PolygonDrawingTool
implements BufferedImageProvider {
	
	private static final int MAX_SEGMENTS = 100;	
	private List<Segment> segmentList;	
	private BufferedImage image; 
	private LocationContext screenLocationContext;
	private boolean changed = false;
	private boolean showOnlyVerteces;
	
	public PolygonDrawingTool(LocationContext screenLocationContext) {
		segmentList = new LinkedList<Segment>();
		image = new BufferedImage(screenLocationContext.getWidth(),
								  screenLocationContext.getHeight(),
								  BufferedImage.TRANSLUCENT);
		this.screenLocationContext = screenLocationContext;
	}
	
	public void plot(List<NCS_Location2D> vertexList, boolean closePolygon,
					 Color color, int lineWidth, int dashLengthPx, long duration, boolean showOnlyVerteces) {
		
		purgeOld();
		
		this.showOnlyVerteces = showOnlyVerteces;
		
		NCS_Location2D p0 = null, p1 = null, p2 = null;
		
		for (NCS_Location2D ncs_loc: vertexList) {
			if (p0 == null)
				p0 = ncs_loc;
			p1 = p2;
			p2 = ncs_loc;	
			if (p1 != null && p2 != null) {
				while (segmentList.size() >= MAX_SEGMENTS)
					segmentList.remove(0);
				if (segmentList.size() <= MAX_SEGMENTS) {
					segmentList.add(
						new Segment(p1.fromNCS(screenLocationContext), 
									p2.fromNCS(screenLocationContext),
									color, lineWidth, dashLengthPx, duration));
					changed = true;
				}
			}			
		}
		
		if (closePolygon) {
			segmentList.add(
				new Segment(p0.fromNCS(screenLocationContext), 
							p2.fromNCS(screenLocationContext),
							color, lineWidth, dashLengthPx, duration));
				changed = true;
		}			
	}
	
	private synchronized void refreshImage() {
		Graphics2D g2 = (Graphics2D)image.createGraphics();
		
		try {
			for (Segment segment: segmentList) {
				if (segment == null)	// TODO FIXME
					continue;
				g2.setColor(segment.color); // TODO FIXME occasionally this throws a null pointer exception
				if (segment.dashLengthPx == 0)
					g2.setStroke(new BasicStroke(segment.lineWidth)); // continuous
				else
					g2.setStroke(new BasicStroke(segment.lineWidth,
									 BasicStroke.CAP_BUTT,
									 BasicStroke.JOIN_MITER, 10.0f,
									 new float[] {segment.dashLengthPx}, 0.0f));
				
				if (!showOnlyVerteces)
					g2.drawLine((int)segment.p1.getX(), (int)segment.p1.getY(),
								(int)segment.p2.getX(), (int)segment.p2.getY());
				else
					g2.fillOval((int)segment.p2.getX(), (int)segment.p2.getY(), 5, 5);
				
			}
		} catch (java.util.ConcurrentModificationException cme){;} // TODO FIXME
		g2.dispose();
		
		changed = false;
	}
	
	private void purgeOld() {
		while (segmentList.size() > 0) {
			if (segmentList.get(0).expirationTimestamp < JistAPI.getTime())
				segmentList.remove(0);
			else
				break;				
		}
	}
	
	public synchronized BufferedImage getImage() {
		if (changed)
			refreshImage();
		
		return image;
	}
	
	class Segment {
		Location2D p1;
		Location2D p2;
		Color color;
		float lineWidth;
		float dashLengthPx;	
		long expirationTimestamp;
		
		public Segment(Location2D p1, Location2D p2, Color color,
					   float lineWidth, float dashLengthPx, long expiration){
			this.p1 = p1;
			this.p2 = p2;
			this.color = color;
			this.lineWidth = lineWidth;
			this.dashLengthPx = dashLengthPx;
			this.expirationTimestamp = expiration;
		}
	}
}
