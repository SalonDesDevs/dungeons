package net.wytrem.jameo.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

import net.wytrem.jameo.utils.MapMask;

public class TiledMapSystem extends BaseSystem {
    @Wire
    AbstractEntityFactorySystem entityFactorySystem;

    @Wire
    MapRenderSystem mapRenderSystem;
    
    @Wire
    Box2dSystem system;

    public TiledMap map;
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;

    private String mapFilename;
    private boolean needsSetup = false;

    public Array<TiledMapTileLayer> layers;

    @Override
    protected void initialize() {
    }

    public void load(String file) {
        if (map != null) {
            map.dispose();
        }
        needsSetup = true;
        mapFilename = file;
        map = new TmxMapLoader().load(mapFilename);
        layers = map.getLayers().getByType(TiledMapTileLayer.class);
        width = map.getProperties().get("width", Integer.class);
        height = map.getProperties().get("height", Integer.class);
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);
    }

    public MapMask getMask(String property) {
        return new MapMask(height, width, tileWidth, tileHeight, layers, property);
    }

    /**
     * Spawn map entities.
     */
    protected void setup() {
        for (TiledMapTileLayer layer : layers) {
            for (int ty = 0; ty < height; ty++) {
                for (int tx = 0; tx < width; tx++) {
                    final TiledMapTileLayer.Cell cell = layer.getCell(tx, ty);
                    if (cell != null) {
                        final MapProperties properties = cell.getTile().getProperties();
                        if (properties.containsKey("entity")) {
                            entityFactorySystem.createEntity((String) properties.get("entity"), tx * tileWidth, ty * tileHeight, properties);
                            layer.setCell(tx, ty, null);
                        }
                        if (properties.containsKey("solid")) {
                            createWall(tx * tileWidth, ty * tileHeight, tileWidth, tileHeight);
                        }
                    }
                }
            }
        }
    }


    protected void createWall(float x, float y, float width, float height) {
        system.createWall(x, y, width, height);
    }

    @Override
    protected void processSystem() {
        if (needsSetup) {
            needsSetup = false;
            mapRenderSystem.setup();
            setup();
        }
    }
}
