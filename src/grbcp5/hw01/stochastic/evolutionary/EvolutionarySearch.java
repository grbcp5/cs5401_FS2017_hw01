package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.StochasticSearch;

import java.util.*;

public class EvolutionarySearch extends StochasticSearch {

  private EvolutionaryDelegate delegate;

  public EvolutionarySearch( EvolutionaryDelegate delegate ) {

    this.delegate = delegate;

  }

  @Override
  public Individual search() {

    /* Local variables */
    Individual[] population;
    Individual[] incompleteGeneration;
    Individual[] children;

    // create initial population
    population = delegate.getInitialPopulation();

    for ( Individual i :
      population ) {
      delegate.handleNewIndividual( i );
    }

    delegate.signalEndOfGeneration();

    // Evolve initial population
    while ( delegate.shouldContinue() ) {

      /* Create children */
      try {

        children = createChildren( population, delegate.getNumChildren() );

      } catch ( DelegateTriggeredStopRequest e ) {

        incompleteGeneration = e.getIndividuals();

        int length = 0;
        while ( length < incompleteGeneration.length &&
          incompleteGeneration[ length ] != null ) {
          length++;
        }

        children = new Individual[ length ];
        System.arraycopy( incompleteGeneration, 0, children, 0, length );
      }

      /* Find survivors */
      population = getSurvivors( population, children );

      delegate.signalEndOfGeneration();
    }

    delegate.handlePopulation( population );

    return delegate.getBestIndividual();
  }

  private Individual[] selectParents(
    Individual[] population,
    int numParents
  ) {

    int k;
    String method = delegate.getParentSelectionMethod().toLowerCase();

    switch ( method ) {
      case "ktournament":
        k = delegate.getParentSelectionTournamentSize();
        return this.kTournSelection( k, population, numParents, true );

      case "uniformrandom":
        return this.uniformRandomSelection( population, numParents );

      case "fitnessproportional":
      default: // Default to fitnessProportional
        return this
          .fitnessProportionalParentSelection( population, numParents );
    }
  }

  private Individual[] fitnessProportionalParentSelection(
    Individual[] pop,
    int numParents
  ) {

    /* Local variables */
    Individual[] parents;
    int numParentsSelected;
    int totalNumParents;
    int currentIndex;
    int populationSize = delegate.getPopulationSize();
    double[] fitness;
    double[] proportion;
    double totalFitness;
    Random rnd;
    
    /* Initialize */
    numParentsSelected = 0;
    totalNumParents = numParents;
    currentIndex = 0;
    rnd = GRandom.getInstance();
    parents = new Individual[ totalNumParents ];
    fitness = new double[ populationSize ];
    proportion = new double[ populationSize ];

    /* Get each members probability value */
    totalFitness = 0;
    for ( int i = 0; i < populationSize; i++ ) {
      fitness[ i ] = delegate.fitness( pop[ i ] );
      totalFitness += fitness[ i ];
    }

    for ( int i = 0; i < populationSize; i++ ) {
      proportion[ i ] = fitness[ i ] / totalFitness;
    }

    while ( numParentsSelected < totalNumParents ) {

      if ( rnd.nextDouble() <= proportion[ currentIndex ] ) {
        parents[ numParentsSelected++ ] = pop[ currentIndex ];
      }

      currentIndex = ( currentIndex + 1 ) % populationSize;
    }

    return parents;
  }

  private Individual[] uniformRandomSelection(
    Individual[] surplus,
    int numToSelect
  ) {

    /* Local varaibles */
    Individual[] result;
    List< Individual > sur = new LinkedList<>( Arrays.asList( surplus ) );
    int rndIdx;
    Random rnd;

    result = new Individual[ numToSelect ];
    rnd = GRandom.getInstance();

    for ( int i = 0; i < numToSelect; i++ ) {
      rndIdx = rnd.nextInt( sur.size() );
      result[ i ] = sur.remove( rndIdx );
    }

    return result;
  }

