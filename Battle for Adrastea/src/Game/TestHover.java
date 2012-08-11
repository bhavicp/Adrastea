/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

/**
 *
 * @author Shane
 */
public class TestHover extends SimpleApplication implements PhysicsTickListener{
    
    Node ship;
    RigidBodyControl ship_phy;
    
    @Override
    public void simpleInitApp() {
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().addTickListener(this);        
        bulletAppState.getPhysicsSpace().setMaxSubSteps(4);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0f,-10f,0f));
        
        ship = new Node();
         
         
        Box b = new Box(Vector3f.ZERO, 1, 0.1f, 1);
        Geometry geom = new Geometry("Box", b);
 
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
         
        Box bx = new Box(new Vector3f(0, 1, 0), 0.1f, 0.1f, 0.1f);
        Geometry geomx = new Geometry("Box", bx);
        Material matx = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matx.setColor("Color", ColorRGBA.Red);
        geomx.setMaterial(matx);
         
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);        
         
         
        ship.attachChild(geomx);
         
        ship_phy = new RigidBodyControl(1f);        
        ship.addControl(ship_phy);        
        rootNode.attachChild(ship);
        bulletAppState.getPhysicsSpace().add(ship_phy);        
        ship_phy.setPhysicsLocation(new Vector3f(0, 0.5f, 0));
         
         
        rootNode.attachChild(geom);
    }

    @Override
    public void prePhysicsTick(PhysicsSpace ps, float f) {
        ship_phy.applyImpulse(new Vector3f(0,10f*f,0), new Vector3f(0,0,0));
    }

    @Override
    public void physicsTick(PhysicsSpace ps, float f) {
        
    }
    
    public static void main(String[] args){
        AppSettings gameSettings = new AppSettings(true);
        //gameSettings.setResolution(1920, 1080);
        gameSettings.setResolution(800, 600);
        gameSettings.setFullscreen(false);
        gameSettings.setVSync(true);
        gameSettings.setTitle("Game");
        gameSettings.setUseInput(true);
        gameSettings.setFrameRate(500);
        gameSettings.setSamples(0);
        gameSettings.setRenderer("LWJGL-OpenGL2");
        
        TestHover games = new TestHover();
        games.setSettings(gameSettings);
        games.setShowSettings(false);
        games.start();
    }
    
}
