import javax.media.j3d.BadTransformException;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;


public class MovingSphere extends Sphere{
	public static boolean gravity;
	private static float G = 0.00000001f;
	
	private Vector3f acceleration;
	private Vector3f velocity;
	private Point3f position;
	private Transform3D transform;
	public TransformGroup transGroup = new TransformGroup();
	private float mass;
	
	public MovingSphere(Vector3f v, Point3f p, float r){
		//The constructors are the velocity, the position, and the radius of the sphere. 
		//The mass is defaulted to 1.
		//Accelleration is defaulted to 0
		super(r);
		this.mass = 1f;
		velocity = v;
		transform = new Transform3D();
		transform.setTranslation(new Vector3f(p.getX(), p.getY(), p.getZ()));
		transGroup.setTransform(transform);
		transGroup.addChild(this);
		transGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		transGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		position = p;
		acceleration = new Vector3f(0,0,0);
		super.setAppearance();
	}
	
	public Transform3D getTransform(){
		//Returns the transformation object
		return transform;
	}
	
	public void move() throws BadTransformException{
		//Moves the sphere by adding the velocity to the position
		velocity = VectorMath.add(velocity, VectorMath.scalarMultiply(acceleration, (1/mass)));
		position = VectorMath.add(position, velocity);
		
		//Sets the new transformation object to the transform group
		transform.setTranslation(new Vector3f(position.getX(), position.getY(), position.getZ()));
		transGroup.setTransform(transform);
	}
	
	public void setMass (float mass){
		//Sets the mass of the object
		this.mass = mass;
	}
	
	public float getMass (){
		//Returns the mass of the object
		return mass;
	}
	
	public void setVelocity(double x, double y, double z){
		//Sets the velocity of the object in terms of 3 double values
		velocity.set((float)x, (float)y, (float)z);
	}
	
	public void setVelocity(Vector3f vector){
		//Sets the velocity of the object in terms of a 3D vector
		this.velocity = vector;
	}
	
	public double [] getVelocity(){
		//Gets the velocity in terms of a size 3 double array
		double [] vector = {velocity.getX(), velocity.getY(), velocity.getZ()};
		return vector;
	}
	
	public Vector3f getVelocityVector(){
		//Gets the velocity in terms of a 3D vector
		return velocity;
	}
	
	public void setPosition(double x, double y, double z){
		//Sets position in terms of 3 double values
		position.set((float)x, (float)y, (float)z);
	}
	
	public void setPosition(Point3f point){
		//Sets the position in terms of a 3D point object
		this.position = point;
	}
	
	public double[] getPosition (){
		//Returns the position of the sphere in terms of a double array
		double [] point = {position.getX(), position.getY(), position.getZ()};
		return point;
	}
	
	public Point3f getPositionPoint(){
		//Gets position in terms of a 3D point object
		return position;
	}
	
	public boolean isColliding (MovingSphere other){
		//Testing if the object is colliding with the other sphere
		Vector3f displacement = VectorMath.subtract(this.getPositionPoint(), other.getPositionPoint());
		float distance = displacement.getX()*displacement.getX() + displacement.getZ()*displacement.getZ() + displacement.getY()*displacement.getY();
		return (distance <= (this.getRadius() + other.getRadius()) * (this.getRadius() + other.getRadius()));
	}
	
	public void resolveCollision (MovingSphere other){
		//System.out.println("BAM");
		//Called when the sphere collides with another
		
		//Finds the distance between the 2 spheres
		Vector3f displacement = VectorMath.subtract(this.getPositionPoint(),other.getPositionPoint()); 
		float distance = VectorMath.getMagnitude(displacement) - this.getRadius()-other.getRadius();
		
		//Finds the velocity of the 2 spheres
		Vector3f velocity1 = this.getVelocityVector(), velocity2 = other.getVelocityVector();
		Vector3f normal = VectorMath.normalize(displacement);
		
		//Moves the sphere outside of eachother
		//The sphere with less velocity is moved
		if(this.getMass() < other.getMass())	
			position.add(VectorMath.scalarMultiply(normal, -distance));
		else
			other.setPosition(VectorMath.add(other.getPositionPoint(), VectorMath.scalarMultiply(normal, distance)));
		
		Vector3f n1 = VectorMath.scalarMultiply(normal, VectorMath.dot(velocity1, normal));
		Vector3f n2 = VectorMath.scalarMultiply(normal, VectorMath.dot(velocity2, normal));
		
		Vector3f o1 = VectorMath.subtract(velocity1, n1);
		Vector3f o2 = VectorMath.subtract(velocity2, n2);
		
		//float v1 = VectorMath.getMagnitude(n1);
		float v1 = VectorMath.dot(n1, normal);
		float m1 = this.getMass();
		
		//float v2 = VectorMath.getMagnitude(n2);
		float v2 = VectorMath.dot(n2, normal);
		float m2 = other.getMass();
		
		float newNormalVelocityS1 = (v1*(m1 - m2) + 2*m2*v2)/(m1 + m2);
		float newNormalVelocityS2 = (v2*(m2 - m1) + 2*m1*v1)/(m1 + m2);
		
		Vector3f newN1 = VectorMath.scalarMultiply(normal, newNormalVelocityS1);
		Vector3f newN2 = VectorMath.scalarMultiply(normal, newNormalVelocityS2);
		
		Vector3f newVelocity1 = VectorMath.add(o1, newN1);
		Vector3f newVelocity2 = VectorMath.add(o2, newN2);
		
		this.setVelocity(newVelocity1);
		other.setVelocity(newVelocity2);
	}
	
	public void setAcceleration (Vector3f a){
		acceleration = a;
	}
	
	public Vector3f getAcceleration (){
		return acceleration;
	}
	
	private Vector3f gravityForce (MovingSphere other){
		Vector3f displacement = VectorMath.subtract(other.getPositionPoint(), this.getPositionPoint());
		float R2 = (displacement.x)*(displacement.x) + (displacement.y)*(displacement.y) + (displacement.z)*(displacement.z);
		float m1 = this.getMass(), m2 = other.getMass();
		displacement.normalize();
		displacement.scale(G*m1*m2/R2);
		//System.out.println("Gravity Force: " + displacement);
		return displacement;
	}
	
	public void resolveGravity (MovingSphere other){
		acceleration.add(gravityForce(other));
	}
}
