import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

import javax.lang.model.type.UnknownTypeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


/** XMLAccessor, reads and writes XML files
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class XMLAccessor extends Accessor {

    private String getTitle(Element element, String tagName) {
    	NodeList titles = element.getElementsByTagName(tagName);
    	return titles.item(0).getTextContent();
    	
    }

	public void loadFile(Presentation presentation, String filename) throws IOException {
		int slideNumber, itemNumber;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(new File(filename)); //Create a JDOM document
			Element doc = document.getDocumentElement();
			presentation.setTitle(getTitle(doc, "showtitle"));

			NodeList slides = doc.getElementsByTagName("slide");
			for (slideNumber = 0; slideNumber < slides.getLength()	; slideNumber++) {
				Element xmlSlide = (Element) slides.item(slideNumber);
				Slide slide = new Slide();
				slide.setTitle(getTitle(xmlSlide, "title"));
				presentation.append(slide);
				NodeList slideItems = xmlSlide.getElementsByTagName("item");
				for (itemNumber = 0; itemNumber < slideItems.getLength(); itemNumber++) {
					Element item = (Element) slideItems.item(itemNumber);
					loadSlideItem(slide, item);
				}
			}
		}
		catch (IOException | SAXException | ParserConfigurationException e) {
			System.err.println(e.getMessage());
		}
	}

	private void loadSlideItem(Slide slide, Element item) {
		NamedNodeMap attributes = item.getAttributes();
		String levelText = attributes.getNamedItem("level").getTextContent();
		if (Integer.parseInt(levelText) != 0) {
			try {
				int level = Integer.parseInt(levelText);
				String type = attributes.getNamedItem("kind").getTextContent();
				if ("text".equals(type)) {
					slide.append(new TextItem(level, item.getTextContent()));
				}
				if ("image".equals(type)) {
					slide.append(new BitmapItem(level, item.getTextContent()));
				}
			}
			catch(NumberFormatException | UnknownTypeException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public void saveFile(Presentation presentation, String filename) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(filename));
		String text = "text";
		String image = "image";
		out.println("<?xml version=\"1.0\"?>");
		out.println("<presentation>");
		out.print("<showtitle>");
		out.print(presentation.getTitle());
		out.println("</showtitle>");
		for (int slideNumber = 0; slideNumber < presentation.getSize(); slideNumber++) {
			Slide slide = presentation.getSlide(slideNumber);
			out.println("<slide>");
			out.println("<title>" + slide.getTitle() + "</title>");
			Vector<SlideItem> slideItems = slide.getSlideItems();
			for (int itemNumber = 0; itemNumber < slideItems.size(); itemNumber++) {
				SlideItem slideItem = slideItems.elementAt(itemNumber);
				if (slideItem instanceof TextItem) {
					out.print("<item kind=\"" + text + "\" level=\"" + slideItem.getLevel() + "\">");
					out.print(((TextItem) slideItem).getText());
					out.println("</item>");
				} else {
					if (slideItem instanceof BitmapItem) {
						out.print("<item kind=\"" + image + "\" level=\"" + slideItem.getLevel() + "\">");
						out.print(((BitmapItem) slideItem).getName());
						out.println("</item>");
					} else {
						System.out.println("Ignoring " + slideItem);
					}
				}
			}
			out.println("</slide>");
		}
		out.println("</presentation>");
		out.close();
	}
}
