package net.wytrem.jameo.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.IntArray;

import net.wytrem.jameo.components.Box2dEntity;
import net.wytrem.jameo.components.Facing;
import net.wytrem.jameo.components.InputControlled;
import net.wytrem.jameo.utils.CardDir;


public class InputSystem extends IteratingSystem implements InputProcessor {

    IntArray keys;

    @Wire
    ComponentMapper<Facing> FacingComponentMapper;

    @Wire
    ComponentMapper<Box2dEntity> box2dEntityComponentMapper;

    public InputSystem() {
        super(Aspect.all(InputControlled.class, Facing.class));
    }

    @Override
    protected void initialize() {
        keys = new IntArray();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    protected void end() {
    }

    @Override
    protected void process(int entityId) {
        float factor = 50.0f;

        Box2dEntity collidingEntity = box2dEntityComponentMapper.get(entityId);
        Body body = collidingEntity.body;
        Vector2 vel = body.getLinearVelocity();
        Vector2 desiredVel = new Vector2();
        
        Facing Facing = FacingComponentMapper.get(entityId);

        if (keys.contains(Input.Keys.S)) {
            desiredVel.y += 1.0f;
            Facing.value = CardDir.NORTH;
        }

        if (keys.contains(Input.Keys.T)) {
            desiredVel.y -= 1.0f;
            Facing.value = CardDir.SOUTH;
        }

        if (keys.contains(Input.Keys.R)) {
            desiredVel.x += 1.0f;
            Facing.value = CardDir.EAST;
        }

        if (keys.contains(Input.Keys.C)) {
            desiredVel.x -= 1.0f; 
            Facing.value = CardDir.WEST;
        }
        
        desiredVel.nor().scl(factor);
        desiredVel.sub(vel);
        desiredVel.scl(body.getMass());

        body.applyLinearImpulse(desiredVel, body.getWorldCenter(), true);
    }

    @Override
    public boolean keyDown(int keycode) {
        keys.add(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.removeValue(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
