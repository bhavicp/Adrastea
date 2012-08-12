package Controllers;

import Game.Terrain;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
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

public class GUI extends SimpleApplication implements ScreenController, Controller {

    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private Element progressBarElement;
    private Terrain terrain;
    private Material mat_terrain;
    private boolean load = false;
    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
    private Future loadFuture = null;
    private TextRenderer textRenderer;

    public static void main(String[] args) {
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

        GUI loadingScreen = new GUI();
        loadingScreen.setShowSettings(false);
        loadingScreen.setSettings(gameSettings);
        loadingScreen.start();

    }

    @Override
    public void simpleInitApp() {
        setDisplayStatView(false);
        setDisplayFps(false);
        assetManager.registerLocator("./assets", FileLocator.class);

        flyCam.setEnabled(false);
        niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, viewPort);
        nifty = niftyDisplay.getNifty();

        nifty.fromXml("Interface/GUI.xml", "start", this);
        guiViewPort.addProcessor(niftyDisplay);

    }

    @Override
    public void simpleUpdate(float tpf) {
        if (load) {
            if (loadFuture == null) {
                loadFuture = exec.submit(loadingCallable);
            }

            if (loadFuture.isDone()) {
                nifty.gotoScreen("hud");
                nifty.exit();
                guiViewPort.removeProcessor(niftyDisplay);

                flyCam.setEnabled(true);
                flyCam.setMoveSpeed(50);
                terrain.addTerrainToNode(rootNode);
                load = false;

            }
        }
    }
    Callable<Void> loadingCallable = new Callable<Void>() {

        @Override
        public Void call() throws Exception {
            Element element = nifty.getScreen("loadlevel").findElementByName("loadingtext");
            textRenderer = element.getRenderer(TextRenderer.class);


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

            terrain = new Terrain(new BulletAppState(), assetManager);
            terrain.setUpLighting();
            terrain.setUpTerrain();

            return null;
        }
    };

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

    public void setBulletCount(final int count) {
        enqueue(new Callable() {

            @Override
            public Object call() throws Exception {
                Element element = nifty.getScreen("hud").findElementByName("bullettext");
                textRenderer = element.getRenderer(TextRenderer.class);
                textRenderer.setText(String.valueOf(count));
                return null;
            }
        });
    }

    public void setMissileCount(final int count) {
        enqueue(new Callable() {

            @Override
            public Object call() throws Exception {
                Element element = nifty.getScreen("hud").findElementByName("missiletext");
                textRenderer = element.getRenderer(TextRenderer.class);
                textRenderer.setText(String.valueOf(count));
                return null;
            }
        });
    }

    public void showLoadingMenu() {
        nifty.gotoScreen("loadlevel");
        load = true;
    }

    @Override
    public void bind(Nifty nifty, Screen screen, Element element, Properties parameter, Attributes controlDefinitionAttributes) {
        progressBarElement = element.findElementByName("progressbar");
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        progressBarElement = nifty.getScreen("loadlevel").findElementByName("progressbar");
    }

    // <editor-fold defaultstate="collapsed" desc="Unused Implementations">
    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    @Override
    public void init(Properties parameter, Attributes controlDefinitionAttributes) {
    }

    @Override
    public void onFocus(boolean getFocus) {
    }

    @Override
    public boolean inputEvent(NiftyInputEvent inputEvent) {
        return false;
    }
    // </editor-fold>
}
