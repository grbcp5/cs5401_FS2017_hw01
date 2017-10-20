package grbcp5.hw01.stochastic.random;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.BinPackingGene;
import grbcp5.hw01.stochastic.BinPackingSolution;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class BinPackingRandomSearchDelegateTest {
  @Test
  public void repair() throws Exception {

    Shape[] shapes = new Shape[] {
      new Shape( "D1" ),
      new Shape( "R1" )
    };

    BinPackingGene[] invalidGenes = new BinPackingGene[] {
      new BinPackingGene( 0, 0, 0 ),
      new BinPackingGene( 0, 0, 0 ),
    };

    Shape resultingSheet = new Shape( 3, 3 );
    resultingSheet = resultingSheet.eat( shapes[ 0 ], 0, 0 );


    BinPackingSolution invalidSol = new BinPackingSolution(
      invalidGenes,
      shapes,
      3,
      3,
      resultingSheet
    );

    System.out.println( invalidSol.getResultingSheet() );

    Map< String, Object > parameter = new HashMap<String, Object>();
    parameter.put( "fitnessEvals", 1 );

    BinPackingProblemDefinition pd = new BinPackingProblemDefinition(
      2,
      shapes,
      3,
      3
    );

    GRandom.setInstance( new Random( 10021996 ) );

    BinPackingRandomSearchDelegate del = new BinPackingRandomSearchDelegate(
      parameter, pd);

    BinPackingSolution validSol;
    validSol = ( BinPackingSolution ) del.repair( invalidSol, 1, 1 );

    System.out.println( validSol.getResultingSheet() );

  }

}