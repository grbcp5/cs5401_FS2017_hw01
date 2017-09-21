package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.StochasticSearch;
import grbcp5.hw01.stochastic.random.RandomSearch;
import grbcp5.hw01.stochastic.random.RandomSearchDelegate;

import java.util.Map;

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

    // Evolve initial population
    while ( delegate.shouldContinue() ) {

      /* Select parents */
      parents = selectParents( population );

      /* Create children */
      children = createChildren( parents );

      /* Find survivors */
      population = getSurvivors( population, children );

    }

    return delegate.getBestIndividual();
  }

  private Individual[] selectParents( Individual[] population ) {

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



}
