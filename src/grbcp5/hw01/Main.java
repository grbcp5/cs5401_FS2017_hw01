package grbcp5.hw01;


import grbcp5.hw01.input.ConfigFileReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;

public class Main {

  private static void printDashedln( int length ) {
    for( int i = 0; i < length; i++ ) {
      System.out.print( '-' );
    }
    System.out.println();
  }

  private static void printDashedln() {
    printDashedln( 80 );
  }

   private static void printInvalidInput() {

    printDashedln();
    System.out.println(
      "\nUsage: main <configFile> <problemDefinitionFile>\n"
    );
    printDashedln();

  }

  public static void main( String[] args ) {

    if( args.length < 2 ) {

      printInvalidInput();
      return;
    }

    /* Local Variables */
    ConfigFileReader configFileReader;
    Map< String, Object > parameters;

    try {

      /* Try to read the file */
      configFileReader = new ConfigFileReader( args[ 0 ] );

    } catch ( ParserConfigurationException | IOException | SAXException e ) {
      e.printStackTrace();
      printInvalidInput();
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
