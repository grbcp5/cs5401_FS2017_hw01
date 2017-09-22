package grbcp5.hw01.shape;

import java.util.LinkedList;

class InitializerReturnValue {
  private final boolean[][] matrix;
  private final int startRow;
  private final int startCol;

  InitializerReturnValue(
    boolean[][] matrix,
    int startRow,
    int startCol ) {

    this.matrix = matrix;
    this.startRow = startRow;
    this.startCol = startCol;

  }

  boolean[][] getMatrix() {
    return matrix;
  }

  int getStartRow() {
    return startRow;
  }

  int getStartCol() {
    return startCol;
  }
}

public class Shape {

  /* Instance Variables */
  private final boolean initFromStringDefiniton;
  private final String definition;
  private final boolean[][] matrix;
  private final int startRow;
  private final int startCol;
  private final int trimmedWidth;

  public Shape( String definition ) {

    /* Local Variables */
    InitializerReturnValue initializerReturnValue;

    /* Initialize */
    this.initFromStringDefiniton = true;
    this.definition = definition;

    /* Initialize matrix */
    initializerReturnValue = createShapeMatrix( this.definition );
    assert initializerReturnValue != null;
    this.matrix = initializerReturnValue.getMatrix();
    this.startRow = initializerReturnValue.getStartRow();
    this.startCol = initializerReturnValue.getStartCol();

    this.trimmedWidth = calcTrimmedWith();

  }

  public Shape( boolean[][] initMatrix, int startRow, int startCol ) {

    /* Initialize */
    this.initFromStringDefiniton = false;
    this.definition = null;

    /* Copy matrix */
    this.matrix = new boolean[ initMatrix.length ][ initMatrix[ 0 ].length ];
    for ( int r = 0; r < this.matrix.length; r++ ) {
      System.arraycopy(
        initMatrix[ r ],
        0,
        this.matrix[ r ],
        0,
        this.matrix[ 0 ].length
      );
    }

    this.startRow = startRow;
    this.startCol = startCol;

    this.trimmedWidth = calcTrimmedWith();

  }

  public Shape( int numRows, int numCols ) {

    this.initFromStringDefiniton = false;
    this.definition = null;

    this.matrix = new boolean[ numRows ][ numCols ];

    this.startRow = -1;
    this.startCol = -1;

    this.trimmedWidth = 0;
  }

  public Shape( Shape copy ) {

    this( copy.matrix, copy.getStartRow(), copy.getStartCol() );

  }

  public String getDefinition() {
    return this.definition;
  }

  public boolean isInitFromStringDefiniton() {
    return this.initFromStringDefiniton;
  }

  public boolean[][] getMatrix() {
    boolean[][] copy
      = new boolean[ this.matrix.length ][ this.matrix[ 0 ].length ];

    for ( int r = 0; r < this.matrix.length; r++ ) {
      System.arraycopy(
        this.matrix[ r ],
        0,
        copy[ r ],
        0,
        this.matrix[ 0 ].length
      );
    }

    return copy;
  }

  public int getStartRow() {
    return startRow;
  }

  public int getStartCol() {
    return startCol;
  }

  public int getNumRows() {
    return this.getMatrix().length;
  }

  public int getNumCols() {
    return this.getMatrix()[ 0 ].length;
  }

  public int getLargestDimension() {
    return Math.max( this.getNumRows(), this.getNumCols() );
  }

  public int getTrimmedWidth() {
    return this.trimmedWidth;
  }

  private int calcTrimmedWith() {
    for ( int c = this.getNumCols() - 1; c >= 0; c-- ) {
      for ( int r = 0; r < this.getNumRows(); r++ ) {
        if ( this.matrix[ r ][ c ] ) {
          return c;
        }
      }
    }

    return 0;
  }

  public double getFreePercentage() {
    int trimW = this.getTrimmedWidth();
    int totlW = this.getNumCols();

    return ( totlW - trimW ) / ( ( double ) totlW );
  }

