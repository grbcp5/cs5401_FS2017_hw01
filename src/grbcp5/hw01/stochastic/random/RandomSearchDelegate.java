package grbcp5.hw01.stochastic.random;

import grbcp5.hw01.stochastic.Gene;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.StochasticDelegate;

public abstract class RandomSearchDelegate extends StochasticDelegate {

  public abstract int getGenePoolSize();

  public abstract Gene getRandomGene( int loci );

  public abstract Individual repair( Individual i, int lowLoci, int highLoci );

}
