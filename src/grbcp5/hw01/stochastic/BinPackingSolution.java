package grbcp5.hw01.stochastic;

import grbcp5.hw01.shape.Shape;

public final class BinPackingSolution extends Individual {

  private Shape shapes[];
  private int sheetWidth;
  private int sheetHeight;
  private Shape resultingSheet;

  public BinPackingSolution( BinPackingGene[] genesToCopy,
                             Shape[] shapesToCopy,
                             int sheetHeight,
                             int sheetWidth,
                             Shape resultingSheet ) {

    super( genesToCopy );

    this.shapes = new Shape[ shapesToCopy.length ];
    for ( int i = 0; i < this.shapes.length; i++ ) {
      this.shapes[ i ] = shapesToCopy[ i ].getCopy();
    }

    this.sheetHeight = sheetHeight;
    this.sheetWidth = sheetWidth;

    this.resultingSheet = resultingSheet.getCopy();
  }

  public Shape[] getShapes() {
    return this.shapes;
  }

  public int getSheetWidth() {
    return sheetWidth;
  }

  public int getSheetHeight() {
    return sheetHeight;
  }

  public Shape getResultingSheet() {
    return this.resultingSheet;
  }

  public void setSheet( Shape sheet ) {
    this.resultingSheet = sheet;
  }


  @Override
  public boolean setGene( int loci, Gene gene ) {
    boolean superResult = super.setGene( loci, gene );

    if( !superResult ) {
      return false;
    }


    return true;

  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    for ( Gene gene :
      this.genes ) {
      sb.append( gene.toString() + "\n" );
    }

    return sb.toString();
  }

  @Override
  public Individual getCopy() {
    BinPackingGene[] thisGenes = new BinPackingGene[ this.genes.length ];

    for( int i = 0; i < thisGenes.length; i++ ) {
      thisGenes[ i ] = ( BinPackingGene )( this.genes[ i ] );
    }

    return new BinPackingSolution(
      thisGenes,
      this.shapes,
      this.sheetHeight,
      this.sheetWidth,
      this.resultingSheet
    );
  }


}
