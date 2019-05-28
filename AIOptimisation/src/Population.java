import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Population {
	private int populationSize; 
	private int dimensions;
	private double replaceRate;
	private double cloneRate;
	private double  minRange, maxRange;
	private int iterations;
	private String funcName;
	private ArrayList<Cell> allCells;
	private ArrayList<Double> top10MeanErrorPerIteration;
	private ArrayList<Double> top50MeanErrorPerIteration;
	private ArrayList<Double> averageErrorPerIteration;
	private ArrayList<Double> topSolutionError;
    
	/**
	 * This constructor creates a population of cells by setting all the necessary attributes below.
	 * It also initialises an array list to store 100 new cells. It does this by looping from 0 to the population
	 * size, adding a new cell object each time.
	 * @param minRange - minimum range of the function.
	 * @param maxRange - maximum range of the function.
	 * @param dimensions - how many inputs for the function. 1 = x, 2 = x, y
	 * @param cloneRate - a decimal number below one eg. 0.1 which would mean 10% is cloned in the population.
	 * @param initialMutateMultiplier - value used as part of the mutation process.
	 * @param replaceRate - a decimal number below one eg. 0.1 which would mean 10% of the population is replaced.
	 * @param populationSize - the amount of cells in the population.
	 * @param iterations - the amount of iterations to do to find the minimum.
	 */
    public Population(double minRange, double maxRange, int dimensions,
    				  double cloneRate, double replaceRate, int populationSize, int iterations, String funcName){
    	
    	this.setMinRange(minRange);
    	this.setMaxRange(maxRange);
    	this.setDimension(dimensions);
    	this.setCloneRate(cloneRate);
    	this.setReplaceRate(replaceRate);
    	this.setPopulationSize(populationSize);
    	this.setIterations(iterations);
    	this.funcName = funcName;
    	this.setTopSolutionError(new ArrayList<Double>());
    	this.setTop10MeanPerIteration(new ArrayList<Double>());
    	this.setAverageErrorPerIteration(new ArrayList<Double>());
    	this.setTop50MeanErrorPerIteration(new ArrayList<Double>());
    	
    	this.allCells = new ArrayList<Cell>();
    	for(int i = 0; i < populationSize; i++){
    		allCells.add(new Cell(dimensions, minRange, maxRange));
    	}
    }
    
    /**
     * This method is the main one that runs all the elements of the clonal selection
     * algorithm together. It starts by running the problem over all cells in the population to get
     * an output for each cell. Then the array list ranked in descending order as we want to find the find
     * minimum and therefore element 0 in the array list is the best cell in the population. 
     * The method will loop n amount of iterations. For each loop I mutate the last 90 cells in population, mutating
     * cells further away from the minimum more than cells closer to it. I do not touch the first 10 cells in the array
     * list as they are the cloned ones. The bottom 10 cells are replaced with randomly generated cells so the population
     * size stays the same. I then run the problem again and re-evaluation by ranking them. After printing the cells to the
     * terminal it will keep repeating starting with the mutation.
     * @param pop - the population object.
     */
    public void run(Population pop) {
    	pop.runProblem();
    	pop.rankPopulation();
    	int count = 0;
    	
    	while(count < getIterations()) {
    		pop.mutatePopulation();
    		pop.removeAndReplace();
        	pop.runProblem();
        	pop.rankPopulation();
        	
        	double optimalSolution;
        	if(funcName == "f1") {
        		optimalSolution = -1.43;
        	} else if(funcName == "f2") {
        		optimalSolution = 0.0;	
        	} else if(funcName == "holder") {
        		optimalSolution = -19.2085;
        	} else {
        		optimalSolution = -959.6407;
        	}
        	
        	double top10CellErrorSum = 0.0;
        	for(int i = 0; i < 10; i++) {
        		top10CellErrorSum += Math.sqrt(Math.pow(optimalSolution - allCells.get(i).getAffinity(), 2));
        	}
        	top10MeanErrorPerIteration.add(top10CellErrorSum / 10);
        	
        	double top50CellErrorsum = 0.0;
        	for(int j = 0; j < 50; j++) {
        		top50CellErrorsum += Math.sqrt(Math.pow(optimalSolution - allCells.get(j).getAffinity(), 2));
        	}
        	top50MeanErrorPerIteration.add(top50CellErrorsum / 50);
        	
        	double meanError = 0.0;
        	for(int k = 0; k < allCells.size(); k++) {
        		meanError += Math.sqrt(Math.pow(optimalSolution - allCells.get(k).getAffinity(), 2));
        	}
        	meanError = meanError / allCells.size();
        	averageErrorPerIteration.add(meanError);
        	
        	topSolutionError.add(Math.sqrt(Math.pow(allCells.get(0).getAffinity(), 2)));
        			
        	count++;
        	
        	/*for(Cell c: allCells){
        		c.printInfo();
        	}*/
        	//System.out.println("********************** ROUND " + count + " **********************");
    	}
    }
    
	/**
	 * This method runs the functions from the ProbelmFunction class.
	 * I check the dimensions of the solution to ensure the correct function is called.
	 * The affinity (output) of the function is stored as output in solution object.
	 */
    public void runProblem() {
    	for (Cell i : allCells) {
    		if(funcName == "f1") {
    			i.setAffinity(ProblemFunctions.function1(i.getInput()[0]));
    		} else if(funcName == "f2") {
    			i.setAffinity(ProblemFunctions.function2(i.getInput()[0], i.getInput()[1]));
    		} else if(funcName == "holder") {
    			i.setAffinity(ProblemFunctions.holderTableFunction(i.getInput()[0], i.getInput()[1]));
    		} else {
    			i.setAffinity(ProblemFunctions.eggFunction(i.getInput()[0], i.getInput()[1]));
    		}	
    	}
    }
    
    /**
     * This method ranks the array list of cells in descending order based on their
     * affinity (output) scores. First element in the cell will be the best with low affinity.
     */
    public void rankPopulation() {
    	Collections.sort(allCells, 
    		    Comparator.comparingDouble(Cell::getAffinity));
    }
    
    /**
     * This method is responsible for mutating the population. I firstly calculate the range we are
     * going to loop through the array list using the replace and clone rates, as I do not need to mutate
     * them because they will either be cloned or replaced. There is no cloning process really we just do
     * not mutate the first N elements in the array list depending on the clone rate.
     * For each cell in the loop we calculate new min and max ranges for the random number generation. I use
     * the index / 100 along with the multiplier to increase and decrease the ranges. The higher the index,
     * the more of a change in ranges for random number. The cells with higher indexes need to be mutated more
     * since the array list is ordered so it works out.
     * I then set the new input/inputs and modify the element in the array list.
     */
    public void mutatePopulation() {
    	double inputArray[];
    	if(getDimension() == 2) {
    		inputArray = new double[2];
    	} else {
    		inputArray = new double[1];
    	}
    	int upperBound = (int) (allCells.size() - (getReplaceRate() * 100));
    	//This will ensure we do not mutate the first n many cells as then they are essentially cloned.
    	int lowerBound = (int) (getCloneRate() * 100);
    	//Number one is not mutated
    	for(int i = lowerBound; i < upperBound; i++) {
    		Cell currentCell = allCells.get(i);
    		Random rndX = new Random();
    		double minRangeX = currentCell.getInput()[0] - (double) i /100;
			double maxRangeX = currentCell.getInput()[0] + (double) i / 100;
			double newInputValueX = minRangeX + (maxRangeX - minRangeX) * rndX.nextDouble();
    		inputArray[0] = newInputValueX;
			if(getDimension() == 2) {
				Random rndY = new Random();
	    		double minRangeY = currentCell.getInput()[1] - (double) i /100;
				double maxRangeY = currentCell.getInput()[1] + (double) i / 100;
				double newInputValueY = minRangeY + (maxRangeY - minRangeY) * rndY.nextDouble();
	    		inputArray[1] = newInputValueY;
			}
    		currentCell.setInput(inputArray);
    		allCells.set(i, currentCell);
    	}
    }
    
    /**
     * This method loops backwards in the array list as we want to replace the last N elements depending 
     * on the replace rate. I then create a new cell object and set it in the array list.
     */
    public void removeAndReplace() {
    	for(int i = allCells.size() - 1; i > (allCells.size() - (getReplaceRate() * 100)); i--) {
    		Cell c = new Cell(getDimension(), getMinRange(), getMaxRange());
    		allCells.set(i, c);
    	}	
    }
    
	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public double getCloneRate() {
		return cloneRate;
	}

	public void setCloneRate(double cloneRate) {
		this.cloneRate = cloneRate;
	}

	public double getMinRange() {
		return minRange;
	}

	public void setMinRange(double minRange) {
		this.minRange = minRange;
	}

	public double getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(double maxRange) {
		this.maxRange = maxRange;
	}

	public double getReplaceRate() {
		return replaceRate;
	}

	public void setReplaceRate(double replaceRate) {
		this.replaceRate = replaceRate;
	}
	
	public int getDimension() {
		return dimensions;
	}

	public void setDimension(int dimensions) {
		this.dimensions = dimensions;
	}
	
	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	
	public ArrayList<Double> getTop10MeanPerIteration() {
		return top10MeanErrorPerIteration;
	}

	public void setTop10MeanPerIteration(ArrayList<Double> top5MeanPerIteration) {
		this.top10MeanErrorPerIteration = top5MeanPerIteration;
	}
	
	public ArrayList<Double> getAverageErrorPerIteration() {
		return averageErrorPerIteration;
	}

	public void setAverageErrorPerIteration(ArrayList<Double> averageErrorPerIteration) {
		this.averageErrorPerIteration = averageErrorPerIteration;
	}
	
	public ArrayList<Double> getTop50MeanErrorPerIteration() {
		return top50MeanErrorPerIteration;
	}

	public void setTop50MeanErrorPerIteration(ArrayList<Double> top50MeanErrorPerIteration) {
		this.top50MeanErrorPerIteration = top50MeanErrorPerIteration;
	}
	
	public ArrayList<Double> getTopSolutionError() {
		return topSolutionError;
	}

	public void setTopSolutionError(ArrayList<Double> topSolutionError) {
		this.topSolutionError = topSolutionError;
	}
	
    public static void main(String[] args){
    	Population newPop1 = new Population(0, 1.5, 1, 0.1, 0.1, 100, 500, "f1");
    	newPop1.run(newPop1);
    	
    	System.out.println("The best cell for function 1 is when X = " + newPop1.allCells.get(0).getInput()[0] + ", with a min of " + newPop1.allCells.get(0).getAffinity());
    	
        SwingUtilities.invokeLater(() -> {
        	XYLineChartCell saChart = new XYLineChartCell("Clonal Selection Algorithm - Function 1", newPop1.getTop10MeanPerIteration(), newPop1.getTop50MeanErrorPerIteration(), newPop1.getAverageErrorPerIteration(), newPop1.getTopSolutionError());
            saChart.setSize(800, 400);
            saChart.setLocationRelativeTo(null);
            saChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            saChart.setVisible(true);
        });
        
        long startTime2 = System.nanoTime();
        
    	Population newPop2 = new Population(-5, 5, 2, 0.1, 0.1, 100, 500, "f2");
    	newPop2.run(newPop2);
    	long endTime2 = System.nanoTime();
    	long duration2 = (endTime2 - startTime2);
    	System.out.println("Clonal Selection Algorithm (function 2) took " + TimeUnit.MILLISECONDS.convert(duration2, TimeUnit.NANOSECONDS) + "milliseconds.");
    	
        SwingUtilities.invokeLater(() -> {
        	XYLineChartCell saChart = new XYLineChartCell("Clonal Selection Algorithm - Function 2", newPop2.getTop10MeanPerIteration(), newPop2.getTop50MeanErrorPerIteration(), newPop2.getAverageErrorPerIteration(), newPop2.getTopSolutionError());
            saChart.setSize(800, 400);
            saChart.setLocationRelativeTo(null);
            saChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            saChart.setVisible(true);
        });
    	
    	System.out.println("The best cell for function 2 is when X = " + newPop2.allCells.get(0).getInput()[0] + " and Y = " + newPop2.allCells.get(0).getInput()[1] + ", with a min of " + newPop2.allCells.get(0).getAffinity());
    
    	//Used throughout the project to compare or test but didnt end up using, ran and visualised holder table and egg functions
    	
    	/*Population newPop3 = new Population(-10, 10, 2, 0.1, 0.1, 100, 250, "holder");
    	newPop3.run(newPop3);
    	
    	System.out.println("The best cell for holder function is when X = " + newPop3.allCells.get(0).getInput()[0] + ", with a min of " + newPop3.allCells.get(0).getAffinity());
    	
        SwingUtilities.invokeLater(() -> {
        	XYLineChartCell saChart = new XYLineChartCell("Clonal Selection Algorithm - Holder", newPop3.getTop10MeanPerIteration(), newPop3.getTop50MeanErrorPerIteration(), newPop3.getAverageErrorPerIteration(), newPop3.getTopSolutionError());
            saChart.setSize(800, 400);
            saChart.setLocationRelativeTo(null);
            saChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            saChart.setVisible(true);
        });
        
    	Population newPop4 = new Population(-512, 512, 2, 0.1, 0.1, 100, 250, "egg");
    	newPop4.run(newPop4);
    	
    	System.out.println("The best cell for holder function is when X = " + newPop4.allCells.get(0).getInput()[0] + ", with a min of " + newPop4.allCells.get(0).getAffinity());
    	
        SwingUtilities.invokeLater(() -> {
        	XYLineChartCell saChart = new XYLineChartCell("Clonal Selection Algorithm - Egg", newPop4.getTop10MeanPerIteration(), newPop4.getTop50MeanErrorPerIteration(), newPop4.getAverageErrorPerIteration(), newPop4.getTopSolutionError());
            saChart.setSize(800, 400);
            saChart.setLocationRelativeTo(null);
            saChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            saChart.setVisible(true);
        });*/
    	
    	
        /**
         * The rest of this main is responsible for the reliability score by
         * generating populations and comparing the best cell 
         * to see if its within a limit of the minimum.
         */
    	double minOptimalSolutionF1 = -1.43;
        double minOptimalSolutionF2 = 0.0;
        double minOptimalSolutionHolder = -19.2085;
        double minOptimalSolutionEgg = -959.6407;
        
        double reliAverScoreF1 = 0.0;
        double reliAverScoreF2 = 0.0;
        double reliAverScoreHolder = 0.0;
        double reliAverScoreEgg = 0.0;
        int iterations = 20;
        for(int j = 0; j < 5; j++) {
            int reliabilityScoreF1 = 0;
            int reliabilityScoreF2 = 0;
            int reliabilityScoreHolder = 0;
            int reliabilityScoreEgg = 0;
	        for(int i = 0; i < iterations; i++) {
	        	Population pop1 = new Population(0, 1.5, 1, 0.1, 0.1, 100, 500, "f1");
	        	pop1.run(pop1);
	        	if(pop1.allCells.get(0).getAffinity() <= (minOptimalSolutionF1 + 0.0000025)) {
	        		reliabilityScoreF1++;
	        	}
	        	
	        	Population pop2 = new Population(-5, 5, 2, 0.1, 0.1, 100, 500, "f2");
	        	pop2.run(pop2);
	        	if(pop2.allCells.get(0).getAffinity() <= (minOptimalSolutionF2 + 0.0008)) {
	        		reliabilityScoreF2++;
	        	}
	        	
	        	Population pop3 = new Population(-10, 10, 2, 0.1, 0.1, 100, 250, "holder");
	        	pop3.run(pop3);
	        	if(pop3.allCells.get(0).getAffinity() <= (minOptimalSolutionHolder + 0.2)) {
	        		reliabilityScoreHolder++;
	        	}
	        	
	        	Population pop4 = new Population(-512, 512, 2, 0.1, 0.1, 100, 250, "egg");
	        	pop4.run(pop4);
	        	if(pop4.allCells.get(0).getAffinity() <= (minOptimalSolutionEgg + 5)) {
	        		reliabilityScoreEgg++;
	        	}
	        }
	        reliAverScoreF1 = reliAverScoreF1 + reliabilityScoreF1;
	        reliAverScoreF2 = reliAverScoreF2 + reliabilityScoreF2;
	        reliAverScoreHolder = reliAverScoreHolder + reliabilityScoreHolder;
	        reliAverScoreEgg = reliAverScoreEgg + reliabilityScoreEgg;
        }
        reliAverScoreF1 = reliAverScoreF1 / 5;
        reliAverScoreF2 = reliAverScoreF2 / 5;
        reliAverScoreHolder = reliAverScoreHolder / 5;
        reliAverScoreEgg = reliAverScoreEgg / 5;
        System.out.println("F1 Reliability: " + reliAverScoreF1 + "/" + iterations);
        System.out.println("F2 Reliability: " + reliAverScoreF2 + "/" + iterations);
        System.out.println("Holder Reliability: " + reliAverScoreHolder + "/" + iterations);
        System.out.println("Egg Reliability: " + reliAverScoreEgg + "/" + iterations);
    }
}
