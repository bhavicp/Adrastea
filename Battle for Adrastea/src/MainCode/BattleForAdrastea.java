package MainCode;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;


public class BattleForAdrastea extends SimpleApplication{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1600, 900);
        settings.setVSync(true);
        settings.setFullscreen(true);
        settings.setFrameRate(60);
        
        BattleForAdrastea app = new BattleForAdrastea();        
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
    }
}
