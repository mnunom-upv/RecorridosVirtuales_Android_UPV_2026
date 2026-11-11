package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * Puente entre Jetpack Compose y OpenGL ES.
 *
 * Compose no reemplaza a GLSurfaceView; lo hospeda con AndroidView.
 * El renderer original CubeRenderer se conserva para no reescribir toda la demo 3D.
 */
@Composable
fun OpenGlSceneView(
    controller: UpvGlSceneController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val renderer = remember { CubeRenderer(context) }
    val glSurfaceView = remember {
        GLSurfaceView(context).apply {
            setEGLContextClientVersion(2)
            preserveEGLContextOnPause = true
            setRenderer(renderer)
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }

    controller.bind(
        queueAction = { action ->
            glSurfaceView.queueEvent {
                action(renderer)
            }
        }
    )

    DisposableEffect(lifecycleOwner, glSurfaceView, renderer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    glSurfaceView.onResume()
                    renderer.startSensors()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    renderer.stopSensors()
                    glSurfaceView.onPause()
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            renderer.stopSensors()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { glSurfaceView }
    )
}
