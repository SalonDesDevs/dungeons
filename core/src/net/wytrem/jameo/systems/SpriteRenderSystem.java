package net.wytrem.jameo.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.wytrem.jameo.components.Position;
import net.wytrem.jameo.components.SpriteComponent;

public class SpriteRenderSystem extends IteratingSystem {

    SpriteBatch batch;

    @Wire
    ComponentMapper<SpriteComponent> spriteComponentComponentMapper;

    @Wire
    ComponentMapper<Position> positionComponentMapper;

    @Wire
    CameraSystem cameraSystem;

    public SpriteRenderSystem() {
        super(Aspect.all(Position.class, SpriteComponent.class));
    }

    @Override
    protected void initialize() {
        batch = new SpriteBatch();
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
    }

    @Override
    protected void process(int entityId) {
        SpriteComponent spriteComponent = spriteComponentComponentMapper.get(entityId);
        Position position = positionComponentMapper.get(entityId);
        batch.draw(spriteComponent.sprite, position.x, position.y);
    }

    @Override
    protected void end() {
        batch.end();
    }
}
