package net.wytrem.jameo.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapRenderSystem extends BaseSystem {

    @Wire
    TiledMapSystem tiledMapSystem;

    TiledMapRenderer renderer;

    @Wire
    CameraSystem cameraSystem;

    public void setup() {
        renderer = new OrthogonalTiledMapRenderer(tiledMapSystem.map, 1.0f);
    }

    @Override
    protected void processSystem() {
        if (renderer != null) {
            renderer.setView(cameraSystem.camera);
            renderer.render();
        }
    }
}
