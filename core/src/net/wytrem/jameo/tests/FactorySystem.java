package net.wytrem.jameo.tests;

import com.artemis.Entity;
import com.badlogic.gdx.maps.MapProperties;
import net.wytrem.jameo.systems.AbstractEntityFactorySystem;

public class FactorySystem extends AbstractEntityFactorySystem {
    @Override
    public Entity createEntity(String entity, int cx, int cy, MapProperties properties) {
        return world.createEntity();
    }
}
