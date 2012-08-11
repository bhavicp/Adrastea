/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;

/**
 *
 * @author Shane
 */
public class BattleForAdrastea extends SimpleApplication{
       
    private BulletAppState bulletAppState;   
    private Terrain terrain;
    
    
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        
        //Need for physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //Debugging
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    
        assetManager.registerLocator("./assets", FileLocator.class);
        
        terrain = new Terrain(bulletAppState, rootNode, assetManager);
        terrain.setUpLighting();
        terrain.setUpTerrain();
        
    }
    
    public static void main(String[] args){
        AppSettings gameSettings = new AppSettings(true);
        gameSettings.setResolution(1500, 900);
        gameSettings.setFullscreen(false);
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
}
