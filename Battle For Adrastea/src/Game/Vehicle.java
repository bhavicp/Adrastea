/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Controllers.PhysicsHoverControl;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 *
 * @author Shane
 */
public class Vehicle {

    private PhysicsHoverControl vehicleControl;
    private Spatial tank;

    public Vehicle(AssetManager assetManager) {

        this.tank = assetManager.loadModel("Models/Tank/HoverTank.blend");
        CollisionShape colShape = CollisionShapeFactory.createDynamicMeshShape(this.tank);
        this.tank.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        this.tank.setLocalTranslation(new Vector3f(-140, 14, -23));
        this.tank.setLocalRotation(new Quaternion(new float[]{0, 0.01f, 0}));

        this.vehicleControl = new PhysicsHoverControl(colShape, 500);
        this.vehicleControl.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);

        this.tank.addControl(this.vehicleControl);       
    }

    public Spatial getTank() {
        return this.tank;
    }

    public PhysicsHoverControl getVehicleControl() {
        return this.vehicleControl;
    }  
    
    public void addTankControl(Control control){
        this.tank.addControl(control);
    }

}
