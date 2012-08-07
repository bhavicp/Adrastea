package MainCode;

import com.jme3.app.SimpleApplication;
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
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;


public class BattleForAdrastea extends SimpleApplication implements ActionListener{

    private TerrainQuad terrain;
    private Material mat_terrain;
    private BulletAppState bulletAppState;
    private VehicleControl vehicleControl;
    private RigidBodyControl landscape; 
    
    //Vehicle stuff
    private float wheelRadius;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    Node model  = new Node("vehicleNode");
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AppSettings gameSettings = new AppSettings(true);
        gameSettings.setResolution(1920, 1080);
        //gameSettings.setFullscreen(true);
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

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        
        //Need for physics
         bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //Debugging
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    
        assetManager.registerLocator("./assets", FileLocator.class);        
              
        Texture heightMapImage = assetManager.loadTexture("Textures/terrain.png");
        AbstractHeightMap heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightmap.setHeightScale(1);
        heightmap.smooth(1);
        heightmap.load();   
        
        mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        mat_terrain.setTexture("Alpha",
              assetManager.loadTexture("Textures/terrain.png"));
        
        int patchSize = 65;
        terrain = new TerrainQuad("Terrain", patchSize, 513, heightmap.getHeightMap());
        
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2f, 1f, 2f);
        

        
        TerrainLodControl terrainControl = new TerrainLodControl(terrain, getCamera());
        terrain.addControl(terrainControl);
        
        //We setup collision dection for the terrian
        CollisionShape terrainShape = CollisionShapeFactory.createMeshShape((Node) terrain);
        landscape = new RigidBodyControl(terrainShape,0);
        terrain.addControl(landscape);
        
        bulletAppState.getPhysicsSpace().add(terrain);
        rootNode.attachChild(terrain);
          
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.0f));        
        rootNode.addLight(al);    
        
        
        
       setuptanks();
       setupKeys();
    }

   
    
    private void setuptanks() {
       //hullCollisionShape shape = new HullCollisionShape(katamari_geo.getMesh());
        Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Red);

        
        Node model = (Node) assetManager.loadModel(
        "Models/HoverTank/tank.mesh.xml" );
        Geometry chasis = findGeom(model, "tank-geom-1");
        BoundingBox box = (BoundingBox) chasis.getModelBound();
       
        
    
        
        //Create a hull collision shape for the chassis
        CollisionShape tank = CollisionShapeFactory.createDynamicMeshShape(chasis);
        
        //create vehicle node
        
        vehicleControl = new VehicleControl(tank, 400);
        model.addControl(vehicleControl);

//        //setting suspension values for wheels, this can be a bit tricky
//        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
//        float stiffness = 10.0f;//200=f1 car
//        float compValue = 0.4f; //(should be lower than damp)
//        float dampValue = 0.6f;
//        vehicleControl.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
//        vehicleControl.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
//        vehicleControl.setSuspensionStiffness(stiffness);
//        vehicleControl.setMaxSuspensionForce(10000.0f);
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
//        vehicleControl.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
//                wheelDirection, wheelAxle, restLength, radius, true);
//
//        Node node2 = new Node("wheel 2 node");
//        Geometry wheels2 = new Geometry("wheel 2", wheelMesh);
//        node2.attachChild(wheels2);
//        wheels2.rotate(0, FastMath.HALF_PI, 0);
//        wheels2.setMaterial(mat);
//        vehicleControl.addWheel(node2, new Vector3f(xOff, yOff, zOff),
//                wheelDirection, wheelAxle, restLength, radius, true);
//
//        Node node3 = new Node("wheel 3 node");
//        Geometry wheels3 = new Geometry("wheel 3", wheelMesh);
//        node3.attachChild(wheels3);
//        wheels3.rotate(0, FastMath.HALF_PI, 0);
//        wheels3.setMaterial(mat);
//        vehicleControl.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
//                wheelDirection, wheelAxle, restLength, radius, false);
//
//        Node node4 = new Node("wheel 4 node");
//        Geometry wheels4 = new Geometry("wheel 4", wheelMesh);
//        node4.attachChild(wheels4);
//        wheels4.rotate(0, FastMath.HALF_PI, 0);
//        wheels4.setMaterial(mat);
//        vehicleControl.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
//                wheelDirection, wheelAxle, restLength, radius, false);
//
//        vehicleNode.attachChild(node1);
//        vehicleNode.attachChild(node2);
//        vehicleNode.attachChild(node3);
//        vehicleNode.attachChild(node4);
       
        //vehicleNode.scale( 3.0f, 1.0f, 1.0f );
        rootNode.attachChild(model);

        getPhysicsSpace().add(vehicleControl);
    
    }
    
    
 
    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_W));
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

    
    
    private PhysicsSpace getPhysicsSpace(){
        return bulletAppState.getPhysicsSpace();
    }
 @Override
    public void simpleUpdate(float tpf) {
        //cam.lookAt(vehicle.getPhysicsLocation(), Vector3f.UNIT_Y);
     
        //this.cam.setLocation( vehicleNode.localToWorld( new Vector3f( 0, 10 /* units above car*/, 10 /* units behind car*/ ), null));
        //this.cam.lookAt(this.vehicleNode.getWorldTranslation(), Vector3f.UNIT_Y);
    }

    @Override

      public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Lefts")) {
            if (isPressed) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }
            vehicleControl.steer(steeringValue);
        } else if (binding.equals("Rights")) {
            if (isPressed) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            vehicleControl.steer(steeringValue);
            
        } else if (binding.equals("Ups")) {
            if (isPressed) {
                accelerationValue -= 800;
            } else {
                accelerationValue += 800;
            }
            vehicleControl.accelerate(accelerationValue);
        } else if (binding.equals("Downs")) {
            if (isPressed) {
                vehicleControl.brake(40f);
            } else {
                vehicleControl.brake(0f);
            }
            

        }
    }
    
    
    private Geometry findGeom(Spatial spatial, String name) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                Geometry result = findGeom(child, name);
                if (result != null) {
                    return result;
                }
            }
        } else if (spatial instanceof Geometry) {
            if (spatial.getName().startsWith(name)) {
                return (Geometry) spatial;
            }
        }
        return null;
    }
}
        