  public Shape rotate( int rotaiton ) {

    /* Local Variables */
    Shape newShape = null;
    boolean[][] newShapeDefinintion;

    if ( this.initFromStringDefiniton ) {
      newShape = new Shape( this.getDefinition() );
    } else {

      if ( this.getStartCol() >= 0 && this.getStartRow() >= 0 ) {
        newShape =
          new Shape( this.matrix, this.getStartRow(), this.getStartCol() );
      } else {
        newShape = new Shape( this );
      }
    }

    rotaiton = rotaiton % 4;

    if ( rotaiton == 0 ) {
      return newShape;
    }

    int newStartCol = -1;
    int newStartRow = -1;

    for ( int i = 0; i < ( 4 - rotaiton ); i++ ) {

      newShapeDefinintion =
        new boolean[ newShape.getNumCols() ][ newShape.getNumRows() ];
      for ( int r = 0; r < newShape.getNumRows(); r++ ) {
        for ( int c = 0; c < newShape.getNumCols(); c++ ) {
          newShapeDefinintion[ ( newShape.getNumCols() - 1 ) - c ][ r ] =
            newShape.matrix[ r ][ c ];
        }
      }

      if ( newShape.getStartCol() >= 0 && newShape.getStartRow() >= 0 ) {
        newStartRow =
          ( newShape.getNumCols() - 1 ) - ( newShape.getStartCol() );
        newStartCol = newShape.getStartRow();

        newShape = new Shape( newShapeDefinintion, newStartRow,
                              newStartCol );
      } else {

        newShape = new Shape( newShapeDefinintion, -1, -1 );
      }

    }

    return newShape;


  }

  /**
   * Returns a shape that is the given shape inside of the calling shape.
   * <p>
   * NOTE: If the two shapes attempt to populate the same cell an
   * OverlapException will be thrown. If the inner shape "falls off the edge" of
   * the outer shape a FallOff exception will be thrown.
   *
   * @param innerShape Shape to be "eaten" or placed inside of calling shape.
   * @param row        row of inner shape to be placed at.
   * @param col        column of inner shape to be placed at.
   * @return The new combined shapes.
   */
  public Shape eat( Shape innerShape, int row, int col ) throws OverlapException, FallOffExcpetion {
    boolean[][] newShapeDefiniton;
    newShapeDefiniton = this.getMatrix();

    for ( int r = 0; r < innerShape.getNumRows(); r++ ) {
      for ( int c = 0; c < innerShape.getNumCols(); c++ ) {

        if ( innerShape.matrix[ r ][ c ] ) {

          /* If this will fall off the outer shape... */
          if ( !( 0 <= ( row + r ) && ( row + r ) < newShapeDefiniton.length ) ||
            !( 0 <= ( col + c ) && ( col + c ) < newShapeDefiniton[ row + r ].length ) ) {

            /* ... throw fall off excpetion */
            throw new FallOffExcpetion( "[" + r + "][" + c + "] Falls off the outer shape" );
          }

          /* If this will overlap with the inner shape... */
          if ( newShapeDefiniton[ row + r ][ col + c ] ) {

            /* ... throw overlap exception */
            throw new OverlapException( "[" + r + "][" + c + "] overlaps with the outer shape" );
          }

          newShapeDefiniton[ row + r ][ col + c ] = true;

        } /* If inner shape populates this square */
      } /* For each inner shape column */
    } /* For each inner shape row */

    return new Shape( newShapeDefiniton, this.startRow, this.startCol );
  }

  @Override
  public String toString() {
    String result = "";

    /* For each row in the matrix */
    for ( int r = 0; r < this.getNumRows(); r++ ) {
      /* For each col in the row */
      for ( int c = 0; c < this.getNumCols(); c++ ) {

        /* Add value to string */
        if ( this.matrix[ r ][ c ] ) {
          result += "#";
        } else {
          result += "_";
        }

      }
      /* Add newline */
      result += "\n";
    }

    return result;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    Shape shape = ( Shape ) o;

    if ( this.getNumRows() != shape.getNumRows() ||
      this.getNumCols() != shape.getNumCols() ) {
      return false;
    }

    for ( int r = 0; r < this.getNumRows(); r++ ) {
      for ( int c = 0; c < this.getNumCols(); c++ ) {
        if ( this.matrix[ r ][ c ] != shape.matrix[ r ][ c ] ) {
          return false;
        }
      }
    }

    return true;
  }

