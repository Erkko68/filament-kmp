package io.github.erkko68.filament.utils.jni;

import org.jetbrains.annotations.NotNull;

public class Manipulator {

    static { Utils.init(); }

    private static final Mode[] sModeValues = Mode.values();
    private long mNativeObject;

    private Manipulator(long nativeObject) {
        mNativeObject = nativeObject;
    }

    public enum Mode { ORBIT, MAP, FLIGHT }
    public enum Fov { VERTICAL, HORIZONTAL }
    public enum Key { FORWARD, LEFT, BACKWARD, RIGHT, UP, DOWN }

    public static class Builder {
        private final long mNativeBuilder;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
        }

        public Builder viewport(int width, int height) {
            nBuilderViewport(mNativeBuilder, width, height);
            return this;
        }

        public Builder targetPosition(float x, float y, float z) {
            nBuilderTargetPosition(mNativeBuilder, x, y, z);
            return this;
        }

        public Builder upVector(float x, float y, float z) {
            nBuilderUpVector(mNativeBuilder, x, y, z);
            return this;
        }

        public Builder zoomSpeed(float arg) {
            nBuilderZoomSpeed(mNativeBuilder, arg);
            return this;
        }

        public Builder orbitHomePosition(float x, float y, float z) {
            nBuilderOrbitHomePosition(mNativeBuilder, x, y, z);
            return this;
        }

        public Builder orbitSpeed(float x, float y) {
            nBuilderOrbitSpeed(mNativeBuilder, x, y);
            return this;
        }

        public Builder fovDirection(Fov fov) {
            nBuilderFovDirection(mNativeBuilder, fov.ordinal());
            return this;
        }

        public Builder fovDegrees(float arg) {
            nBuilderFovDegrees(mNativeBuilder, arg);
            return this;
        }

        public Builder farPlane(float arg) {
            nBuilderFarPlane(mNativeBuilder, arg);
            return this;
        }

        public Builder mapExtent(float width, float height) {
            nBuilderMapExtent(mNativeBuilder, width, height);
            return this;
        }

        public Builder mapMinDistance(float arg) {
            nBuilderMapMinDistance(mNativeBuilder, arg);
            return this;
        }

        public Builder flightStartPosition(float x, float y, float z) {
            nBuilderFlightStartPosition(mNativeBuilder, x, y, z);
            return this;
        }

        public Builder flightStartOrientation(float pitch, float yaw) {
            nBuilderFlightStartOrientation(mNativeBuilder, pitch, yaw);
            return this;
        }

        public Builder flightMaxMoveSpeed(float maxSpeed) {
            nBuilderFlightMaxMoveSpeed(mNativeBuilder, maxSpeed);
            return this;
        }

        public Builder flightSpeedSteps(int steps) {
            nBuilderFlightSpeedSteps(mNativeBuilder, steps);
            return this;
        }

        public Builder flightPanSpeed(float x, float y) {
            nBuilderFlightPanSpeed(mNativeBuilder, x, y);
            return this;
        }

        public Builder flightMoveDamping(float damping) {
            nBuilderFlightMoveDamping(mNativeBuilder, damping);
            return this;
        }

        public Builder groundPlane(float a, float b, float c, float d) {
            nBuilderGroundPlane(mNativeBuilder, a, b, c, d);
            return this;
        }

        public Builder panning(boolean enabled) {
            nBuilderPanning(mNativeBuilder, enabled);
            return this;
        }

