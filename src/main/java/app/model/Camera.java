package app.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import suite.suite.Subject;
import suite.suite.Suite;

public class Camera {

    public enum CameraMovement {
        FORWARD, BACKWARD, LEFT, RIGHT
    }

    public static final Object MOVEMENT_SPEED = new Object(), MOUSE_SENSITIVITY = new Object(), ZOOM = new Object();

    private static final float DEFAULT_YAW = -90f;
    private static final float DEFAULT_PITCH = 0f;
    private static final float DEFAULT_SPEED = 2.5f;
    private static final float DEFAULT_SENSITIVITY = 0.1f;
    private static final float DEFAULT_ZOOM = 45f;

    Vector3f position;
    Vector3f front;
    Vector3f up;
    Vector3f right;
    Vector3f worldUp;
    float yaw;
    float pitch;
    float movementSpeed;
    float mouseSensitivity;
    float zoom;

    public static Camera form(Subject sub) {
        float posX = Suite.from(sub).get("posX").or("pos", s -> s.asGiven(Subject.class).at(0)).orGiven(0f);
        float posY = Suite.from(sub).get("posY").or("pos", s -> s.asGiven(Subject.class).at(1)).orGiven(0f);
        float posZ = Suite.from(sub).get("posZ").or("pos", s -> s.asGiven(Subject.class).at(2)).orGiven(0f);
        float upX = sub.get("upX").orGiven(0f);
        float upY = sub.get("upY").orGiven(1f);
        float upZ = sub.get("upZ").orGiven(0f);
        float yaw = sub.get("yaw").orGiven(DEFAULT_YAW);
        float pitch = sub.get("pitch").orGiven(DEFAULT_PITCH);
        float movementSpeed = Suite.from(sub).get(MOVEMENT_SPEED).or("speed").orGiven(DEFAULT_SPEED);
        float mouseSensitivity = Suite.from(sub).get(MOUSE_SENSITIVITY).or("mouseSensitivity").orGiven(DEFAULT_SENSITIVITY);
        float zoom = Suite.from(sub).get(ZOOM).or("zoom").orGiven(DEFAULT_ZOOM);
        return new Camera(posX, posY, posZ, upX, upY, upZ, yaw, pitch, movementSpeed, mouseSensitivity, zoom);
    }

    public Camera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch,
           float movementSpeed, float mouseSensitivity, float zoom) {
        front = new Vector3f(0f, 0f, -1f);
        position = new Vector3f(posX, posY, posZ);
        worldUp = new Vector3f(upX, upY, upZ);
        this.yaw = yaw;
        this.pitch = pitch;
        this.movementSpeed = movementSpeed;
        this.mouseSensitivity = mouseSensitivity;
        this.zoom = zoom;
        updateCameraVectors();
    }

    public Matrix4f getView() {
        return new Matrix4f().lookAt(position, new Vector3f(position).add(front), up);
    }

    public void processKeyboard(CameraMovement direction, float deltaTime)
    {
        float velocity = movementSpeed * deltaTime;
        if (direction == CameraMovement.FORWARD)
            position.add(new Vector3f(front).mul(velocity));
        if (direction == CameraMovement.BACKWARD)
            position.sub(new Vector3f(front).mul(velocity));
        if (direction == CameraMovement.LEFT)
            position.sub(new Vector3f(right).mul(velocity));
        if (direction == CameraMovement.RIGHT)
            position.add(new Vector3f(right).mul(velocity));
    }

    public void processMouseMovement(float xoffset, float yoffset, boolean constrainPitch)
    {
        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;

        yaw += xoffset;
        pitch += yoffset;

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch)
        {
            if (pitch > 89.0f)
                pitch = 89.0f;
            if (pitch < -89.0f)
                pitch = -89.0f;
        }

        // update Front, Right and Up Vectors using the updated Euler angles
        updateCameraVectors();
    }

    public void processMouseScroll(float yoffset)
    {
        zoom -= yoffset * 0.1f;
        if (zoom < 1.0f)
            zoom = 1.0f;
        if (zoom > 45.0f)
            zoom = 45.0f;
    }

    private void updateCameraVectors()
    {
        Vector3f direction = new Vector3f();
        direction.x = (float)(Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        direction.y = (float)(Math.sin(Math.toRadians(pitch)));
        direction.z = (float)(Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front = direction.normalize();
        right = new Vector3f(front).cross(worldUp).normalize();
        up = new Vector3f(right).cross(front).normalize();
    }

    public float getZoom() {
        return zoom;
    }
}
