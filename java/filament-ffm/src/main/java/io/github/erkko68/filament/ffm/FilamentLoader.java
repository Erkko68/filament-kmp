package io.github.erkko68.filament.ffm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Extracts the combined {@code libfilament-c} from this module's JAR resources to a temp
 * file and {@link System#load(String) loads} it.
 *
 * <p>The jextract-generated {@link FilamentC} class resolves symbols through
 * {@code SymbolLookup.loaderLookup()}, which finds symbols in native libraries loaded by
 * the classloader of the generated class. Because this loader lives in the same module
 * (hence the same classloader) as the generated bindings, loading the image here makes
 * every {@code Fila*} symbol resolvable. A single loaded image also keeps Filament's
 * process-global singletons (e.g. EntityManager) to one instance.
 */
public final class FilamentLoader {

    private static volatile boolean sLoaded = false;

    private FilamentLoader() {
    }

    /** Idempotently loads {@code libfilament-c}. Safe to call from multiple entry points. */
    public static synchronized void load() {
        if (sLoaded) return;
        try {
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
            } else if (os.contains("nux") || os.contains("nix") || os.contains("aix")) {
                platform = "linux";
                extension = ".so";
            } else {
                throw new UnsupportedOperationException("Unsupported OS: " + os);
            }

            if (arch.contains("aarch64") || arch.contains("arm64")) {
                arch = "arm64";
            } else {
                arch = "x64";
            }

            String libName = "filament-c";
            String libFileName = prefix + libName + extension;
            String resourcePath = "/natives/" + platform + "-" + arch + "/" + libFileName;

            InputStream is = FilamentLoader.class.getResourceAsStream(resourcePath);
            if (is == null) {
                // Dev fallback: allow a manually placed library on java.library.path.
                try {
                    System.loadLibrary(libName);
                    sLoaded = true;
                    return;
                } catch (UnsatisfiedLinkError e) {
                    throw new FileNotFoundException("Native library not found in resources: "
                            + resourcePath + " (and loadLibrary failed: " + e.getMessage() + ")");
                }
            }

            File tempLib = File.createTempFile(prefix + libName + "_", extension);
            tempLib.deleteOnExit();
            Files.copy(is, tempLib.toPath(), StandardCopyOption.REPLACE_EXISTING);
            is.close();

            System.load(tempLib.getAbsolutePath());
            sLoaded = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load native library 'filament-c': " + e.getMessage(), e);
        }
    }
}
