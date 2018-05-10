package net.wytrem.jameo.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import net.wytrem.jameo.components.Box2dEntity;
import net.wytrem.jameo.components.Position;
import net.wytrem.jameo.components.Size;

public class Box2dSystem extends BaseEntitySystem {

    @Wire
    ComponentMapper<Box2dEntity> box2dEntityComponentMapper;

    @Wire
    ComponentMapper<Position> positionComponentMapper;

    @Wire
    ComponentMapper<Size> sizeComponentMapper;

    public Box2dSystem() {
        super(Aspect.all(Box2dEntity.class, Position.class, Size.class));
    }

    World world;

    @Override
    protected void initialize() {
        world = new World(new Vector2(), true);
    }

    @Override
    protected void processSystem() {
        world.step(super.world.delta, 6, 2);
        world.setContactListener(new ContactListener() {

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                // TODO Auto-generated method stub
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                // TODO Auto-generated method stub
            }

            @Override
            public void endContact(Contact contact) {
                System.out.println(contact.toString());
            }

            @Override
            public void beginContact(Contact contact) {
                System.out.println(contact.toString());
            }
        });
    }

    @Override
    protected void inserted(int entityId) {
        Box2dEntity component = box2dEntityComponentMapper.get(entityId);
        Position pos = positionComponentMapper.get(entityId);
        Size size = sizeComponentMapper.get(entityId);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((pos.x + size.width / 2), (pos.y + size.height / 2));

        component.body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.width / 2, size.height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;

        component.body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void createWall(float x, float y, float width, float height) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(x, y);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);
        Body body = world.createBody(bd);
        body.createFixture(shape, 1);

        shape.dispose();
    }

}
