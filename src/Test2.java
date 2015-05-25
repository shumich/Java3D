import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Locale;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.media.j3d.WakeupOnCollisionMovement;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.picking.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * This class demonstrates the use of the CollisionDetector class to perform
 * processing when objects collide. When this program is run the white cube can
 * be selected and moved by dragging on it with the right mouse button. You
 * should notice that there is a problem if the movable cube comes into contact
 * with both of the static cubes at one time. A way round this is given in the
 * SimpleCollision2 application.
 * 
 * @see CollisionDetector
 * @see SimpleCollision2
 * @author I.J.Palmer
 * @version 1.0
 */
public class Test2 extends Frame implements ActionListener {
  protected Canvas3D myCanvas3D;

  protected Button exitButton = new Button("Exit");

  protected BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0,
      0.0), 100.0);

  /** Transform for the left cube. */
  protected TransformGroup leftGroup;

  /** Transform for the right cube */
  protected TransformGroup rightGroup;

  /**
   * Transform for the movable cube. This has read, write and pick reporting
   * capabilities enabled.
   */
  protected TransformGroup moveGroup;

  /** A transform to change the size of the movable cube. */
  protected TransformGroup scaleGroup;

  /** The left static cube. */
  protected Shape3D leftCube;

  /** The right static cube. */
  protected Shape3D rightCube;

  /** The movable cube that will collide with the other two cubes */
  protected Shape3D moveCube;

  /**
   * This builds the view branch of the scene graph.
   */
  protected BranchGroup buildViewBranch(Canvas3D c) {
    BranchGroup viewBranch = new BranchGroup();
    Transform3D viewXfm = new Transform3D();
    viewXfm.set(new Vector3f(0.0f, 0.0f, 10.0f));
    TransformGroup viewXfmGroup = new TransformGroup(viewXfm);
    ViewPlatform myViewPlatform = new ViewPlatform();
    PhysicalBody myBody = new PhysicalBody();
    PhysicalEnvironment myEnvironment = new PhysicalEnvironment();
    viewXfmGroup.addChild(myViewPlatform);
    viewBranch.addChild(viewXfmGroup);
    View myView = new View();
    myView.addCanvas3D(c);
    myView.attachViewPlatform(myViewPlatform);
    myView.setPhysicalBody(myBody);
    myView.setPhysicalEnvironment(myEnvironment);
    return viewBranch;
  }

  /**
   * This adds some lights to the content branch of the scene graph.
   * 
   * @param b
   *            The BranchGroup to add the lights to.
   */
  protected void addLights(BranchGroup b) {
    Color3f ambLightColour = new Color3f(0.5f, 0.5f, 0.5f);
    AmbientLight ambLight = new AmbientLight(ambLightColour);
    ambLight.setInfluencingBounds(bounds);
    Color3f dirLightColour = new Color3f(1.0f, 1.0f, 1.0f);
    Vector3f dirLightDir = new Vector3f(-1.0f, -1.0f, -1.0f);
    DirectionalLight dirLight = new DirectionalLight(dirLightColour,
        dirLightDir);
    dirLight.setInfluencingBounds(bounds);
    b.addChild(ambLight);
    b.addChild(dirLight);
  }

  /**
   * Creates the content branch of the scene graph.
   * 
   * @return BranchGroup with content attached.
   */
  protected BranchGroup buildContentBranch() {
    //First create a different appearance for each cube
    Appearance app1 = new Appearance();
    Appearance app2 = new Appearance();
    Appearance app3 = new Appearance();
    Color3f ambientColour1 = new Color3f(1.0f, 0.0f, 0.0f);
    Color3f ambientColour2 = new Color3f(1.0f, 1.0f, 0.0f);
    Color3f ambientColour3 = new Color3f(1.0f, 1.0f, 1.0f);
    Color3f emissiveColour = new Color3f(0.0f, 0.0f, 0.0f);
    Color3f specularColour = new Color3f(1.0f, 1.0f, 1.0f);
    Color3f diffuseColour1 = new Color3f(1.0f, 0.0f, 0.0f);
    Color3f diffuseColour2 = new Color3f(1.0f, 1.0f, 0.0f);
    Color3f diffuseColour3 = new Color3f(1.0f, 1.0f, 1.0f);
    float shininess = 20.0f;
    app1.setMaterial(new Material(ambientColour1, emissiveColour,
        diffuseColour1, specularColour, shininess));
    app2.setMaterial(new Material(ambientColour2, emissiveColour,
        diffuseColour2, specularColour, shininess));
    app3.setMaterial(new Material(ambientColour3, emissiveColour,
        diffuseColour3, specularColour, shininess));

    //Create the vertex data for the cube. Since each shape is
    //a cube we can use the same vertex data for each cube
    IndexedQuadArray indexedCube = new IndexedQuadArray(8,
        IndexedQuadArray.COORDINATES | IndexedQuadArray.NORMALS, 24);
    Point3f[] cubeCoordinates = { new Point3f(1.0f, 1.0f, 1.0f),
        new Point3f(-1.0f, 1.0f, 1.0f),
        new Point3f(-1.0f, -1.0f, 1.0f),
        new Point3f(1.0f, -1.0f, 1.0f), new Point3f(1.0f, 1.0f, -1.0f),
        new Point3f(-1.0f, 1.0f, -1.0f),
        new Point3f(-1.0f, -1.0f, -1.0f),
        new Point3f(1.0f, -1.0f, -1.0f) };
    Vector3f[] cubeNormals = { new Vector3f(0.0f, 0.0f, 1.0f),
        new Vector3f(0.0f, 0.0f, -1.0f),
        new Vector3f(1.0f, 0.0f, 0.0f),
        new Vector3f(-1.0f, 0.0f, 0.0f),
        new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f) };
    int cubeCoordIndices[] = { 0, 1, 2, 3, 7, 6, 5, 4, 0, 3, 7, 4, 5, 6, 2,
        1, 0, 4, 5, 1, 6, 7, 3, 2 };
    int cubeNormalIndices[] = { 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3,
        3, 3, 4, 4, 4, 4, 5, 5, 5, 5 };
    indexedCube.setCoordinates(0, cubeCoordinates);
    indexedCube.setNormals(0, cubeNormals);
    indexedCube.setCoordinateIndices(0, cubeCoordIndices);
    indexedCube.setNormalIndices(0, cubeNormalIndices);

    //Create the three cubes
    leftCube = new Shape3D(indexedCube, app1);
    rightCube = new Shape3D(indexedCube, app2);
    moveCube = new Shape3D(indexedCube, app3);

    //Define the user data so that we can print out the
    //name of the colliding cube.
    leftCube.setUserData(new String("left cube"));
    rightCube.setUserData(new String("right cube"));

    //Create the content branch and add the lights
    BranchGroup contentBranch = new BranchGroup();
    addLights(contentBranch);

    //Create and set up the movable cube's TransformGroup.
    //This scales and translates the cube and then sets the
    // read, write and pick reporting capabilities.
    Transform3D moveXfm = new Transform3D();
    moveXfm.set(0.7, new Vector3d(0.0, 2.0, 1.0));
    moveGroup = new TransformGroup(moveXfm);
    moveGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    moveGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    moveGroup.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

    //Create the left cube's TransformGroup
    Transform3D leftGroupXfm = new Transform3D();
    leftGroupXfm.set(new Vector3d(-1.5, 0.0, 0.0));
    leftGroup = new TransformGroup(leftGroupXfm);

    //Create the right cube's TransformGroup
    Transform3D rightGroupXfm = new Transform3D();
    rightGroupXfm.set(new Vector3d(1.5, 0.0, 0.0));
    rightGroup = new TransformGroup(rightGroupXfm);

    //Add the behaviour to allow us to move the cube
    PickTranslateBehavior pickTranslate = new PickTranslateBehavior(
        contentBranch, myCanvas3D, bounds);
    contentBranch.addChild(pickTranslate);

    //Add our CollisionDetector class to detect collisions with
    //the movable cube.
    CollisionDetector myColDet = new CollisionDetector(moveCube, bounds);
    contentBranch.addChild(myColDet);

    //Create the content branch hierarchy.
    contentBranch.addChild(moveGroup);
    contentBranch.addChild(leftGroup);
    contentBranch.addChild(rightGroup);
    moveGroup.addChild(moveCube);
    leftGroup.addChild(leftCube);
    rightGroup.addChild(rightCube);

    return contentBranch;

  }

  /**
   * Process the exit button action to exit the application.
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == exitButton) {
      dispose();
      System.exit(0);
    }
  }

  public Test2() {
    VirtualUniverse myUniverse = new VirtualUniverse();
    Locale myLocale = new Locale(myUniverse);
    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    myCanvas3D = new Canvas3D(config);
    myLocale.addBranchGraph(buildViewBranch(myCanvas3D));
    myLocale.addBranchGraph(buildContentBranch());
    setTitle("SimpleWorld");
    setSize(400, 400);
    setLayout(new BorderLayout());
    Panel bottom = new Panel();
    bottom.add(exitButton);
    add(BorderLayout.CENTER, myCanvas3D);
    add(BorderLayout.SOUTH, bottom);
    exitButton.addActionListener(this);
    setVisible(true);
  }

  public static void main(String[] args) {
    Test2 sw = new Test2();
  }
}

/**
 * A simple collision detector class. This responds to a collision event by
 * printing a message with information about the type of collision event and the
 * object that has been collided with.
 * 
 * @author I.J.Palmer
 * @version 1.0
 */

