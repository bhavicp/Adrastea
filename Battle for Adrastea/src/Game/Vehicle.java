/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author bhavicp
 */
public class Vehicle {
    
    
    private AssetManager assetManager;
    private Node rootNode;
    private BulletAppState bulletAppState;
    private Node tank;
    private VehicleControl vehicleControl;
    private float radius;
    
    public Vehicle (BulletAppState bulletAppState, Node rootNode, AssetManager assetManager) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.bulletAppState = bulletAppState;        
    }
    
    public Node getTank() {
        
        return this.tank;
    }
    
    public VehicleControl getTankControl() {
        return this.vehicleControl;
    }
    
    public void makeTank() {
        tank = (Node) assetManager.loadModel("Models/Tank/HoverTank.blend");
        CollisionShape tankHull = CollisionShapeFactory.createDynamicMeshShape(tank);
        tank.setShadowMode(ShadowMode.CastAndReceive);
        
        vehicleControl = new VehicleControl(tankHull, 500);
        vehicleControl.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        
        tank.addControl(vehicleControl);
        setUpWheels();
        //rootNode.attachChild(tank);
        //getPhysicsSpace().add(vehicleControl); 
        
        //vehicleControl.setPhysicsLocation(new Vector3f(0,10,0));
        //vehicleControl.accelerate(500f);
        
    
    }
    
    /**
     * This adds wheels to the vehicle.
     */
    private void setUpWheels() {
        float stiffness = 120.0f;//200=f1 car
        float compValue = 0.2f; //(lower than damp!)
        float dampValue = 0.3f;
        
        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0

        float restLength = 0.3f;
        float yOff = -1.5f;
        float xOff = 2f;
        float zOff = 3f;

        
        //Setting default values for wheels
        vehicleControl.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        vehicleControl.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicleControl.setSuspensionStiffness(stiffness);
        vehicleControl.setMaxSuspensionForce(10000);

        //Create four wheels and add them at their locations

        //Mat for testing
        Cylinder wheelMesh = new Cylinder(16, 16, radius, radius * 0.6f, true);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Red);
     

        Node node1 = new Node("wheel 1 node");
        Geometry wheels1 = new Geometry("wheel 1", wheelMesh);
        node1.attachChild(wheels1);
        wheels1.rotate(0, FastMath.HALF_PI, 0);
        wheels1.setMaterial(mat);
        vehicleControl.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node2 = new Node("wheel 2 node");
        Geometry wheels2 = new Geometry("wheel 2", wheelMesh);
        node2.attachChild(wheels2);
        wheels2.rotate(0, FastMath.HALF_PI, 0);
        wheels2.setMaterial(mat);
        vehicleControl.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node3 = new Node("wheel 3 node");
        Geometry wheels3 = new Geometry("wheel 3", wheelMesh);
        node3.attachChild(wheels3);
        wheels3.rotate(0, FastMath.HALF_PI, 0);
        wheels3.setMaterial(mat);
        vehicleControl.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        Node node4 = new Node("wheel 4 node");
        Geometry wheels4 = new Geometry("wheel 4", wheelMesh);
        node4.attachChild(wheels4);
        wheels4.rotate(0, FastMath.HALF_PI, 0);
        wheels4.setMaterial(mat);
        vehicleControl.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        tank.attachChild(node1);
        tank.attachChild(node2);
        tank.attachChild(node3);
        tank.attachChild(node4);
        
        vehicleControl.getWheel(2).setFrictionSlip(4);
        vehicleControl.getWheel(3).setFrictionSlip(4);
        
    }
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
}
