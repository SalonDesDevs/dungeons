package net.wytrem.jameo.components;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class Box2dEntity extends Component {
    public Body body;

    public void setLinearVelocity(float vX, float vY) {
        body.setLinearVelocity(vX, vY);
    }

    public void applyForceToCenter(float forceX, float forceY, boolean wake) {
        body.applyForceToCenter(forceX, forceY, wake);
    }
}
