package grbcp5.hw01;


import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.input.ConfigFileReader;
import grbcp5.hw01.input.ProblemDefinitonFileReader;
import grbcp5.hw01.stochastic.StochasticDelegate;
import grbcp5.hw01.stochastic.StochasticSearch;
import grbcp5.hw01.stochastic.random.BinPackingRandomSearchDelegate;
import grbcp5.hw01.stochastic.random.RandomSearch;
import grbcp5.hw01.stochastic.random.RandomSearchDelegate;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

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
    long randomSeed;
    ConfigFileReader configFileReader;
    ProblemDefinitonFileReader problemDefinitonFileReader;
    Map< String, Object > parameters;
    BinPackingProblemDefinition problemDefinition;
    StochasticSearch searcher;
    StochasticDelegate delegate;
    long startTime;
    long endTime;
    long elapsedTime;

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

    /* Set random seed */
    if( ( Boolean )( parameters.get( "seedSpecified" ) ) ) {
      randomSeed = ( Long )( parameters.get( "seed" ) );
    } else {
      randomSeed = System.currentTimeMillis();
    }
    GRandom.setInstance( new Random( randomSeed ) );

    /* Get problem definition */
    problemDefinitonFileReader = new ProblemDefinitonFileReader( args[ 1 ] );
    problemDefinition = problemDefinitonFileReader.getProblemDefinition();

    if( parameters.get( "searchType" ).equals( "RandomSearch" )  ) {

      startTime = System.currentTimeMillis();

      for ( int r = 0; r < ( ( Integer ) ( parameters.get( "runs" ) ) ); r++ ) {


        parameters.put( "currentRun", new Integer( r ) );
        delegate = new BinPackingRandomSearchDelegate(
            parameters,
            problemDefinition
        );
        searcher = new RandomSearch(
            ( RandomSearchDelegate ) ( delegate )
        );


        searcher.search();

      }

      endTime = System.currentTimeMillis();

      elapsedTime = endTime - startTime;
      System.out.println( "Total time: " + elapsedTime );


    } else {
      System.out.println( "Cannot handle '" + parameters.get( "searchType" )
                            + "'." );
      return;
    }

  } /* Main function */

} /* Main class */
