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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 *
 * @author Shane
 */
public class Terrain {
    
    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    private DirectionalLight dl;
            private DirectionalLight dl2;
            private Geometry floor_geo;
    
     public Terrain (BulletAppState bulletAppState, AssetManager assetManager) {
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        
    }
     
     public void setUpLighting() {
         dl = new DirectionalLight();
        dl.setColor(new ColorRGBA(1.0f, 0.94f, 0.8f, 1f).multLocal(1.3f));
        dl.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
        

        Vector3f lightDir2 = new Vector3f(0.70518064f, 0.5902297f, -0.39287305f);
         dl2 = new DirectionalLight();
        dl2.setColor(new ColorRGBA(0.7f, 0.85f, 1.0f, 1f));
        dl2.setDirection(lightDir2);
        
    }
     
     public void setUpTerrain() {
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/ColoredTextured.j3md");
        TextureKey key3 = new TextureKey("Textures/dirt.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(Texture.WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
        
        Box floor = new Box(Vector3f.ZERO, 512f, 0.1f, 512f);
        floor.scaleTextureCoordinates(new Vector2f(3, 6));
        
        floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(floor_mat);
        floor_geo.setLocalTranslation(0, -1f, 0);
        
        /* Make the floor physical with mass 0.0f! */
        RigidBodyControl floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);
     }
     
     public void addLightToNode(Node rootNode){
         rootNode.addLight(dl);
         rootNode.addLight(dl2);
     }
     
     public void addTerrainToNode(Node rootNode){
         rootNode.attachChild(floor_geo);
     }
}
