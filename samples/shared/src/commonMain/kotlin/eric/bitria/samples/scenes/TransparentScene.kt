package eric.bitria.samples.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TransparentScene(onBack: () -> Unit) {
    var transparent by remember { mutableStateOf(true) }

    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 2f, 5f),
        initialTarget     = Position(0f, 0.5f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit = rememberOrbitCameraController(cameraState)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E1E30),
                        Color(0xFF3F2B96),
                        Color(0xFFA8C0FF)
                    )
                )
            )
    ) {
        // Background Compose UI elements to demonstrate transparency
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Compose UI Background Layer",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Text(
                    text = "When transparency is enabled, this Compose card and background gradient are visible behind the 3D model!",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // 3D Filament View overlay
        FilamentSceneView(
            modifier = Modifier
                .fillMaxSize()
                .orbitGestures(orbit),
            cameraState = cameraState,
            transparent = transparent,
        ) {
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = LightIntensity.LuminousPower(100_000f),
            )
            GltfInstance(
                asset    = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") },
                position = Position(0f, 0f, 0f),
                scale    = Scale(1f),
            )
        }

        // Controls overlay
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (transparent) "Transparency Enabled" else "Opaque Mode",
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = transparent,
                    onCheckedChange = { transparent = it }
                )
            }
        }

        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}
