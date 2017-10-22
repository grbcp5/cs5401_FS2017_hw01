package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.stochastic.Gene;
import grbcp5.hw01.stochastic.Individual;
import grbcp5.hw01.stochastic.StochasticDelegate;

import java.util.Comparator;

public abstract class EvolutionaryDelegate extends StochasticDelegate {

  public abstract Individual mutate( Individual i );

  public abstract Individual[] getInitialPopulation();

  public abstract Individual getEmptyIndividual();

  public abstract int getPopulationSize();

  public abstract String getParentSelectionMethod();

  public abstract String getSurviorSelectionMethod();

  public abstract int getSurvivalTournamentSize();

  public abstract double getMutationRate();

  public abstract int getNumParentsPerChild();

  public abstract String getMultiaryOperator();

  public abstract int getNumCrossoverPoints();

  public abstract int getNumChildren();

  public abstract Gene getBestGene( Gene g1, Gene g2 );

  public abstract boolean handlePopulation( Individual[] population );

  public abstract int getParentSelectionTournamentSize();

  /* Assignment 1C additons */

  public abstract String getConstraintSatisfactionType();

  public abstract double getPenaltyCoefficient();

  public abstract void handlePotentiallyInvalidIndividual( Individual i );

  public abstract String getSurvivalStrategyType();

  public abstract boolean isMutationRateSelfAdaptive();

}
