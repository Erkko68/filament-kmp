package io.github.erkko68.filament.utils;

import io.github.erkko68.filament.Engine;
import io.github.erkko68.filament.Texture;

public class IBLPrefilterContext {
    private long mNativeObject;

    public IBLPrefilterContext(Engine engine) {
        mNativeObject = nCreate(engine.getNativeObject());
        if (mNativeObject == 0) throw new IllegalStateException("Couldn't create IBLPrefilterContext");
    }

    public void destroy() {
        if (mNativeObject != 0) {
            nDestroy(mNativeObject);
            mNativeObject = 0;
        }
    }

    public static class EquirectangularToCubemap {
        private long mNativeObject;

        public EquirectangularToCubemap(IBLPrefilterContext context) {
            mNativeObject = nCreateEquirectHelper(context.getNativeObject());
        }

        public Texture run(Texture equirect) {
            long nativeTexture = nEquirectHelperRun(getNativeObject(), equirect.getNativeObject());
            return new Texture(nativeTexture);
        }

        public void destroy() {
            if (mNativeObject != 0) {
                nDestroyEquirectHelper(mNativeObject);
                mNativeObject = 0;
            }
        }

        protected long getNativeObject() {
            if (mNativeObject == 0) {
                throw new IllegalStateException("Calling method on destroyed EquirectangularToCubemap");
            }
            return mNativeObject;
        }
    }

    public static class SpecularFilter {
        private long mNativeObject;

        public SpecularFilter(IBLPrefilterContext context) {
            mNativeObject = nCreateSpecularFilter(context.getNativeObject());
        }

        public Texture run(Texture skybox) {
            long nativeTexture = nSpecularFilterRun(getNativeObject(), skybox.getNativeObject());
            return new Texture(nativeTexture);
        }

        public void destroy() {
            if (mNativeObject != 0) {
                nDestroySpecularFilter(mNativeObject);
                mNativeObject = 0;
            }
        }

        protected long getNativeObject() {
            if (mNativeObject == 0) {
                throw new IllegalStateException("Calling method on destroyed SpecularFilter");
            }
            return mNativeObject;
        }
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed IBLPrefilterContext");
        }
        return mNativeObject;
    }

    private static native long nCreate(long nativeEngine);
    private static native void nDestroy(long nativeObject);

    private static native long nCreateEquirectHelper(long nativeContext);
    private static native long nEquirectHelperRun(long nativeHelper, long nativeEquirect);
    private static native void nDestroyEquirectHelper(long nativeObject);

    private static native long nCreateSpecularFilter(long nativeContext);
    private static native long nSpecularFilterRun(long nativeHelper, long nativeSkybox);
    private static native void nDestroySpecularFilter(long nativeObject);
}
