import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingLeaf;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import com.sun.j3d.utils.universe.SimpleUniverse;


@SuppressWarnings("serial")
public class Main extends JFrame implements KeyListener, MouseMotionListener, MouseListener{
	
	private BranchGroup lightBranch, viewBranch;
	private VirtualUniverse universe;
	private BoundingSphere bounds;
	private Transform3D cameraTransform;
	private Vector3f position = new Vector3f(0f, 45f, 0f), cameraView = new Vector3f(1f, -5f, 1f);
	private TransformGroup cameraTransGroup;
	private boolean key1Pressed = false, key2Pressed = false, key3Pressed = false, key4Pressed = false, centreMouse = false;
	private boolean[] arrowKeys = new boolean[4];
	private Tick tick = new Tick();
	private Locale locale;
	private Point3d viewPoint = new Point3d(position.getX(), position.getY(), position.getZ() - 0.1);
	static final int xDimension = 1000, yDimension = 600;
	MoveObjects bouncingSpheres;
	Canvas canvas;
	Graphics g;
	
	public Main(){
		tick.main = this;
		
		//Initializes the frame and the 3D canvas
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas(config);
		this.add(canvas);
		this.setSize(xDimension + 6, yDimension + 28);
		this.setVisible(true);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.setResizable(false);
		
		//Creates the lighting of the universe
		//There are 2 point light sources in this universe
		//There's also ambient lighting in this universe - the defracted light
		bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0f);
		Color3f color = new Color3f(0.8f, 0.8f, 0.8f);
		PointLight light = new PointLight();
		light.setPosition(new Point3f(5f, 5f, 0f));
		light.setInfluencingBounds(bounds);
		light.setColor(color);
		PointLight light2 = new PointLight();
		light2.setPosition(new Point3f(-5f, 5f, 0f));
		light2.setInfluencingBounds(bounds);
		light2.setColor(color);
		AmbientLight ambientLight = new AmbientLight();
		ambientLight.setInfluencingBounds(bounds);
		ambientLight.setColor(new Color3f(2.55f, 2.55f, 2.55f));
		
		//Initializes the lighting branch group
		lightBranch = new BranchGroup();
		lightBranch.addChild(light);
		lightBranch.addChild(light2);
		lightBranch.addChild(ambientLight);
		
		//Initializes the virtual universe and adds everything to it
		universe = new VirtualUniverse();
		locale = new Locale(universe);
		locale.addBranchGraph(lightBranch);
		viewBranch = buildViewBranch(canvas);
		locale.addBranchGraph(viewBranch);
		
