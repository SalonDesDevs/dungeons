package net.wytrem.jameo.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import net.wytrem.jameo.components.Camera;
import net.wytrem.jameo.components.Position;

/**
 * Lock camera center on camera entity.
 *
 * Rotation support.
 *
 * @author Daan van Yperen
 */
public class CameraFollowEntitySystem extends IteratingSystem {

    @Wire
    ComponentMapper<Position> pm;

    @Wire
    CameraSystem cameraSystem;

    public CameraFollowEntitySystem() {
        super(Aspect.all(Position.class, Camera.class));
    }

    @Override
    protected void process(int e) {
        final Position pos = pm.get(e);

        cameraSystem.camera.position.x = pos.x;
        cameraSystem.camera.position.y = pos.y;

        cameraSystem.camera.update();
    }
}
