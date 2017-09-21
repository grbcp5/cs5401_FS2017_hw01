package grbcp5.hw01.input;

import grbcp5.hw01.shape.Shape;

public class ProblemDefinition {

  private final int numShapes;
  private final Shape[] shapes;
  private final int sheetHeight;
  private final int sheetWidth;

  public ProblemDefinition( int numShapes,
                            Shape[] shapes,
                            int sheetHeight,
                            int sheetWidth

  ) {

    this.numShapes = numShapes;
    this.shapes = shapes;
    this.sheetHeight = sheetHeight;
    this.sheetWidth = sheetWidth;

  }

  public int getNumShapes() {
    return numShapes;
  }

  public Shape[] getShapes() {
    return shapes;
  }

  public int getSheetHeight() {
    return sheetHeight;
  }

  public int getSheetWidth() {
    return sheetWidth;
  }
}
