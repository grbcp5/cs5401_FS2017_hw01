package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.BinPackingSolution;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.random.BinPackingRandomSearchDelegate;
import grbcp5.hw01.stochastic.random.RandomSearch;

import java.util.HashMap;
import java.util.Map;

public class BinPackingEADelegate extends EvolutionaryDelegate {

  private Map< String , Object > parameters;
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
      ( ( Integer )( this.parameters.get( "populationSize" ) ) );

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
    return false;
  }

  @Override
  public void handleNewIndividual( Individual i ) {

  }

  @Override
  public Individual mutate( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution )( i );



    return sol;
  }

  @Override
  public Individual[] getInitialPopulation() {

    // Randomly generate initial population
    this.randomSearch.search();

    return this.randomSearchDelegate.getPopulation();
  }

  @Override
  public int getPopulationSize() {
    return this.getPopulationSize();
  }

  @Override
  public Individual getBestIndividual() {
    return null;
  }

  @Override
  public Individual repair( Individual i, int lowLoci, int highLoci ) {
    BinPackingSolution sol = ( BinPackingSolution )( i );

    return sol;
  }

  @Override
  public double fitness( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) ( i );
    Shape resultingSheet = sol.getResultingSheet();

    return resultingSheet.getTrimmedWidth() / resultingSheet.getNumCols();
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
  public void handleNewIndividual( Individual i ) {
    population[ currentIndex++ ] = ( ( BinPackingSolution )( i ) );
  }

  @Override
  public Individual getBestIndividual() {
    return null;
  }

  public BinPackingSolution[] getPopulation() {
    return this.population;
  }

}
