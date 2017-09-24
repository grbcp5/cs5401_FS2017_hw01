package grbcp5.hw01;


import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.input.ConfigFileReader;
import grbcp5.hw01.input.ProblemDefinitonFileReader;
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
import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class Main {

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
    Individual runBest;
    double runBestFitness;
    Individual currentBest = null;
    double currentBestFitness = -1;

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
    parameters.put( "fitnessEvals", ( numFitnessEvals / numRuns ) );

    /* Run problem */
    startTime = 0;
    for ( int r = 0; r < numRuns; r++ ) {

      parameters.put( "currentRun",  r );
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

      runBest = searcher.search();
      runBestFitness = delegate.fitness( runBest );

      if ( runBestFitness > currentBestFitness ) {
        currentBestFitness = runBestFitness;
        currentBest = runBest;

        System.out.println( "Run produced a better individual." );
      }

    }

    endTime = System.currentTimeMillis();
    elapsedTime = endTime - startTime;
    System.out.println( "Total time: " + elapsedTime );
    System.out.println( "Best individual (" + currentBestFitness + "): " );
    System.out.println(
      ( ( BinPackingSolution ) ( currentBest ) ).getResultingSheet()
    );

  } /* Main function */

} /* Main class */
