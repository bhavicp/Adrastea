package Game;

import Controllers.PhysicsHoverControl;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;


public class BattleForAdrastea extends SimpleApplication implements ActionListener, PhysicsTickListener {
    private Vehicle tank;
    private BulletAppState bulletAppState;
    PhysicsHoverControl vehicleControl;
    
    private float wheelRadius;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private BoundingBox box;
    private VehicleWheel fr, fl, br, bl;
    private Node node_fr, node_fl, node_br, node_bl;
    private float radius = 0.5f;

      
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
        
        BattleForAdrastea games = new BattleForAdrastea();
        games.setSettings(gameSettings);
        games.setShowSettings(false);
        games.start();
    }
    
    
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(50);
        cam.setFrustumFar(150f);
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0f,-10f,0f));
        
        setDisplayFps(false);
        setDisplayStatView(false);
        
        assetManager.registerLocator("./assets", FileLocator.class);
        Terrain terrain = new Terrain(bulletAppState,rootNode,assetManager);
        terrain.setUpLighting();
        terrain.setUpTerrain();
        
        tank = new Vehicle(bulletAppState,rootNode,assetManager);
        tank.makeTank();
        rootNode.attachChild(tank.getTank());
        ////////////EERRRRROR HERE NO MORE////////////////////////////////
        getPhysicsSpace().add(tank.getTankControl()); 
           
       
        
        ChaseCamera chaseCam = new ChaseCamera(cam, inputManager);
        //.addControl(chaseCam);
        tank.getTank().addControl(chaseCam);
        flyCam.setEnabled(false);
        
        setUpKeys();
        setUpHUD(); 
        
    }
    

    
   
    
   

    private void setUpHUD() {
        setDisplayStatView(false);
        setDisplayFps(false);
        
        Picture missilePicture = new Picture("Missile Picture");
        missilePicture.setImage(assetManager, "Textures/MissileDisplay.png", true);
        missilePicture.setWidth(178);
        missilePicture.setHeight(78);
        missilePicture.setPosition(0,settings.getHeight() - 78);
        guiNode.attachChild(missilePicture);
        
        Picture bulletPicture = new Picture("Missile Picture");
        bulletPicture.setImage(assetManager, "Textures/BulletDisplay.png", true);
        bulletPicture.setWidth(178);
        bulletPicture.setHeight(78);
        bulletPicture.setPosition(settings.getWidth() - 178,settings.getHeight() - 78);
        guiNode.attachChild(bulletPicture);
    }

    
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    /**
     * Setup mappings for keys
     */
     private void setUpKeys() {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_F));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_H));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_T));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_G));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
  }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        
       if (binding.equals("Left")) {
            if (value) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }
            tank.getTankControl().steer(steeringValue);
        } else if (binding.equals("Right")) {
            if (value) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
             tank.getTankControl().steer(steeringValue);
        } //Backups
        else if (binding.equals("Up")) {
            if (value) {
                accelerationValue += 800;
            } else {
                accelerationValue -= 800;
            }
             tank.getTankControl().accelerate(accelerationValue);
            //vehicleControl.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape(tank));
        } else if (binding.equals("Down")) {
            if (value) {
                 tank.getTankControl().brake(40f);
            } else {
                 tank.getTankControl().brake(0f);
            }
        }
    }

    @Override
    public void prePhysicsTick(PhysicsSpace ps, float f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void physicsTick(PhysicsSpace ps, float f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

