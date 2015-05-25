import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.*;

import com.sun.j3d.utils.universe.*;
import java.awt.Color;

import javax.media.j3d.*;

import javax.vecmath.*;

/**
 * Hello world!
 *
 */
public class MouseRotateTest {

    private SimpleUniverse universe;
    private Appearance cubeAppearance;
    private BranchGroup rootGroup;
    private BoundingSphere bounds;

    public void init() {
        createUniverse();
        createAppearance();
        createCubeOfCubes();
        createLights();
        createBehaviourInteractors();
        universe.getViewingPlatform().setNominalViewingTransform();

        // add the cube group of objects to SimpleUnvirse object
        universe.addBranchGraph(rootGroup);
    }

    private void createUniverse() {
        universe = new SimpleUniverse();
        rootGroup = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        
    }

    private void createAppearance() {
        cubeAppearance = new Appearance();
        Color3f ambientColour = new Color3f();
        ambientColour.set(Color.GRAY);
        Color3f emissiveColour = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f specularColour = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f diffuseColour = new Color3f();
        diffuseColour.set(Color.GRAY);

        float shininess = 20.0f;
        cubeAppearance.setMaterial(new Material(ambientColour, emissiveColour,
                diffuseColour, specularColour, shininess));
    }

    private void createCubeOfCubes() {
        // build up a cube of 10 cube for each axis (x, y, z)
        for (float x = -.5f; x <= .5f; x = x + .1f) {
            for (float y = -.5f; y <= .5f; y += .1f) {
                for (float z = -.5f; z <= .5f; z += .1f) {
                    Box box = new Box(0.02f, 0.02f, 0.02f, cubeAppearance);
                    TransformGroup tg = new TransformGroup();
                    Transform3D transform = new Transform3D();
                    Vector3f vector = new Vector3f(x, y, z);
                    transform.setTranslation(vector);
                    tg.setTransform(transform);
                    tg.addChild(box);
                    rootGroup.addChild(tg);
                }
            }

        }
    }

    private void createLights() {
        Color3f ambientLightColour = new Color3f(0.9f, 0.9f, 0.9f);
        AmbientLight ambientLight = new AmbientLight(ambientLightColour);
        ambientLight.setInfluencingBounds(bounds);
        Color3f directionLightColour = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f directionLightDir = new Vector3f(-1.0f, -1.0f, -1.0f);
        DirectionalLight directionLight = new DirectionalLight(directionLightColour, directionLightDir);
        directionLight.setInfluencingBounds(bounds);
        rootGroup.addChild(ambientLight);
        rootGroup.addChild(directionLight);
    }

    private void createBehaviourInteractors() {
        TransformGroup viewTransformGroup =
                universe.getViewingPlatform().getViewPlatformTransform();

        KeyNavigatorBehavior keyInteractor =
                new KeyNavigatorBehavior(viewTransformGroup);

        BoundingSphere movingBounds = new BoundingSphere(new Point3d(0.0, 0.0,
        0.0), 100.0);
        keyInteractor.setSchedulingBounds(movingBounds);
        rootGroup.addChild(keyInteractor);

        MouseRotate behavior = new MouseRotate();
        behavior.setTransformGroup(viewTransformGroup);
        rootGroup.addChild(behavior);
        behavior.setSchedulingBounds(bounds);
    }

    public static void main(String[] args) {
        (new MouseRotateTest()).init();
    }
}