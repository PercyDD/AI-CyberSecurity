import java.util.Random;

public class Solution {
	
	private double input[];
	private double output;
	
	/**
	 * This constructor initialises the input array based on the dimensions and creates
	 * random values for it.
	 * @param dimensions - this decides which function to use. eg 1 would be the function with x as input.
	 * @param minRange - lower bound of range.
	 * @param maxRange - upper bound of range.
	 */
	public Solution(int dimensions, double minRange, double maxRange) {
		this.setInput(new double[dimensions]);
		initialise(minRange, maxRange);
	}
	
	/**
	 * This method inserts random values for the input/inputs depending on whether
	 * the dimensions were 1 or 2.
	 * @param minRange - lower bound of range for function.
	 * @param maxRange - upper bound of range for function.
	 */
	public void initialise(double minRange, double maxRange) {
		for(int i = 0; i < input.length; i++) {
			input[i] = generateRandNum(minRange, maxRange);
		}
	}
	
	/**
	 * This method creates a random number between range parameters.
	 * @param minRange - lower bound of range for function.
	 * @param maxRange - upper bound of range for function.
	 * @return
	 */
	public double generateRandNum(double minRange, double maxRange) {
		Random rnd = new Random();
		double newRandomNum = minRange + (maxRange - minRange) * rnd.nextDouble();
		return newRandomNum;	
	}
	
	/**
	 * This method is used to print the inputs and outputs of the solutions nicely.
	 */
	public void printInfo(){
		
		for(int i = 0; i < input.length; i++) {
			System.out.print("Input: " + input[i] + ",");
		}
		System.out.println(" Output: " + output);
	}
	
	public double[] getInput() {
		return input;
	}

	public void setInput(double input[]) {
		this.input = input;
	}

	public double getOutput() {
		return output;
	}

	public void setOutput(double output) {
		this.output = output;
	}
}
