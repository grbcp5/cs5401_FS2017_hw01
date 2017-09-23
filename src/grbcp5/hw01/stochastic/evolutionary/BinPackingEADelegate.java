package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.BinPackingSolution;
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

  }

  @Override
  public boolean shouldContinue() {
    return true;
  }

  @Override
  public boolean handleNewIndividual( Individual i ) {

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

  public int getNumChildren() {
    return ( ( int ) ( parameters.get( "numChildren" ) ) );
  }

  @Override
  public int getPopulationSize() {
    return this.populationSize;
  }

  @Override
  public Individual getBestIndividual() {
    return null;
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

    this.population = new BinPackingSolution[ getNumFitnessEvalsLeft() ];
    this.currentIndex = 0;

  }

  @Override
  public boolean handleNewIndividual( Individual i ) {
    population[ currentIndex++ ] = ( ( BinPackingSolution ) ( i ) );

    return this.shouldContinue();
  }

  @Override
  public Individual getBestIndividual() {
    return null;
  }

  public BinPackingSolution[] getPopulation() {
    return this.population;
  }

}
