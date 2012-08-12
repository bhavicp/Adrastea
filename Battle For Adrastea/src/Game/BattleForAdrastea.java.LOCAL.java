/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author Shane
 */
public class BattleForAdrastea extends SimpleApplication implements ActionListener, ScreenController, Controller {

    private BulletAppState bulletAppState;
    private Terrain terrain;
    private Vehicle mtank;
    private Weapon missile;
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private boolean load = false;
    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
    private Future loadFuture = null;
    private TextRenderer textRenderer;
    private Element progressBarElement;
    private Character alien;

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator("./assets", FileLocator.class);
        //loadGUI();

//Need for physics
            bulletAppState = new BulletAppState();
            stateManager.attach(bulletAppState);
            //Debugging
            bulletAppState.getPhysicsSpace().enableDebug(assetManager);

            //Terrain
            terrain = new Terrain(bulletAppState, assetManager);
            terrain.setUpLighting();
            terrain.setUpTerrain();

            //Tank
            mtank = new Vehicle(assetManager);
            getPhysicsSpace().add(mtank.getVehicleControl());

            setupKeys(); //This is to set bindings

            //Missile
            missile = new Weapon(assetManager);
            
            terrain.addLightToNode(rootNode);
            terrain.addTerrainToNode(rootNode);
            rootNode.attachChild(mtank.getTank());
            
            //Chara
            alien = new Character(bulletAppState, assetManager);
            rootNode.attachChild(alien.getChar());
            getPhysicsSpace().add(alien.getControl());


        //Camera
//        ChaseCamera chaseCam = new ChaseCamera(cam, inputManager);
//        this.mtank.addTankControl(chaseCam);
//        flyCam.setEnabled(false);



    }

    public static void main(String[] args) {
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

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    private void setupKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            this.mtank.getVehicleControl().steer(value ? 50f : 0);
        } else if (binding.equals("Right")) {
            this.mtank.getVehicleControl().steer(value ? -50f : 0);
        } else if (binding.equals("Up")) {
            this.mtank.getVehicleControl().accelerate(value ? 100f : 0);
        } else if (binding.equals("Down")) {
            this.mtank.getVehicleControl().accelerate(value ? -100f : 0);
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                this.mtank.getVehicleControl().setPhysicsLocation(new Vector3f(-140, 14, -23));
                this.mtank.getVehicleControl().setPhysicsRotation(new Matrix3f());
                this.mtank.getVehicleControl().clearForces();
            } else {
            }
        } else if (binding.equals("Space") && value) {

            missile.fireMissile(mtank);
            rootNode.attachChild(missile.getMissile());
            getPhysicsSpace().add(missile.getMissile());
        }
    }
    public static final Quaternion PITCH011_25 = new Quaternion().fromAngleAxis(FastMath.PI / 16, new Vector3f(1, 0, 0));

    @Override
    public void simpleUpdate(float tpf) {
        //cam.lookAt(mtank.getVehicleControl().getPhysicsLocation(), Vector3f.UNIT_Y);
        //cam.lookAt(mtank.getTank().getWorldTranslation(), Vector3f.UNIT_Y);

        //Try this new one
        Vector3f vLoc = mtank.getTank().getWorldTranslation();
        Quaternion qRot = mtank.getTank().getWorldRotation().clone();

        //pitch the rotation a bit down --- so you look towards the bottom, used for setting camera rotation later
        qRot.normalizeLocal();
        qRot.multLocal(PITCH011_25);
        //set the pitched down rotation of the car - look a bit to the bottom
        cam.setRotation(qRot);
        //get the forward direction of the vehicle
        Vector3f vRot = mtank.getVehicleControl().getForwardVector(null).mult(5f);


        //calculate the position of the camera (above car), using the vehicles position and the vehicles forward direction
        Vector3f vPos = new Vector3f(vLoc.x - vRot.x, vLoc.y + 3.5f, vLoc.z - vRot.z);
        //set the position of camera
        cam.setLocation(vPos);

        if (load) {
            if (loadFuture == null) {
                loadFuture = exec.submit(loadingCallable);
            }

            if (loadFuture.isDone()) {
                nifty.gotoScreen("end");
                nifty.exit();
                guiViewPort.removeProcessor(niftyDisplay);
                flyCam.setEnabled(true);
                flyCam.setMoveSpeed(10);
                
                terrain.addLightToNode(rootNode);
                terrain.addTerrainToNode(rootNode);
                rootNode.attachChild(mtank.getTank());
                
                
                load = false;
            }
        }
    }

    private void loadGUI() {
        setDisplayStatView(false);
        setDisplayFps(false);

        flyCam.setEnabled(false);
        niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, viewPort);
        nifty = niftyDisplay.getNifty();

        nifty.fromXml("Interface/GUI.xml", "start", this);
        guiViewPort.addProcessor(niftyDisplay);
    }
    
    Callable<Void> loadingCallable = new Callable<Void>() {

        @Override
        public Void call() throws Exception {
            Element element = nifty.getScreen("loadlevel").findElementByName("loadingtext");
            textRenderer = element.getRenderer(TextRenderer.class);


            //Need for physics
            bulletAppState = new BulletAppState();
            stateManager.attach(bulletAppState);
            //Debugging
            bulletAppState.getPhysicsSpace().enableDebug(assetManager);

            //Terrain
            terrain = new Terrain(bulletAppState, assetManager);
            terrain.setUpLighting();
            terrain.setUpTerrain();

            //Tank
            mtank = new Vehicle(assetManager);
            getPhysicsSpace().add(mtank.getVehicleControl());

            setupKeys(); //This is to set bindings

            //Missile
            missile = new Weapon(assetManager);



            //setProgress is thread safe (see below)
            setProgress(0.2f, "Loading grass");

            Thread.sleep(200);
            setProgress(0.4f, "Loading dirt");

            Thread.sleep(200);
            setProgress(0.5f, "Loading rocks");
            Thread.sleep(200);

            setProgress(0.6f, "Creating terrain");

            Thread.sleep(200);
            setProgress(0.8f, "Positioning terrain");

            Thread.sleep(200);
            setProgress(0.9f, "Loading cameras");

            Thread.sleep(200);
            setProgress(1f, "Loading complete");
            Thread.sleep(200);
            showHUD();
            return null;
        }
    };

    public void showLoadingMenu() {
        nifty.gotoScreen("loadlevel");
        load = true;
    }
    
    public void showHUD() {
        nifty.gotoScreen("hud");
        load = true;
    }

    public void setProgress(final float progress, final String loadingText) {
        enqueue(new Callable() {

            public Object call() throws Exception {
                final int MIN_WIDTH = 32;
                int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
                progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
                progressBarElement.getParent().layoutElements();

                textRenderer.setText(loadingText);
                return null;
            }
        });
    }

    @Override
    public void bind(Nifty nifty, Screen screen, Element element, Properties parameter, Attributes controlDefinitionAttributes) {
        progressBarElement = element.findElementByName("progressbar");
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        progressBarElement = nifty.getScreen("loadlevel").findElementByName("progressbar");
    }

    @Override
    public void onStartScreen() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onEndScreen() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void init(Properties prprts, Attributes atrbts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onFocus(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean inputEvent(NiftyInputEvent nie) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
