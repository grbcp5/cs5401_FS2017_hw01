package grbcp5.hw01;


import java.util.Random;

public class GRandom {

  private static Random instance = null;

  public static void setInstance( Random rnd ) {
    instance = rnd;
  }

  public static Random getInstance() {
    return instance;
  }

}