class CollisionDetector extends Behavior {
  /** The separate criteria used to wake up this beahvior. */
  protected WakeupCriterion[] theCriteria;

  /** The OR of the separate criteria. */
  protected WakeupOr oredCriteria;

  /** The shape that is watched for collision. */
  protected Shape3D collidingShape;

  /**
   * @param theShape
   *            Shape3D that is to be watched for collisions.
   * @param theBounds
   *            Bounds that define the active region for this behaviour
   */
  public CollisionDetector(Shape3D theShape, Bounds theBounds) {
    collidingShape = theShape;
    setSchedulingBounds(theBounds);
  }

  /**
   * This creates an entry, exit and movement collision criteria. These are
   * then OR'ed together, and the wake up condition set to the result.
   */
  public void initialize() {
    theCriteria = new WakeupCriterion[3];
    theCriteria[0] = new WakeupOnCollisionEntry(collidingShape);
    theCriteria[1] = new WakeupOnCollisionExit(collidingShape);
    theCriteria[2] = new WakeupOnCollisionMovement(collidingShape);
    oredCriteria = new WakeupOr(theCriteria);
    wakeupOn(oredCriteria);
  }

  /**
   * Where the work is done in this class. A message is printed out using the
   * userData of the object collided with. The wake up condition is then set
   * to the OR'ed criterion again.
   */
  public void processStimulus(Enumeration criteria) {
    WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
    if (theCriterion instanceof WakeupOnCollisionEntry) {
      Node theLeaf = ((WakeupOnCollisionEntry) theCriterion)
          .getTriggeringPath().getObject();
      System.out.println("Collided with " + theLeaf.getUserData());
    } else if (theCriterion instanceof WakeupOnCollisionExit) {
      Node theLeaf = ((WakeupOnCollisionExit) theCriterion)
          .getTriggeringPath().getObject();
      System.out.println("Stopped colliding with  "
          + theLeaf.getUserData());
    } else {
      Node theLeaf = ((WakeupOnCollisionMovement) theCriterion)
          .getTriggeringPath().getObject();
      System.out.println("Moved whilst colliding with "
          + theLeaf.getUserData());
    }
    wakeupOn(oredCriteria);  }
}