		//Sets user input tool to canvas
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		
		//Creates the bouncing sphere object which handles the collisions
		bouncingSpheres = new MoveObjects(locale, 0);
		tick.bouncingSpheres = bouncingSpheres;
		canvas.setMenuOn(true);
	}
	
	public BranchGroup buildViewBranch(Canvas3D c) {
		//Initializes the view branch group and the transform object for the camera
		viewBranch = new BranchGroup();
		cameraTransform = new Transform3D();
		
		//Sets the camera to be 2 along the z axis away from the origin and looks at the origin
		cameraTransform.set(new Vector3f(0.0f, 15.0f, 0.0f));
		cameraTransform.lookAt(new Point3d(position.getX(), position.getY(), position.getZ()), viewPoint, new Vector3d(0, 1, 0));
		cameraTransform.invert();
		
		//Initializes the camera's transform group
		cameraTransGroup = new TransformGroup(cameraTransform);
		cameraTransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		cameraTransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		//Adds the camera's transform group to the universe through viewing platform
		BoundingSphere movingBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		BoundingLeaf boundLeaf = new BoundingLeaf(movingBounds);
		ViewPlatform viewPlatform = new ViewPlatform();
		cameraTransGroup.addChild(boundLeaf);
		PhysicalBody myBody = new PhysicalBody();
		PhysicalEnvironment myEnvironment = new PhysicalEnvironment();
		cameraTransGroup.addChild(viewPlatform);
		viewBranch.addChild(cameraTransGroup);
		viewBranch.setCapability(BranchGroup.ALLOW_DETACH);
		
		//Attaches the universe to the view object
		View myView = new View();
		myView.addCanvas3D(c);
		myView.attachViewPlatform(viewPlatform);
		myView.setPhysicalBody(myBody);
		myView.setPhysicalEnvironment(myEnvironment);
		myView.setBackClipDistance(100);
		return viewBranch;
	}
	
	public void translate(){
		
		//Transforms the camera in the direction of the keys being pressed
		if(!tick.isRunning()){
			//In case where the tick thread is not running
			tick.setRunning(true);
			tick.setValues(position, cameraView);
			tick.setCamera(cameraTransform, cameraTransGroup, viewBranch);
			tick.start();
		}
		
		//Sets the keys being pressed currently. Supports multiple keys being pressed at the same time
		tick.setKeys(key1Pressed, key2Pressed, key3Pressed, key4Pressed);
	}
	
	public void turn(MouseEvent e){
		
		//Turning camera in case of mouse input
		if(!tick.isRunning()){
			tick.setRunning(true);
			tick.setValues(position, cameraView);
			tick.setCamera(cameraTransform, cameraTransGroup, viewBranch);
			tick.start();
		}
		tick.setRotating(true);
		tick.setMouseEvent(e);
	}
	
	public void turn(){
		
		//Turn method for arrow keys
		if(!tick.isRunning()){
			tick.setRunning(true);
			tick.setValues(position, cameraView);
			tick.setCamera(cameraTransform, cameraTransGroup, viewBranch);
			tick.start();
		}
		tick.setArrowKeys(arrowKeys);
		tick.setArrowRotate(true);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {

		//Method that handles all key press events
		/*boolean exitMenu = false;
		if(canvas.isMenuOn()){
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				canvas.setMenuOn(false);
				canvas.getGraphics2D().flush(false);
				exitMenu = true;
				translate();
			}
			else
				return;
		}*/
		
		if(e.getKeyChar() == ' '){
			
			//If space is pressed the program will pause and release mouse or unpause and capture the mouse
			if(centreMouse){
				
				//Sets the cursor to default and unlocks the cursor
				centreMouse = false;
				this.setCursor(Cursor.getPredefinedCursor(0));
				tick.pauseThread();
			}
			else{
				
				//Locks cursor and resumes the thread
				centreMouse = true;
				tick.pauseSim(false);
				tick.resumeThread();
				
				//Sets the cursor to invisible and locks cursor to centre of screen
				Point locOnScreen = this.getLocationOnScreen();
				BufferedImage image = getGraphicsConfiguration().createCompatibleImage(1, 1, Transparency.BITMASK);  
				Graphics2D g = image.createGraphics();  
				g.setBackground(new Color(0,0,0,0));  
				g.clearRect(0,0,1,1);
				
				Cursor invisibleCursor = getToolkit().createCustomCursor(image, new Point(0,0), "Invisible");  
				this.setCursor(invisibleCursor);
				
				//Moves cursor to centre of screen
				int middleX = locOnScreen.x + (this.getWidth() / 2);
				int middleY = locOnScreen.y + (this.getHeight() / 2);
				try{
					Robot rob = new Robot();
					rob.mouseMove(middleX, middleY);
				}catch(Exception ex){System.out.println(ex);}
				translate();
			}
			return;
		}
		
		//If simulation is paused then the rest of the method will not be executed
		if(!centreMouse){
			return;
		}

		//Handles the keys where they are not case sensitive.
		//The keys being monitored are the arrow keys, WASD keys, the p key, and enter key
		switch(e.getKeyCode()){
			case 37: arrowKeys[0] = true; turn(); break;
			case 38: arrowKeys[1] = true; turn(); break;
			case 39: arrowKeys[2] = true; turn(); break;
			case 40: arrowKeys[3] = true; turn(); break;
			case 87: key1Pressed = true; tick.setMoving(true); break;
			case 65: key2Pressed = true; tick.setMoving(true); break;
			case 83: key3Pressed = true; tick.setMoving(true); break;
			case 68: key4Pressed = true; tick.setMoving(true); break;
			case 80: tick.pauseSim(!tick.isSimPaused()); break;
			case KeyEvent.VK_ENTER: bouncingSpheres.setGravity(!bouncingSpheres.isGravityOn()); break;
		}
		
		//Handles the situations where the keys are case sensitive. 
		//Keys being monitored are upper and lower case r and m, which controls the mass and radius of the ball being fired
		switch(e.getKeyChar()){
			case 'r': tick.setRadius(tick.getRadius() - 0.005f); canvas.radius = tick.getRadius(); break;
			case 'm': tick.setMass(tick.getMass() / 2); canvas.mass = tick.getMass(); break;
			case 'R': tick.setRadius(tick.getRadius() + 0.005f); canvas.radius = tick.getRadius(); break;
			case 'M': tick.setMass(tick.getMass() * 2); canvas.mass = tick.getMass(); break;
		}
		
		translate();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		//If simulation is paused then method will not be executed
		if(!centreMouse)
			return;
		
		//Monitors the releasing of keys
		//If a key is released it will stop the action the key is performing
		switch(e.getKeyCode()){
			case 37: arrowKeys[0] = false; break;
			case 38: arrowKeys[1] = false; break;
			case 39: arrowKeys[2] = false; break;
			case 40: arrowKeys[3] = false; break;
			case 87: key1Pressed = false; break;
			case 65: key2Pressed = false; break;
			case 83: key3Pressed = false; break;
			case 68: key4Pressed = false; break;
		}
		
		//If none of the WASD keys are being pressed then it will set move to false to not do excessive calculations
		if(!key1Pressed && !key2Pressed && !key3Pressed && !key4Pressed)
			tick.setMoving(false);
		if(!arrowKeys[0] && !arrowKeys[1] && !arrowKeys[2] && !arrowKeys[3])
			tick.setArrowRotate(false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
		//Moves the screen and centres the mouse
		Point locOnScreen = this.getLocationOnScreen();
		int middleX = locOnScreen.x + (this.getWidth() / 2);
		int middleY = locOnScreen.y + (this.getHeight() / 2);
		if(e.getX() == 500 && e.getY() == 289){
			return;
		}
		
		//Executed when the program is not paused
		if(centreMouse){
			turn(e);
			try{
				Robot rob = new Robot();
				rob.mouseMove(middleX, middleY);
			}catch(AWTException ex){System.out.println(ex);}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		//Does the same thing as mouse dragged
		Point locOnScreen = this.getLocationOnScreen();
		int middleX = locOnScreen.x + (this.getWidth() / 2);
		int middleY = locOnScreen.y + (this.getHeight() / 2);
		if(e.getX() == 500 && e.getY() == 289){
			return;
		}
		if(centreMouse){
			turn(e);
			try{
				Robot rob = new Robot();
				rob.mouseMove(middleX, middleY);
			}catch(Exception ex){System.out.println(ex);}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		//Checks if mouse one was clicked
		if(e.getButton() == MouseEvent.BUTTON1){
			
			//Program will return if simulation is paused
			if(!centreMouse || tick.isSimPaused())
				return;
			
			//Sets creating new sphere to true and start counter in tick
			tick.setCreateNewSphere(true);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		//If button one is released then will execute
		if(e.getButton() == MouseEvent.BUTTON1){
			
			//Returns if simulation is paused
			if(!centreMouse || tick.isSimPaused())
				return;
			
			//stops counter in tick and creates a new sphere
			tick.setCreateNewSphere(true);
			
			//Sets the charge meter rectangle to off screen
			canvas.startX = 0;
			canvas.startY = 0;
			canvas.endX = 0;
			canvas.endY = 0;
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
