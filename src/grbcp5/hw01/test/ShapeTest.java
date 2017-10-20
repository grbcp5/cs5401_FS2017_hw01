package grbcp5.hw01.test;

import grbcp5.hw01.shape.Shape;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShapeTest {


  @Test
  public void rotate() throws Exception {

    String[] definitions = new String[]{
      "R2 D4 L4 U5"
    };

    for ( String definition :
      definitions ) {

      Shape s = new Shape( definition );
      System.out.println( "Shape:" );
      System.out.println( "Start Positon: [" + s.getStartRow() + "][" + s.getStartCol() + "]" );
      System.out.println( s );

      for( int i = 1; i < 4; i++) {
        Shape sR = s.rotate( i );

        System.out.println( "Rotation " + i + ": " );
        System.out.println( "Start Positon: [" + sR.getStartRow() + "][" + sR.getStartCol() + "]" );
        System.out.println( sR );
      }

    }

    System.out.println( "\n-----------\n" );

    Shape s = new Shape( "R2 D4 L4 U5" );
    System.out.println( "Shape:" );
    System.out.println( "Start Positon: [" + s.getStartRow() + "][" + s.getStartCol() + "]" );
    System.out.println( s );

    Shape s1 = s.rotate( 3 );
    System.out.println( "Rotation " + 1 + ": " );
    System.out.println( "Start Positon: [" + s1.getStartRow() + "][" + s1.getStartCol() + "]" );
    System.out.println( s1 );

    Shape s2 = s1.rotate( 3 );
    System.out.println( "Rotation " + 2 + ": " );
    System.out.println( "Start Positon: [" + s2.getStartRow() + "][" + s2.getStartCol() + "]" );
    System.out.println( s2 );

  }

  @Test
  public void eat() throws Exception {

    Shape outer = new Shape( "D5 R4 U4 L2" );
    Shape inner = new Shape( "L1 D2 R2 D2" );
    Shape combination;

    combination = outer.eat( inner, 0, 1 );

    System.out.println( "Outer: " );
    System.out.println( outer );

    System.out.println( "Inner: " );
    System.out.println( inner );

    System.out.println( "Combination: " );
    System.out.println( combination );

  }

}