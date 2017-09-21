package grbcp5.hw01.stochastic;


public final class BinPackingGene extends Gene{

  private final int x;
  private final int y;
  private final int rotation;

  public BinPackingGene() {
    this.x = -1;
    this.y = -1;
    this.rotation = -1;
  }

  public BinPackingGene( int x, int y, int rotation ) {
    this.x = x;
    this.y = y;
    this.rotation = rotation;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getRotation() {
    return rotation;
  }

  @Override
  public Gene getCopy() {
    return new BinPackingGene( this.x, this.y, this.rotation );
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    BinPackingGene that = ( BinPackingGene ) o;

    if ( getX() != that.getX() ) {
      return false;
    }
    if ( getY() != that.getY() ) {
      return false;
    }
    return getRotation() == that.getRotation();
  }

  @Override
  public int hashCode() {
    int result = getX();
    result = 31 * result + getY();
    result = 31 * result + getRotation();
    return result;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return this.getCopy();
  }

  @Override
  public String toString() {
    return x + ", " + y + ", " + rotation;
  }
}
