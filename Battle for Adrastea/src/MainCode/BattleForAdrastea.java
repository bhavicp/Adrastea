package MainCode;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;


public class BattleForAdrastea extends SimpleApplication{

    private TerrainQuad terrain;
    private Material mat_terrain;
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1600, 900);
        settings.setVSync(true);
        settings.setFullscreen(true);
        
        BattleForAdrastea app = new BattleForAdrastea();        
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(50);
        
        assetManager.registerLocator("./assets", FileLocator.class);        
              
        Texture heightMapImage = assetManager.loadTexture("Textures/terrain.png");
        AbstractHeightMap heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightmap.setHeightScale(20);
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
        rootNode.attachChild(terrain);

        
        TerrainLodControl terrainControl = new TerrainLodControl(terrain, getCamera());
        terrain.addControl(terrainControl);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.0f));        
        rootNode.addLight(al);    
    }
}
