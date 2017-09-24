package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.StochasticSearch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

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

    // Evolve initial population
    while ( delegate.shouldContinue() ) {

      /* Create children */
      try {

        children = createChildren( population, delegate.getNumChildren() );

      } catch ( DelegateTriggeredStopRequest e ) {

        incompleteGeneration = e.getIndividuals();

        int length = 0;
        while ( incompleteGeneration[ length ] != null ) {
          length++;
        }

        children = new Individual[ length ];
        System.arraycopy( incompleteGeneration, 0, children, 0, length );
      }

      /* Find survivors */
      population = getSurvivors( population, children );

      delegate.signalEndOfGeneration();
    }

    return delegate.getBestIndividual();
  }

  private Individual[] selectParents(
    Individual[] population,
    int numParents
  ) {

    int k;
    String method = delegate.getParentSelectionMethod();

    switch ( method ) {
      case "kTournament":
        k = delegate.getParentSelectionTournamentSize();
        return this.kTournSelection( k, population, numParents );

      case "fitnessProportional":
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
        System.out.println( "New Parent selected" );
        parents[ numParentsSelected++ ] = pop[ currentIndex ];
      }

      currentIndex = ( currentIndex + 1 ) % populationSize;
    }

    return parents;
  }

  private Individual[] kTournSelection(
    int k,
    Individual[] pop,
    int returnSize
  ) {

    /* Local Variables */
    Individual[] parents;
    LinkedList<Individual> potentialParents;
    Individual[][] groups;
    int[] parentsPutInGroup;
    Random rnd;
    int groupToPutParentIn;
    int maxGroupSize;
    int numSelected;
    int rndIdx;

    /* Initialize */
    numSelected = pop.length / k;
    parents = new Individual[ returnSize ];
    potentialParents = new LinkedList<>();
    groups = new Individual[ numSelected ][ k ];
    parentsPutInGroup = new int[ numSelected ];
    rnd = GRandom.getInstance();
    maxGroupSize = k;

    // Split parents up groups
    for ( Individual parent : pop ) {
      // Pick a random group to put potential parent in
      groupToPutParentIn = rnd.nextInt( numSelected );

      // If that group is already full...
      while ( parentsPutInGroup[ groupToPutParentIn ] == maxGroupSize ) {
        // see if next group is full
        groupToPutParentIn =
          ( groupToPutParentIn + 1 ) % numSelected;
      }

      // Put parent in group
      groups[ groupToPutParentIn ][ parentsPutInGroup[ groupToPutParentIn ]++ ]
        = parent;

    }

    // Pick best potential parent from each group
    for ( int p = 0; p < numSelected; p++ ) {
        Arrays.sort( groups[ p ], delegate );
        potentialParents.addLast( groups[ p ][ k - 1 ] );
    }

    // Return only the desired number of parents
    for ( int i = 0; i < returnSize; i++ ) {
      rndIdx = rnd.nextInt( potentialParents.size() );
      parents[ i ] = potentialParents.remove( rndIdx );
    }

    return parents;
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
      children[ c ] = this.createChild( pop );
      children[ c ] = delegate.mutate( children[ c ] );

      // If delegate indicates to stop
      if( !delegate.handleNewIndividual( children[ c ] ) ) {
        throw new DelegateTriggeredStopRequest( children );
      }

    }

    return children;
  }

  private Individual createChild( Individual[] parents ) {

    /* Local variables */
    Individual child;
    int n;

    /* Initialize */
    n = delegate.getNumCrossoverPoints();

    switch ( delegate.getMultiaryOperator() ) {
      case "nPointCrossover":
      default: // default to n point crossover
        child = MultiaryOperator.nPointCrossOver( n, parents, delegate );
    }

    return child;
  }

  private Individual[] getSurvivors( Individual[] population,
                                     Individual[] children ) {

    Individual[] surplus;
    Individual[] survivors;
    int k;

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

    switch ( delegate.getSurviorSelectionMethod() ) {
      case "kTournament":
        k = delegate.getSurvivalTournamentSize();
        survivors = kTournSelection( k, surplus, population.length );
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
