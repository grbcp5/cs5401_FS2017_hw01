package grbcp5.hw01.stochastic;

public class MutationRateGene extends Gene {

  private Double mutationRate;

  public MutationRateGene( Double mutationRate ) {
    super();

    this.mutationRate = mutationRate;
  }

  public double getMutationRate() {
    return mutationRate;
  }

  public void setMutationRate( double mutationRate ) {
    this.mutationRate = mutationRate;
  }

  @Override
  public Gene getCopy() {
    return new MutationRateGene( this.mutationRate );
  }

  @Override
  public String toString() {
    return "Mutation Rate: " + this.mutationRate;
  }

}
