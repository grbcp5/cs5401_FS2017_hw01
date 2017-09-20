package grbcp5.hw01;


import grbcp5.hw01.input.ConfigFileReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;

public class Main {

  public static void main( String[] args ) {

    ConfigFileReader configFileReader;
    Map< String, Object > parameters;

    try {

      /* Try to read the file */
      configFileReader = new ConfigFileReader( args[ 0 ] );

    } catch ( ParserConfigurationException | IOException | SAXException e ) {
      e.printStackTrace();
      return;
    }

    /* Get parameters */
    parameters = configFileReader.getParameters();
    Double d = ( Double ) ( parameters.get( "testDouble" ) );
    Integer i = ( Integer )( parameters.get( "testInteger" ) );
    String s = ( String )( parameters.get( "testString" ) );

    System.out.println( "Double: " + d + "\nInteger: " + i + "\nString: " + s );

  } /* Main function */

} /* Main class */
