import java.awt.Color;
import java.util.Random;

import javax.media.j3d.Appearance;
import javax.media.j3d.BadTransformException;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Locale;
import javax.media.j3d.Material;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class MoveObjects {
	MovingSphere[] sphereArray;
	BranchGroup spheres;
	Locale locale;

	private boolean isGravityOn = true;
	/*public MoveObjects(Locale l){
		locale = l;
		spheres = new BranchGroup();
		spheres.setCapability(BranchGroup.ALLOW_DETACH);
		for(int i = 0; i < sphereArray.length; i++){

			sphereArray[i] = new MovingSphere(new Vector3f(0.005f * ((float)Math.random() - 0.5f), 0.005f * ((float)Math.random() - 0.5f), 0.005f * ((float)Math.random() - 0.5f)), new Point3f((float)Math.random() * 2, (float)Math.random() * 2, (float)Math.random() * 2), 0.02f);
			spheres.addChild(sphereArray[i].transGroup);
		}
		//sphereArray[sphereArray.length - 1] = new MovingSphere(new Vector3f(0f, 0f, 0f), new Point3f(0f, 0f, 0f), 0.5f);
		//sphereArray[sphereArray.length - 1].setMass(10);
		//spheres.addChild(sphereArray[sphereArray.length - 1].transGroup);

		locale.addBranchGraph(spheres);
	}*/
	
	public MoveObjects(Locale l, int x){
		locale = l;
		spheres = new BranchGroup();
		spheres.setCapability(BranchGroup.ALLOW_DETACH);
		x=2;
		if(x == 0){
			sphereArray = new MovingSphere[1000];

			sphereArray[0] = new MovingSphere(new Vector3f(0,0,0), new Point3f(0,0,0), 0.5f);
			sphereArray[0].setMass(100000);
			Appearance a = sphereArray[0].getAppearance();
			Material m = a.getMaterial();
			m.setDiffuseColor(new Color3f(0.1f, 0.5f, 01.8f));
			ColoringAttributes col = new ColoringAttributes();
			col.setColor(new Color3f(2.55f, 0, 0));
			a.setColoringAttributes(col);
			sphereArray[0].setAppearance(a);

			/*sphereArray[1] = new MovingSphere(new Vector3f(0f,0,-0.015f), new Point3f(6f,0,0f), 0.1f);
			sphereArray[1].setMass(1000);
			sphereArray[2] = new MovingSphere(new Vector3f(0f,0,-0.025f), new Point3f(6.15f,0,0), 0.04f);
			sphereArray[2].setMass(0.100f);
			
			sphereArray[3] = new MovingSphere(new Vector3f(0,0f,0.020f), new Point3f(2f,0,0f), 0.1f);
			sphereArray[3].setMass(1000);
			sphereArray[4] = new MovingSphere(new Vector3f(0f,0,0.030f), new Point3f(2.15f,0,0f), 0.04f);
			sphereArray[4].setMass(0.1f);
			
			sphereArray[5] = new MovingSphere(new Vector3f(0,0f,0.010f), new Point3f(8f,0,0f), 0.1f);
			sphereArray[5].setMass(1000);
			sphereArray[6] = new MovingSphere(new Vector3f(0f,0,0.020f), new Point3f(8.15f,0,0f), 0.04f);
			sphereArray[6].setMass(0.1f);
			sphereArray[7] = new MovingSphere(new Vector3f(0,0.0f,0.0f), new Point3f(7.85f,0,0f), 0.04f);
			sphereArray[7].setMass(0.1f);
			sphereArray[7].setAppearance(a);*/
			
			Random rand = new Random();
			float radii;
			
			for (int i = 1; i < 1000; i ++)
			{
				radii = rand.nextFloat()*3f + 12f;
				float x1 = rand.nextFloat() * radii * ((rand.nextInt(2)*2)-1);;
				float z1 = (float) Math.sqrt(radii*radii - x1*x1) * ((rand.nextInt(2)*2)-1);
				float multiplier = (float) (1f/(radii*31.6227f*Math.sqrt(radii))); //rand.nextFloat()/500;
				sphereArray[i] = new MovingSphere(new Vector3f(z1*multiplier,0f,-x1*multiplier), new Point3f(x1,0,z1), rand.nextFloat()/10);
				sphereArray[i].setMass(0.00001f /*rand.nextFloat()*1*/);
			}
			
			for (int i = 0; i < sphereArray.length; i++)
			{
				spheres.addChild(sphereArray[i].transGroup);
			}
		}
		
		else if(x == 1){
			sphereArray = new MovingSphere[1000];
			sphereArray[0] = new MovingSphere(new Vector3f(0f,0,0), new Point3f(-30f,3f,0), 1f);
			sphereArray[0].setMass(1000);
			sphereArray[0].setAcceleration(new Vector3f(0.05f*1000f,0,0));
			spheres.addChild(sphereArray[0].transGroup);
			
			sphereArray[1] = new MovingSphere(new Vector3f(0f,0,0), new Point3f(-40f,0,5f), 2f);
			sphereArray[1].setMass(1000);
			sphereArray[1].setAcceleration(new Vector3f(0.05f*1000f,0,0));
			spheres.addChild(sphereArray[1].transGroup);
			
			sphereArray[2] = new MovingSphere(new Vector3f(0f,0,0), new Point3f(-60f,5f,2f), 3f);
			sphereArray[2].setMass(1000);
			sphereArray[2].setAcceleration(new Vector3f(0.05f*1000f,0,0));
			spheres.addChild(sphereArray[2].transGroup);
			
			sphereArray[3] = new MovingSphere(new Vector3f(0f,0,0), new Point3f(-80f,-5f,-3f), 4f);
			sphereArray[3].setMass(1000);
			sphereArray[3].setAcceleration(new Vector3f(0.05f*1000f,0,0));
			spheres.addChild(sphereArray[3].transGroup);
			
			sphereArray[4] = new MovingSphere(new Vector3f(0f,0,0), new Point3f(-100f,0,0), 5f);
			sphereArray[4].setMass(1000);
			sphereArray[4].setAcceleration(new Vector3f(0.05f*1000f,0,0));
			spheres.addChild(sphereArray[4].transGroup);
			isGravityOn = false;
			
			Random rand = new Random();
			
			for (int i = 5; i < sphereArray.length; i ++)
			{
				sphereArray[i] = new MovingSphere(new Vector3f(0,0,0), new Point3f(rand.nextFloat()*10f - 5f, rand.nextFloat()*10f - 5f, rand.nextFloat()*10f - 5f), 0.05f);
				sphereArray[i].setMass(0.000001f);
				spheres.addChild(sphereArray[i].transGroup);
			}
		}
		
		else if(x == 3){
			sphereArray = new MovingSphere[100];
			sphereArray[0] = new MovingSphere(new Vector3f(0,0,0), new Point3f(0,0,0), 1.5f);
			sphereArray[0].setMass(10000000);
			spheres.addChild(sphereArray[0].transGroup);

			float radii;
			Vector3f velocity, random;
			Point3f position;

			for (int i = 1; i < sphereArray.length; i ++)
			{
				radii = i*0.6f + 1.7f;
				
				random = VectorMath.random();
				
				position = new Point3f(random.x*radii, random.y*radii, random.z*radii);
				velocity = VectorMath.scalarMultiply(VectorMath.normalize(new Vector3f(random.y + random.z, -random.x, -random.x)), (float)(0.31622776601683793319988935444327/Math.sqrt(radii)));
				
				//sphereArray[i] = new MovingSphere(new Vector3f(0,0,(float)(0.31622776601683793319988935444327/Math.sqrt(radii))), new Point3f(radii,0,0), 0.1f);
				sphereArray[i] = new MovingSphere(velocity, position, 0.1f);
				sphereArray[i].setMass(0.000001f);
				spheres.addChild(sphereArray[i].transGroup);
			}
		}
			
		else if(x == 2){
			sphereArray = new MovingSphere[100];
			
			Random rand = new Random();
			
			for (int i = 0; i < sphereArray.length; i ++)
			{
				sphereArray[i] = new MovingSphere(new Vector3f(0,0,0), new Point3f(rand.nextFloat()*1f - 0.5f, rand.nextFloat()*1f - 0.5f, rand.nextFloat()*1f - 0.5f), 0.5f);
				sphereArray[i].setMass(1000f);
				spheres.addChild(sphereArray[i].transGroup);
			}
		}
	}
 
	
	/*public MoveObjects(Locale l){
		locale = l;
		spheres = new BranchGroup();
		spheres.setCapability(BranchGroup.ALLOW_DETACH);
		for(int i = 0; i < sphereArray.length; i++){

			//sphereArray[i] = new MovingSphere(new Vector3f(0.005f * ((float)Math.random() - 0.5f), 0.005f * ((float)Math.random() - 0.5f), 0.005f * ((float)Math.random() - 0.5f)), new Point3f((float)Math.random() * 2, (float)Math.random() * 2, (float)Math.random() * 2), 0.02f);
			sphereArray[i] = new MovingSphere(new Vector3f(-0.0005f, 0.0f, 0f), new Point3f(0f + i * 0.1f,0f,0f), 0.02f);
			//sphereArray[i].setAcceleration(new Vector3f(0.0001f, 0, 0));
			spheres.addChild(sphereArray[i].transGroup);
		}
		sphereArray[sphereArray.length - 1] = new MovingSphere(new Vector3f(0f, 0f, 0f), new Point3f(0f, 0f, 0f), 0.5f);
		sphereArray[sphereArray.length - 1].setMass(1000000000);
		spheres.addChild(sphereArray[sphereArray.length - 1].transGroup);

		sphereArray[0] = new MovingSphere(new Vector3f(0,0,0), new Point3f(0,0,0), 0.5f);
		sphereArray[0].setMass(100000);

		sphereArray[1] = new MovingSphere(new Vector3f(0f,0,-0.015f), new Point3f(3f,0,0f), 0.1f);
		sphereArray[1].setMass(100);
		
		sphereArray[2] = new MovingSphere(new Vector3f(0,0,0.02f), new Point3f(4f,0,0), 0.1f);
		sphereArray[2].setMass(100);
		
		sphereArray[3] = new MovingSphere(new Vector3f(0,0.01f,0.03f), new Point3f(5f,0,3f), 0.1f);
		sphereArray[3].setMass(100);
		
		
		sphereArray[4] = new MovingSphere(new Vector3f(0.03f,0,0), new Point3f(0,0,4f), 0.1f);
		sphereArray[4].setMass(1);
		
		spheres.addChild(sphereArray[0].transGroup);
		spheres.addChild(sphereArray[1].transGroup);
		spheres.addChild(sphereArray[2].transGroup);
		spheres.addChild(sphereArray[3].transGroup);
		spheres.addChild(sphereArray[4].transGroup);
		
		locale.addBranchGraph(spheres);
	}*/

	public void createNewSphere(Vector3f velocity, Point3f position, float radius, float mass){
		addToArray();
		if(mass <= 0){
			mass = 0.0001f;
		}
		if(radius <= 0){
			radius = 0.001f;
		}
		spheres.detach();
		sphereArray[sphereArray.length - 1] = new MovingSphere(velocity, position, radius);
		sphereArray[sphereArray.length - 1].setMass(mass);
		spheres.addChild(sphereArray[sphereArray.length - 1].transGroup);
		locale.addBranchGraph(spheres);
	}
	
	public void setGravity(boolean b){
		isGravityOn = b;
	}
	
	public boolean isGravityOn(){
		return isGravityOn;
	}

	public void addToArray(){
		MovingSphere[] temp = new MovingSphere[sphereArray.length + 1];
		for(int i = 0; i < sphereArray.length; i++){
			temp[i] = sphereArray[i];
		}
		sphereArray = temp;
	}

	public void move(){
		
		for(int i = 0; i < sphereArray.length && isGravityOn(); i++)
		{
			sphereArray[i].setAcceleration(new Vector3f(0,0,0));
			for (int j = 0; j < sphereArray.length; j++)
			{
				if (i!=j)
				{
					sphereArray[i].resolveGravity(sphereArray[j]);
				}
			}
		}
		for (int i = 0; i < sphereArray.length; i ++)
		{
			try{
				sphereArray[i].move();
			}catch(BadTransformException e){}
			for(int j = i + 1; j < sphereArray.length; j++)
			{
				if(j != i && sphereArray[i].isColliding(sphereArray[j]))
					sphereArray[i].resolveCollision(sphereArray[j]);
			}
		}
		
		//System.out.println("Velocity of Centre of Mass: " + centreofmassV);
	}
}
