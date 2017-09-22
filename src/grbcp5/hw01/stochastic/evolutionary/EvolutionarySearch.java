package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.StochasticSearch;
import grbcp5.hw01.stochastic.random.RandomSearch;
import grbcp5.hw01.stochastic.random.RandomSearchDelegate;

import java.util.Map;
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
    Individual[] parents;
    Individual[] children;

    // create initial population
    population = delegate.getInitialPopulation();
    sendToDelegate( population );

    // Evolve initial population
    while ( delegate.shouldContinue() ) {

      /* Select parents */
      parents = selectParents( population );
      sendToDelegate( parents );

      /* Create children */
      children = createChildren( parents );

      /* Find survivors */
      population = getSurvivors( population, children );

    }

    return delegate.getBestIndividual();
  }

  private Individual[] selectParents( Individual[] population ) {

    int k;
    String method = delegate.getParentSelectionMethod();

    switch ( method ) {
      case "kTournament":
        k = delegate.getParentSelectionTournamentSize();
        return this.kTournSelWithReplacement( k, population );

      case "fitnessProportional":
      default: // Default to fitnessProportional
        return this.fitnessProportionalParentSelection( population );
    }
  }

  private Individual[] fitnessProportionalParentSelection( Individual[] pop ) {

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
    totalNumParents = delegate.getNumParents();
    currentIndex = 0;
    rnd = GRandom.getInstance();
    parents = new Individual[ totalNumParents ];
    fitness = new double[ populationSize ];
    proportion = new double[ populationSize ];

    /* Get each members probability value */
    totalFitness = 0;
    for( int i = 0; i < populationSize; i++ ) {
      fitness[ i ] = delegate.fitness( pop[ i ] );
      totalFitness += fitness[ i ];
    }

    for ( int i = 0; i < populationSize; i++ ) {
      proportion[ i ] = fitness[ i ] / totalFitness;
    }

    while( numParentsSelected < totalNumParents ) {

      if( rnd.nextDouble() <= proportion[ currentIndex ] ) {
        System.out.println( "New Parent selected" );
        parents[ numParentsSelected++ ] = pop[ currentIndex ];
      }

      currentIndex = ( currentIndex + 1 ) % populationSize;
    }
    
    return parents;
  }

  private Individual[] kTournSelWithReplacement( int k, Individual[] pop ) {

    return null;
  }

  private Individual[] createChildren( Individual[] parents ) {

    return null;
  }

  private Individual createChild( Individual[] parents ) {

    return null;
  }

  private Individual[] getSurvivors( Individual[] population, Individual[] children ) {

    return null;
  }
  
  
  private void sendToDelegate( Individual[] individuals ) {
    for ( Individual i : individuals ) {
      delegate.handleNewIndividual( i );
    }
  }

}
