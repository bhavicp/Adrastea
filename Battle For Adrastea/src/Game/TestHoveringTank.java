/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package Game;

import Controllers.BombControl;
import Controllers.PhysicsHoverControl;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer.CompareMode;
import com.jme3.shadow.PssmShadowRenderer.FilterMode;

public class TestHoveringTank extends SimpleApplication implements AnalogListener, ActionListener
         {

    private Vehicle tank;
    private BulletAppState bulletAppState;
    Terrain terrain;
    Material matRock;
    boolean wireframe = false;
    protected BitmapText hintText;
    PointLight pl;
    Geometry lightMdl;
    Geometry collisionMarker;

    public static void main(String[] args) {
        TestHoveringTank app = new TestHoveringTank();
        app.start();
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Lefts");
        inputManager.addListener(this, "Rights");
        inputManager.addListener(this, "Ups");
        inputManager.addListener(this, "Downs");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator("./assets", FileLocator.class);
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
          BloomFilter bf=new BloomFilter(BloomFilter.GlowMode.Objects);
          fpp.addFilter(bf);
          viewPort.addProcessor(fpp);
         
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        bulletAppState.getPhysicsSpace().setAccuracy(1f/30f);
        //rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        PssmShadowRenderer pssmr = new PssmShadowRenderer(assetManager, 2048, 3);
        pssmr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
        pssmr.setLambda(0.55f);
        pssmr.setShadowIntensity(0.6f);
        pssmr.setCompareMode(CompareMode.Hardware);
        pssmr.setFilterMode(FilterMode.Bilinear);
        viewPort.addProcessor(pssmr);

        terrain = new Terrain(bulletAppState, rootNode, assetManager);
        
        setupKeys();
        terrain.setUpTerrain();
        terrain.setUpLighting();
        
        
        this.tank = new Vehicle(assetManager);       
        ChaseCamera chaseCam = new ChaseCamera(cam, inputManager);
        this.tank.addTankControl(chaseCam);
        flyCam.setEnabled(false);
        
        rootNode.attachChild(this.tank.getTank());
        bulletAppState.getPhysicsSpace().add(this.tank.getVehicleControl());  
        
        
    }

    public void makeMissile() {
        Vector3f pos = this.tank.getTank().getWorldTranslation().clone();
        Quaternion rot = this.tank.getTank().getWorldRotation();
        Vector3f dir = rot.getRotationColumn(2);

        Spatial missile = assetManager.loadModel("Models/SpaceCraft/Rocket.mesh.xml");
        missile.scale(0.5f);
        missile.rotate(0, FastMath.PI, 0);
        missile.updateGeometricState();

        BoundingBox box = (BoundingBox) missile.getWorldBound();
        final Vector3f extent = box.getExtent(null);

        BoxCollisionShape boxShape = new BoxCollisionShape(extent);

        missile.setName("Missile");
        missile.rotate(rot);
        missile.setLocalTranslation(pos.addLocal(0, extent.y * 4.5f, 0));
        missile.setLocalRotation(this.tank.getVehicleControl().getPhysicsRotation());
        missile.setShadowMode(ShadowMode.Cast);
        RigidBodyControl control = new BombControl(assetManager, boxShape, 20);
        control.setLinearVelocity(dir.mult(100));
        control.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_03);
        missile.addControl(control);


        rootNode.attachChild(missile);
        getPhysicsSpace().add(missile);
    }

    public void onAnalog(String binding, float value, float tpf) {
    }

    public void updateCamera() {
        rootNode.updateGeometricState();

        Vector3f pos = this.tank.getTank().getWorldTranslation().clone();
        Quaternion rot = this.tank.getTank().getWorldRotation();
        Vector3f dir = rot.getRotationColumn(2);

        // make it XZ only
        Vector3f camPos = new Vector3f(dir);
        camPos.setY(0);
        camPos.normalizeLocal();

        // negate and multiply by distance from object
        camPos.negateLocal();
        camPos.multLocal(15);

        // add Y distance
        camPos.setY(2);
        camPos.addLocal(pos);
        cam.setLocation(camPos);

        Vector3f lookAt = new Vector3f(dir);
        lookAt.multLocal(7); // look at dist
        lookAt.addLocal(pos);
        cam.lookAt(lookAt, Vector3f.UNIT_Y);
    }

    @Override
    public void simpleUpdate(float tpf) {
    } 
    
    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            this.tank.getVehicleControl().steer(value ? 50f : 0);
        } else if (binding.equals("Rights")) {
            this.tank.getVehicleControl().steer(value ? -50f : 0);
        } else if (binding.equals("Ups")) {
            this.tank.getVehicleControl().accelerate(value ? 100f : 0);
        } else if (binding.equals("Downs")) {
            this.tank.getVehicleControl().accelerate(value ? -100f : 0);
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                this.tank.getVehicleControl().setPhysicsLocation(new Vector3f(-140, 14, -23));
                this.tank.getVehicleControl().setPhysicsRotation(new Matrix3f());
                this.tank.getVehicleControl().clearForces();
            } else {
            }
        } else if (binding.equals("Space") && value) {
            
            makeMissile();
        }       
    }
}
