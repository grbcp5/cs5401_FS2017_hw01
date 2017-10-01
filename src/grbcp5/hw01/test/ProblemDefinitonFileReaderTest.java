package grbcp5.hw01.test;

import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.input.ProblemDefinitonFileReader;
import grbcp5.hw01.shape.Shape;

public class ProblemDefinitonFileReaderTest {

  @org.junit.Test
  public void getProblemDefinition() throws Exception {

    ProblemDefinitonFileReader fileReader;
    BinPackingProblemDefinition definition;
    Shape[] shapes;
    String[] configFiles;
    int shapeNum;

    configFiles = new String[] {
      "config/50Shapes.txt",
      "config/100Shapes.txt",
      "config/100ShapesComplex.txt"
    };

    for ( String problemDefinitionFilePath :
      configFiles ) {

      fileReader = new ProblemDefinitonFileReader(
        problemDefinitionFilePath );

      definition = fileReader.getProblemDefinition();

      shapes = definition.getShapes();

      shapeNum = 0;
      for ( Shape s :
        shapes ) {
        System.out.println( "Shape " + shapeNum++ + ": " );
        System.out.println( s );
      }

    }



  }

}