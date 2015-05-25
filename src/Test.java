import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.keyboard.*;

import java.awt.event.*;
import java.util.Enumeration;

//   SimplebehaviorApp renders a single ColorCube
//   that rotates when any key is pressed.

public class Test extends Applet {

    private SimpleUniverse u;
    private BoundingSphere bounds;
    private ViewingPlatform ourView;

    

    public class SimpleViewbehavior extends Behavior{

        private TransformGroup targetTG;
        private ViewingPlatform targetVP;
        private Transform3D rotation = new Transform3D();
        private double angle = 0.0;
        private TransformGroup chaseTG;
        private Vector3d camVec;

        // create Simplebehavior
        SimpleViewbehavior(ViewingPlatform targetViewP, TransformGroup chasedTG){
            this.targetVP = targetViewP;
            this.chaseTG = chasedTG;
            this.targetTG = targetViewP.getViewPlatformTransform();
            camVec = new Vector3d();
        }

        // initialize the behavior
        //     set initial wakeup condition
        //     called when behavior beacomes live
        public void initialize(){
            // set initial wakeup condition
            this.wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
        }

        // behave
        // called by Java 3D when appropriate stimulus occures
        public void processStimulus(Enumeration criteria){
            // decode event

            // do what is necessary
            angle += 0.1;
            Transform3D newrot = new Transform3D();
            chaseTG.getTransform(newrot);
           // System.out.println(newrot.toString());
            Vector3d translate = new Vector3d();
            Vector3d up = new Vector3d(0, 1, 0);
            Vector3d camV = new Vector3d();
            newrot.get(translate);
            newrot.get(camV);
            camV.y = camV.y + 3d;
            camV.z = camV.z - 3d ;
 
             System.out.println(translate.toString());
             //System.out.println(camVec.toString());

              //Commented out attempts at making the camera chase the target
              // -------------------------
              // rotation.lookAt(camV, translate, up);
             //rotation.set(camVec);
             //rotation.invert();
            

            /*
             Vector3d ourVec = new Vector3d();
            Vector3d up = new Vector3d(0, 1, 0);
            newrot.get(ourVec);
            ourVec.add(camVec);

            Point3d cam = new Point3d();
            Point3d man = new Point3d();

            rotation.setTranslation(ourVec);
            newrot.transform(man);
            rotation.transform(cam);
            rotation.lookAt(cam, man, up);
            //rotation.invert();
              */
           //rotation.lookAt(Point3d eye, Point3d center, Vector3d up);

            //enable this when chase cam actually transforms correctly
            // targetTG.setTransform(rotation);
            this.wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
        }

    } // end of class SimpleViewbehavior

    //create 3d land to travel over
    Shape3D createLand(){
        LineArray landGeom = new LineArray(44, GeometryArray.COORDINATES
                                            | GeometryArray.COLOR_3);
        float l = -50.0f;
        for(int c = 0; c < 44; c+=4){

            landGeom.setCoordinate( c+0, new Point3f( -50.0f, 0.0f,  l ));
            landGeom.setCoordinate( c+1, new Point3f(  50.0f, 0.0f,  l ));
            landGeom.setCoordinate( c+2, new Point3f(   l   , 0.0f, -50.0f ));
            landGeom.setCoordinate( c+3, new Point3f(   l   , 0.0f,  50.0f ));
            l += 10.0f;
        }

        Color3f c = new Color3f(0.1f, 0.8f, 0.1f);
        for(int i = 0; i < 44; i++) landGeom.setColor( i, c);

        return new Shape3D(landGeom);
    }

    public BranchGroup createSceneGraph(SimpleUniverse su) {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

        objRoot.addChild(createLand());

        TransformGroup objRotate = new TransformGroup();
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

	objRoot.addChild(objRotate);
	objRotate.addChild(new ColorCube(0.4));

        //Implement behavior listeners for Cube and Camera
        KeyNavigatorBehavior myRotationbehavior = new KeyNavigatorBehavior(objRotate);
        SimpleViewbehavior myViewRotationbehavior = new SimpleViewbehavior(this.ourView,objRotate);
        myRotationbehavior.setSchedulingBounds(new BoundingSphere());
        objRoot.addChild(myRotationbehavior);
        myViewRotationbehavior.setSchedulingBounds(new BoundingSphere());
        objRoot.addChild(myViewRotationbehavior);

	// Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

	return objRoot;
    } // end of CreateSceneGraph method of SimplebehaviorApp

    // Create a simple scene and attach it to the virtual universe

    public Test() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);

        

        // SimpleUniverse is a Convenience Utility class
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
        this.u = simpleU;
        ourView = u.getViewingPlatform();

	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
        Transform3D locator = new Transform3D();
         locator.setTranslation(new Vector3f(0, 3f, -3f));
         locator.lookAt(new Point3d(0d, 3d, -6d), new Point3d(0d, 0d, 5d), new Vector3d(0d, 1d, 0d));
         locator.invert();
         this.ourView.getViewPlatformTransform().setTransform(locator);
         BranchGroup scene = createSceneGraph(this.u);

        simpleU.addBranchGraph(scene);
    } // end of SimplebehaviorApp (constructor)
    //  The following allows this to be run as an application
    //  as well as an applet

    public static void main(String[] args) {
        System.out.print("SimplebehaviorApp.java \n- a demonstration of creating a simple");
        System.out.println("moveable cube on top of a map.");
        System.out.println("Use the arrow keys to rotate and move the orb. The green face is the front\n.");
        System.out.println("This is modified from the tutorials at The Java 3D API Tutorial at ");
        System.out.println("http://java.sun.com/products/java-media/3D/collateral");
        Frame frame = new MainFrame(new Test(), 256, 256);
    } // end of main (method of SimplebehaviorApp)

} // end of class SimplebehaviorApp

