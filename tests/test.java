import objects.*;
public class test {
	public static void main(String[] args){
		Pointd y = new Pointd(10.0,5.0);
		Pointd x = new Pointd(1.0,5.0);
		Pointd nx = new Pointd(-1.0,5.1);
		Line xl = new Line(x,nx);
		double result = y.distance(xl);
		System.out.println(String.format(
			"%.3f", result));}}