        public Manipulator build(Mode mode) {
            long nativeManipulator = nBuilderBuild(mNativeBuilder, mode.ordinal());
            if (nativeManipulator == 0) throw new IllegalStateException("Couldn't create Manipulator");
            nDestroyBuilder(mNativeBuilder);
            return new Manipulator(nativeManipulator);
        }
    }

    public void destroy() {
        if (mNativeObject != 0) {
            nDestroyManipulator(mNativeObject);
            mNativeObject = 0;
        }
    }

    public Mode getMode() { return sModeValues[nGetMode(mNativeObject)]; }

    public void setViewport(int width, int height) {
        nSetViewport(mNativeObject, width, height);
    }

    public void getLookAt(@NotNull float[] outEye, @NotNull float[] outTarget, @NotNull float[] outUp) {
        nGetLookAt(mNativeObject, outEye, outTarget, outUp);
    }

    public void raycast(int x, int y, @NotNull float[] outResult) {
        nRaycast(mNativeObject, x, y, outResult);
    }

    public void grabBegin(int x, int y, boolean strafe) {
        nGrabBegin(mNativeObject, x, y, strafe);
    }

    public void grabUpdate(int x, int y) {
        nGrabUpdate(mNativeObject, x, y);
    }

    public void grabEnd() {
        nGrabEnd(mNativeObject);
    }

    public void keyDown(Key key) {
        nKeyDown(mNativeObject, key.ordinal());
    }

    public void keyUp(Key key) {
        nKeyUp(mNativeObject, key.ordinal());
    }

    public void scroll(int x, int y, float scrolldelta) {
        nScroll(mNativeObject, x, y, scrolldelta);
    }

    public void update(float deltaTime) {
        nUpdate(mNativeObject, deltaTime);
    }

    public Bookmark getCurrentBookmark() {
        return new Bookmark(nGetCurrentBookmark(mNativeObject));
    }

    public Bookmark getHomeBookmark() {
        return new Bookmark(nGetHomeBookmark(mNativeObject));
    }

    public void jumpToBookmark(Bookmark bookmark) {
        nJumpToBookmark(mNativeObject, bookmark.getNativeObject());
    }

    public long getNativeObject() { return mNativeObject; }

    public static class Bookmark {
        private final long mNativeObject;

        Bookmark(long nativeObject) {
            mNativeObject = nativeObject;
        }

        public long getNativeObject() {
            return mNativeObject;
        }

        public void destroy() {
            nDestroyBookmark(mNativeObject);
        }

        private static native void nDestroyBookmark(long nativeBookmark);
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderViewport(long nativeBuilder, int width, int height);
    private static native void nBuilderTargetPosition(long nativeBuilder, float x, float y, float z);
    private static native void nBuilderUpVector(long nativeBuilder, float x, float y, float z);
    private static native void nBuilderZoomSpeed(long nativeBuilder, float arg);
    private static native void nBuilderOrbitHomePosition(long nativeBuilder, float x, float y, float z);
    private static native void nBuilderOrbitSpeed(long nativeBuilder, float x, float y);
    private static native void nBuilderFovDirection(long nativeBuilder, int arg);
    private static native void nBuilderFovDegrees(long nativeBuilder, float arg);
    private static native void nBuilderFarPlane(long nativeBuilder, float distance);
    private static native void nBuilderMapExtent(long nativeBuilder, float width, float height);
    private static native void nBuilderMapMinDistance(long nativeBuilder, float arg);
    private static native void nBuilderFlightStartPosition(long nativeBuilder, float x, float y, float z);
    private static native void nBuilderFlightStartOrientation(long nativeBuilder, float pitch, float yaw);
    private static native void nBuilderFlightMaxMoveSpeed(long nativeBuilder, float maxSpeed);
    private static native void nBuilderFlightSpeedSteps(long nativeBuilder, int steps);
    private static native void nBuilderFlightPanSpeed(long nativeBuilder, float x, float y);
    private static native void nBuilderFlightMoveDamping(long nativeBuilder, float damping);
    private static native void nBuilderGroundPlane(long nativeBuilder, float a, float b, float c, float d);
    private static native void nBuilderPanning(long nativeBuilder, boolean enabled);
    private static native long nBuilderBuild(long nativeBuilder, int mode);

    private static native void nDestroyManipulator(long nativeManip);
    private static native int nGetMode(long nativeManip);
    private static native void nSetViewport(long nativeManip, int width, int height);
    private static native void nGetLookAt(long nativeManip, float[] eyePosition, float[] targetPosition, float[] upward);
    private static native void nRaycast(long nativeManip, int x, int y, float[] result);
    private static native void nGrabBegin(long nativeManip, int x, int y, boolean strafe);
    private static native void nGrabUpdate(long nativeManip, int x, int y);
    private static native void nGrabEnd(long nativeManip);
    private static native void nKeyDown(long nativeManip, int key);
    private static native void nKeyUp(long nativeManip, int key);
    private static native void nScroll(long nativeManip, int x, int y, float scrolldelta);
    private static native void nUpdate(long nativeManip, float deltaTime);
    private static native long nGetCurrentBookmark(long nativeManip);
    private static native long nGetHomeBookmark(long nativeManip);
    private static native void nJumpToBookmark(long nativeManip, long nativeBookmark);
}
