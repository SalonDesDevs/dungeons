package net.wytrem.jameo.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.wytrem.jameo.components.InputControlled;
import net.wytrem.jameo.components.Position;

public class HudSystem extends IteratingSystem {
    public HudSystem() {
        super(Aspect.all(InputControlled.class, Position.class));
    }

    BitmapFont font;
    SpriteBatch batch;

    @Wire
    ComponentMapper<Position> positionComponentMapper;

    @Wire
    CameraSystem cameraSystem;

    @Override
    protected void initialize() {
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
        batch.begin();
    }

    @Override
    protected void process(int entityId) {
        Position pos = positionComponentMapper.get(entityId);

        font.draw(batch, pos.x + "," + pos.y, 20, 20);
    }

    @Override
    protected void end() {
        batch.end();
    }
}
