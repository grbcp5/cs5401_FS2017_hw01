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
    this( genesToCopy, shapesToCopy, sheetHeight, sheetWidth, resultingSheet,
          null );
  }

  public BinPackingSolution( BinPackingGene[] genesToCopy,
                             Shape[] shapesToCopy,
                             int sheetHeight,
                             int sheetWidth,
                             Shape resultingSheet,
                             MutationRateGene mgr ) {
    super( genesToCopy, mgr );

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

  public double getFreePercentage() {
    return this.getResultingSheet().getFreePercentage();
  }

  public void setSheet( Shape sheet ) {
    this.resultingSheet = sheet;
  }


  @Override
  public boolean setGene( int loci, Gene gene ) {

    return super.setGene( loci, gene );

  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    for ( Gene gene :
      this.genes ) {
      if( gene == null ) {
        sb.append( "null\n" );
      } else {
        sb.append( gene.toString() );
        sb.append( "\n" );
      }
    }

    return sb.toString();
  }

  @Override
  public Individual getCopy() {
    BinPackingSolution result;
    BinPackingGene[] thisGenes = new BinPackingGene[ this.genes.length ];

    for( int i = 0; i < thisGenes.length; i++ ) {
      thisGenes[ i ] = ( BinPackingGene )( this.genes[ i ] );
    }

    result = new BinPackingSolution(
      thisGenes,
      this.shapes,
      this.sheetHeight,
      this.sheetWidth,
      this.resultingSheet
    );

    result.penaltyValue = this.penaltyValue;

    return result;
  }


}
