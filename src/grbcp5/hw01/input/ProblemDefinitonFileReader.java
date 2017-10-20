package grbcp5.hw01.input;


import grbcp5.hw01.shape.Shape;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ProblemDefinitonFileReader {

  private final File problemDefinitionFile;

  public ProblemDefinitonFileReader( String problemDefinitionFilePath ) {

    problemDefinitionFile = new File( problemDefinitionFilePath );

  }

  public BinPackingProblemDefinition getProblemDefinition() {

    /* Local Variables */
    Scanner problemScanner;
    int sheetWidth;
    int numShapes;
    Shape[] shapes;
    int sheetHeight;
    String dummy;
    String shapeDefinition;

    /* Create scanner. return null if file does not exist */
    try {
      problemScanner = new Scanner( this.problemDefinitionFile );
    } catch ( FileNotFoundException e ) {
      e.printStackTrace();
      return null;
    }

    /* Get listed variables */
    sheetHeight = problemScanner.nextInt();
    numShapes = problemScanner.nextInt();
    dummy = problemScanner.nextLine();

    /* Create shape array */
    shapes = new Shape[ numShapes ];

    /* Read in shapes */
    sheetWidth = 0;
    for( int s = 0; s < numShapes; s++ ) {
      shapeDefinition = problemScanner.nextLine();
      shapes[ s ] = new Shape( shapeDefinition );

      /* Calculate width of sheet */
      sheetWidth += shapes[ s ].getLargestDimension();
    }

    return new BinPackingProblemDefinition( numShapes, shapes, sheetHeight, sheetWidth );
  }

}