  private Individual[] kTournSelection(
    int k,
    Individual[] population,
    int numToSelect,
    boolean replace
  ) {

    /* Local variables */
    List< Individual > pop;
    Individual[] result;
    Random rnd;
    int rndIdx;
    int maxIdx;

    /* Initialize */
    result = new Individual[ numToSelect ];
    Arrays.sort( population, delegate );
    rnd = GRandom.getInstance();

    if( replace ) {
      pop = new ArrayList< Individual >( Arrays.asList( population ) );
    } else {
      pop = new LinkedList< Individual >( Arrays.asList( population ) );
    }

    /* For each number to select */
    for ( int i = 0; i < numToSelect; i++ ) {

      /* Try k different potential individuals */
      maxIdx = -1;
      for ( int j = 0; j < k; j++ ) {

        /* Try at random */
        rndIdx = rnd.nextInt( pop.size() - 1 );

        /* Select the best one */
        if( rndIdx > maxIdx ) {
          maxIdx = rndIdx;
        }
      }

      /* Put selected individual in the result */
      if( replace ) {
        result[ i ] = pop.get( maxIdx );
      } else {
        result[ i ] = pop.remove( maxIdx );
      }

    }

    return result;
  }

  private Individual[] createChildren( Individual[] pop,
                                       int numChildren ) throws
    DelegateTriggeredStopRequest {

    /* Local variables */
    Individual[] children;

    /* Initialize */
    children = new Individual[ numChildren ];

    /* Create each child */
    for ( int c = 0; c < numChildren; c++ ) {

      if( c == 19 ) {
        c = 19;
      }

      children[ c ] = this.createChild(
        selectParents(
          pop,
          delegate.getNumParentsPerChild()
        )
      );
      assert children[ c ] != null;
      children[ c ] = delegate.mutate( children[ c ] );

      // If delegate indicates to stop
      if ( !delegate.handleNewIndividual( children[ c ] ) ) {
        throw new DelegateTriggeredStopRequest( children );
      }

    }

    return children;
  }

  private Individual createChild( Individual[] parents ) {

    /* Local variables */
    Individual child;
    int n;

    switch ( delegate.getMultiaryOperator() ) {
      case "nPointCrossover":
      default: // default to n point crossover
        n = delegate.getNumCrossoverPoints();
        child = MultiaryOperator.nPointCrossOver( n, parents, delegate );
        assert child != null;
    }


    return child;
  }

  private Individual[] getSurvivors( Individual[] population,
                                     Individual[] children ) {

    Individual[] surplus;
    Individual[] survivors;
    String survivalStrategyType;
    int k;

    survivalStrategyType = delegate.getSurvivalStrategyType();

    if( survivalStrategyType.toLowerCase().equals( "comma" ) ) {

      surplus = new Individual[ children.length ];

      System.arraycopy(
        children,
        0,
        surplus,
        0,
        children.length
      );

    } else { /* Default to plus */

      surplus = new Individual[ population.length + children.length ];

      System.arraycopy(
        population,
        0,
        surplus,
        0,
        population.length
      );

      System.arraycopy(
        children,
        0,
        surplus,
        population.length,
        children.length
      );
    }

    switch ( delegate.getSurviorSelectionMethod().toLowerCase() ) {
      case "ktournament":
        k = delegate.getSurvivalTournamentSize();
        survivors = kTournSelection( k, surplus, population.length, false );
        break;

      case "uniformrandom":
        survivors = uniformRandomSelection( surplus, population.length );
        break;

      case "fitnessproportional":
        survivors = fitnessProportionalParentSelection( surplus, population
          .length );
        break;

      case "truncation":
      default: // default to truncation
        survivors = survivalTruncation( surplus, population.length );
    }

    return survivors;
  }

  private Individual[] survivalTruncation(
    Individual[] surplus,
    int populationSize
  ) {

    Individual[] survivors;

    survivors = new Individual[ populationSize ];

    Arrays.sort( surplus, delegate );

    for ( int i = 0; i < populationSize; i++ ) {
      survivors[ i ] = surplus[ ( surplus.length - 1 ) - i ];
    }

    return survivors;
  }

  private void sendToDelegate( Individual[] individuals ) {
    for ( Individual i : individuals ) {
      delegate.handleNewIndividual( i );
    }
  }

}
