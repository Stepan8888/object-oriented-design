import java.awt.MenuBar;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.io.IOException;
import java.io.Serial;

import javax.swing.JOptionPane;

/** <p>The controller for the menu</p>
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */
public class MenuController extends MenuBar {

	@Serial
	private static final long serialVersionUID = 227L;

	private final Frame parent; //The frame, only used as parent for the Dialogs
	private final Presentation presentation; //Commands are given to the presentation

	public MenuController(Frame frame, Presentation presentation) {
		this.parent = frame;
		this.presentation = presentation;
		MenuItem menuItem;
		Menu fileMenu = new Menu("File");
		fileMenu.add(menuItem = mkMenuItem("Open"));
		openAction(menuItem);
		fileMenu.add(menuItem = mkMenuItem("New"));
		newAction(menuItem);
		fileMenu.add(menuItem = mkMenuItem("Save"));
		saveAction(menuItem);
		fileMenu.addSeparator();
		fileMenu.add(menuItem = mkMenuItem("Exit"));
		exitAction(menuItem);
		add(fileMenu);
		Menu viewMenu = new Menu("View");
		viewMenu.add(menuItem = mkMenuItem("Next"));
		nextSlideAction(menuItem);
		viewMenu.add(menuItem = mkMenuItem("Prev"));
		previousSlideAction(menuItem);
		viewMenu.add(menuItem = mkMenuItem("Go to"));
		goToAction(menuItem);
		add(viewMenu);
		Menu helpMenu = new Menu("Help");
		helpMenu.add(menuItem = mkMenuItem("About"));
		helpAction(menuItem);
		setHelpMenu(helpMenu);		//Needed for portability (Motif, etc.).
	}

	private void helpAction(MenuItem menuItem) {
		menuItem.addActionListener(actionEvent -> AboutBox.show(parent));
	}

	private void goToAction(MenuItem menuItem) {
		menuItem.addActionListener(actionEvent -> {
			String pageNumberStr = JOptionPane.showInputDialog("Page number?");
			int pageNumber = Integer.parseInt(pageNumberStr);
			presentation.setSlideNumber(pageNumber - 1);
		});
	}

	private void previousSlideAction(MenuItem menuItem) {
		menuItem.addActionListener(actionEvent -> presentation.prevSlide());
	}

	private void nextSlideAction(MenuItem menuItem) {
		menuItem.addActionListener(actionEvent -> presentation.nextSlide());
	}

	private void exitAction(MenuItem menuItem) {
		menuItem.addActionListener(actionEvent -> presentation.exit(0));
	}

	private void saveAction(MenuItem menuItem) {
		menuItem.addActionListener(e -> {
			Accessor xmlAccessor = new XMLAccessor();
			try {
				xmlAccessor.saveFile(presentation, "savedPresentation.xml");
			} catch (IOException exc) {
				JOptionPane.showMessageDialog(parent, "IO Exception" + exc,
						"Save error ", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	private void newAction(MenuItem menuItem) {
		menuItem.addActionListener(actionEvent -> {
			presentation.clear();
			parent.repaint();
		});
	}

	private void openAction(MenuItem menuItem) {
		menuItem.addActionListener(actionEvent -> {
			presentation.clear();
			Accessor xmlAccessor = new XMLAccessor();
			try {
				xmlAccessor.loadFile(presentation, "testPresentation.xml");
				presentation.setSlideNumber(0);
			} catch (IOException exc) {
				JOptionPane.showMessageDialog(parent, "IO Exception" + exc,
				 "Load error ", JOptionPane.ERROR_MESSAGE);
			}
			parent.repaint();
		});
	}

	//Creating a menu-item
	public MenuItem mkMenuItem(String name) {
		return new MenuItem(name, new MenuShortcut(name.charAt(0)));
	}
}
