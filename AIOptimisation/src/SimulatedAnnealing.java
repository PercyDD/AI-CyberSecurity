import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class SimulatedAnnealing {
	
	private double temp;
	private double coolingRate;
	private int iterations;
	private int dimensions;
	private double minRange;
	private double maxRange;
	private String functionName;
	private Solution bestSolution;
	private Solution currentSolution;
	private ArrayList<Double> currentValueErrorPerTemp;
	private ArrayList<Double> averageErrorPerTemp;
	private ArrayList<Double> bestValuePerTemp;
	
	/**
	 * The constructor that sets a lot of the variables and creates a random value between the range.
	 * We set the current and best solutions initially to the first random solution.
	 * @param temp - starting temperature.
	 * @param coolingRate - small number to reduce temperature.
	 * @param iterations - the amount of iterations for each temperature.
	 * @param dimensions - this decides which function to use. eg 1 would be the function with x as input.
	 * @param minRange - lower bound of range.
	 * @param maxRange - upper bound of range.
	 */
	public SimulatedAnnealing(double temp, double coolingRate, int iterations, int dimensions, double minRange, double maxRange, String functionName) {
		this.setTemp(temp);
		this.setCoolingRate(coolingRate);
		this.setIterations(iterations);
		this.setDimensions(dimensions);
		this.setMinRange(minRange);
		this.setMaxRange(maxRange);
		this.setFunctionName(functionName);
		this.currentSolution = new Solution(getDimensions(), getMinRange(), getMaxRange());
		this.bestSolution = currentSolution;
		this.currentValueErrorPerTemp = new ArrayList<Double>();
		this.bestValuePerTemp = new ArrayList<Double>();
		this.setAverageErrorPerTemp(new ArrayList<Double>());
		
		runProblem(currentSolution);
		runProblem(bestSolution);
	}
	
	/**
	 * This method brings all the elements together to create the process.
	 * This is my main loop which will iterate until the temperature is low.
	 * Each temperature it will loop N times, creating a solution with random inputs in the range of the problem function.
	 * The new solution is compared to the current solution to see which output is closer to the minimum.
	 * If the new solution is closer we set that as the current solution and check if its closer to the minimum than the best solution.
	 * If it is the best solution is also set to the new solution.
	 * Otherwise we calculate the acceptance probability and compare to a random number between 0 and 1.
	 * If its greater than the random number we accept the new solution and otherwise repeat to get another new solution.
	 * @param sa - the simulated annealing object created in main.
	 */
	public void run(SimulatedAnnealing sa) {
		int count = 0;
		
    	double meanError = 0.0;
    	double minOptimalSolution;
    	if(getFunctionName() == "f1") {
    		minOptimalSolution = -1.43;
    	} else if(getFunctionName() == "f2") {
    		minOptimalSolution = 0.0;	
    	} else if(getFunctionName() == "holder") {
    		minOptimalSolution = -19.2085;
    	} else {
    		minOptimalSolution = -959.6407;
    	}
		
		while(temp > 1) {
			double sumCurrSolution = 0.0;
			for(int i = 0; i < getIterations(); i++) {
				Solution newSolution = new Solution(getDimensions(), getMinRange(), getMaxRange());
				runProblem(newSolution);
				double distance = calculateDistance(currentSolution.getOutput(), newSolution.getOutput());
				if (distance > 0) {
					currentSolution = newSolution;
					distance = calculateDistance(bestSolution.getOutput(), newSolution.getOutput());
					if (distance > 0) {
						bestSolution = newSolution;
					}
				} else if(acceptanceProbability(-distance) > generateRandNum(0, 1)) {
					currentSolution = newSolution; 
				}
				sumCurrSolution += Math.sqrt(Math.pow(minOptimalSolution - newSolution.getOutput(), 2));		
			}
			
			meanError = sumCurrSolution / getIterations();
			averageErrorPerTemp.add(meanError);
			currentValueErrorPerTemp.add(Math.sqrt(Math.pow(minOptimalSolution - currentSolution.getOutput(), 2)));
			bestValuePerTemp.add(Math.sqrt(Math.pow(minOptimalSolution - bestSolution.getOutput(), 2)));
			count++;
			
			temp *= 1 - getCoolingRate();
		}
	}
	
	/**
	 * This method runs the functions from the ProbelmFunction class.
	 * I check the dimensions of the solution to ensure the correct function is called.
	 * The output of the function is stored as output in solution object.
	 * @param sol - the solution that contains an input.
	 */
	public void runProblem(Solution sol) {
		if(getFunctionName() == "f1") {
			sol.setOutput(ProblemFunctions.function1(sol.getInput()[0]));
		} else if (getFunctionName() == "f2"){
			sol.setOutput(ProblemFunctions.function2(sol.getInput()[0], sol.getInput()[1]));
		} else if (getFunctionName() == "holder") {
			sol.setOutput(ProblemFunctions.holderTableFunction(sol.getInput()[0], sol.getInput()[1]));
		} else if (getFunctionName() == "egg") {
			sol.setOutput(ProblemFunctions.eggFunction(sol.getInput()[0], sol.getInput()[1]));
		}
	}
	
	/**
	 * This method calculates the distance by subtracting the new solution output from the original solution.
	 * A positive number will always mean the second one is better.
	 * @param bestSolutionCost - the output for best solution
	 * @param newSolutionCost - the output for the new solution.
	 * @return distance - A pos or neg number. Dont care exactly the value for this, only whether its pos or neg.
	 */
	public double calculateDistance(double originalSolutionCost, double newSolutionCost) {
		double distance = originalSolutionCost - newSolutionCost;
		return distance;
	}
	
	/**
	 * This method calculates the acceptance probability.
	 * @param distance - the distance calculated between two solutions.
	 * @return probability - the chance the new solution will be accepted to get out of local minima.
	 */
	public double acceptanceProbability(double distance) {
		//Not sure if its meant to be distance or -distance.
		double probability = Math.exp(-distance / getTemp());
		return probability;
	}
	
	/**
	 * This method generates a random number between two range.
	 * @param minRange - lower bound
	 * @param maxRange - upper bound
	 * @return newRandomNum - the random number that has been generated.
	 */
	public double generateRandNum(double minRange, double maxRange) {
		Random rnd = new Random();
		double newRandomNum = minRange + (maxRange - minRange) * rnd.nextDouble();
		return newRandomNum;	
	}
	
	public ArrayList<Double> getcurrentValueErrorPerTemp() {
		return currentValueErrorPerTemp;
	}
	
	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public double getCoolingRate() {
		return coolingRate;
	}

	public void setCoolingRate(double coolingRate) {
		this.coolingRate = coolingRate;
	}	

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}	

	public int getDimensions() {
		return dimensions;
	}

	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
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
	
	public Solution getBestSolution() {
		return bestSolution;
	}
	
	public Solution getCurrentSolution() {
		return currentSolution;
	}
	
	public ArrayList<Double> getaverageErrorPerTemp() {
		return averageErrorPerTemp;
	}

	public void setAverageErrorPerTemp(ArrayList<Double> averageErrorPerTemp) {
		this.averageErrorPerTemp = averageErrorPerTemp;
	}
	
	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
    public static void main(String[] args){
    	//Function 1
    	SimulatedAnnealing sa = new SimulatedAnnealing(2000, 0.005, 10, 1, 0, 1.5, "f1");
    	sa.run(sa);
    	
    	System.out.println("Function 1: Best result was with input X = " + sa.getBestSolution().getInput()[0] + ", with a minimum of " + sa.getBestSolution().getOutput());
    	
        SwingUtilities.invokeLater(() -> {
        	XYLineChartSolution saChart = new XYLineChartSolution("Simulated Annealing - Function 1", sa.getcurrentValueErrorPerTemp(), sa.getaverageErrorPerTemp(), sa.bestValuePerTemp);
            saChart.setSize(800, 400);
            saChart.setLocationRelativeTo(null);
            saChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            saChart.setVisible(true);
          });
        
        //Function 2
        long startTimeF2 = System.nanoTime();
    	SimulatedAnnealing sa2 = new SimulatedAnnealing(2000, 0.005, 10, 2, -5, 5, "f2");
    	sa2.run(sa2);
    	long endTimeF2 = System.nanoTime();
    	long durationF2 = (endTimeF2 - startTimeF2);
    	System.out.println("Simulated Annealing (function 2) took " + TimeUnit.MILLISECONDS.convert(durationF2, TimeUnit.NANOSECONDS) + "milliseconds.");
    	
    	System.out.println("The best cell for function 2 is when X = " + sa2.getBestSolution().getInput()[0] + " and Y = " + sa2.getBestSolution().getInput()[1] + ", with a min of " + sa2.getBestSolution().getOutput());
        
        SwingUtilities.invokeLater(() -> {
        	XYLineChartSolution saChart = new XYLineChartSolution("Simulated Annealing - Function 2", sa2.getcurrentValueErrorPerTemp(), sa2.getaverageErrorPerTemp(), sa2.bestValuePerTemp);
            saChart.setSize(800, 400);
            saChart.setLocationRelativeTo(null);
            saChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            saChart.setVisible(true);
          });
        
    	SimulatedAnnealing sa3 = new SimulatedAnnealing(2000, 0.005, 10, 2, -10, 10, "holder");
    	sa3.run(sa3);
    	
    	System.out.println("The best cell for function 2 is when X = " + sa3.getBestSolution().getInput()[0] + " and Y = " + sa3.getBestSolution().getInput()[1] + ", with a min of " + sa3.getBestSolution().getOutput());
        
    	//Used for testing purposes, visualised and ran the holder table and egg function
    	
    	/**SwingUtilities.invokeLater(() -> {
        	XYLineChartSolution saChart = new XYLineChartSolution("Simulated Annealing - holder", sa3.getcurrentValueErrorPerTemp(), sa3.getaverageErrorPerTemp());
            saChart.setSize(800, 400);
            saChart.setLocationRelativeTo(null);
            saChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            saChart.setVisible(true);
          });
        
    	SimulatedAnnealing sa4 = new SimulatedAnnealing(5000, 0.005, 10, 2, -512, 512, "egg");
    	sa4.run(sa4);
    	
    	System.out.println("The best cell for function 2 is when X = " + sa4.getBestSolution().getInput()[0] + " and Y = " + sa4.getBestSolution().getInput()[1] + ", with a min of " + sa4.getBestSolution().getOutput());
        
        SwingUtilities.invokeLater(() -> {
        	XYLineChartSolution saChart = new XYLineChartSolution("Simulated Annealing - egg", sa4.getcurrentValueErrorPerTemp(), sa4.getaverageErrorPerTemp());
            saChart.setSize(800, 400);
            saChart.setLocationRelativeTo(null);
            saChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            saChart.setVisible(true);
          });**/
        
        //Testing reliability of functions by running it ten times and averaging the outputs.
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
	        	SimulatedAnnealing saF1 = new SimulatedAnnealing(2000, 0.0005, 10, 1, 0, 1.5, "f1");
	        	saF1.run(saF1);
	        	if(saF1.getBestSolution().getOutput() <= (minOptimalSolutionF1 + 0.0000025)) {
	        		reliabilityScoreF1++;
	        	}
	        	
	        	SimulatedAnnealing saF2 = new SimulatedAnnealing(2000, 0.0005, 10, 2, -5, 5, "f2");
	        	saF2.run(saF2);
	        	if(saF2.getBestSolution().getOutput() <= (minOptimalSolutionF2 + 0.0008)) {
	        		reliabilityScoreF2++;
	        	}
	        	
	        	SimulatedAnnealing saHolder = new SimulatedAnnealing(2000, 0.005, 10, 2, -10, 10, "holder");
	        	saHolder.run(saHolder);
	        	if(saHolder.getBestSolution().getOutput() <= (minOptimalSolutionHolder + 0.2)) {
	        		reliabilityScoreHolder++;
	        	}
	        	
	        	SimulatedAnnealing saEgg = new SimulatedAnnealing(2000, 0.005, 10, 2, -512, 512, "egg");
	        	saEgg.run(saEgg);
	        	if(saEgg.getBestSolution().getOutput() <= (minOptimalSolutionEgg + 5)) {
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
