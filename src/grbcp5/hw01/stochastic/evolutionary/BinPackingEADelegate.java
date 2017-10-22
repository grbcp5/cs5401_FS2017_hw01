package grbcp5.hw01.stochastic.evolutionary;

import grbcp5.hw01.GRandom;
import grbcp5.hw01.Main;
import grbcp5.hw01.input.BinPackingProblemDefinition;
import grbcp5.hw01.shape.Shape;
import grbcp5.hw01.stochastic.*;
import grbcp5.hw01.stochastic.random.BinPackingRandomSearchDelegate;
import grbcp5.hw01.stochastic.random.RandomSearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class BinPackingEADelegate extends EvolutionaryDelegate {


  private Map< String, Object > parameters;
  private BinPackingProblemDefinition problemDefinition;
  private int populationSize;

  private RandomSearch randomSearch;
  private RandomSearchDelegateForEADelegate randomSearchDelegate;

  private PrintWriter logWriter;
  private PrintWriter solWriter;

  private int numGenerations;
  private int numNewIndividuals;

  private BinPackingSolution currentBest;
  private double currentBestFitness;
  private int getLastGenerationWithBestChange;
  private BinPackingSolution[] population;

  private int currentDenom;

  private int currentBestGeneration;
  private int prematureConverganceThreshold;
  private boolean converged;

  private double penaltyCoefficient;

  private int bound;

  private int invalidIndividuals;

  private double selfAdaptiveMutationRate;

  private LinkedList< LinkedList< BinPackingSolution > > levels;

  /* Constructor */
  public BinPackingEADelegate(
    Map< String, Object > parameters,
    BinPackingProblemDefinition problemDefinition
  ) {

    this.parameters = parameters;
    this.problemDefinition = problemDefinition;

    this.bound = this.problemDefinition.getSheetWidth() / 2;

    this.populationSize =
      ( ( Integer ) ( this.parameters.get( "populationSize" ) ) );

    this.logWriter =
      ( PrintWriter ) this.parameters.get( "logWriter" );

    // Random Search
    Map< String, Object > randomSearchParameters = new HashMap<>();
    randomSearchParameters.put( "fitnessEvals", this.populationSize );
    this.randomSearchDelegate = new RandomSearchDelegateForEADelegate(
      randomSearchParameters,
      problemDefinition,
      bound
    );
    this.randomSearch = new RandomSearch( this.randomSearchDelegate );

    // Instance variables */
    this.numGenerations = 0;
    this.numNewIndividuals = 0;

    this.currentDenom = 3;
    this.bound = this.problemDefinition.getSheetWidth() / this.currentDenom;

    this.currentBestFitness = -1;
    this.currentBestGeneration = -1;
    this.population = new BinPackingSolution[ populationSize ];

    this.prematureConverganceThreshold =
      ( int ) parameters.get( "convergenceCriterion" ) +
      GRandom.getInstance().nextInt( 10 );
    this.converged = false;

    this.penaltyCoefficient = -1;

    this.invalidIndividuals = 0;

    this.selfAdaptiveMutationRate =
      ( double ) ( this.parameters.get( "mutationRate" ) );

  }

  @Override
  public boolean shouldContinue() {

    Integer run;


    if ( this.numGenerations == this.prematureConverganceThreshold ) {
      run = ( int ) ( parameters.get( "currentRun" ) );
      System.out.println( "Terminating run " + run + " due to premature " +
                            "convergence." );
      return false;
    }

    if ( this.numNewIndividuals >=
      ( ( int ) ( parameters.get( "fitnessEvals" ) ) )
      ) {
      run = ( int ) ( parameters.get( "currentRun" ) );
      System.out.println( "Terminating run " + run + " due to fitness eval " +
                            "exhaustion." );
      return false;
    }

    return true;
  }

  @Override
  public boolean handleNewIndividual( Individual ind ) {
    BinPackingSolution sol = ( BinPackingSolution ) ( ind );

    int run;
    double avgFit;
    boolean newBestLevel;

    run = ( int ) ( parameters.get( "currentRun" ) );
    this.numNewIndividuals++;

    //    // Update average
    //    this.fitnessSum += this.fitness( sol );
    //    avgFit = this.fitnessSum / this.numNewIndividuals;
    //    if ( avgFit > this.averageFitness ) {
    //      this.lastGenerationWithAverageChange = this.numGenerations;
    //    }
    //    this.averageFitness = avgFit;
    //
    //    // Print to keep application responsive
    //    if ( this.numNewIndividuals % 100 == 0 ) {
    //      System.out.println( "Run " + run + ": Used " + this.numNewIndividuals +
    //                            " evaluations." );
    //    }

    //    // Update if new best
    //    if ( this.fitness( sol ) > this.currentBestFitness ) {
    //      this.currentBest = sol;
    //      this.currentBestFitness = this.fitness( sol );
    //      this.currentBestGeneration = this.numGenerations;
    //
    //      System.out.println( "New best of " + this.currentBestFitness );
    //      updateBound( sol );
    //    }

    //    // Check if premature convergance
    //    // No change in best
    //    if ( ( this.numGenerations - this.currentBestGeneration ) >=
    //      this.prematureConverganceThreshold ) {
    //
    //      this.converged = true;
    //      return false;
    //
    //    }
    //    // No change in average
    //    if ( ( this.numGenerations - this.lastGenerationWithAverageChange ) >=
    //      this.prematureConverganceThreshold ) {
    //
    //      this.converged = true;
    //      return false;
    //
    //    }

    //    if ( sol.getPenaltyValue() != null ) {
    //      this.invalidIndividuals++;
    //    }

    newBestLevel = addIndividualToLevel( sol );

    if( newBestLevel ) {

      this.getLastGenerationWithBestChange = this.numGenerations;
//      System.out.print( "Added new level: " );
//      System.out.print( "Horizontal: " + horizontalFitness( sol ) );
//      System.out.println( "Vertical: " + verticalFitenss( sol ) );
//      System.out.println( sol.getResultingSheet() );

    }

    return this.shouldContinue();
  }

  private boolean doesOneDominateTwo( BinPackingSolution one,
                                      BinPackingSolution two ) {
    if ( horizontalFitness( one ) > horizontalFitness( two ) ) {

      if ( verticalFitenss( one ) >= verticalFitenss( two ) ) {
        return true;
      }

    } else if ( horizontalFitness( one ) == horizontalFitness( two ) ) {

      if ( verticalFitenss( one ) > verticalFitenss( two ) ) {
        return true;
      }

    }

    return false;

  }

  void addToLevel( int level, BinPackingSolution sol ) {
    if ( level > this.levels.size() ) {
      throw new IndexOutOfBoundsException( level + " out of bounds bro" );
    }

    this.levels.get( level ).add( sol );
  }

  private boolean addIndividualToLevel( BinPackingSolution sol ) {

    boolean placed;
    boolean newBestLevel;
    Queue< BinPackingSolution > cascadeDominance;
    BinPackingSolution curSol;
    BinPackingSolution dominatedIndividual;

    placed = false;

    newBestLevel = true;
    /* See if this individual dominates everything */
    for( int i = 0; this.levels.size() > 0 && i < this.levels.get( 0 ).size();
         i++ ) {
      if( !doesOneDominateTwo( sol, levels.get( 0 ).get( i ) ) ) {
        newBestLevel = false;
        break;
      }
    }

    if( newBestLevel ) {

      levels.add( 0, new LinkedList<>() );
      sol.setLevel( 0 );
      addToLevel( 0, sol );

    } else {

      /* Find this individuals level */
      for ( int l = 0; !placed && l < this.levels.size(); l++ ) {

        if ( !isDominatedByAnything( sol, this.levels.get( l ) ) ) {
          sol.setLevel( l );
          addToLevel( l, sol );
          placed = true;
        }

      }

      /* If new sol is dominated by something on every level */
      if ( !placed ) {
        /* Place it in a new level */
        levels.add( levels.size(), new LinkedList<>() );
        sol.setLevel( levels.size() - 1 );
        addToLevel( levels.size() - 1, sol );
      }

    }

    cascadeDominance = new LinkedList<>();
    cascadeDominance.add( sol );

    int itr = -1;

    while ( !cascadeDominance.isEmpty() ) {
      curSol = cascadeDominance.remove();

      itr++;

      /* Determine if it dominates anything on its level */
      for ( int i = 0; i < this.levels.get( curSol.getLevel() ).size(); i++ ) {

        dominatedIndividual = levels.get( curSol.getLevel() ).get( i );
        if ( doesOneDominateTwo( curSol, dominatedIndividual ) ) {

          /* Move their level */
          this.levels.get( curSol.getLevel() ).remove( i );
          dominatedIndividual.setLevel( dominatedIndividual.getLevel() + 1 );

          /* Make sure to not try to add them off the end */
          if ( dominatedIndividual.getLevel() == levels.size() ) {
            levels.add( levels.size(), new LinkedList<>() );
          }

          /* Add them to the next level */
          addToLevel( dominatedIndividual.getLevel(), dominatedIndividual );

          /* Check if they dominate anyone that needs to be moved down */
          cascadeDominance.add( dominatedIndividual );
        }

      }

    }

    return newBestLevel;
  }

  private boolean isDominatedByAnything(
    BinPackingSolution sol,
    List< BinPackingSolution > anything ) {

    /* Compare against everything in this list */
    for ( BinPackingSolution individual : anything ) {

      /* If something in the list dominates it */
      if ( doesOneDominateTwo( individual, sol ) ) {
        return true;
      }

    }

    /* Nothing in the list dominates this solution */
    return false;

  }

  private void constructLevels( Individual[] pop ) {

    /* Local Variables */
    BinPackingSolution sol;
    List< BinPackingSolution > individualsLeft;
    int curLevel;
    BinPackingSolution curSol;
    boolean isDominated;

    /* Initialize */
    individualsLeft = new LinkedList<>();
    for ( Individual i : pop ) {
      individualsLeft.add( ( BinPackingSolution ) i );
    }

    this.levels = new LinkedList< LinkedList< BinPackingSolution > >();
    curLevel = -1;

    /* Do until all individuals are placed */
    while ( !individualsLeft.isEmpty() ) {

      /* Create new level */
      curLevel++;
      this.levels.add( curLevel, new LinkedList<>() );

      /* Find each individual on this level */
      for ( int i = 0; i < individualsLeft.size(); i++ ) {
        curSol = individualsLeft.get( i );

        /* Detirmine if this individual is dominated by any other individual */
        isDominated = isDominatedByAnything( curSol, individualsLeft );

        /* If it is not */
        if ( !isDominated ) {

          curSol.setLevel( curLevel );
          addToLevel( curLevel, curSol );
          individualsLeft.remove( i );
          i--;

        }

      } /* For each individual left */

    } /* While individuals remain */

  } /* constructLevels */

  @Override
  public boolean handlePopulation( Individual[] pop ) {
    this.population = new BinPackingSolution[ pop.length ];

    // Local variables
    double sum;
    int run;

    // Initialize
    sum = 0.0;
    run = ( int ) ( parameters.get( "currentRun" ) );

    constructLevels( pop );

    // Update self adaptive genes
    if ( this.isMutationRateSelfAdaptive() ) {

      for ( Individual i :
        pop ) {
        sum += i.getMutationRate();
      }

      this.selfAdaptiveMutationRate = sum / pop.length;

    }

    // Log end of generation
    System.out.println( "Run " + run + ": End of generation: " + this
      .numGenerations + " Evals: " + this.numNewIndividuals );

    // TODO: Log subfitenss best and average

    // Print to the log writer
    this.logWriter.println(
      this.numNewIndividuals + "\t" +
        this.currentBestFitness
    );

    // Increate number of generations
    this.numGenerations++;

    // Reset penalty function specific information
    if ( this.getConstraintSatisfactionType().toLowerCase().equals( "penalty"
    ) ) {
      System.out.println( "Generation: " + this.numGenerations + " had " + this
        .invalidIndividuals +
                            " (" + ( this.invalidIndividuals /
        ( float ) this.populationSize ) + ") " +
                            "invalid " +
                            "individuals." );

      this.invalidIndividuals = 0;
    }

    if ( this.numGenerations - this.getLastGenerationWithBestChange >
      this.prematureConverganceThreshold ) {
      this.converged = true;
    }

    // Handle for premature convergance
    if ( this.converged && Main.debug() ) {

      System.out.println( "Population: " );
      for ( int i = 0; i < pop.length; i++ ) {
        this.population[ i ] = ( BinPackingSolution ) ( pop[ i ] );

        System.out.println( "\t" + this.fitness( this.population[ i ] ) );
      }

      int remainingGenerations;

      remainingGenerations =
        ( ( ( int ) ( parameters.get( "fitnessEvals" ) ) ) - this
          .numNewIndividuals ) / this.populationSize;

      // Fill out the rest of the log
      for ( int i = 1; i <= remainingGenerations; i++ ) {

        // TODO: Fill out average
        // Print to the log writer
        this.logWriter.println(
          this.numNewIndividuals + ( i * this.populationSize ) + "\t" +
            /*this.averageFitness + "\t" +*/
            this.currentBestFitness
        );

      }

    }

    return this.shouldContinue();
  }

  private int getBound() {
    return this.bound;
  }

  private void updateBound( BinPackingSolution sol ) {

    if ( sol.getPenaltyValue() != null ) {
      return;
    }

    if ( this.horizontalFitness( sol ) * sol.getSheetWidth() >
      sol.getSheetWidth() - ( sol.getSheetWidth() / this.currentDenom ) ) {
      System.out.println( "Updating bound" );
      this.currentDenom++;
      this.bound = sol.getSheetWidth() / this.currentDenom;
    }

  }

  public void removeShapeFromSheet( BinPackingSolution sol, int shapeNum ) {

        Shape sheet;
        boolean[][] matrix;
        BinPackingGene gene;
        Shape shapeToRemove;
        int row;
        int col;
        int rot;

        sheet = sol.getResultingSheet();
        matrix = sheet.getMatrix();
        gene = ( BinPackingGene )sol.getGene( shapeNum );
        rot = gene.getRotation();
        row = gene.getY() -
          sol.getShapes()[ shapeNum ].rotate( rot ).getStartRow();
        col = gene.getX() -
          sol.getShapes()[ shapeNum ].rotate( rot ).getStartCol();
        shapeToRemove = sol.getShapes()[ shapeNum ].rotate( rot );

        for( int r = 0; r < shapeToRemove.getNumRows(); r++ ) {
          for( int c = 0; c < shapeToRemove.getNumCols(); c++ ) {
            if( shapeToRemove.getMatrix()[ r ][ c ] ) {

              if( row + r >= matrix.length || col + c >= matrix[ 0 ].length ) {
                r = r;
              }

              matrix[ row + r ][ col + c ] = false;
            }
          }
        }

        sol.setSheet( new Shape( matrix, -1, -1  ) );



//    Shape sheet = sol.getResultingSheet();
//    Shape newSheet = new Shape(
//      new boolean[ sheet.getNumRows() ][ sheet.getNumCols() ],
//      -1,
//      -1
//    );
//    BinPackingGene gene;
//
//    for ( int i = 0; i < sol.getShapes().length; i++ ) {
//
//      if ( i != shapeNum ) {
//
//        gene = ( BinPackingGene ) sol.getGene( i );
//
//        newSheet = newSheet.eatWithoutConcern(
//          sol.getShapes()[ i ].rotate( gene.getRotation() ),
//          gene.getY() - sol.getShapes()[ i ].rotate( gene.getRotation() )
//                                            .getStartRow(),
//          gene.getX() - sol.getShapes()[ i ].rotate( gene.getRotation() )
//                                            .getStartCol()
//        );
//      }
//
//
//
//    }
//
//    sol.setSheet( newSheet );

  }

  @Override
  public Individual mutate( Individual i ) {

    BinPackingSolution result = ( BinPackingSolution ) i.getCopy();
    Random rnd = GRandom.getInstance();
    double mutationRate;

    mutationRate = this.getMutationRate();

    // For each gene location
    for ( int loci = 0; loci < randomSearchDelegate.getGenePoolSize();
          loci++ ) {

      // See if this gene should be mutated
      if ( rnd.nextDouble() <= mutationRate ) {

        removeShapeFromSheet( result, loci );

        // If so, mutate this gene
        result.setGene(
          loci,
          randomSearchDelegate.getRandomGene(
            loci,
            ( ( BinPackingGene ) result.getGene( loci ) ).getY()
            + result.getShapes()[ loci ].getLargestDimension(),
            this.getBound()
          )
        );

        result = ( BinPackingSolution ) repair( result, loci, loci );
      }

    }

    if ( this.getConstraintSatisfactionType().toLowerCase().equals(
      "penalty" ) ) {
      this.handlePotentiallyInvalidIndividual( result );
    }

    return result;

  }

  @Override
  public String getSurviorSelectionMethod() {
    return ( ( String ) ( this.parameters.get( "survivorSelectionMethod" ) ) );
  }

  @Override
  public int getNumParentsPerChild() {
    return ( ( int ) ( parameters.get( "parentsPerChild" ) ) );
  }

  @Override
  public int getSurvivalTournamentSize() {
    return (
      ( int ) ( this.parameters.get( "survivorSelectionTournamentSize" ) )
    );
  }

  @Override
  public String getParentSelectionMethod() {
    return ( ( String ) ( this.parameters.get( "parentSelectionMethod" ) ) );
  }

  @Override
  public double getMutationRate() {
    if ( this.isMutationRateSelfAdaptive() ) {
      return this.selfAdaptiveMutationRate;
    } else {
      return ( ( double ) ( this.parameters.get( "mutationRate" ) ) );
    }
  }

  @Override
  public int getParentSelectionTournamentSize() {
    return ( ( int ) ( parameters.get( "parentSelectionTournamentSize" ) ) );
  }

  public String getMultiaryOperator() {
    return ( ( String ) ( parameters.get( "multiaryOperator" ) ) );
  }

  public int getNumCrossoverPoints() {
    return ( ( int ) ( parameters.get( "numCrossoverPoints" ) ) );
  }

  private BinPackingSolution createIndividualFromSolutionDefinition(
    String fileContainingDefinition
  ) {
    BinPackingSolution result;
    String line;
    String[] lineParts;
    int x, y, rot;
    File definitionFile;
    Scanner definitionScanner;
    BinPackingGene[] genes;
    Shape sheet;

    definitionFile = new File( fileContainingDefinition );

    try {
      definitionScanner = new Scanner( definitionFile );
    } catch ( FileNotFoundException e ) {
      e.printStackTrace();
      definitionScanner = null;
    }

    /* Parse all genes */
    sheet = new Shape( problemDefinition.getSheetHeight(), problemDefinition
      .getSheetWidth() );
    genes = new BinPackingGene[ problemDefinition.getNumShapes() ];
    for ( int i = 0; i < problemDefinition.getNumShapes(); i++ ) {

      line = definitionScanner.nextLine();
      lineParts = line.split( ", " );

      x = Integer.parseInt( lineParts[ 0 ] );
      y = Integer.parseInt( lineParts[ 1 ] );
      rot = Integer.parseInt( lineParts[ 2 ] );

      genes[ i ] = new BinPackingGene(
        x,
        y,
        rot
      );

      sheet =
        sheet.eatWithoutConcern( problemDefinition.getShapes()[ i ].rotate(
          rot ), y, x );

    }

    result = new BinPackingSolution(
      genes,
      problemDefinition.getShapes(),
      problemDefinition.getSheetHeight(),
      problemDefinition.getSheetWidth(),
      new Shape( problemDefinition.getSheetHeight(), problemDefinition
        .getSheetWidth() )
    );

    return result;
  }

  @Override
  public Individual[] getInitialPopulation() {

    Individual[] population;
    Random rnd;
    Object parameterValue;
    boolean isInitialPopSeeded;
    String[] valuesToInclude;

    rnd = GRandom.getInstance();

    // Randomly generate initial population
    this.randomSearch.search();

    population = this.randomSearchDelegate.getPopulation();

    parameterValue = parameters.get( "includeInitialPopulationSeedValues" );
    if ( parameterValue != null ) {
      isInitialPopSeeded = ( boolean ) parameterValue;

      if ( isInitialPopSeeded ) {

        valuesToInclude =
          ( String[] ) parameters.get( "includeInInitialPopulation" );

        for ( int i = 0; i < valuesToInclude.length && i < population.length;
              i++ ) {

          population[ i ] = createIndividualFromSolutionDefinition(
            valuesToInclude[ i ]
          );

        }

      }

    }

    if ( this.isMutationRateSelfAdaptive() ) {

      for ( Individual i :
        population ) {
        i.setMutationRate(
          ( Double ) parameters.get( "mutationRate" ) +
            rnd.nextGaussian() * 0.1
        );
      }

    }

    return population;
  }

  @Override
  public Individual getEmptyIndividual() {

    return new BinPackingSolution(
      new BinPackingGene[ this.problemDefinition.getNumShapes() ],
      this.problemDefinition.getShapes(),
      this.problemDefinition.getSheetHeight(),
      this.problemDefinition.getSheetWidth(),
      new Shape( this.problemDefinition.getSheetHeight(), this
        .problemDefinition.getSheetWidth() )
    );
  }

  public int getNumChildren() {
    return ( ( int ) ( parameters.get( "numChildren" ) ) );
  }

  @Override
  public Gene getBestGene( Gene g1, Gene g2 ) {
    BinPackingGene bpg1 = ( BinPackingGene ) g1;
    BinPackingGene bpg2 = ( BinPackingGene ) g2;

    if ( bpg1 == null || bpg1.getX() < bpg2.getX() ) {
      return bpg2;
    } else {
      return bpg1;
    }

  }

  @Override
  public int getPopulationSize() {
    return this.populationSize;
  }

  @Override
  public Individual getBestIndividual() {
    return currentBest;
  }

  @Override
  public Individual repair( Individual i, int lowLoci, int highLoci ) {

    /* Random repair */
    return randomSearchDelegate.repair( i, lowLoci, highLoci );

  }

  @Override
  public double fitness( Individual i ) {
    if ( i == null ) {
      return -1.0;
    }
    return ( ( BinPackingSolution ) i ).getLevel();
  }

  public double horizontalFitness( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) ( i );
    Shape resultingSheet = sol.getResultingSheet();
    int trimW;
    int totlW;
    double fitness;
    double penalty = 0.0;

    trimW = resultingSheet.getTrimmedWidth();
    totlW = resultingSheet.getNumCols();

    fitness = ( totlW - trimW ) / ( ( double ) ( totlW ) );

    if ( sol.getPenaltyValue() != null ) {
      penalty = sol.getPenaltyValue();
    }

    return fitness - penalty;
  }

  public double verticalFitenss( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) ( i );
    Shape resultingSheet = sol.getResultingSheet();
    int trimH;
    int totlH;
    double fitness;
    double penalty = 0.0;

    trimH = resultingSheet.getTrimmedHeight();
    totlH = resultingSheet.getNumRows();

    fitness = ( totlH - trimH ) / ( ( double ) ( totlH ) );

    if ( sol.getPenaltyValue() != null ) {
      penalty = sol.getPenaltyValue();
    }

    return fitness - penalty;
  }

  @Override
  public int compare( Individual i1, Individual i2 ) {
    BinPackingSolution sol1 = ( BinPackingSolution ) ( i1 );
    BinPackingSolution sol2 = ( BinPackingSolution ) ( i2 );

    Double fitness1 = this.fitness( sol1 );
    Double fitness2 = this.fitness( sol2 );

    if( fitness1 != fitness2 ) {
      return fitness1.compareTo( fitness2 );
    }

    fitness1 = verticalFitenss( sol1 );
    fitness2 = verticalFitenss( sol2 );

    if( fitness1 != fitness2 ) {
      return fitness1.compareTo( fitness2 );
    }

    fitness1 = horizontalFitness( sol1 );
    fitness2 = horizontalFitness( sol2 );

    return fitness1.compareTo( fitness2 );
  }


  /*

  Assignment 1C additions

   */

  @Override
  public String getConstraintSatisfactionType() {
    return ( String ) parameters.get( "constraintSatisfaction" );
  }

  @Override
  public double getPenaltyCoefficient() {

    if ( this.penaltyCoefficient < 0 ) {
      this.penaltyCoefficient
        = ( Double ) parameters.get( "penaltyCoefficient" );
    }

    assert this.penaltyCoefficient >= 0;

    return this.penaltyCoefficient;
  }

  @Override
  public void handlePotentiallyInvalidIndividual( Individual i ) {
    BinPackingSolution sol = ( BinPackingSolution ) i;
    BinPackingSolution check;

    check = BinPackingSolutionChecker.checkSolution( sol, 0, sol.getShapes()
      .length - 1 );

    if ( check != null ) {
      sol.setPenaltyValue( null );
      sol.setSheet( check.getResultingSheet() );
      return;
    }

    sol.setPenaltyValue( this.getPenaltyCoefficient() );
    sol.setSheet( BinPackingSolutionChecker.getSheetWithoutConcern( sol ) );

  }

  @Override
  public String getSurvivalStrategyType() {
    return ( String ) parameters.get( "survivalStrategy" );
  }


  @Override
  public boolean isMutationRateSelfAdaptive() {
    Boolean result = ( Boolean ) parameters.get( "selfAdaptiveMutationRate" );

    return ( result != null && result );
  }


  @Override
  public Individual[] getIndividualsOnBestFront() {
    return levels.get( 0 ).toArray( new Individual[ levels.get( 0 ).size() ] );
  }


} /* Bin packing EA delegate */



