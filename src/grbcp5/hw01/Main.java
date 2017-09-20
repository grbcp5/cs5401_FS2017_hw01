package grbcp5.hw01;


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

public class Main {

  public static void main( String[] args ) {

    /* Local variables */
    File xmlFile;
    DocumentBuilderFactory documentBuilderFactory;
    DocumentBuilder documentBuilder;
    Document document;
    Node rootNode;
    NodeList parameters;
    Node node;
    Element element;

    /* Reference File */
    xmlFile = new File( args[ 0 ] );
    documentBuilderFactory = DocumentBuilderFactory.newInstance();

    /* Try to read file */
    try {

      /* Throws ParserConfigurationException */
      documentBuilder = documentBuilderFactory.newDocumentBuilder();

      /* Throws SAXException, IOException */
      document = documentBuilder.parse( xmlFile );

      /* Read document */
      document.getDocumentElement().normalize();
      rootNode = document.getChildNodes().item( 0 );
      parameters = rootNode.getChildNodes();

      /* For each child of the root node */
      for( int p = 0; p < parameters.getLength(); p++ ) {
        node = parameters.item( p );

        if (node.getNodeType() == Node.ELEMENT_NODE) {
          element = ( Element )( node );
          System.out.print( element.getTagName() + ": " );
          System.out.println( element.getTextContent() );
        }

      }

    /* Catch errors */
    } catch ( ParserConfigurationException e ) {
      e.printStackTrace();
    } catch ( SAXException e ) {
      e.printStackTrace();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

  } /* Main function */

} /* Main class */
