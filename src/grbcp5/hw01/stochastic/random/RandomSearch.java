package grbcp5.hw01.stochastic.random;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.stochastic.StochasticSearch;

import java.util.Random;

public class RandomSearch extends StochasticSearch{

  /* Instance variables */
  private RandomSearchDelegate delegate;

  /* Constructor */
  public RandomSearch( RandomSearchDelegate delegate ) {
    this.delegate = delegate;
  }

  @Override
  public void search() {

    /* local Variables */
    Random rnd;


    /* Initialize */
    rnd = GRandom.getInstance();

  }


}
