package serializer;

import models.DescriptionData;
import models.Offer;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class XmlSerializer {
    final static Logger LOGGER = Logger.getLogger(XmlSerializer.class);
    private String filename;

    public XmlSerializer(String filename) {
        this.filename = filename;
    }

    public String write(List<Offer> offers) {
        Element offersElements = new Element("offers");
        Document myDocument = new Document(offersElements);
        for (Offer offer : offers) {
            Element newElement = new Element("offer");
            newElement.addContent(new Element("name").setText(offer.getName()));
            newElement.addContent(new Element("articleId").setText(offer.getArticleId()));
            newElement.addContent(new Element("brand").setText(offer.getBrand()));
            newElement.addContent(new Element("color").setText(offer.getColor()));
            newElement.addContent(new Element("price").setText(offer.getPrice()));
            newElement.addContent(new Element("initialPrice").setText(offer.getInitialPrice()));
            newElement.addContent(new Element("shippingCosts").setText(offer.getShippingCosts()));
            Element descriptionElement = new Element("description");
            for (DescriptionData data : offer.getDescription()) {
                Element value = new Element(data.getName());
                for (String valueString : data.getData()) {
                    Element val = new Element("value").setText(valueString);
                    value.addContent(val);
                }
                descriptionElement.addContent(value);
            }
            newElement.addContent(descriptionElement);

            for (String size : offer.getSizes()) {
                Element currElem = newElement.clone();
                currElem.addContent(new Element("size").setText(size));
                offersElements.addContent(currElem);
            }
        }
        File file = new File(filename);
        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            FileWriter writer = null;
            writer = new FileWriter(file);
            outputter.output(myDocument, writer);
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return file.getAbsolutePath();
    }
}