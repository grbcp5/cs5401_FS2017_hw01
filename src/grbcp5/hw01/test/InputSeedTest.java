package grbcp5.hw01.test;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.input.ConfigFileReader;
import grbcp5.hw01.input.ProblemDefinitonFileReader;
import grbcp5.hw01.stochastic.BinPackingSolution;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.evolutionary.BinPackingEADelegate;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class InputSeedTest {

  @Before
  public void setUp() throws Exception {
    Random rnd = new Random( 10021996 );
    GRandom.setInstance( rnd );
  }

  @Test
  public void inputSeedTest() {

    String configFilePath;
    ConfigFileReader configFileReader;
    Map< String, Object > parameters;
    ProblemDefinitonFileReader pdfr;
    String pd_FilePath;
    BinPackingProblemDefinition bppd;
    Object parameterValue;
    String[] includeInInitialPopulation;
    BinPackingEADelegate delegate;
    Individual[] pop;

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

      pd_FilePath = "config/50Shapes.txt";
      pdfr = new ProblemDefinitonFileReader( pd_FilePath );
      bppd = pdfr.getProblemDefinition();

      delegate = new BinPackingEADelegate( parameters, bppd );

      pop = delegate.getInitialPopulation();

      for( int i = 0; i < pop.length; i++ ) {
        System.out.println( "\n" + i + ": " );
        System.out.println( ( ( BinPackingSolution) pop[ i ]).getResultingSheet() );
      }

    } catch ( ParserConfigurationException | IOException | SAXException e ) {
      e.printStackTrace();
    }


  }

}