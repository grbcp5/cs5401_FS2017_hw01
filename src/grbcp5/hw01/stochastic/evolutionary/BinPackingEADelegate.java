package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.Main;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.*;
import grbcp5.hw01.stochastic.random.BinPackingRandomSearchDelegate;
import grbcp5.hw01.stochastic.random.RandomSearch;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BinPackingEADelegate extends EvolutionaryDelegate {


  private Map< String, Object > parameters;
  private BinPackingProblemDefinition problemDefinition;
  private int populationSize;

  private RandomSearch randomSearch;
  private RandomSearchDelegateForEADelegate randomSearchDelegate;

  private PrintWriter logWriter;
  private PrintWriter solWriter;

  private int numGenerations;
  private int numNewIndividuals;

  private BinPackingSolution currentBest;
  private double currentBestFitness;
  private double fitnessSum;
  private double averageFitness;
  private BinPackingSolution[] population;

  private int currentDenom;

  private int currentBestGeneration;
  private int prematureConverganceThreshold;
  private boolean converged;

  private double penaltyCoefficient;

  private int bound;

  private int invalidIndividuals;

  /* Constructor */
  public BinPackingEADelegate(
    Map< String, Object > parameters,
    BinPackingProblemDefinition problemDefinition
  ) {

    this.parameters = parameters;
    this.problemDefinition = problemDefinition;

    this.bound = this.problemDefinition.getSheetWidth() / 2;

    this.populationSize =
      ( ( Integer ) ( this.parameters.get( "populationSize" ) ) );

    this.logWriter =
      ( PrintWriter ) this.parameters.get( "logWriter" );

    // Random Search
    Map< String, Object > randomSearchParameters = new HashMap<>();
    randomSearchParameters.put( "fitnessEvals", this.populationSize );
    this.randomSearchDelegate = new RandomSearchDelegateForEADelegate(
      randomSearchParameters,
      problemDefinition,
      bound
    );
    this.randomSearch = new RandomSearch( this.randomSearchDelegate );

    // Instance variables */
    this.numGenerations = 0;
    this.numNewIndividuals = 0;
    this.averageFitness = 0;
    this.fitnessSum = 0;

    this.currentDenom = 3;
    this.bound = this.problemDefinition.getSheetWidth() / this.currentDenom;

    this.currentBestFitness = -1;
    this.currentBestGeneration = -1;
    this.population = new BinPackingSolution[ populationSize ];

    this.prematureConverganceThreshold =
      ( int ) parameters.get( "convergenceCriterion" );
    this.converged = false;

    this.penaltyCoefficient = -1;

    this.invalidIndividuals = 0;

  }


  @Override
  public void signalEndOfGeneration() {
    int run;

    run = ( int ) ( parameters.get( "currentRun" ) );

    System.out.println( "Run " + run + ": End of generation: " + this
      .numGenerations );

    this.logWriter.println(
      this.numNewIndividuals + "\t" +
        this.averageFitness + "\t" +
        this.currentBestFitness
    );

    this.numGenerations++;

    System.out.println( "Generation: " + this.numGenerations + " had " + this
      .invalidIndividuals +
    " (" + ( this.invalidIndividuals / ( float ) this.populationSize) + ") " +
      "invalid " +
                          "individuals." );

    this.invalidIndividuals = 0;
  }

  @Override
  public boolean shouldContinue() {

    Integer run;


    if ( this.converged ) {
      run = ( int ) ( parameters.get( "currentRun" ) );
      if ( run == null ) {
        run = 0;
      }
      System.out.println( "Terminating run " + run + " due to premature " +
                            "convergence." );
      return false;
    }

    if ( this.numNewIndividuals >=
      ( ( int ) ( parameters.get( "fitnessEvals" ) ) )
      ) {
      run = ( int ) ( parameters.get( "currentRun" ) );
      if ( run == null ) {
        run = 0;
      }
      System.out.println( "Terminating run " + run + " due to fitness eval " +
                            "exhaustion." );
      return false;
    }

    return true;
  }

  @Override
  public boolean handleNewIndividual( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) ( i );

    int run;

    run = ( int ) ( parameters.get( "currentRun" ) );
    this.numNewIndividuals++;

    // Update average
    this.fitnessSum += this.fitness( sol );
    this.averageFitness = this.fitnessSum / this.numNewIndividuals;

    // Print to keep application responsive
    if ( this.numNewIndividuals % 100 == 0 ) {
      System.out.println( "Run " + run + ": Used " + this.numNewIndividuals +
                            " evaluations." );
    }

    // Update if new best
    if ( this.fitness( sol ) > this.currentBestFitness ) {
      this.currentBest = sol;
      this.currentBestFitness = this.fitness( sol );
      this.currentBestGeneration = this.numGenerations;

      System.out.println( "New best of " + this.currentBestFitness );
      updateBound( sol );
    }

    // Check if premature convergance
    if ( ( this.numGenerations - this.currentBestGeneration ) >=
      this.prematureConverganceThreshold ) {

      this.converged = true;
      return false;

    }

    if( sol.getPenaltyValue() != null ) {
      this.invalidIndividuals++;
    }

    return this.shouldContinue();
  }

  @Override
  public void handlePopulation( Individual[] pop ) {
    this.population = new BinPackingSolution[ pop.length ];

    if ( this.converged && Main.debug() ) {
      System.out.println( "Population: " );
      for ( int i = 0; i < pop.length; i++ ) {
        this.population[ i ] = ( BinPackingSolution ) ( pop[ i ] );

        System.out.println( "\t" + this.fitness( this.population[ i ] ) );
      }
    }

  }

  private int getBound() {
    return this.bound;
  }

  private void updateBound( BinPackingSolution sol ) {

    if( sol.getPenaltyValue() != null ) {
      return;
    }

    if ( this.fitness( sol ) * sol.getSheetWidth() >
      sol.getSheetWidth() - ( sol.getSheetWidth() / this.currentDenom ) ) {
      System.out.println( "Updating bound" );
      this.currentDenom++;
      this.bound = sol.getSheetWidth() / this.currentDenom;
    }

  }

  @Override
  public Individual mutate( Individual i ) {

    BinPackingSolution result = ( BinPackingSolution ) i.getCopy();
    Random rnd = GRandom.getInstance();
    double mutationRate;

    mutationRate = this.getMutationRate();

    // For each gene location
    for ( int loci = 0; loci < randomSearchDelegate.getGenePoolSize();
          loci++ ) {

      // See if this gene should be mutated
      if ( rnd.nextDouble() <= mutationRate ) {

        // If so, mutate this gene
        result.setGene( loci, randomSearchDelegate.getRandomGene(
          loci,
          this.getBound()
        ) );

        // Fix it if it is valid
        if( this.getConstraintSatisfactionType().toLowerCase().equals(
          "penalty" ) ) {

        } else {
          result = ( BinPackingSolution ) repair( result, loci, loci );
        }
      }

    }

    if( this.getConstraintSatisfactionType().toLowerCase().equals(
      "penalty" ) ) {
      this.handlePotentiallyInvalidIndividual( result );
    }

    return result;
  }

  @Override
  public String getSurviorSelectionMethod() {
    return ( ( String ) ( this.parameters.get( "survivorSelectionMethod" ) ) );
  }

  @Override
  public int getNumParentsPerChild() {
    return ( ( int ) ( parameters.get( "parentsPerChild" ) ) );
  }

  @Override
  public int getSurvivalTournamentSize() {
    return (
      ( int ) ( this.parameters.get( "survivorSelectionTournamentSize" ) )
    );
  }

  @Override
  public String getParentSelectionMethod() {
    return ( ( String ) ( this.parameters.get( "parentSelectionMethod" ) ) );
  }

  @Override
  public double getMutationRate() {
    return ( ( double ) ( this.parameters.get( "mutationRate" ) ) );
  }

  @Override
  public int getParentSelectionTournamentSize() {
    return ( ( int ) ( parameters.get( "parentSelectionTournamentSize" ) ) );
  }

  public String getMultiaryOperator() {
    return ( ( String ) ( parameters.get( "multiaryOperator" ) ) );
  }

  public int getNumCrossoverPoints() {
    return ( ( int ) ( parameters.get( "numCrossoverPoints" ) ) );
  }

  @Override
  public Individual[] getInitialPopulation() {

    // Randomly generate initial population
    this.randomSearch.search();

    return this.randomSearchDelegate.getPopulation();
  }

  @Override
  public Individual getEmptyIndividual() {

    return new BinPackingSolution(
      new BinPackingGene[ this.problemDefinition.getNumShapes() ],
      this.problemDefinition.getShapes(),
      this.problemDefinition.getSheetHeight(),
      this.problemDefinition.getSheetWidth(),
      new Shape( this.problemDefinition.getSheetHeight(), this
        .problemDefinition.getSheetWidth() )
    );
  }

  public int getNumChildren() {
    return ( ( int ) ( parameters.get( "numChildren" ) ) );
  }

  @Override
  public Gene getBestGene( Gene g1, Gene g2 ) {
    BinPackingGene bpg1 = ( BinPackingGene ) g1;
    BinPackingGene bpg2 = ( BinPackingGene ) g2;

    if ( bpg1 == null || bpg1.getX() < bpg2.getX() ) {
      return bpg2;
    } else {
      return bpg1;
    }

  }

  @Override
  public int getPopulationSize() {
    return this.populationSize;
  }

  @Override
  public Individual getBestIndividual() {
    return currentBest;
  }

  @Override
  public Individual repair( Individual i, int lowLoci, int highLoci ) {

    /* Random repair */
    return randomSearchDelegate.repair( i, lowLoci, highLoci );

  }

  @Override
  public double fitness( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) ( i );
    Shape resultingSheet = sol.getResultingSheet();
    int trimW;
    int totlW;
    double fitness;
    double penalty = 0.0;

    trimW = resultingSheet.getTrimmedWidth();
    totlW = resultingSheet.getNumCols();

    fitness = ( totlW - trimW ) / ( ( double ) ( totlW ) );

    if ( sol.getPenaltyValue() != null ) {
      penalty = sol.getPenaltyValue();
    }

    return fitness - penalty;
  }

  @Override
  public int compare( Individual i1, Individual i2 ) {
    BinPackingSolution sol1 = ( BinPackingSolution ) ( i1 );
    BinPackingSolution sol2 = ( BinPackingSolution ) ( i2 );

    Double fitness1 = this.fitness( sol1 );
    Double fitness2 = this.fitness( sol2 );

    return fitness1.compareTo( fitness2 );
  }


  /*

  Assignment 1C additions

   */

  @Override
  public String getConstraintSatisfactionType() {
    return ( String ) parameters.get( "constraintSatisfaction" );
  }

  @Override
  public double getPenaltyCoefficient() {

    if ( this.penaltyCoefficient < 0 ) {
      this.penaltyCoefficient
        = ( Double ) parameters.get( "penaltyCoefficient" );
    }

    assert this.penaltyCoefficient >= 0;

    return this.penaltyCoefficient;
  }

  @Override
  public void handlePotentiallyInvalidIndividual( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) i;
    BinPackingSolution check;

    check = BinPackingSolutionChecker.checkSolution( sol, 0, sol.getShapes()
      .length - 1 );

    if( check != null ) {
      sol.setPenaltyValue( null );
      sol.setSheet( check.getResultingSheet() );
      return;
    }

    sol.setPenaltyValue( this.getPenaltyCoefficient() );
    sol.setSheet( BinPackingSolutionChecker.getSheetWithoutConcern( sol ) );

  }

} /* Bin packing EA delegate */



