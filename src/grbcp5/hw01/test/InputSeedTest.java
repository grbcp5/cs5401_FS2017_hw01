package grbcp5.hw01.test;

import grbcp5.hw01.input.ConfigFileReader;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class InputSeedTest {

  @Test
  public void inputSeedTest() {

    String configFilePath;
    ConfigFileReader configFileReader;
    Map< String, Object > parameters;
    Object parameterValue;
    String[] includeInInitialPopulation;

    configFilePath = "config/EASeedingTest.xml";

    try {

      configFileReader = new ConfigFileReader( configFilePath );
      parameters = configFileReader.getParameters();

      /* Assert a value was returned */
      parameterValue = parameters.get( "includeInInitialPopulation" );
      assertNotEquals( null, parameterValue );

      /* Assert the list has the correct length */
      includeInInitialPopulation = ( String[] )parameterValue;
      assertEquals( 1, includeInInitialPopulation.length );

      /* Assert the value is correct */
      assertEquals(
        "initialPopulationValues/50Shape1",
        includeInInitialPopulation[ 0 ]
      );


    } catch ( ParserConfigurationException | IOException | SAXException e ) {
      e.printStackTrace();
    }


  }

}