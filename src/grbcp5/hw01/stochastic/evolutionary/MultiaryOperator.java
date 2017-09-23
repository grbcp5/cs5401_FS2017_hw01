package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.stochastic.Gene;
import grbcp5.hw01.stochastic.Individual;

import java.util.Arrays;
import java.util.Random;

public class MultiaryOperator {

  public static Individual nPointCrossOver(
    int n,
    Individual parents[],
    EvolutionaryDelegate delegate
  ) {

    Individual[] result = new Individual[ n + 1 ];
    int currentIdx = 0;

    for ( int i = 0; i < ( n + 1 ); i++ ) {

      result[ i ] =
        parents[ currentIdx ];
      currentIdx = ( currentIdx + 1 ) % parents.length;

    }

    return executeCrossover( n, result, delegate );
  }

  private static Individual executeCrossover(
    int n,
    Individual[] individuals,
    EvolutionaryDelegate delegate
  ) {

    /* Return null if not enough individuals to cross over */
    if ( individuals.length != ( n + 1 ) ) {
      return null;
    }

    /* Return null if individuals don't all have the same length */
    for ( int i = 0; i < ( individuals.length - 1 ); i++ ) {
      if ( individuals[ i ].getGenes().length
        != individuals[ i + 1 ].getGenes().length ) {
        return null;
      }
    }

    /* Local variables */
    int[] crossOverPoints;
    int newCrossoverPoint;
    boolean newCrossoverPointIsValid;
    int numGenes;
    Random rnd;
    int curIdx;
    Gene tryGene;

    /* Initialize */
    rnd = GRandom.getInstance();
    crossOverPoints = new int[ n + 1 ];
    numGenes = individuals[ 0 ].getGenes().length;
    Individual currrentIndividual;
    Individual resultingIndividual;

    /* Create random cross over points */
    for ( int i = 0; i < n; i++ ) {

      do {
        newCrossoverPoint = rnd.nextInt( numGenes - 1 );
        newCrossoverPointIsValid = true;

        for ( int j = 0; j < i; j++ ) {
          if ( newCrossoverPoint == crossOverPoints[ j ] ) {
            newCrossoverPointIsValid = false;
          }
        }

      } while ( !newCrossoverPointIsValid );

      crossOverPoints[ i ] = newCrossoverPoint;
    }
    /* Make last cross over point the last position of the solutions */
    crossOverPoints[ n ] = ( numGenes - 1 );

    /* Put cross over points in order */
    Arrays.sort( crossOverPoints );

    /* Initialize loop variables */
    curIdx = crossOverPoints[ 0 ];
    resultingIndividual = individuals[ 0 ].getCopy();

    for ( int cop = 0; cop < crossOverPoints.length; cop++ ) {
      for ( curIdx = curIdx; curIdx <= crossOverPoints[ cop ]; curIdx++ ) {
        currrentIndividual = individuals[ cop ];

        tryGene = currrentIndividual.getGenes()[ curIdx ].getCopy();
        resultingIndividual.setGene( curIdx, tryGene );

        delegate.repair( resultingIndividual, curIdx, curIdx );

      }
    }

    return null;
  }

}
