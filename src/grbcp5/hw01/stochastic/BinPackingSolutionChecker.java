package grbcp5.hw01.stochastic;

import grbcp5.hw01.shape.FallOffExcpetion;
import grbcp5.hw01.shape.OverlapException;
import grbcp5.hw01.shape.Shape;

public class BinPackingSolutionChecker {

  public static BinPackingSolution checkSolution(
    BinPackingSolution sol,
    int lowLoci,
    int highLoci )  {

    /* Local Variables */
    Shape intermediateSheet;
    BinPackingSolution newSol;

    /* Initialize */
    newSol = ( BinPackingSolution )( sol.getCopy() );

    if ( lowLoci < 0 || newSol.getShapes().length < highLoci || newSol.getShapes().length <
      highLoci ) {
      return null;
    }

    /* Local Variables */
    Shape rotatedShape;
    int shapeRowIdx;
    int shapeColIdx;

    for ( int i = lowLoci; i <= highLoci; i++ ) {

      rotatedShape = newSol.getShapes()[ i ].rotate(
        ( ( BinPackingGene )( newSol.getGenes()[ i ] ) ).getRotation()
      );

      shapeRowIdx = ( ( BinPackingGene )( newSol.getGenes()[ i ] ) ).getY()
        - rotatedShape.getStartRow();
      shapeColIdx = ( ( BinPackingGene )( newSol.getGenes()[ i ] ) ).getX()
        - rotatedShape.getStartCol();

      try {
         intermediateSheet = newSol.getResultingSheet().eat(
           rotatedShape,
           shapeRowIdx,
           shapeColIdx
         );
         newSol.setSheet( intermediateSheet );


      } catch ( FallOffExcpetion | OverlapException FOE ) {
        return null;
      }

    }

    return newSol;
  }

  public static Shape getSheetWithoutConcern(
    BinPackingSolution sol
  ) {

    Shape sheet = new Shape( sol.getSheetHeight(), sol.getSheetWidth() );
    Shape[] shapes = sol.getShapes();
    Shape rotatedShape;
    int shapeRowIdx;
    int shapeColIdx;


    for( int i = 0; i < shapes.length; i++ ) {
      rotatedShape = shapes[ i ].rotate(
        ( ( BinPackingGene )( sol.getGenes()[ i ] ) ).getRotation()
      );

      shapeRowIdx = ( ( BinPackingGene )( sol.getGenes()[ i ] ) ).getY()
        - rotatedShape.getStartRow();
      shapeColIdx = ( ( BinPackingGene )( sol.getGenes()[ i ] ) ).getX()
        - rotatedShape.getStartCol();

      sheet = sheet.eatWithoutConcern( rotatedShape, shapeRowIdx, shapeColIdx );
    }

    return sheet;
  }

}
