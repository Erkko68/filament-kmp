package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.RenderableManager
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.TextureSampler
import io.github.erkko68.filament.VertexBuffer
import io.github.erkko68.filament.compose.FilamentEffect
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.Skybox
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberMaterial
import io.github.erkko68.filament.toBytes
import kotlin.random.Random
import org.jetbrains.compose.resources.ExperimentalResourceApi
import io.github.erkko68.filament.Box as FilamentBox

// Texture width: GLES 3.0 guarantees 2048, far past InstanceBuffer's cap (64, or 8 on web).
private const val PARTICLE_COUNT = 2048
private const val LIFETIME = 2.5f
private const val GRAVITY = -9.8f
private const val BOUNCE = 0.5f
private const val SIZE = 0.07f
private const val RGBA = 4

/** A CPU-simulated spark fountain: [texels] holds the texture rows `particles.mat` reads. */
private class Sparks {
    val texels = FloatArray(PARTICLE_COUNT * 2 * RGBA)
    private val velocities = FloatArray(PARTICLE_COUNT * 3)
    private val positions = FloatArray(PARTICLE_COUNT * 3)
    // Staggered so the fountain is steady from the first frame, not one burst.
    private val ages = FloatArray(PARTICLE_COUNT) { Random.nextFloat() * LIFETIME }

    init {
        for (i in 0 until PARTICLE_COUNT) respawn(i)
    }

    private fun respawn(i: Int) {
        val p = i * 3
        positions.fill(0f, p, p + 3)
        velocities[p] = (Random.nextFloat() - 0.5f) * 3f
        velocities[p + 1] = 4.5f + Random.nextFloat() * 2f
        velocities[p + 2] = (Random.nextFloat() - 0.5f) * 3f
    }

    fun step(dt: Float) {
        for (i in 0 until PARTICLE_COUNT) {
            ages[i] += dt
            if (ages[i] >= LIFETIME) {
                ages[i] -= LIFETIME
                respawn(i)
            }
            val p = i * 3
            velocities[p + 1] += GRAVITY * dt
            positions[p] += velocities[p] * dt
            positions[p + 1] += velocities[p + 1] * dt
            positions[p + 2] += velocities[p + 2] * dt
            if (positions[p + 1] < 0f) {
                positions[p + 1] = 0f
                velocities[p + 1] *= -BOUNCE
            }

            val life = ages[i] / LIFETIME
            val row0 = i * RGBA
            val row1 = (PARTICLE_COUNT + i) * RGBA
            texels[row0] = positions[p]
            texels[row0 + 1] = positions[p + 1]
            texels[row0 + 2] = positions[p + 2]
            texels[row0 + 3] = SIZE
            texels[row1] = 1f
            texels[row1 + 1] = 0.75f * (1f - life)
            texels[row1 + 2] = 0.3f * (1f - life) * (1f - life)
            texels[row1 + 3] = 1f - life
        }
    }
}

/** One instanced quad, drawn [PARTICLE_COUNT] times; a stateless effect could drop [Sparks] and the texture. */
@Composable
private fun FilamentSceneScope.Fountain(material: Material) {
    FilamentEffect(material) {
        val instance = material.createInstance()
        val quad = VertexBuffer.Builder().vertexCount(6).bufferCount(0).build(engine)
        val texture = Texture.Builder()
            .width(PARTICLE_COUNT)
            .height(2)
            .levels(1)
            .format(Texture.InternalFormat.RGBA32F)
            .build(engine)
        instance.setParameter("particles", texture, TextureSampler())

        val entity = engine.entityManager.create()
        RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, quad)
            .material(0, instance)
            .instances(PARTICLE_COUNT)
            .boundingBox(FilamentBox(floatArrayOf(0f, 2f, 0f), floatArrayOf(5f, 2.5f, 5f)))
            .build(engine, entity)
        scene.addEntity(entity)

        val sparks = Sparks()
        onFrame { frame ->
            sparks.step(frame.deltaSeconds)
            val bytes = sparks.texels.toBytes()
            texture.setImage(engine, 0, Texture.PixelBufferDescriptor(bytes, bytes.size, Texture.Format.RGBA, Texture.Type.FLOAT))
        }
        onDispose {
            scene.remove(entity)
            engine.renderableManager.destroy(entity)
            engine.entityManager.destroy(entity)
            engine.destroy(instance)
            engine.destroy(texture)
            engine.destroy(quad)
        }
    }
}

/**
 * A particle fountain built from instancing and a custom material: Filament has no particle system.
 * Thousands of sparks are simulated on the CPU and uploaded as a texture each frame.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun ParticlesScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 2.5f, 7f),
        initialTarget     = Position(0f, 1f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit = rememberOrbitCameraController(cameraState)

    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            modifier = Modifier.fillMaxSize().orbitGestures(orbit),
            cameraState = cameraState,
        ) {
            Skybox(color = LinearColor(0.01f, 0.01f, 0.02f))
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = LightIntensity.LuminousPower(30_000f),
            )
            Plane(
                material = rememberColorMaterialInstance(LinearColor(0.1f, 0.1f, 0.12f)),
                position = Position(0f, -0.01f, 0f),
                width = 12f,
                depth = 12f,
            )
            rememberMaterial { Res.readBytes("files/materials/particles.filamat") }?.let { Fountain(it) }
        }
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}
