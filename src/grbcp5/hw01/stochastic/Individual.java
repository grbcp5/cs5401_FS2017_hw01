package grbcp5.hw01.stochastic;

public abstract class Individual {

  protected Gene[] genes;

  public Individual( Gene[] copyGenes ) {

    this.genes = new Gene[ copyGenes.length ];

    for( int i = 0; i < copyGenes.length; i++ ) {
      this.genes[ i ] = ( copyGenes[ i ] == null ) ?
        null : copyGenes[ i ].getCopy();
    }

  }

  public Gene[] getGenes() {
     return this.genes;
  }

  public Gene getGene( int loci ) {

    if( !( 0 <=  loci && loci < this.genes.length ) ) {
      return null;
    }

    return this.genes[ loci ].getCopy();
  }

  public boolean setGene( int loci, Gene gene ) {

    if( !( 0 <=  loci && loci < this.genes.length ) ) {
      return false;
    }

    this.genes[ loci ] = gene;
    return true;
  }

  public abstract Individual getCopy();

  @Override
  public abstract String toString();

}