  public Shape getCopy() {

    if( this.isInitFromStringDefiniton() ) {
      return new Shape( this.getDefinition() );
    }

    return new Shape( this.matrix, this.startRow, this.startCol );

  }

  @Override
  protected Shape clone() {
    return this.getCopy();
  }

  /* Private helper functions */

  private static InitializerReturnValue createShapeMatrix( String definition ) {

    /* Local variables */
    boolean resultMatrix[][];
    int matrixRows;
    int matrixCols;
    int minWidth = 0;
    int maxWidth = 0;
    int minHeight = 0;
    int maxHeight = 0;
    int curWidth = 0;
    int curHeight = 0;
    int curMag;
    int rowIdx = 0;
    int colIdx = 0;
    int rowStart;
    int colStart;
    ShapeDefinitionToken currentToken;
    ShapeDefinitionToken[] tokens;

    try {
      tokens = parseInput( definition );
    } catch ( InvalidMagnitude | InvalidDirection invalidMagnitude ) {
      invalidMagnitude.printStackTrace();

      return null;
    }

    for ( ShapeDefinitionToken token1 : tokens ) {
      currentToken = token1;
      curMag = currentToken.getMagnitude();

      switch ( currentToken.getDirection() ) {
        case UP:
          curHeight += curMag;
          if ( curHeight > maxHeight ) {
            maxHeight = curHeight;
          } else {
            rowIdx -= curMag;
          }
          break;
        case DOWN:
          curHeight -= curMag;
          if ( curHeight < minHeight ) {
            minHeight = curHeight;
          }
          rowIdx += curMag;
          break;
        case LEFT:
          curWidth -= curMag;
          if ( curWidth < minWidth ) {
            minWidth = curWidth;
          } else {
            colIdx -= curMag;
          }
          break;
        case RIGHT:
          curWidth += curMag;
          if ( curWidth > maxWidth ) {
            maxWidth = curWidth;
          }
          colIdx += curMag;
          break;
      }
    }

    matrixCols = Math.abs( minWidth ) + Math.abs( maxWidth ) + 1;
    matrixRows = Math.abs( minHeight ) + Math.abs( maxHeight ) + 1;

    resultMatrix = new boolean[ matrixRows ][ matrixCols ];

    rowIdx += curHeight;
    colIdx -= curWidth;
    resultMatrix[ rowIdx ][ colIdx ] = true;
    rowStart = rowIdx;
    colStart = colIdx;

    for ( ShapeDefinitionToken token : tokens ) {
      currentToken = token;

      for ( int i = 0; i < currentToken.getMagnitude(); i++ ) {
        switch ( currentToken.getDirection() ) {
          case UP:
            rowIdx -= 1;
            break;
          case DOWN:
            rowIdx += 1;
            break;
          case LEFT:
            colIdx -= 1;
            break;
          case RIGHT:
            colIdx += 1;
            break;
        }
        resultMatrix[ rowIdx ][ colIdx ] = true;
      }
    }

    return new InitializerReturnValue( resultMatrix, rowStart, colStart );
  }

  private static ShapeDefinitionToken[] parseInput( String input ) throws
    InvalidMagnitude, InvalidDirection {
    LinkedList< ShapeDefinitionToken > result = new LinkedList<>();
    ShapeDefinitionToken currentToken;

    String[] pieces = input.split( " " );

    for ( String piece : pieces ) {
      currentToken = new ShapeDefinitionToken( piece );
      for ( int j = 0; j < currentToken.getMagnitude(); j++ ) {
        result
          .add( new ShapeDefinitionToken( currentToken.getDirection(), 1 ) );
      }
    }

    return result.toArray( new ShapeDefinitionToken[ result.size() ] );
  }


}
