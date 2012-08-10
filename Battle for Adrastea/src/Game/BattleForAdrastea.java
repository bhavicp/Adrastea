package Game;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.ui.Picture;


public class BattleForAdrastea extends SimpleApplication implements ActionListener {
    private Node tank;
    private BulletAppState bulletAppState;
    private VehicleControl vehicleControl;
    
    private boolean left = false, right = false, up = false, down = false;
    private float wheelRadius;
    private float steeringValue = 0;
    private float accelerationValue = 0;
      
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
        setUpLighting();
        setUpTerrain();
        setUpTank();
        setUpKeys();
        setUpHUD(); 
    }
    

    private void setUpTerrain() {
        
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/ColoredTextured.j3md");
        TextureKey key3 = new TextureKey("Textures/dirt.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
        
        Box floor = new Box(Vector3f.ZERO, 512f, 0.1f, 512f);
        floor.scaleTextureCoordinates(new Vector2f(3, 6));
        
        Geometry floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(floor_mat);
        floor_geo.setLocalTranslation(0, -1f, 0);
        this.rootNode.attachChild(floor_geo);
        /* Make the floor physical with mass 0.0f! */
        RigidBodyControl floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);
        
        
        
    }

    private void setUpTank() {
       
        tank = (Node) assetManager.loadModel("Models/Tank/HoverTank.blend");
        CollisionShape tankHull = CollisionShapeFactory.createMeshShape((Node)tank);
        
        vehicleControl = new VehicleControl(tankHull, 400);
        tank.addControl(vehicleControl);
        
        float stiffness = 60.0f;//200=f1 car
        float compValue = .3f; //(should be lower than damp)
        float dampValue = .4f;
        vehicleControl.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        vehicleControl.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicleControl.setSuspensionStiffness(stiffness);
        vehicleControl.setMaxSuspensionForce(10000.0f);
    
        vehicleControl.setCollideWithGroups(1);
        vehicleControl.setCollisionGroup(1);
        
        
        
        rootNode.attachChild(tank);
        getPhysicsSpace().add(vehicleControl); 
        vehicleControl.accelerate(11500);
        
    
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

    private void setUpLighting() {
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1));
        rootNode.addLight(dl);
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
     private void setUpKeys() {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_T));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_H));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_F));
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
            vehicleControl.steer(steeringValue);
        } else if (binding.equals("Right")) {
            if (value) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            vehicleControl.steer(steeringValue);
        } //Backups
        else if (binding.equals("Up")) {
            if (value) {
                System.out.println("TEST YES");
                accelerationValue -= 800;
            } else {
                accelerationValue += 800;
            }
            vehicleControl.accelerate(accelerationValue);
            //vehicleControl.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape(tank));
        } else if (binding.equals("Down")) {
            if (value) {
                vehicleControl.brake(40f);
            } else {
                vehicleControl.brake(0f);
            }
        }
    }
}

