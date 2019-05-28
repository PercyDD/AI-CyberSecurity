
public class ProblemFunctions {
	
	public static void main(String[] args){
		System.out.println(function1(0.9));
		System.out.println(function2(0,0));
	}
    
	/**
	 * This method replicates the function 1 given in the coursework.
	 * @param x - input x to be run the function.
	 * @return value - the value output from function 1 with x
	 */
    public static double function1(double x){
    	double part1 = 3 * Math.pow((x - 0.75),4);
    	double part2 = Math.sin((5 * Math.PI * x) - (0.4 * Math.PI)) - 0.43;
    	double value = part1 + part2;
    	return value;
    }
    
    /**
     * 
     * @param x - input x to be run the function.
     * @param y - input y to be run the function.
     * @return value - the value output from function 2 with x and y
     */
    public static double function2(double x, double y){  	
    	double part1 = Math.pow(Math.sin(3 * Math.PI * x), 2);
    	double part2 = Math.pow((x - 1), 2) * (1 + Math.pow(Math.sin(3 * Math.PI * y), 2));
    	double part3 = Math.pow((y - 1), 2) * (1 + Math.pow(Math.sin(2 * Math.PI * y), 2));
    	double value = part1 + part2 + part3;
    	return value;
    }
    
    public static double holderTableFunction(double x, double y) {
		double x1 = x;
		double x2 = y;
		
		double part1 = Math.sin(x1) * Math.cos(x2);
		double part2 = Math.exp(Math.abs(1 - (Math.sqrt(Math.pow(x1, 2) + 
				Math.pow(x2, 2))) / Math.PI));
		
		double f = -Math.abs(part1 * part2);
		
		return f;
    }
    
    public static double eggFunction(double x, double y) {
    	return -(y+47)*Math.sin(Math.sqrt(Math.abs((x/2)+(y+47))))-x*Math.sin(Math.sqrt(Math.abs(x-(y+47))));
    }
        
}
