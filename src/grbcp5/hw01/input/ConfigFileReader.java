package grbcp5.hw01.input;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigFileReader {

  private final File xmlFile;
  private final Document document;
  private final Node rootNode;
  private final NodeList parameterNodeList;
  private final int numChildNodes;

  public ConfigFileReader( final String configFilePath ) throws
    ParserConfigurationException, IOException, SAXException {

    /* Local variables */
    DocumentBuilderFactory documentBuilderFactory;
    DocumentBuilder documentBuilder;
    NodeList parameters;
    Node node;
    Element element;

    xmlFile = new File( configFilePath );
    documentBuilderFactory = DocumentBuilderFactory.newInstance();

    /* Throws ParserConfigurationException */
    documentBuilder = documentBuilderFactory.newDocumentBuilder();

    /* Throws SAXException, IOException */
    this.document = documentBuilder.parse( this.xmlFile );

    /* Read document */
    this.document.getDocumentElement().normalize();
    this.rootNode = document.getChildNodes().item( 0 );
    this.parameterNodeList = rootNode.getChildNodes();
    this.numChildNodes = this.parameterNodeList.getLength();


  }

  public Map< String, Object > getParameters() {
    Map< String, Object > parameterMap
      = new HashMap< String, Object >( this.numChildNodes );
    Node currentNode;
    Element element;

    /* For each child of the root node */
    for ( int p = 0; p < this.numChildNodes; p++ ) {
      currentNode = this.parameterNodeList.item( p );

      if ( currentNode.getNodeType() == Node.ELEMENT_NODE ) {
        element = ( Element ) ( currentNode );
        String nodeText = element.getTextContent();
        String nodeType = element.getAttribute( "dataType" );
        Object value;

        switch ( nodeType ) {
          case "Integer":
            value = Integer.parseInt( nodeText );
            break;
          case "Long":
            value = Long.parseLong( nodeText );
            break;
          case "Double":
            value = Double.parseDouble( nodeText );
            break;
          case "Boolean":
            value = Boolean.parseBoolean( nodeText );
            break;
          default:
            // Assume string
            value = nodeText;
            break;
        }

        parameterMap.put( element.getTagName(), value );
      }

    }

    return parameterMap;
  }

}