/*

  Random search for EA delegate

 */


class RandomSearchDelegateForEADelegate extends BinPackingRandomSearchDelegate {

  private BinPackingSolution[] population;
  private int currentIndex;
  private int bound;

  RandomSearchDelegateForEADelegate(
    Map< String, Object > parameters,
    BinPackingProblemDefinition problemDefinition,
    int bound
  ) {
    super( parameters, problemDefinition );

    this.population = new BinPackingSolution[ this.getNumFitnessEvalsLeft() ];
    this.currentIndex = 0;
    this.bound = bound;

  }

  @Override
  public boolean handleNewIndividual( Individual i ) {
    population[ currentIndex ] = ( ( BinPackingSolution ) ( i ) );
    currentIndex++;

    return this.shouldContinue();
  }

  @Override
  public Individual repair( Individual i, int lowLoci, int highLoci ) {

    /* Local variables */
    BinPackingSolution resultingSoluiton;
    BinPackingSolution newSolution;
    int bound;
    int numTries;

    /* Initialize */
    resultingSoluiton = ( BinPackingSolution ) ( i.getCopy() );
    newSolution = BinPackingSolutionChecker.checkSolution(
      resultingSoluiton,
      lowLoci,
      highLoci
    );

    numTries = 0;

    /* Until a valid solution is found */
    while ( newSolution == null ) {

      numTries++;

      /* Refil each location with a new random gene */
      for ( int loci = lowLoci; loci <= highLoci; loci++ ) {

        bound = this.bound + numTries;

        resultingSoluiton.setGene(
          loci,
          this.getRandomGene(
            loci,
            ( ( BinPackingGene ) resultingSoluiton.getGene( loci ) ).getY()
            + resultingSoluiton.getShapes()[ loci ].getLargestDimension(),
            bound
          ) );
      }

      /* Check to see if that solution is valid */
      newSolution = BinPackingSolutionChecker.checkSolution(
        resultingSoluiton,
        lowLoci,
        highLoci
      );

    }

    return newSolution;
  }

