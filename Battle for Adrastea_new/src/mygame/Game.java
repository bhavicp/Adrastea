/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

/**
 *
 * @author Shane
 */
public class Game extends SimpleApplication{

    @Override
    public void simpleInitApp() {       
        flyCam.setMoveSpeed(50);
        assetManager.registerLocator("./assets", FileLocator.class);
        setUpLighting();
        setUpWorldTerrain();    
        setUpTank();
        
    }

    private void setUpLighting() {
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(directionalLight);
    }

    private void setUpWorldTerrain() {
        Spatial sceneModel = assetManager.loadModel("Scenes/WorldScene.j3o");
        sceneModel.setLocalTranslation(0,0,0);
        rootNode.attachChild(sceneModel);
    }

    private void setUpTank() {
        Spatial tank = assetManager.loadModel("Models/HoverTank/tank.j3o");
        tank.setLocalTranslation(0,0,0);
        tank.setLocalScale(1f);
        rootNode.attachChild(tank);
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
        
        Game game = new Game();
        game.setSettings(gameSettings);
        game.setShowSettings(false);
        game.start();
    }
}
