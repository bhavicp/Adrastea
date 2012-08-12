/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author bhavicp
 */
public class Character {
    
    private BulletAppState bulletAppState;
    private AssetManager assetManager;
    private Node character;
    private RigidBodyControl control;

    Character(BulletAppState bulletAppState, AssetManager assetManager) {
        this.bulletAppState = bulletAppState;
        this.assetManager = assetManager;
        
        character = (Node) assetManager.loadModel("Models/BeamRunner/BeamRunnerShip/BeamRunnerShip.blend");
        CollisionShape charShape = CollisionShapeFactory.createDynamicMeshShape(character);
    
        control = new RigidBodyControl(charShape,100);
        control.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        

    }
    
    public Spatial getChar() {
        return this.character;
    }
    
    public RigidBodyControl getControl() {
        return this.control;
    }
    
}
