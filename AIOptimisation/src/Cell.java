import java.util.concurrent.ThreadLocalRandom;

public class Cell {

	private double input[];
	//This is the output of the function for each cell. I have named it affinity since I do not
	//calculate affinity since I am interested the minimum I just rank the affinity (the output) values for each
	//cell. Therefore, in this case the lower affinity cells are cloned and the high affinity cells are replaced.
	private double affinity;
	
	/**
	 * This constructor initialises the input array based on the dimensions and creates
	 * random values for it.
	 * @param dimensions - this decides which function to use. eg 1 would be the function with x as input.
	 * @param minRange - lower bound of range for function.
	 * @param maxRange - upper bound of range for function.
	 */
	public Cell(int dimensions, double minRange, double maxRange){
		this.setInput(new double[dimensions]);
		//Random initialisation of each cell
		randomInit(minRange, maxRange);
	}
	
	/**
	 * This method inserts random values for the input/inputs depending on whether
	 * there is 1 or 2 dimensions.
	 * @param minRange - lower bound of range for function.
	 * @param maxRange - upper bound of range for function.
	 */
	public void randomInit(double minRange, double maxRange){
		for(int i = 0; i < input.length; i++){
			input[i] = ThreadLocalRandom.current().nextDouble(minRange, maxRange);
		}
	}
	
	/**
	 * This method is used to print the inputs and outputs of the solutions nicely.
	 */
	public void printInfo(){
		
		for(int i = 0; i < input.length; i++) {
			System.out.print("Input: " + input[i] + ",");
		}
		System.out.println(" Affinity: " + affinity);
	}
	
	public double getAffinity() {
		return affinity;
	}

	public void setAffinity(double affinity) {
		this.affinity = affinity;
	}

	public double[] getInput() {
		return input;
	}

	public void setInput(double input[]) {
		this.input = java.util.Arrays.copyOf(input, input.length);
	}
}