/*

  Random search for EA delegate

 */



class RandomSearchDelegateForEADelegate extends BinPackingRandomSearchDelegate {

  private BinPackingSolution[] population;
  private int currentIndex;
  private int bound;

  RandomSearchDelegateForEADelegate(
    Map< String, Object > parameters,
    BinPackingProblemDefinition problemDefinition,
    int bound
  ) {
    super( parameters, problemDefinition );

    this.population = new BinPackingSolution[ this.getNumFitnessEvalsLeft() ];
    this.currentIndex = 0;
    this.bound = bound;

  }

  @Override
  public boolean handleNewIndividual( Individual i ) {
    population[ currentIndex ] = ( ( BinPackingSolution ) ( i ) );
    currentIndex++;

    return this.shouldContinue();
  }

  @Override
  public Individual repair( Individual i, int lowLoci, int highLoci ) {

    /* Local variables */
    BinPackingSolution resultingSoluiton;
    BinPackingSolution newSolution;
    int bound;
    int numTries;

    /* Initialize */
    resultingSoluiton = ( BinPackingSolution ) ( i.getCopy() );
    newSolution = BinPackingSolutionChecker.checkSolution(
      resultingSoluiton,
      lowLoci,
      highLoci
    );

    numTries = 0;

    /* Until a valid solution is found */
    while ( newSolution == null ) {

      numTries++;

      /* Refil each location with a new random gene */
      for ( int loci = lowLoci; loci <= highLoci; loci++ ) {

        bound = this.bound + numTries;

        resultingSoluiton.setGene(
          loci,
          this.getRandomGene(
            loci,
            bound
          ) );
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

  public Gene getRandomGene( int loci, int maxCol ) {

    /* Local variabes*/
    Random rnd;
    Shape tryShape;
    int minRow;
    int maxRow;
    int tryRow;

    int minCol;
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
    tryCol = randInt( rnd, minCol, maxCol );

    /* Return generated random gene configuration */
    return new BinPackingGene( tryCol, tryRow, tryRotation );
  }

  /* Private helper for getRandomGene */
  private static int randInt( Random rnd, int min, int max ) {

    if ( !( ( max - min ) + 1 > 0 ) ) {
      max = max;
    }

    return rnd.nextInt( ( max - min ) + 1 ) + min;
  }

  void setBound( int bnd ) {
    this.bound = bnd;
  }

  @Override
  public boolean shouldContinue() {
    return this.currentIndex < this.population.length;
  }

  @Override
  public Individual getBestIndividual() {
    return null;
  }

  public BinPackingSolution[] getPopulation() {
    return this.population;
  }


}
