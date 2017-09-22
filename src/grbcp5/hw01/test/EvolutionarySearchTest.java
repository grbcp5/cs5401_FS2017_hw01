package grbcp5.hw01.test;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.input.ProblemDefinitonFileReader;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.evolutionary.BinPackingEADelegate;
import grbcp5.hw01.stochastic.evolutionary.EvolutionarySearch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class EvolutionarySearchTest {

  private EvolutionarySearch searcherUnderTest;
  private EADelegateParentTest searcherDelegate;

  @Before
  public void setUp() throws Exception {

    /* Initialize instance variables */
    Map< String, Object > eaParameters;
    BinPackingEADelegate delegateUnderTest;
    BinPackingProblemDefinition problemDefinition;

    int populationSize = 100;
    int numParents = populationSize / 2;
    int tournamentSize = populationSize / numParents;
    eaParameters = new HashMap<>();
    eaParameters.put( "populationSize", populationSize );
    eaParameters.put( "parentSelection", "fitnessProportional" );
    eaParameters.put( "numParents", numParents );
    eaParameters.put( "parentSelectionTournamentSize", tournamentSize );

    problemDefinition = new ProblemDefinitonFileReader(
      "config/50Shapes.txt"
    ).getProblemDefinition();

    searcherDelegate = new EADelegateParentTest(
      eaParameters,
      problemDefinition
    );

    this.searcherUnderTest = new EvolutionarySearch( searcherDelegate );

    /* Set random number generator */
    GRandom.setInstance( new Random( 10021996 ) );

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void parentSelection() throws Exception {

    Individual[] population;
    Individual[] resultingParents;
    boolean parentFoundInPopulation;
    double totalFitness = 0;

    this.searcherUnderTest.search();
    population = searcherDelegate.getPopulation();
    resultingParents = searcherDelegate.getParents();

    totalFitness = 0;
    for ( int i = 0; i < searcherDelegate.getPopulationSize(); i++ ) {
      totalFitness += searcherDelegate.fitness( population[ i ] );
    }

    double averageFitness = ( ( ( double )( totalFitness ) )
      / ( ( double ) searcherDelegate.getPopulationSize() ) );

    for ( Individual parent : resultingParents ) {

      parentFoundInPopulation = false;
      for ( Individual i : population ) {
        if( parent == i ) {
          parentFoundInPopulation = true;
          break;
        }
      }
      assertEquals( true, parentFoundInPopulation );

      if( searcherDelegate.fitness( parent ) < averageFitness ) {
        System.out.print( "Less --> " );
      }
      System.out.println( "Parent with fitness: " +
        searcherDelegate.fitness( parent ) + " selected." );
    }

    System.out.println( "Average population fitness: " + averageFitness );

  }

}


class EADelegateParentTest extends BinPackingEADelegate {

  private int count;
  private int individualsRecieved;
  private Individual[] parents;
  private Individual[] population;

  public EADelegateParentTest(
  Map< String, Object > parameters,
  BinPackingProblemDefinition problemDefinition
  )
  {
    super( parameters, problemDefinition );

    count = 1;
    individualsRecieved = 0;

    parents = new Individual[ super.getNumParents() ];
    population = new Individual[ super.getPopulationSize() ];
  }

  @Override
  public boolean shouldContinue() {
    return count-- > 0;
  }

  @Override
  public void handleNewIndividual( Individual i ) {
    System.out.println( "Recieved individual " + individualsRecieved );
    if( individualsRecieved < super.getPopulationSize() ) {
      population[ individualsRecieved++ ] = i;
    } else {
      parents[ individualsRecieved++ - super.getPopulationSize() ] = i;
    }
  }

  public Individual[] getParents() {
    return parents;
  }

  public Individual[] getPopulation() {
    return population;
  }
}

