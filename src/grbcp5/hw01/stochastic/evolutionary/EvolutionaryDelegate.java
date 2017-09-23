package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.StochasticDelegate;

import java.util.Comparator;

public abstract class EvolutionaryDelegate extends StochasticDelegate {

  public abstract Individual mutate( Individual i );

  public abstract Individual[] getInitialPopulation();

  public abstract int getPopulationSize();

  public abstract String getParentSelectionMethod();

  public abstract String getSurviorSelectionMethod();

  public abstract int getSurvivalTournamentSize();

  public abstract void signalEndOfGeneration();

  public abstract double getMutationRate();

  public abstract String getMultiaryOperator();

  public abstract int getNumCrossoverPoints();

  public abstract int getNumChildren();

  public abstract int getParentSelectionTournamentSize();
}
