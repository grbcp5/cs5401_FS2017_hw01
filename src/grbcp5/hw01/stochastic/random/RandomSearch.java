package grbcp5.hw01.stochastic.random;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.StochasticSearch;

import java.util.Random;

public class RandomSearch extends StochasticSearch {

  /* Instance variables */
  private RandomSearchDelegate delegate;

  /* Constructor */
  public RandomSearch( RandomSearchDelegate delegate ) {

    this.delegate = delegate;

  }

  @Override
  public Individual search() {

    /* local Variables */
    Random rnd;
    Individual currentIndividual;

    /* Initialize */
    rnd = GRandom.getInstance();

    while ( delegate.shouldContinue() ) {

      /* Initialize loop variables */
      currentIndividual = delegate.getInitialIndividual();

      /* For each gene */
      for ( int loci = 0; loci < delegate.getGenePoolSize(); loci++ ) {

        /* Generate a new random gene */
        currentIndividual.setGene( loci, delegate.getRandomGene( loci ) );

        /* Repair if new gene caused individual to be invalid */
        currentIndividual = delegate.repair( currentIndividual, loci, loci );
      }

      /* Alert delegate of new random individual */
      delegate.handleNewIndividual( currentIndividual );

    }

    return delegate.getBestIndividual();

  }


}
