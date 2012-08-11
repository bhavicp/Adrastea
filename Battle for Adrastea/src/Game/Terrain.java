/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/**
 *
 * @author bhavicp
 */
public class Terrain   {
    
    private AssetManager assetManager;
    private Node rootNode;
    private BulletAppState bulletAppState;
    
    public Terrain (BulletAppState bulletAppState, Node rootNode, AssetManager assetManager) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.bulletAppState = bulletAppState;
        
    }
           
    
    
    
    /**
     * 
     */
    public void setUpLighting() {
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1));
        rootNode.addLight(dl);
    }
    
    public void setUpTerrain() {
        
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
}
