package com.example.xrapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.xr.compose.platform.LocalHasXrSpatialFeature
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.spatial.EdgeOffset
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.OrbiterEdge
import androidx.xr.compose.spatial.SpatialDialog
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.SpatialRow
import androidx.xr.compose.subspace.layout.SpatialRoundedCornerShape
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.fillMaxWidth
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.padding
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.width
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.Session
import androidx.xr.scenecore.SpatialEnvironment
import com.example.xrapplication.ui.theme.XRApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var spatializedSession = Session.create(this)

        setContent {
            XRApplicationTheme {
                val session = LocalSession.current
                Log.d(
                    "MainActivity",
                    "Spatial UI enabled: ${LocalSpatialCapabilities.current.isSpatialUiEnabled}"
                )
                Log.d(
                    "MainActivity",
                    "isAppEnvironmentEnabled enabled: ${LocalSpatialCapabilities.current.isAppEnvironmentEnabled}"
                )
                Log.d(
                    "MainActivity",
                    "isContent3dEnabled enabled: ${LocalSpatialCapabilities.current.isContent3dEnabled}"
                )
                Log.d(
                    "MainActivity",
                    "isPassthroughControlEnabled enabled: ${LocalSpatialCapabilities.current.isPassthroughControlEnabled}"
                )
                Log.d(
                    "MainActivity",
                    "isSpatialAudioEnabled enabled: ${LocalSpatialCapabilities.current.isSpatialAudioEnabled}"
                )


                if (LocalSpatialCapabilities.current.isSpatialUiEnabled) {
                    Subspace {
                        MySpatialContent(
                            spatializedSession,
                            onRequestHomeSpaceMode = { session?.requestHomeSpaceMode() })
                    }
                } else {
                    My2DContent(
                        spatializedSession,
                        onRequestFullSpaceMode = { session?.requestFullSpaceMode() })
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun MySpatialContent(session: Session, onRequestHomeSpaceMode: () -> Unit) {
    SpatialRow(curveRadius = 0.dp) {

        SpatialPanel(
            SubspaceModifier
                .width(384.dp)
                .height(592.dp)

        ) {
            Surface {
                MainContent(
                    session = session,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp),
                    "Left Panel"
                )
            }

        }


        SpatialPanel(SubspaceModifier.width(1280.dp).height(800.dp).resizable().movable())
        {
            Surface {
                MainContent(
                    session = session,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp),
                    "Center Panel"
                )
            }
            Orbiter(
                position = OrbiterEdge.Top,
                offset = EdgeOffset.inner(offset = 20.dp),
                alignment = Alignment.End,
                shape = SpatialRoundedCornerShape(CornerSize(28.dp))
            ) {
                HomeSpaceModeIconButton(
                    onClick = onRequestHomeSpaceMode,
                    modifier = Modifier.size(56.dp)
                )
            }

            Orbiter(
                position = OrbiterEdge.Top,
                offset = EdgeOffset.inner(offset = 20.dp),
                alignment = Alignment.CenterHorizontally,
                shape = SpatialRoundedCornerShape(CornerSize(28.dp))
            ) {

                Row {
                    Button(onClick = {

                    }) {
                        Text(text = "Action 1")
                    }

                    Button(onClick = {

                    }) {
                        Text(text = "Action 2")
                    }

                }
            }

        }



        SpatialPanel(
            SubspaceModifier
                .width(384.dp)
                .height(592.dp)

        ) {
            Surface {
                MainContent(
                    session = session,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp),
                    "Right Panel"
                )

            }

        }


    }
}

@SuppressLint("RestrictedApi")
@Composable
fun My2DContent(session: Session, onRequestFullSpaceMode: () -> Unit) {
    Surface {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MainContent(session = session, modifier = Modifier.padding(48.dp), "2DContent")
            if (LocalHasXrSpatialFeature.current) {
                FullSpaceModeIconButton(
                    onClick = onRequestFullSpaceMode,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }
    }
}

@Composable
fun MainContent(session: Session, modifier: Modifier = Modifier, title: String) {
    var context= LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column() {

        Box(modifier = Modifier.height(100.dp).width(100.dp)) {

        Text(text = title, modifier = modifier, color = Color.White)
        }

        Button(onClick = {

            Log.d("MainActivity","1")
            coroutineScope.launch {
                try {
                    val model = withContext(Dispatchers.Main) {
                        Log.d("MainActivity","2")
                        session.createGltfResourceAsync("models/lieutenantHead/lieutenantHead.gltf")
                    }
                    Log.d("MainActivity","3")
                    val gltfEntity = session.createGltfEntity(model.get())
                    val newOrientation = Quaternion.fromEulerAngles(0f, 0f, 180f)
                    gltfEntity.setHidden(false)
                    gltfEntity.setScale(2f)
                    gltfEntity.addChild(gltfEntity)
                    Log.d("MainActivity","4")
                } catch (e: Exception) {
                    Log.e("MainContent", "Error loading GLTF model", e)
                }
            }

        }) {
            Text(text = "MyButton1")
        }
        Button(onClick = {

            val THREED_MODEL_URL = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/FlightHelmet/glTF/FlightHelmet.gltf"
            val MIME_TYPE = "model/gltf-binary"
            val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
            val intentUri =
                Uri.parse("https://arvr.google.com/scene-viewer/1.2")
                    .buildUpon()
                    .appendQueryParameter("file", THREED_MODEL_URL)
                    .build()
            sceneViewerIntent.setDataAndType(intentUri, MIME_TYPE)
            context.startActivity(sceneViewerIntent)

        }) {
            Text(text = "Scene Viewer")
        }


    }

}

@Composable
fun FullSpaceModeIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_full_space_mode_switch),
            contentDescription = stringResource(R.string.switch_to_full_space_mode)
        )
    }
}

@Composable
fun HomeSpaceModeIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalIconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_home_space_mode_switch),
            contentDescription = stringResource(R.string.switch_to_home_space_mode)
        )
    }
}

@PreviewLightDark
@Composable
fun My2dContentPreview() {
   /* XRApplicationTheme {
        My2DContent(session = Session, onRequestFullSpaceMode = {})
    }*/
}

@Preview(showBackground = true)
@Composable
fun FullSpaceModeButtonPreview() {
    XRApplicationTheme {
        FullSpaceModeIconButton(onClick = {})
    }
}

@PreviewLightDark
@Composable
fun HomeSpaceModeButtonPreview() {
    XRApplicationTheme {
        HomeSpaceModeIconButton(onClick = {})
    }
}