  public Gene getRandomGene( int loci, int maxRow, int maxCol ) {

    /* Local variabes*/
    Random rnd;
    Shape tryShape;
    int minRow;
    int tryRow;

    int minCol;
    int tryCol;

    int tryRotation;

    /* Initialize */
    rnd = GRandom.getInstance();
    tryShape = this.problemDefinition.getShapes()[ loci ];

    /* Rotate shape */
    tryRotation = rnd.nextInt( 3 );
    tryShape = tryShape.rotate( tryRotation );

    /* Get random row */
    minRow = tryShape.getStartRow();

    if(  ( maxRow - minRow ) + 1 < 0 ) {
      minRow = minRow;
    }

    tryRow = randInt( rnd, minRow, maxRow );

    /* Get random Column */
    minCol = tryShape.getStartCol();
    tryCol = randInt( rnd, minCol, maxCol );

    /* Return generated random gene configuration */
    return new BinPackingGene( tryCol, tryRow, tryRotation );
  }

  /* Private helper for getRandomGene */
  private static int randInt( Random rnd, int min, int max ) {
    return rnd.nextInt( ( max - min ) + 1 ) + min;
  }

  void setBound( int bnd ) {
    this.bound = bnd;
  }

  @Override
  public boolean shouldContinue() {
    return this.currentIndex < this.population.length;
  }

  @Override
  public Individual getBestIndividual() {
    return null;
  }

  public BinPackingSolution[] getPopulation() {
    return this.population;
  }


}
