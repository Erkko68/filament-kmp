package io.github.erkko68.filament.jni.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class NativeLoader {
    private NativeLoader() {
    }

    public static void load(String libName) {
        try {
            // 1. Detect platform
            String os = System.getProperty("os.name").toLowerCase();
            String arch = System.getProperty("os.arch").toLowerCase();

            String platform;
            String extension;
            String prefix = "lib";

            if (os.contains("win")) {
                platform = "windows";
                extension = ".dll";
                prefix = "";
            } else if (os.contains("mac") || os.contains("darwin")) {
                platform = "macos";
                extension = ".dylib";
            } else {
                throw new UnsupportedOperationException("Unsupported OS: " + os);
            }

            if (arch.contains("aarch64") || arch.contains("arm64")) {
                arch = "arm64";
            } else {
                arch = "x64";
            }

            // 2. Resolve resource path
            String libFileName = prefix + libName + extension;
            String resourcePath = "/natives/" + platform + "-" + arch + "/" + libFileName;

            // 3. Try to load from resources
            InputStream is = NativeLoader.class.getResourceAsStream(resourcePath);
            if (is == null) {
                // Fallback to System.loadLibrary if resource not found (allows manual dev setup)
                try {
                    System.loadLibrary(libName);
                    return;
                } catch (UnsatisfiedLinkError e) {
                    throw new FileNotFoundException("Native library not found in resources: " + resourcePath + " (and loadLibrary failed: " + e.getMessage() + ")");
                }
            }

            // 4. Extract to temporary file
            File tempLib = File.createTempFile(prefix + libName + "_", extension);
            tempLib.deleteOnExit();

            Files.copy(is, tempLib.toPath(), StandardCopyOption.REPLACE_EXISTING);
            is.close();

            // 5. Load
            System.load(tempLib.getAbsolutePath());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to load native library '" + libName + "': " + e.getMessage(), e);
        }
    }
}
