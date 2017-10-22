package grbcp5.hw01.stochastic;

import grbcp5.hw01.stochastic.evolutionary.PrematureConvergenceException;

import java.util.Comparator;

public abstract class StochasticDelegate implements Comparator< Individual> {

  public abstract double fitness( Individual i );

  public abstract boolean shouldContinue();

  public abstract boolean handleNewIndividual( Individual i );

  public abstract Individual repair( Individual i, int lowLoci, int highLoci );

  public abstract Individual getBestIndividual();

  /* Assignment 1D additions */
  public abstract Individual[] getIndividualsOnBestFront();

  @Override
  public abstract int compare( Individual i1, Individual i2 );


}
