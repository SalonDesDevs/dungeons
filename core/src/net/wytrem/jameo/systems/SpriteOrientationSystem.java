package net.wytrem.jameo.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.wytrem.jameo.components.Facing;
import net.wytrem.jameo.components.SpriteComponent;
import net.wytrem.jameo.utils.CardDir;

public class SpriteOrientationSystem extends IteratingSystem {

    @Wire
    ComponentMapper<Facing> facingComponentComponentMapper;

    @Wire
    ComponentMapper<SpriteComponent> spriteComponentComponentMapper;

    public SpriteOrientationSystem() {
        super(Aspect.all(Facing.class, SpriteComponent.class));
    }

    @Override
    protected void process(int entityId) {
        CardDir facing = facingComponentComponentMapper.get(entityId).value;
        TextureRegion region = spriteComponentComponentMapper.get(entityId).sprite;

        if (facing.equals(CardDir.EAST)) {
            if (region.isFlipX()) {
                region.flip(true, false);
            }
        }
        else if (facing.equals(CardDir.WEST)) {
            if (!region.isFlipX()) {
                region.flip(true, false);
            }
        }
    }
}
