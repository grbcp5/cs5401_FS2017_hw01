package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.BinPackingGene;
import grbcp5.hw01.stochastic.BinPackingSolution;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.random.BinPackingRandomSearchDelegate;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class MultiaryOperatorTest {

  public BinPackingSolution createSol1() throws Exception {
    Shape[] shapes = new Shape[] {
      new Shape( "D1" ),
      new Shape( "R1" )
    };

    BinPackingGene[] g1 = new BinPackingGene[] {
      new BinPackingGene( 0, 0, 0 ),
      new BinPackingGene( 1, 2, 0 ),
    };

    Shape resultingSheet = new Shape( 3, 3 );
    resultingSheet = resultingSheet.eat( shapes[ 0 ], 0, 0 );
    resultingSheet = resultingSheet.eat( shapes[ 1 ], 2, 1 );


    BinPackingSolution sol1 = new BinPackingSolution(
      g1,
      shapes,
      3,
      3,
      resultingSheet
    );

    return sol1;
  }

  public BinPackingSolution createSol2() throws Exception {
    Shape[] shapes = new Shape[] {
      new Shape( "D1" ),
      new Shape( "R1" )
    };

    BinPackingGene[] g1 = new BinPackingGene[] {
      new BinPackingGene( 2, 0, 0 ),
      new BinPackingGene( 0, 2, 0 ),
    };

    Shape resultingSheet = new Shape( 3, 3 );
    resultingSheet = resultingSheet.eat( shapes[ 0 ], 0, 2 );
    resultingSheet = resultingSheet.eat( shapes[ 1 ], 2, 0 );


    BinPackingSolution sol1 = new BinPackingSolution(
      g1,
      shapes,
      3,
      3,
      resultingSheet
    );

    return sol1;
  }

  @Test
  public void executeCrossover() throws Exception {

    Shape[] shapes = new Shape[] {
      new Shape( "D1" ),
      new Shape( "R1" )
    };

    BinPackingSolution sol1 = createSol1();
    BinPackingSolution sol2 = createSol2();

    System.out.println( sol1.getResultingSheet() );
    System.out.println( sol2.getResultingSheet() );

    Map< String, Object > parameter = new HashMap<String, Object>();
    parameter.put( "fitnessEvals", 1 );
    parameter.put( "populationSize", 25 );

    BinPackingProblemDefinition pd = new BinPackingProblemDefinition(
      2,
      shapes,
      3,
      3
    );

    GRandom.setInstance( new Random( 10021996 ) );

    EvolutionaryDelegate del = new BinPackingEADelegate(
      parameter, pd
    );

    BinPackingSolution child = (BinPackingSolution ) MultiaryOperator.nPointCrossOver(
      1,
      new Individual[] {
        sol1,
        sol2
      },
      del
    );

    System.out.println( child.getResultingSheet() );


  }

}