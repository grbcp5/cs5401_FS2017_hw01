package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.BinPackingSolution;
import grbcp5.hw01.stochastic.Individual;

public class BinPackingEADelegate extends EvolutionaryDelegate {

  @Override
  public boolean shouldContinue() {
    return false;
  }

  @Override
  public void handleNewIndividual( Individual i ) {

  }

  @Override
  public Individual mutate( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution )( i );



    return sol;
  }

  @Override
  public Individual repair( Individual i, int loci ) {
    BinPackingSolution sol = ( BinPackingSolution )( i );

    return sol;
  }

  @Override
  public double fitness( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) ( i );
    Shape resultingSheet = sol.getResultingSheet();

    return resultingSheet.getTrimmedWidth() / resultingSheet.getNumCols();
  }

  @Override
  public int compare( Individual i1, Individual i2 ) {
    BinPackingSolution sol1 = ( BinPackingSolution )( i1 );
    BinPackingSolution sol2 = ( BinPackingSolution )( i2 );

    Double fitness1 = this.fitness( sol1 );
    Double fitness2 = this.fitness( sol2 );

    return fitness1.compareTo( fitness2 );
  }
}
