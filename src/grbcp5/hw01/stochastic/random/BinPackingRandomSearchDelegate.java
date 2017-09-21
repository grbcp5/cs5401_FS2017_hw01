package grbcp5.hw01.stochastic.random;


import grbcp5.hw01.GRandom;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.*;

import java.util.Map;
import java.util.Random;

public class BinPackingRandomSearchDelegate extends RandomSearchDelegate {

  private Map< String, Object > parameters;
  private BinPackingProblemDefinition problemDefinition;
  private int numFitnessEvalsLeft;
  private BinPackingSolution currentBest;
  private double currentBestFitness;
  private int newBestInstanceNumber;
  private long startTime;

  public BinPackingRandomSearchDelegate( Map< String, Object > parameters,
                                         BinPackingProblemDefinition pd ) {
    this.parameters = parameters;
    this.problemDefinition = pd;
    this.numFitnessEvalsLeft = ( Integer ) ( parameters.get( "fitnessEvals" ) );
    this.currentBestFitness = -1.0;
    this.newBestInstanceNumber = 0;
  }

  @Override
  public int getGenePoolSize() {
    return this.problemDefinition.getNumShapes();
  }

  @Override
  public Individual getInitialIndividual() {

    BinPackingGene[] genes
      = new BinPackingGene[ this.problemDefinition.getNumShapes() ];
    for ( int g = 0; g < this.problemDefinition.getNumShapes(); g++ ) {
      genes[ g ] = new BinPackingGene();
    }

    return new BinPackingSolution( genes,
      this.problemDefinition.getShapes(),
      this.problemDefinition.getSheetHeight(),
      this.problemDefinition.getSheetWidth(),
      new Shape(
        this.problemDefinition.getSheetHeight(),
        this.problemDefinition.getSheetWidth()
      )
    );
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

    /* Return generated random gene configuration */
    return new BinPackingGene( tryCol, tryRow, tryRotation );
  }

  /* Private helper for getRandomGene */
  private static int randInt( Random rnd, int min, int max ) {
    return rnd.nextInt( ( max - min ) + 1 ) + min;
  }

  @Override
  public Individual repair( Individual i, int lowLoci, int highLoci ) {

    /* Local variables */
    BinPackingSolution resultingSoluiton;
    BinPackingSolution newSolution;

    /* Initialize */
    resultingSoluiton = ( BinPackingSolution ) ( i.getCopy() );
    newSolution = BinPackingSolutionChecker.checkSolution(
      resultingSoluiton,
      lowLoci,
      highLoci
    );

    /* Until a valid solution is found */
    while ( newSolution == null ) {

      /* Refil each location with a new random gene */
      for ( int loci = lowLoci; loci <= highLoci; loci++ ) {
        resultingSoluiton.setGene( loci, this.getRandomGene( loci ) );
      }

      /* Check to see if that solution is valid */
      newSolution = BinPackingSolutionChecker.checkSolution(
        resultingSoluiton,
        lowLoci,
        highLoci
      );

    }

    return newSolution;
  }

  @Override
  public double fitness( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) ( i );
    Shape resultingSheet = sol.getResultingSheet();
    int trimW;
    int totlW;

    trimW = resultingSheet.getTrimmedWidth();
    totlW = resultingSheet.getNumCols();

    return ( totlW - trimW ) / ( ( double )( totlW ) );
  }

  @Override
  public boolean shouldContinue() {

    this.startTime = System.currentTimeMillis();

    return ( numFitnessEvalsLeft-- > 0 );
  }

  public int getNumFitnessEvalsLeft() {
    return numFitnessEvalsLeft;
  }

  @Override
  public void handleNewIndividual( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) ( i );

    /* Local Variables */
    double solFitness;

    /* Initialize */
    solFitness = this.fitness( sol );

    if ( solFitness > currentBestFitness ) {
      this.newBestInstanceNumber++;
      this.currentBest = ( BinPackingSolution ) ( sol.getCopy() );
      this.currentBestFitness = solFitness;

      System.out.println( "Found new best (" + solFitness + ") for the " +
        this.newBestInstanceNumber + " time."
      );
    }

    if ( this.numFitnessEvalsLeft % 100 == 0 ) {

      long stopTime = System.currentTimeMillis();
      long elapsedTime = stopTime - this.startTime;

      System.out.println(
        "Run " + parameters.get( "currentRun" ) +
          " eval " + this.numFitnessEvalsLeft +
          " ( " + elapsedTime + " )"
      );

    }


  }

  @Override
  public Individual getBestIndividual() {
    return this.currentBest.getCopy();
  }

  @Override
  public int compare( Individual i1, Individual i2 ) {
    BinPackingSolution sol1 = ( BinPackingSolution ) ( i1 );
    BinPackingSolution sol2 = ( BinPackingSolution ) ( i2 );

    Double fitness1 = this.fitness( sol1 );
    Double fitness2 = this.fitness( sol2 );

    return fitness1.compareTo( fitness2 );
  }

}
