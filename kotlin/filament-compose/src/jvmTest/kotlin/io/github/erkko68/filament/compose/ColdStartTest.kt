package io.github.erkko68.filament.compose

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.SurfaceOrientation
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.filamat.MaterialBuilder
import io.github.erkko68.filament.utils.Manipulator
import io.github.erkko68.filament.utils.Mode
import kotlin.test.Test
import kotlin.test.fail

// Entry points a user can hit before anything calls Filament.init(); each must load libfilament-c itself.
private val coldStarts: Map<String, () -> Unit> = mapOf(
    "Engine.create" to { Engine.create(Engine.Backend.NOOP).destroy() },
    "Engine.Builder" to { Engine.Builder().backend(Engine.Backend.NOOP).build().destroy() },
    "Engine.steadyClockTimeNano" to { Engine.steadyClockTimeNano },
    "EntityManager.get" to { EntityManager.get().create() },
    "Manipulator.Builder" to { Manipulator.Builder().viewport(1, 1).build(Mode.ORBIT).destroy() },
    "SurfaceOrientation.Builder" to {
        SurfaceOrientation.Builder().vertexCount(1).normals(floatArrayOf(0f, 0f, 1f)).build()!!.destroy()
    },
    "IndirectLight statics" to { IndirectLight.getDirectionEstimate(FloatArray(27)) },
    "Texture statics" to {
        Texture.computeTextureDataSize(Texture.Format.RGBA, Texture.Type.UBYTE, 4, 4, 1)
        Texture.validatePixelFormatAndType(Texture.InternalFormat.RGBA8, Texture.Format.RGBA, Texture.Type.UBYTE)
    },
    "MaterialBuilder.init" to {
        MaterialBuilder.init()
        MaterialBuilder().name("ColdStart").shading(MaterialBuilder.Shading.UNLIT).build()
        MaterialBuilder.shutdown()
    },
)

/** Child-JVM entry: runs one [coldStarts] case in a process where nothing has loaded the library yet. */
object ColdStartMain {
    @JvmStatic
    fun main(args: Array<String>) = coldStarts.getValue(args[0])()
}

class ColdStartTest {
    // A loaded native library can't be unloaded, so each case needs its own JVM to start cold.
    @Test
    fun everyEntryPointLoadsTheLibraryItself() {
        val java = ProcessHandle.current().info().command().get()
        val failures = coldStarts.keys.mapNotNull { case ->
            val p = ProcessBuilder(
                java, "--enable-native-access=ALL-UNNAMED",
                "-cp", System.getProperty("java.class.path"), ColdStartMain::class.java.name, case,
            ).redirectErrorStream(true).start()
            val out = p.inputStream.bufferedReader().readText()
            if (p.waitFor() == 0) null else "── $case\n$out"
        }
        if (failures.isNotEmpty()) fail("Entry points that don't load libfilament-c:\n${failures.joinToString("\n")}")
    }
}
