package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.stochastic.Individual;

public class DelegateTriggeredStopRequest extends Exception {

  private Individual[] individuals;


  public DelegateTriggeredStopRequest( Individual[] individuals ) {
    this.individuals = individuals;
  }

  public Individual[] getIndividuals() {
    return individuals;
  }
}
