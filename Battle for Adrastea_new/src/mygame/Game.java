/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
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
    private Node landscape;
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
//        landscape = (Node)assetManager.loadModel("Scenes/Terrain.j3o");
//        
//        TerrainQuad terrain = (TerrainQuad) landscape.getChild("terrain-Terrain");
        landscape = (Node)assetManager.loadModel("Scenes/WorldScene.j3o");
        
        TerrainQuad terrain = (TerrainQuad) landscape.getChild("terrain-WorldScene");
   
        CollisionShape collisionTerrainShape = CollisionShapeFactory.createMeshShape(terrain);
        
        RigidBodyControl terrainlandscape = new RigidBodyControl(collisionTerrainShape,0.0f);
        
        landscape.addControl(terrainlandscape);            
        rootNode.attachChild(landscape);
        this.bulletAppState.getPhysicsSpace().addAll(landscape);
        
        
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

        final float mass = 400;       
        
        tank = assetManager.loadModel("Models/Tank/HoverTank.j3o");

        
        CollisionShape tankHull = CollisionShapeFactory.createDynamicMeshShape((Node) tank);
        player = new VehicleControl(tankHull, mass);        
     
       
        
               
        tank.addControl(player);
        rootNode.attachChild(tank);        
        bulletAppState.getPhysicsSpace().addAll(tank);

        
        
//        Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.getAdditionalRenderState().setWireframe(true);
//        mat.setColor("Color", ColorRGBA.Red);
// 
//        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
//        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
//        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
//        BoxCollisionShape box = new BoxCollisionShape(new Vector3f(1.2f, 0.5f, 2.4f));
//        compoundShape.addChildShape(box, new Vector3f(0, 1, 0));
// 
//        //create vehicle node
//        Node vehicleNode=new Node("vehicleNode");
//        player = new VehicleControl(compoundShape, 400);
//        vehicleNode.addControl(player);
// 
//        //setting suspension values for wheels, this can be a bit tricky
//        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
//        float stiffness = 60.0f;//200=f1 car
//        float compValue = .3f; //(should be lower than damp)
//        float dampValue = .4f;
//        player.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
//        player.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
//        player.setSuspensionStiffness(stiffness);
//        player.setMaxSuspensionForce(10000.0f);
// 
//        //Create four wheels and add them at their locations
//        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
//        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0
//        float radius = 0.5f;
//        float restLength = 0.3f;
//        float yOff = 0.5f;
//        float xOff = 1f;
//        float zOff = 2f;
// 
//        Cylinder wheelMesh = new Cylinder(16, 16, radius, radius * 0.6f, true);
// 
//        Node node1 = new Node("wheel 1 node");
//        Geometry wheels1 = new Geometry("wheel 1", wheelMesh);
//        node1.attachChild(wheels1);
//        wheels1.rotate(0, FastMath.HALF_PI, 0);
//        wheels1.setMaterial(mat);
//        player.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
//                wheelDirection, wheelAxle, restLength, radius, true);
// 
//        Node node2 = new Node("wheel 2 node");
//        Geometry wheels2 = new Geometry("wheel 2", wheelMesh);
//        node2.attachChild(wheels2);
//        wheels2.rotate(0, FastMath.HALF_PI, 0);
//        wheels2.setMaterial(mat);
//        player.addWheel(node2, new Vector3f(xOff, yOff, zOff),
//                wheelDirection, wheelAxle, restLength, radius, true);
// 
//        Node node3 = new Node("wheel 3 node");
//        Geometry wheels3 = new Geometry("wheel 3", wheelMesh);
//        node3.attachChild(wheels3);
//        wheels3.rotate(0, FastMath.HALF_PI, 0);
//        wheels3.setMaterial(mat);
//        player.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
//                wheelDirection, wheelAxle, restLength, radius, false);
// 
//        Node node4 = new Node("wheel 4 node");
//        Geometry wheels4 = new Geometry("wheel 4", wheelMesh);
//        node4.attachChild(wheels4);
//        wheels4.rotate(0, FastMath.HALF_PI, 0);
//        wheels4.setMaterial(mat);
//        player.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
//                wheelDirection, wheelAxle, restLength, radius, false);
// 
//        vehicleNode.attachChild(node1);
//        vehicleNode.attachChild(node2);
//        vehicleNode.attachChild(node3);
//        vehicleNode.attachChild(node4);
//        rootNode.attachChild(vehicleNode);
// 
//        getPhysicsSpace().add(player);
        
              
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
