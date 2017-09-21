package grbcp5.hw01.stochastic;

import java.util.Comparator;

public abstract class StochasticDelegate implements Comparator< Individual> {

  public abstract double fitness( Individual i );

  public abstract boolean shouldContinue();

  public abstract void handleNewIndividual( Individual i );

  public abstract Individual repair( Individual i, int lowLoci, int highLoci );

  @Override
  public abstract int compare( Individual i1, Individual i2 );


}
