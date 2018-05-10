package net.wytrem.jameo.tests;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.wytrem.jameo.components.Box2dEntity;
import net.wytrem.jameo.components.Camera;
import net.wytrem.jameo.components.Facing;
import net.wytrem.jameo.components.InputControlled;
import net.wytrem.jameo.components.Motion;
import net.wytrem.jameo.components.Position;
import net.wytrem.jameo.components.Size;
import net.wytrem.jameo.components.SpriteComponent;
import net.wytrem.jameo.systems.Box2dSystem;
import net.wytrem.jameo.systems.CameraFollowEntitySystem;
import net.wytrem.jameo.systems.CameraSystem;
import net.wytrem.jameo.systems.ClearScreenSystem;
import net.wytrem.jameo.systems.HudSystem;
import net.wytrem.jameo.systems.InputSystem;
import net.wytrem.jameo.systems.MapRenderSystem;
import net.wytrem.jameo.systems.PosSyncSystem;
import net.wytrem.jameo.systems.SpriteOrientationSystem;
import net.wytrem.jameo.systems.SpriteRenderSystem;
import net.wytrem.jameo.systems.TiledMapSystem;

public class Dungeons extends ApplicationAdapter {

    World world;


    @Override
    public void create() {
        WorldConfigurationBuilder configurationBuilder = new WorldConfigurationBuilder();

        WorldConfiguration configuration = configurationBuilder.build();

        configuration.setSystem(FactorySystem.class);
        configuration.setSystem(TiledMapSystem.class);

        configuration.setSystem(InputSystem.class);
        configuration.setSystem(new CameraSystem(2f));
        configuration.setSystem(CameraFollowEntitySystem.class);

        configuration.setSystem(Box2dSystem.class);
        configuration.setSystem(PosSyncSystem.class);

        configuration.setSystem(ClearScreenSystem.class);
        configuration.setSystem(MapRenderSystem.class);
        configuration.setSystem(SpriteOrientationSystem.class);
        configuration.setSystem(SpriteRenderSystem.class);
        configuration.setSystem(HudSystem.class);
        world = new World(configuration);

        Archetype playerArchetype = new ArchetypeBuilder().add(SpriteComponent.class)
                .add(Position.class).add(Size.class).add(Motion.class).add(Camera.class).add(InputControlled.class)
                .add(Facing.class).add(Box2dEntity.class).build(world);

        Entity player = world.createEntity(playerArchetype);

        Texture texture = new Texture(Gdx.files.internal("dungeon_tileset.png"));
        SpriteComponent spriteComponent = player.getComponent(SpriteComponent.class);
        spriteComponent.sprite = new TextureRegion(texture, 9 * 16, 14 * 16, 16, 16);
        
        player.getComponent(Size.class).set(16, 16);
    }


    @Override
    public void resize(int width, int height) {
        world.getSystem(CameraSystem.class).resized();
    }

    boolean loaded = false;

    @Override
    public void render() {
        world.delta = Gdx.graphics.getDeltaTime();
        world.process();
        if (!loaded) {
            loaded = true;
            world.getSystem(TiledMapSystem.class).load("base.tmx");
        }
    }

    @Override
    public void dispose() {
        world.dispose();
    }
}
