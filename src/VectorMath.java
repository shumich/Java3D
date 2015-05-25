import java.util.Random;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;


public abstract class VectorMath {
	
	/*public static void main (String [] args)
	{
		Vector3f [] array = decompose (new Vector3f (5,6,7), new Vector3f (1,-1,0), new Vector3f (2,2,0), new Vector3f (0,0,1));
		System.out.print(Arrays.toString(array));
	}*/
	
	public static Vector3f add(Vector3f a, Vector3f b){
		Vector3f temp = new Vector3f(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
		return temp;
	}

	public static Vector3f subtract(Vector3f a, Vector3f b){
		Vector3f temp = new Vector3f(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
		return temp;
	}
	
	public static Vector3f add(Point3f a, Point3f b){
		Vector3f temp = new Vector3f(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
		return temp;
	}

	public static Vector3f subtract(Point3f a, Point3f b){
		Vector3f temp = new Vector3f(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
		return temp;
	}
	
	public static Vector3f scalarMultiply (Vector3f a, float scalar){
		Vector3f temp = new Vector3f(a.getX()*scalar, a.getY()*scalar, a.getZ()*scalar);
		return temp;
	}
	
	public static Point3f add(Point3f a, Vector3f b){
		Point3f point = new Point3f(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
		return point;
	}
	
	public static Point3f subtract(Point3f a, Vector3f b){
		Point3f point = new Point3f(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
		return point;
	}
	
	public static float dot (Vector3f a, Vector3f b){
		float x1 = a.getX(), y1 = a.getY(), z1 = a.getZ();
		float x2 = b.getX(), y2 = b.getY(), z2 = b.getZ();
		
		return (x1*x2 + y1*y2 + z1*z2);
		
	}
	
	public static float getMagnitude (Vector3f a){
		float mag = (float)Math.sqrt(a.getX()*a.getX() + a.getY()*a.getY() + a.getZ()*a.getZ());
		return mag;
	}
	
	public static Vector3f normalize (Vector3f a){
		float mag = getMagnitude (a);
		Vector3f unit = new Vector3f (a.getX()/mag, a.getY()/mag, a.getZ()/mag);
		return unit;
	}
	
	public static Vector3f random(){
		Random rand = new Random();
		
		float x = (float) rand.nextGaussian();
		float y = (float) rand.nextGaussian();
		float z = (float) rand.nextGaussian();
		
		Vector3f random = new Vector3f(x,y,z);
		random.normalize();
		
		return random;
	}
	
	//works only if a,b,c are mutually perpendicular
	public static Vector3f [] decompose (Vector3f vector, Vector3f a, Vector3f b, Vector3f c){
		Vector3f unitA = normalize(a);
		Vector3f unitB = normalize(b);
		Vector3f unitC = normalize(c);
		
		Vector3f [] components = new Vector3f [3];
		
		/*System.out.println(dot(vector, unitA));
		System.out.println(dot(vector, unitB));
		System.out.println(dot(vector, unitC));*/
		
		components [0] = scalarMultiply(a,dot(vector, unitA));
		components [1] = scalarMultiply(b,dot(vector, unitB));
		components [2] = scalarMultiply(c,dot(vector, unitC));
		
		return components;
	}

}
