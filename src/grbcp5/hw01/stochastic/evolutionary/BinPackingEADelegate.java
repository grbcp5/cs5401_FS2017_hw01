package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.Main;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.BinPackingGene;
import grbcp5.hw01.stochastic.BinPackingSolution;
import grbcp5.hw01.stochastic.Gene;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.random.BinPackingRandomSearchDelegate;
import grbcp5.hw01.stochastic.random.RandomSearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BinPackingEADelegate extends EvolutionaryDelegate {


  private Map< String, Object > parameters;
  BinPackingProblemDefinition problemDefinition;
  private int populationSize;

  private RandomSearch randomSearch;
  private RandomSearchDelegateForEADelegate randomSearchDelegate;

  private int numGenerations;
  private int numNewIndividuals;

  private BinPackingSolution currentBest;
  private double currentBestFitness;

  /* Constructor */
  public BinPackingEADelegate(
    Map< String, Object > parameters,
    BinPackingProblemDefinition problemDefinition
  ) {

    this.parameters = parameters;
    this.problemDefinition = problemDefinition;

    this.populationSize =
      ( ( Integer ) ( this.parameters.get( "populationSize" ) ) );

    // Random Search
    Map< String, Object > randomSearchParameters = new HashMap<>();
    randomSearchParameters.put( "fitnessEvals", this.populationSize );
    this.randomSearchDelegate = new RandomSearchDelegateForEADelegate(
      randomSearchParameters,
      problemDefinition
    );
    this.randomSearch = new RandomSearch( this.randomSearchDelegate );

    // Instance variables */
    this.numGenerations = 0;
    this.numNewIndividuals = 0;

    currentBestFitness = -1;
  }


  @Override
  public void signalEndOfGeneration() {
    int run;

    run = ( int ) ( parameters.get( "currentRun" ) );

    System.out.println( "Run " + run + ": End of generation: " + this
      .numGenerations );

    this.numGenerations++;
  }

  @Override
  public boolean shouldContinue() {
    return this.numNewIndividuals <
      ( ( int )( parameters.get( "fitnessEvals" ) ) );
  }

  @Override
  public boolean handleNewIndividual( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution )( i );

    int run;

    run = ( int ) ( parameters.get( "currentRun" ) );
    this.numNewIndividuals++;

    if( this.numNewIndividuals % 100 == 0 ) {
      System.out.println( "Run " + run + ": Used " + this.numNewIndividuals +
                            " evaluations." );
    }

    if( sol.getFreePercentage() > this.currentBestFitness ) {
      this.currentBest = sol;
      this.currentBestFitness = sol.getFreePercentage();

      System.out.println( "New best of " + this.currentBestFitness );
    }

    return this.shouldContinue();
  }

  @Override
  public Individual mutate( Individual i ) {

    Individual result = i.getCopy();
    Random rnd = GRandom.getInstance();
    double mutationRate;

    mutationRate = this.getMutationRate();

    // For each gene location
    for ( int loci = 0; loci < randomSearchDelegate.getGenePoolSize();
          loci++ ) {

      // See if this gene should be mutated
      if ( rnd.nextDouble() <= mutationRate ) {

        // If so, mutate this gene
        result.setGene( loci, randomSearchDelegate.getRandomGene( loci ) );
      }

    }

    return result;
  }

  @Override
  public String getSurviorSelectionMethod() {
    return ( ( String ) ( this.parameters.get( "survivorSelectionMethod" ) ) );
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
    BinPackingSolution result = new BinPackingSolution(
      new BinPackingGene[ this.problemDefinition.getNumShapes() ],
      this.problemDefinition.getShapes(),
      this.problemDefinition.getSheetHeight(),
      this.problemDefinition.getSheetWidth(),
      new Shape( this.problemDefinition.getSheetHeight(), this
        .problemDefinition.getSheetWidth() )
    );

    return result;
  }

  public int getNumChildren() {
    return ( ( int ) ( parameters.get( "numChildren" ) ) );
  }

  @Override
  public Gene getBestGene( Gene g1, Gene g2 ) {
    BinPackingGene bpg1 = ( BinPackingGene ) g1;
    BinPackingGene bpg2 = ( BinPackingGene ) g2;

    if( bpg1 == null || bpg1.getX() < bpg2.getX() ) {
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

    trimW = resultingSheet.getTrimmedWidth();
    totlW = resultingSheet.getNumCols();

    return ( totlW - trimW ) / ( ( double ) ( totlW ) );
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

class RandomSearchDelegateForEADelegate extends BinPackingRandomSearchDelegate {

  private BinPackingSolution[] population;
  private int currentIndex;

  RandomSearchDelegateForEADelegate(
    Map< String, Object > parameters,
    BinPackingProblemDefinition problemDefinition
  ) {
    super( parameters, problemDefinition );

    this.population = new BinPackingSolution[ this.getNumFitnessEvalsLeft() ];
    this.currentIndex = 0;

  }

  @Override
  public boolean handleNewIndividual( Individual i ) {
    population[ currentIndex ] = ( ( BinPackingSolution ) ( i ) );
    currentIndex++;

    return this.shouldContinue();
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
