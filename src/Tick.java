import java.awt.event.MouseEvent;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;


public class Tick extends Thread{

	private Vector3f position, cameraView, unit;
	private Transform3D cameraTransform;
	private TransformGroup cameraTransGroup;
	private boolean runFlag = false;
	private boolean move = false, rotate = false, rotateWithArrowKeys = false, key1Pressed, key2Pressed, key3Pressed, key4Pressed, createNewSphere = false, isSimPaused = true;
	private float scalar = 0.05f, cameraViewMagnitude, radius = 0.01f, mass = 1f;
	private BranchGroup viewBranch;
	private MouseEvent e;
	private boolean paused;
	private boolean[] arrowKeys;
	Main main;
	MoveObjects bouncingSpheres;
	private long time = 0, timeCounter = 0;
	private boolean charging = false;
	
	public Tick(){
		//Default constructor that does nothing
	}
	
	public void setMass(float f){
		//Sets the mass of the ball being fired
		mass = f;
	}
	
	public float getMass(){
		//Gets the mass of the ball being fired
		return mass;
	}
	
	public void setRadius(float r){
		//Sets the radius of the ball being fired
		radius = r;
	}
	
	public float getRadius(){
		//Gets the radius of the ball being fired
		return radius;
	}

	public void pauseSim(boolean b){
		//Pauses the simulation or starts it
		isSimPaused = b;
	}
	
	public boolean isSimPaused(){
		//Returns the status of the sim
		return isSimPaused;
	}
	
	public void setKeys(boolean a, boolean b, boolean c, boolean d){
		//Sets the keys being pressed for camera translation
		key1Pressed = a;
		key2Pressed = b;
		key3Pressed = c;
		key4Pressed = d;
		translate();
	}
	
	public void setArrowKeys(boolean[] b){
		//Sets the arrow keys being pressed for camera rotation
		arrowKeys = b;
	}
	
	public void setArrowRotate(boolean b){
		//Sets whether arrow keys are being pressed
		rotateWithArrowKeys = b;
	}
	
	public void pauseThread(){
		//Pauses the simulation completely including camera movement
		paused = true;
	}
	
	public void resumeThread(){
		//Resumes the thread completely
		paused = false;
	}

	public void setCamera(Transform3D cameraTransform, TransformGroup cameraTransGroup, BranchGroup viewBranch){
		//Sets all the values needed for camera transformation
		this.cameraTransform = cameraTransform;
		this.cameraTransGroup = cameraTransGroup;
		this.viewBranch = viewBranch;
	}

	public void setCameraView(Vector3f vector){
		//Sets the vector of the camera
		cameraView = vector;
	}
	
	public void setMouseEvent(MouseEvent e){
		//Sets the mouse event that was fired
		this.e = e;
	}

	public Transform3D getCameraTransform(){
		//Returns the transformation of camera
		return cameraTransform;
	}

	public void setRunning(boolean run){
		//Sets the run flag of the thread
		runFlag = run;
	}

	public void setMoving(boolean move){
		//Sets the movement flag
		this.move = move;
	}
	
	public void setRotating(boolean rotate){
		//Sets the rotation flag
		this.rotate = rotate;
	}

	public boolean isRunning(){
		//Returns the state of the thread
		return runFlag;
	}

	public void setValues(Vector3f position, Vector3f cameraView){
		//Sets the position and vector of the camera
		this.position = position;
		this.cameraView = cameraView;
	}

