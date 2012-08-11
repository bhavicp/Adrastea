/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Controllers.BombControl;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;

/**
 *
 * @author bhavicp
 */
public class Weapon {
    private AssetManager assetManager;
    Spatial missile;
    
    public Weapon(AssetManager assestManager) {
        this.assetManager = assestManager;
        
    }
    
    
    public void makeMissile(Vehicle vehicle) {
        Spatial tank = vehicle.getTank();
        
        Vector3f pos = tank.getWorldTranslation().clone();
        Quaternion rot = tank.getWorldRotation();
        Vector3f dir = rot.getRotationColumn(2);

         missile = assetManager.loadModel("Models/SpaceCraft/Rocket.mesh.xml");
        missile.scale(0.5f);
        missile.rotate(0, FastMath.PI, 0);
        missile.updateGeometricState();

        BoundingBox box = (BoundingBox) missile.getWorldBound();
        final Vector3f extent = box.getExtent(null);

        BoxCollisionShape boxShape = new BoxCollisionShape(extent);

        missile.setName("Missile");
        missile.rotate(rot);
        missile.setLocalTranslation(pos.addLocal(0, extent.y * 4.5f, 0));
        missile.setLocalRotation(vehicle.getTankControl().getPhysicsRotation());
        missile.setShadowMode(ShadowMode.Cast);
        RigidBodyControl control = new BombControl(assetManager, boxShape, 20);
        control.setLinearVelocity(dir.mult(100));
        control.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_03);
        missile.addControl(control);


        //rootNode.attachChild(missile);
        //getPhysicsSpace().add(missile);
    }
    public Spatial getMissile() {
        return this.missile;
    }
    
}
