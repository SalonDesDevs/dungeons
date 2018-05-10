package net.wytrem.jameo.components;

import com.artemis.Component;

public class Size extends Component {
    public float width = 16.0f, height = 16.0f;

    public void set(float width, float height) {
        this.width = width;
        this.height = height;
    }
}