	@Override
	public void run(){
		//The loop part of the class that handles all the multi-threading
		while(isRunning()){
			
			//Sleeps for 10 milliseconds to save processing power
			try {
				Thread.sleep(10); 
			} catch (InterruptedException e) {}
			
			//If simulation is paused then program will do nothing
			if(paused){
				time = 0;
				continue;
			}

			//Translate the camera according to keys being pressed
			if(move){
				translate();
			}
			
			//Rotates the camera according to user input
			if(rotate){
				rotate();
			}
			
			//Rotates with arrow keys instead of mouse
			if(rotateWithArrowKeys){
				rotateWithArrow();
			}
			
			//Checks if only the simulation is paused and the user is still able to move
			if(isSimPaused)
				continue;
			
			//Creates a new sphere. There are 2 stages to this part of the code. 
			//Part one is when the user first presses the mouse.
			//Part one will start the charge counter to determine velocity of the user's shot
			//Part 2 is the actual creating of the sphere where the user is located.
			//Both parts are done in this if statement
			if(createNewSphere && bouncingSpheres != null){
				charging = true;
				
				//Part 2 of the creation process when the user released the mouse
				if(time != 0){
					
					//The charge meter goes from 0 to 1 then back to 0, so has cycle of 2 seconds
					float time2 = (System.currentTimeMillis() - time) % 2000;
					time2 /= 100;
					float speed;
					if (time2 < 10)
						speed = time2;
					else
						speed = (20 - time2);
					speed /= 200;
					
					//Finds the velocity and position of sphere based on camera position
					Vector3f v = VectorMath.normalize(cameraView);
					v.set(v.getX() * speed, v.getY() * speed, v.getZ() * speed);
					
					//Creates the new sphere
					bouncingSpheres.createNewSphere(v, new Point3f(position.getX() + cameraView.getX() / 2, position.getY() + cameraView.getY() / 2, position.getZ() + cameraView.getZ() / 2), radius, mass);
					createNewSphere = false;
					time = 0;
					charging = false;
					continue;
				}
				
				//Part one of the process. Initializes the time counter to the current time
				time = System.currentTimeMillis();
				createNewSphere = false;
			}
			
			//Moves all the spheres in the universe
			if(main.bouncingSpheres != null)
				main.bouncingSpheres.move();
			
			//Draws the charge meter on the canvas based on the time lapse
			if(charging){
				timeCounter = (System.currentTimeMillis() - time) % 2000 / 100;
				if(timeCounter > 10)
					timeCounter = 20 - timeCounter;
				main.canvas.startX = 0;
				main.canvas.startY = 600 - (int)timeCounter * 20;
				main.canvas.endX = 50;
				main.canvas.endY = 200;
			}
		}
		
		//Makes sure neither mass nor radius is less than 0
		if(mass < 0)
			mass = 0.005f;
		if(radius < 0)
			radius = 0.005f;
	}
	
	public void rotate(){
		
		//Rotates with mouse
		double x = (e.getX() - Main.xDimension/2), y = (e.getY() - Main.yDimension/2 + 11);
		
		if (x>400 || y > 400 || x < - 400 || y < -400)
			return;
		
		double xAngle = (x/300), yAngle = (y/300);
		
		//Breaks the rotation into 2 parts, the XZ plane amd the YZ plane
		rotXZ(cameraView, xAngle);
		rotYZ(cameraView, yAngle);
		
		//Changes the camera magnitude to 1
		cameraViewMagnitude = (float) Math.sqrt(cameraView.getX()*cameraView.getX() + cameraView.getY()*cameraView.getY() + cameraView.getZ()*cameraView.getZ());
		unit = new Vector3f (cameraView.getX()/cameraViewMagnitude, cameraView.getY()/cameraViewMagnitude, cameraView.getZ()/cameraViewMagnitude);
		
		setRotating(false);
		transformCamera();
	}
	
	public void rotateWithArrow(){
		
		//Same thing as method above, just with arrow keys
		double xAngle = 0, yAngle = 0;
		if(arrowKeys[0])
			xAngle -= 0.01;
		if(arrowKeys[1])
			yAngle -= 0.01;
		if(arrowKeys[2])
			xAngle += 0.01;
		if(arrowKeys[3])
			yAngle += 0.01;
		
		
		rotXZ(cameraView, xAngle);
		rotYZ(cameraView, yAngle);
		cameraViewMagnitude = (float) Math.sqrt(cameraView.getX()*cameraView.getX() + cameraView.getY()*cameraView.getY() + cameraView.getZ()*cameraView.getZ());
		unit = new Vector3f (cameraView.getX()/cameraViewMagnitude, cameraView.getY()/cameraViewMagnitude, cameraView.getZ()/cameraViewMagnitude);
		
		setRotating(false);
		transformCamera();
	}
	
	public void rotXZ(Vector3f old, double xAngle){
		
		//Rotates camera along the XZ plane using trigonometry
		float x = old.getX(), y = old.getY(), z = old.getZ();
		Vector3f temp = new Vector3f((float)(x * Math.cos(xAngle) - z * Math.sin(xAngle)), y, (float)(x * Math.sin(xAngle) + z * Math.cos(xAngle)));
		cameraView = temp;
		
		//System.out.println("("+ cameraView.getX() + "," + cameraView.getY() + "," + cameraView.getZ() + ")");
	}
	
