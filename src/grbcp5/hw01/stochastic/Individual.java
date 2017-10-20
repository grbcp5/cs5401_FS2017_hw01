package grbcp5.hw01.stochastic;

public abstract class Individual {

  protected Gene[] genes;
  protected MutationRateGene mutationRateGene;

  protected Double penaltyValue;

  public Individual( Gene[] copyGenes, MutationRateGene mrg ) {

    this.genes = new Gene[ copyGenes.length ];

    for( int i = 0; i < copyGenes.length; i++ ) {
      this.genes[ i ] = ( copyGenes[ i ] == null ) ?
        null : copyGenes[ i ].getCopy();
    }

    this.penaltyValue = null;
    this.mutationRateGene = mrg;

  }

  public Individual( Gene[] copyGenes, double mutRate ) {

    this( copyGenes, new MutationRateGene( mutRate ) );

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

  public MutationRateGene getMutationRateGene() {
    return mutationRateGene;
  }

  public void setMutationRateGene(
    MutationRateGene mutationRateGene ) {
    this.mutationRateGene = mutationRateGene;
  }

  public Double getMutationRate() {
    return mutationRateGene.getMutationRate();
  }

  public void setMutationRate( double mutationRate ) {
    mutationRateGene.setMutationRate( mutationRate );
  }

  public Double getPenaltyValue() {
    return penaltyValue;
  }

  public void setPenaltyValue( Double penaltyValue ) {
    this.penaltyValue = penaltyValue;
  }

  public abstract Individual getCopy();

  @Override
  public abstract String toString();

}
