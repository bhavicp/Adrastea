/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

/**
 *
 * @author Shane
 */
public class BattleForAdrastea extends SimpleApplication implements ActionListener{
       
    private BulletAppState bulletAppState;   
    private Terrain terrain;
    private Vehicle mtank;
    private Weapon missile;
    
    
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        
        //Need for physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //Debugging
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    
        assetManager.registerLocator("./assets", FileLocator.class);
        
        //Terrain
        terrain = new Terrain(bulletAppState, rootNode, assetManager);
        terrain.setUpLighting();
        terrain.setUpTerrain();
        
        //Tank
        mtank = new Vehicle(assetManager);
        rootNode.attachChild(mtank.getTank());
        getPhysicsSpace().add(mtank.getVehicleControl());
        
        setupKeys(); //This is to set bindings
        
        //Missile
        missile = new Weapon(assetManager);
        
        
        //Camera
//        ChaseCamera chaseCam = new ChaseCamera(cam, inputManager);
//        this.mtank.addTankControl(chaseCam);
//        flyCam.setEnabled(false);
        
        
        
    }
    
    public static void main(String[] args){
        AppSettings gameSettings = new AppSettings(true);
        gameSettings.setResolution(1500, 900);
        gameSettings.setFullscreen(false);
        gameSettings.setVSync(true);
        gameSettings.setTitle("Game");
        gameSettings.setUseInput(true);
        gameSettings.setFrameRate(500);
        gameSettings.setSamples(0);
        gameSettings.setRenderer("LWJGL-OpenGL2");
        
        BattleForAdrastea app = new BattleForAdrastea();        
        app.setSettings(gameSettings);
        app.setShowSettings(false);
        app.start();
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    private void setupKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            this.mtank.getVehicleControl().steer(value ? 50f : 0);
        } else if (binding.equals("Right")) {
            this.mtank.getVehicleControl().steer(value ? -50f : 0);
        } else if (binding.equals("Up")) {
            this.mtank.getVehicleControl().accelerate(value ? 100f : 0);
        } else if (binding.equals("Down")) {
            this.mtank.getVehicleControl().accelerate(value ? -100f : 0);
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                this.mtank.getVehicleControl().setPhysicsLocation(new Vector3f(-140, 14, -23));
                this.mtank.getVehicleControl().setPhysicsRotation(new Matrix3f());
                this.mtank.getVehicleControl().clearForces();
            } else {
            }
        } else if (binding.equals("Space") && value) {
            
            missile.fireMissile(mtank);
            rootNode.attachChild(missile.getMissile());
            getPhysicsSpace().add(missile.getMissile());
        }       
    }
    
    public static final Quaternion PITCH011_25 = new Quaternion().fromAngleAxis(FastMath.PI/16,   new Vector3f(1,0,0));
    @Override
    public void simpleUpdate(float tpf) {
        //cam.lookAt(mtank.getVehicleControl().getPhysicsLocation(), Vector3f.UNIT_Y);
        //cam.lookAt(mtank.getTank().getWorldTranslation(), Vector3f.UNIT_Y);
        
        //Try this new one
        Vector3f vLoc=mtank.getTank().getWorldTranslation();
        Quaternion qRot=mtank.getTank().getWorldRotation().clone();
        
        //pitch the rotation a bit down --- so you look towards the bottom, used for setting camera rotation later
        qRot.normalizeLocal();
        qRot.multLocal(PITCH011_25);
        //set the pitched down rotation of the car - look a bit to the bottom
        cam.setRotation(qRot);
         //get the forward direction of the vehicle
        Vector3f vRot= mtank.getVehicleControl().getForwardVector(null).mult(5f);

 
        //calculate the position of the camera (above car), using the vehicles position and the vehicles forward direction
        Vector3f vPos=new Vector3f(vLoc.x-vRot.x,vLoc.y+3.5f,vLoc.z-vRot.z);
        //set the position of camera
        cam.setLocation(vPos);
    }
    
}