	public void rotYZ(Vector3f old, double yAngle){
		
		//Rotates the camera along the YZ plane using a really bad method because we encountered some unknown and unsolvable errors
		//which we spent a month trying to fix
		float x = old.getX(), y = old.getY(), z = old.getZ();
		if(Math.abs(y) > 1.5 && Math.abs(y) < 3)
			yAngle *= 2;
		else if(Math.abs(y) > 3 && Math.abs(y) < 4.5)
			yAngle *= 4;
		else if(Math.abs(y) > 4.5 && Math.abs(y) < 6)
			yAngle *= 8;
		else if(Math.abs(y) > 6 && Math.abs(y) < 7.5)
			yAngle *= 16;
		else if(Math.abs(y) > 7.5 && Math.abs(y) < 15)
			yAngle *= 32;
		else if(Math.abs(y) > 15 && Math.abs(y) < 25)
			yAngle *= 100;
		
		if(Math.abs(y - yAngle) >= 15)
			return;
		
		Vector3f temp = new Vector3f((float) x, (float) (y - yAngle), (float)(z));
		cameraView = temp;
	}
	
	public void translate(){
		
		//Translates the camera with use of WASD keys
		cameraViewMagnitude = (float) Math.sqrt(cameraView.getX()*cameraView.getX() + cameraView.getY()*cameraView.getY() + cameraView.getZ()*cameraView.getZ());
		unit = new Vector3f (cameraView.getX()/cameraViewMagnitude, cameraView.getY()/cameraViewMagnitude, cameraView.getZ()/cameraViewMagnitude);
		Vector3f cameraViewShadow = new Vector3f(cameraView.getX(), 0, cameraView.getZ());
		float shadowMagnitude =  (float) Math.sqrt(cameraViewShadow.getX()*cameraViewShadow.getX() + cameraViewShadow.getY()*cameraViewShadow.getY() + cameraViewShadow.getZ()*cameraViewShadow.getZ());
		Vector3f shadowUnit = new Vector3f(cameraViewShadow.getX()/shadowMagnitude, cameraViewShadow.getY()/shadowMagnitude, cameraViewShadow.getZ()/shadowMagnitude);
		Vector3f perpendicularUnit = new Vector3f(shadowUnit.getZ(), 0, (-1)*shadowUnit.getX());

		//These are if statements and not else if statements to allow multiple keys being pressed at the same time
		if(key1Pressed){
			Vector3f temp = new Vector3f(unit.getX()*scalar, unit.getY()*scalar, unit.getZ()*scalar);
			position = VectorMath.add(position,temp);
			transformCamera();
		}
		
		if(key3Pressed){
			Vector3f temp = new Vector3f(unit.getX()*scalar, unit.getY()*scalar, unit.getZ()*scalar);
			position = VectorMath.subtract(position,temp);
			transformCamera();
		}
		
		if(key2Pressed){
			Vector3f temp = new Vector3f(perpendicularUnit.getX()*scalar, perpendicularUnit.getY()*scalar, perpendicularUnit.getZ()*scalar);
			position = VectorMath.add(position, temp);
			transformCamera();
		}
		
		if(key4Pressed){
			Vector3f temp = new Vector3f(perpendicularUnit.getX()*scalar, perpendicularUnit.getY()*scalar, perpendicularUnit.getZ()*scalar);
			position = VectorMath.subtract(position, temp);
			transformCamera();
		}
	}
	
	public void transformCamera(){
		
		//Sets the new camera values to the view branch group
		cameraTransform.set(position);
		cameraTransform.lookAt(new Point3d(position.getX(), position.getY(), position.getZ()), new Point3d(unit.getX()+position.getX(), unit.getY()+position.getY(), unit.getZ()+position.getZ()), new Vector3d(0, 1, 0));
		cameraTransform.invert();
		
		//There are cases where x, y, or z are equal to 0 and the invert method fails. This is handled with a try catch
		try{
			cameraTransGroup.setTransform(cameraTransform);
		}catch(Exception e){}
	}

	public BranchGroup getViewBranch() {
		
		//Returns the branch group containing the camera
		return viewBranch;
	}

	public void setCreateNewSphere(boolean b) {
		//creates new sphere
		createNewSphere = b;
	}

}
