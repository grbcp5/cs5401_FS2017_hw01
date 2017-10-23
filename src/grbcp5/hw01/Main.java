package grbcp5.hw01;

import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.input.ConfigFileReader;
import grbcp5.hw01.input.ProblemDefinitonFileReader;
import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.BinPackingSolution;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.StochasticDelegate;
import grbcp5.hw01.stochastic.StochasticSearch;
import grbcp5.hw01.stochastic.evolutionary.BinPackingEADelegate;
import grbcp5.hw01.stochastic.evolutionary.EvolutionaryDelegate;
import grbcp5.hw01.stochastic.evolutionary.EvolutionarySearch;
import grbcp5.hw01.stochastic.random.BinPackingRandomSearchDelegate;
import grbcp5.hw01.stochastic.random.RandomSearch;
import grbcp5.hw01.stochastic.random.RandomSearchDelegate;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class Main {

  public static void main( String[] args ) throws FileNotFoundException {

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
    Individual[] currentBestFront = null;
    PrintWriter logWriter;
    PrintWriter solWriter;
    Individual[] bestFront;

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

    int numRuns = ( ( Integer ) ( parameters.get( "runs" ) ) );
    int numFitnessEvals = ( int ) parameters.get( "fitnessEvals" );

    if( !( parameters.get( "debug" ) == null ) && ( boolean ) parameters
      .get( "debug" ) ) {
      Main.debug = true;
    } else {
      Main.debug = false;
    }

    if( !( parameters.get( "showShapes" ) == null ) && ( boolean ) parameters
      .get( "showShapes" ) ) {
      System.out.println( "Read in shapes:" );
      for ( Shape s :
        problemDefinition.getShapes() ) {
        System.out.println( s );
      }
    }

    if( !( parameters.get( "showConfig" ) == null ) && ( boolean ) parameters
      .get( "showConfig" ) ) {
      System.out.println( "Parameters:" );
      for ( String key :
        parameters.keySet() ) {
        System.out.println( key + ": " + parameters.get( key ) );
      }
    }

    logWriter = new PrintWriter(
      ( String ) parameters.get( "logFilePath" )
    );
    parameters.put( "logWriter", logWriter );

    solWriter = new PrintWriter(
      ( String ) parameters.get( "solFilePath" )
    );
    parameters.put( "solWriter", solWriter );


    logWriter.println( "Result Log:\n\n" );
    logWriter.println( "Problem instance: " + args[ 1 ] );
    logWriter.println( "Random number generator seed: " + randomSeed );
    logWriter.println( "Parameters:" );
    for ( String key :
      parameters.keySet() ) {
      logWriter.println( "\t" + key + ": " + parameters.get( key ) );
    }


    /* Run problem */
    startTime = 0;
    for ( int r = 0; r < numRuns; r++ ) {

      parameters.put( "currentRun",  r );
      logWriter.println( "\nRun " + r + ": " );
      startTime = System.currentTimeMillis();

      if ( parameters.get( "searchType" ).equals( "RandomSearch" ) ) {

        delegate = new BinPackingRandomSearchDelegate(
          parameters,
          problemDefinition
        );
        searcher = new RandomSearch(
          ( RandomSearchDelegate ) ( delegate )
        );



      } else if ( parameters.get( "searchType" ).equals( "EvolutionarySearch" )
        ) {

        delegate = new BinPackingEADelegate(
          parameters,
          problemDefinition
        );
        searcher = new EvolutionarySearch(
          ( ( EvolutionaryDelegate ) ( delegate ) )
        );


      } else {
        System.out.println( "Cannot handle '" + parameters.get( "searchType" )
                              + "'." );
        return;
      }

      searcher.search();
      bestFront = delegate.getIndividualsOnBestFront();

      System.out.println( "Best front had " + bestFront.length + " " +
                            "individuals " );
      for ( Individual i :
        bestFront ) {
        BinPackingSolution sol = ( BinPackingSolution ) ( i );

        System.out.println( "Solution " );
        System.out.println( sol.getResultingSheet() );
      }
      printDashedln();

      if( currentBestFront == null || currentBestFront.length <
        bestFront.length ) {
        currentBestFront = bestFront;
      }

    }

    endTime = System.currentTimeMillis();
    elapsedTime = endTime - startTime;
    System.out.println( "Total time: " + elapsedTime );

    solWriter.println( currentBestFront.length + "\n" );
    for ( Individual i : currentBestFront ) {
      solWriter.println( i + "\n" );
    }
    
    logWriter.close();
    solWriter.close();

  } /* Main function */








  public static void printDashedln( int length ) {
    for( int i = 0; i < length; i++ ) {
      System.out.print( '-' );
    }
    System.out.println();
  }

  public static void printDashedln() {
    printDashedln( 80 );
  }

  private static void printInvalidInput() {

    printDashedln();
    System.out.println(
      "\nUsage: main <configFile> <problemDefinitionFile>\n"
    );
    printDashedln();

  }

  private static boolean debug;

  public static boolean debug() {
    return Main.debug;
  }

} /* Main class */
