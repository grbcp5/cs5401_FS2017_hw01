package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.shape.Shape;
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

  public static Individual executeCrossover(
    int n,
    Individual[] individuals,
    EvolutionaryDelegate delegate
  ) {

    /* Return null if not enough individuals to cross over */
    if ( n == 0 || individuals.length != ( n + 1 ) ) {
      assert false;
      return null;
    }

    /* Return null if individuals don't all have the same length */
    for ( int i = 0; i < ( individuals.length - 1 ); i++ ) {
      if ( individuals[ i ].getGenes().length
        != individuals[ i + 1 ].getGenes().length ) {
        assert false;
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
    curIdx = 0;
    resultingIndividual = delegate.getEmptyIndividual();

    for ( int cop = 0; cop < crossOverPoints.length; cop++ ) {
      currrentIndividual = individuals[ cop ];
      for ( curIdx = curIdx; curIdx <= crossOverPoints[ cop ]; curIdx++ ) {

        tryGene = currrentIndividual.getGenes()[ curIdx ].getCopy();
        resultingIndividual.setGene( curIdx, tryGene );

        if( delegate.getConstraintSatisfactionType().toLowerCase()
                    .equals( "penalty" ) ) {

        } else { // Default to repair function
          resultingIndividual = delegate.repair( resultingIndividual, curIdx,
                                                 curIdx );
        }


      }
    }

    if( delegate.getConstraintSatisfactionType().toLowerCase()
                .equals( "penalty" ) ) {
      delegate.handlePotentiallyInvalidIndividual( resultingIndividual );
    }

    return resultingIndividual;
  }

  private static Individual executeLookInCrossover(
    Individual[] individuals,
    EvolutionaryDelegate delegate
  ) {

    /* Return null if individuals don't all have the same length */
    for ( int i = 0; i < ( individuals.length - 1 ); i++ ) {
      if ( individuals[ i ].getGenes().length
        != individuals[ i + 1 ].getGenes().length ) {
        assert false;
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
    Gene bestGene;
    Gene tryGene;

    /* Initialize */
    rnd = GRandom.getInstance();
    numGenes = individuals[ 0 ].getGenes().length;
    Individual currrentIndividual;
    Individual resultingIndividual;

    /* Initialize loop variables */
    resultingIndividual = individuals[ 0 ].getCopy();

    for( int i = 0; i < numGenes; i++ ) {

      bestGene = null;
      for ( int j = 0; j < individuals.length; j++ ) {
        tryGene = individuals[ j ].getGene( i );
        bestGene = delegate.getBestGene( bestGene, tryGene );
      }

      resultingIndividual.setGene( i, bestGene );
      delegate.repair( resultingIndividual, i, i );

    }

    return resultingIndividual;
  }

}
