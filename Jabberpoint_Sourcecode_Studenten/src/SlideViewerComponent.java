import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serial;
import javax.swing.JComponent;
import javax.swing.JFrame;


/** <p>SlideViewerComponent is a graphical component that ca display Slides.</p>
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class SlideViewerComponent extends JComponent {
	
	@Serial
	private static final long serialVersionUID = 227L;

	private Slide slide; //The current slide
	private final Font labelFont; //The font for labels
	private Presentation presentation; //The presentation
	private final JFrame frame;
	public SlideViewerComponent(Presentation presentation, JFrame frame) {
		this.setBackground(Color.WHITE);
		this.presentation = presentation;
		this.labelFont = new Font("Dialog", Font.BOLD, 10);
		this.frame = frame;
	}

	public Dimension getPreferredSize() {
		return new Dimension(Slide.WIDTH, Slide.HEIGHT);
	}

	public void update(Presentation presentation, Slide data) {
		if (data != null) {
			this.presentation = presentation;
			this.slide = data;
			repaint();
			frame.setTitle(presentation.getTitle());
		}
	}

//Draw the slide
	public void paintComponent(Graphics g) {
		int x_POS = 1100;
		int y_POS = 20;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getSize().width, getSize().height);
		if (presentation.getSlideNumber() < 0 || slide == null) {
			return;
		}
		g.setFont(labelFont);
		g.setColor(Color.BLACK);
		g.drawString("Slide " + (1 + presentation.getSlideNumber()) + " of " +
                 presentation.getSize(), x_POS, y_POS);
		Rectangle area = new Rectangle(0, y_POS, getWidth(), (getHeight() - y_POS));
		slide.draw(g, area, this);
		}
}

