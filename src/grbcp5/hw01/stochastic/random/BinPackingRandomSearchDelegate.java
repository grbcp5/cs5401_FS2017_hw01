package grbcp5.hw01.stochastic.random;


import grbcp5.hw01.GRandom;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.BinPackingGene;
import grbcp5.hw01.stochastic.BinPackingSolution;
import grbcp5.hw01.stochastic.Gene;
import grbcp5.hw01.stochastic.Individual;

import java.util.Map;
import java.util.Random;

public class BinPackingRandomSearchDelegate extends RandomSearchDelegate {

  private Map<String, Object> parameters;
  private BinPackingProblemDefinition problemDefinition;

  public BinPackingRandomSearchDelegate( Map<String, Object> parameters,
                                         BinPackingProblemDefinition pd ){
    this.parameters = parameters;
    this.problemDefinition = pd;
  }

  @Override
  public int getGenePoolSize() {
    return problemDefinition.getNumShapes();
  }

  @Override
  public Gene getRandomGene( int loci ) {

    /* Local variabes*/
    Random rnd;
    Shape tryShape;
    int minRow;
    int maxRow;
    int tryRow;

    int minCol;
    int maxCol;
    int tryCol;

    int tryRotation;

    /* Initialize */
    rnd = GRandom.getInstance();
    tryShape = this.problemDefinition.getShapes()[ loci ];

    /* Rotate shape */
    tryRotation = rnd.nextInt( 3 );
    tryShape = tryShape.rotate( tryRotation );

    /* Get random row */
    minRow = tryShape.getStartRow();
    maxRow = this.problemDefinition.getSheetHeight()
      - ( tryShape.getNumRows() - tryShape.getStartRow() );
    tryRow = randInt( rnd, minRow, maxRow );

    /* Get random Column */
    minCol = tryShape.getStartCol();
    maxCol = this.problemDefinition.getSheetWidth()
      - ( tryShape.getNumCols() - tryShape.getStartCol() );
    tryCol = randInt( rnd, minCol, maxCol );

    return new BinPackingGene( tryCol, tryRow, tryRotation );
  }

  private static int randInt( Random rnd, int min, int max ) {
    return rnd.nextInt( ( max - min ) + 1 ) + min;
  }

  @Override
  public Individual repair( Individual i, int loci ) {
    BinPackingSolution sol = ( BinPackingSolution )( i );

    /* Local variables */
    boolean solutionValid;


    return sol;
  }

  @Override
  public double fitness( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) ( i );
    Shape resultingSheet = sol.getResultingSheet();

    return resultingSheet.getTrimmedWidth() / resultingSheet.getNumCols();
  }

  @Override
  public boolean shouldContinue() {
    return false;
  }

  @Override
  public void handleNewIndividual( Individual i ) {

  }

  @Override
  public int compare( Individual i1, Individual i2 ) {
    BinPackingSolution sol1 = ( BinPackingSolution )( i1 );
    BinPackingSolution sol2 = ( BinPackingSolution )( i2 );

    Double fitness1 = this.fitness( sol1 );
    Double fitness2 = this.fitness( sol2 );

    return fitness1.compareTo( fitness2 );
  }
}
