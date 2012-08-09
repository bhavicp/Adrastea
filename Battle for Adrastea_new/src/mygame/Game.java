/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.terrain.geomipmap.TerrainQuad;


/**
 *
 * @author Shane
 */
public class Game extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private float wheelRadius;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private VehicleControl player;
    private Spatial tank;
    private Spatial landscape;
    private RigidBodyControl control;
    
    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //Debugging
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
        flyCam.setMoveSpeed(50);
        assetManager.registerLocator("./assets", FileLocator.class);
        setUpLighting();
        setUpWorldTerrain();    
        setUpTank();
        setupKeys();
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

    private void setUpLighting() {
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(directionalLight);
    }

    private void setUpWorldTerrain() {
        
        //This is the scene object
        Node terrain = (Node)assetManager.loadModel("Scenes/WorldScene.j3o");
        //This is the terrain object in the scene, its a child of the sceene
        TerrainQuad terrrainObj = (TerrainQuad)terrain.getChild("terrain-WorldScene");
        
        CollisionShape terrainShape = CollisionShapeFactory.createDynamicMeshShape(terrrainObj);
        RigidBodyControl terrainlandscape = terrrainObj.getControl(RigidBodyControl.class);
        
        terrain.addControl(terrainlandscape);     
        
        
        rootNode.attachChild(terrain);
        this.bulletAppState.getPhysicsSpace().add(terrain);
        this.bulletAppState.getPhysicsSpace().add(terrrainObj);
        
        
////        landscape = assetManager.loadModel("Scenes/WorldScene.j3o");        
//        landscape = assetManager.loadModel("Scenes/Terrain.j3o");        
//        landscape.setLocalTranslation(0,0,0);
//        
//        
//        
//        CollisionShape terrainShape = CollisionShapeFactory.createMeshShape((Node) landscape);
//        RigidBodyControl lanSc = new RigidBodyControl(terrainShape, 0);
//        landscape.addControl(lanSc);
//        
//        rootNode.attachChild(landscape);        
//        bulletAppState.getPhysicsSpace().addAll(landscape);
                    
    }

    private void setUpTank() {
//        float stiffness = 120.0f;//200=f1 car
//        float compValue = 0.2f; //(lower than damp!)
//        float dampValue = 0.3f;
//        final float mass = 400;
        
        
        tank = assetManager.loadModel("Models/HoverTank/tank.j3o");
        tank.setLocalTranslation(0,0,0);
        //tank.setLocalScale(1f);
        
//        CollisionShape tankHull = CollisionShapeFactory.createDynamicMeshShape((Node) tank);
//        player = new VehicleControl(tankHull, mass);
//                 
//        player.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
//        player.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
//        player.setSuspensionStiffness(stiffness);
//        player.setMaxSuspensionForce(10000);
//        player.setPhysicsLocation(new Vector3f(0, 10, 0));
        
         player = tank.getControl(VehicleControl.class);
        
        rootNode.attachChild(tank);
        bulletAppState.getPhysicsSpace().addAll(tank);
              
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
        
        Game games = new Game();
        games.setSettings(gameSettings);
        games.setShowSettings(false);
        games.start();
    }
    
    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Lefts");
        inputManager.addListener(this, "Rights");
        inputManager.addListener(this, "Ups");
        inputManager.addListener(this, "Downs");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if (value) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }            
            player.steer(steeringValue);
        } else if (binding.equals("Rights")) {
            if (value) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            player.steer(steeringValue);
        } //note that our fancy car actually goes backwards..
        else if (binding.equals("Ups")) {
            if (value) {
                accelerationValue -= 800;
            } else {
                accelerationValue += 800;
            }
            player.accelerate(accelerationValue);
        } else if (binding.equals("Downs")) {
            if (value) {
                player.brake(40f);
            } else {
                player.brake(0f);
            }
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                player.setPhysicsLocation(Vector3f.ZERO);
                player.setPhysicsRotation(new Matrix3f());
                player.setLinearVelocity(Vector3f.ZERO);
                player.setAngularVelocity(Vector3f.ZERO);
                player.resetSuspension();
            } else {
            }
        }
    }
    @Override
    public void simpleUpdate(float tpf) {
        //cam.lookAt(tank.getWorldTranslation(), Vector3f.UNIT_Y);
    }

}
