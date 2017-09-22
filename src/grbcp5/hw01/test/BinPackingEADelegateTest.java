package grbcp5.hw01.test;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.input.ProblemDefinitonFileReader;
import grbcp5.hw01.stochastic.BinPackingSolution;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.evolutionary.BinPackingEADelegate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class BinPackingEADelegateTest {

  private BinPackingEADelegate delegateUnderTest;

  @Before
  public void setUp() throws Exception {

    /* Initialize instance variables */
    Map< String, Object > eaParameters;
    BinPackingEADelegate delegateUnderTest;
    BinPackingProblemDefinition problemDefinition;

    int populationSize = 100;
    eaParameters = new HashMap<>();
    eaParameters.put( "populationSize", populationSize );

    problemDefinition = new ProblemDefinitonFileReader(
      "config/50Shapes.txt"
    ).getProblemDefinition();

    this.delegateUnderTest = new BinPackingEADelegate(
      eaParameters,
      problemDefinition
    );

    /* Set random number generator */
    GRandom.setInstance( new Random( 10021996 ) );

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void getInitialPopulation() throws Exception {

    Individual[] resultingPopulation;

    resultingPopulation = delegateUnderTest.getInitialPopulation();

    for ( Individual i :
           resultingPopulation ) {
      System.out.println( "New Shape: " );
      System.out.println(
        ( ( BinPackingSolution ) i ).getResultingSheet()
      );
    }

  }

}