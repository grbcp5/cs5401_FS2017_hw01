package grbcp5.hw01.stochastic;

import grbcp5.hw01.shape.FallOffExcpetion;
import grbcp5.hw01.shape.OverlapException;
import grbcp5.hw01.shape.Shape;

public class BinPackingSolutionChecker {

  public BinPackingSolution checkSolution( BinPackingSolution sol, int
    lowLoci, int highLoci )  {

    /* Local Variables */
    Shape[] shapes;
    BinPackingGene[] genes;
    Shape intermediateSheet;
    BinPackingSolution newSol;

    /* Initialize */
    shapes = sol.getShapes();
    genes = new BinPackingGene[ sol.getGenes().length ];
    for ( int i = 0; i < sol.getGenes().length; i++ ) {
      genes[ i ] = ( BinPackingGene )( sol.getGenes()[ i ] );
    }
    newSol = new BinPackingSolution( genes, sol.getShapes(), sol
      .getSheetHeight(), sol.getSheetWidth(), sol.getResultingSheet() );

    if ( lowLoci < 0 || shapes.length < highLoci || genes.length < highLoci ) {
      return null;
    }

    /* Local Variables */
    Shape rotatedShape;
    int shapeRowIdx;
    int shapeColIdx;

    for ( int i = lowLoci; i <= highLoci; i++ ) {

      rotatedShape = shapes[ i ].rotate( genes[ i ].getRotation() );

      shapeRowIdx = genes[ i ].getY() - rotatedShape.getStartRow();
      shapeColIdx = genes[ i ].getX() - rotatedShape.getStartCol();

      try {
         intermediateSheet = sol.getResultingSheet().eat(
           rotatedShape,
           shapeRowIdx,
           shapeColIdx
         );
         sol.setSheet( intermediateSheet );
      } catch ( FallOffExcpetion | OverlapException FOE ) {
        return null;
      }

    }

    return null;
  }

}
