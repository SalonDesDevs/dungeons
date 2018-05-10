package net.wytrem.jameo.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import net.wytrem.jameo.components.Box2dEntity;
import net.wytrem.jameo.components.Position;

public class PosSyncSystem extends IteratingSystem {

    @Wire
    ComponentMapper<Position> positionComponentMapper;

    @Wire
    ComponentMapper<Box2dEntity> box2dEntityComponentMapper;

    public PosSyncSystem() {
        super(Aspect.all(Position.class, Box2dEntity.class));
    }

    @Override
    protected void process(int entityId) {
        Position pos = positionComponentMapper.get(entityId);
        Box2dEntity collidingEntity = box2dEntityComponentMapper.get(entityId);

        pos.x = (float) collidingEntity.body.getPosition().x;
        pos.y = (float) collidingEntity.body.getPosition().y;
    }
}
