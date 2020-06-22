package app.model;

public abstract class GLObject {
    protected final int glid;

    protected GLObject(int glid) {
        this.glid = glid;
    }

    public int getGlid() {
        return glid;
    }
}
