package com.example.xrapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition.Center.position
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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntSize
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
import androidx.xr.compose.subspace.Volume
import androidx.xr.compose.subspace.layout.SpatialRoundedCornerShape
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.fillMaxWidth
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.compose.subspace.layout.onGloballyPositioned
import androidx.xr.compose.subspace.layout.padding
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.scale
import androidx.xr.compose.subspace.layout.width
import androidx.xr.compose.unit.IntVolumeSize
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.AnchorPlacement
import androidx.xr.scenecore.Dimensions
import androidx.xr.scenecore.Entity
import androidx.xr.scenecore.InputEvent
import androidx.xr.scenecore.JxrPlatformAdapter.SpatialCapabilities
import androidx.xr.scenecore.OnSpaceUpdatedListener
import androidx.xr.scenecore.PermissionHelper
import androidx.xr.scenecore.PlaneSemantic
import androidx.xr.scenecore.PlaneType
import androidx.xr.scenecore.ResizeListener
import androidx.xr.scenecore.Session
import androidx.xr.scenecore.SpatialEnvironment
import androidx.xr.scenecore.SpatialEnvironment.SpatialEnvironmentPreference
import com.example.xrapplication.ui.theme.XRApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

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
    var panelSize: IntSize by remember { mutableStateOf(IntSize.Zero) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
    }

    SpatialRow(curveRadius = 0.dp) {

        SpatialPanel(
            SubspaceModifier
                .width(384.dp)
                .height(592.dp)
                .resizable().movable()
               /* .onGloballyPositioned { coordinates ->
                    //  panelSize = coordinates.size
                    Log.d(
                        "SpatialPanel",
                        "Width: ${coordinates.size.width}, Height: ${coordinates.size.height}"
                    )
                }*/
        ) {
            Surface {
                LeftContent(
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
            //    MyNavigationRail()
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

                        // ask storage permission
                        launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

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
                .resizable().movable()
        ) {
            Surface {
                RightContent(
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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
      //  permissionGranted.value = isGranted
    }
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
    var context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column  {

        Box(modifier = Modifier
            .height(100.dp)
            .width(100.dp)) {

            Text(text = title, modifier = modifier, color = Color.White)
        }

        Button(onClick = {

            Log.d("MainActivity", "1")
            coroutineScope.launch {
                try {
                    val model = withContext(Dispatchers.Main) {
                        Log.d("MainActivity", "2")
                        session.createGltfResourceAsync("models/military_combat_specialist_character_model.glb")
                    }
                    val exir = withContext(Dispatchers.Main) {
                        Log.d("MainActivity", "22")
                      session.createExrImageResource("models/MtTamWest.exr")
                    }
                    session.spatialEnvironment.addOnSpatialEnvironmentChangedListener {
                        Log.d("MainActivity", "onSpatialEnvironmentChanged")
                    }

                    Log.d("MainActivity", "3")
                    Log.d("MainActivity", model.get().toString())
                        val gltfEntity = session.createGltfEntity(model.get())
                    val spatialEnvironmentPreference = SpatialEnvironmentPreference(exir, model.get())

                    val preferenceResult = session.spatialEnvironment.setSpatialEnvironmentPreference(spatialEnvironmentPreference)



                    //     val newPosition = Vector3(0f, 0f, -2f)
                    //    val newOrientation = Quaternion.fromEulerAngles(0f, 0f, 180f)
                        gltfEntity.setHidden(false)
                      //  gltfEntity.setScale(2f)
                     //   gltfEntity.setPose(Pose(newPosition, newOrientation))
                        gltfEntity.addChild(gltfEntity)
                        Log.d("MainActivity", "4")
                        Log.d("MainActivity", gltfEntity.isHidden().toString())
                        Log.d("MainActivity", "getWorldSpaceScale")
                        Log.d("MainActivity", gltfEntity.getWorldSpaceScale().toString())

                      /*  val anchorPlacement = AnchorPlacement.createForPlanes(
                            planeTypeFilter = setOf(PlaneSemantic.FLOOR, PlaneSemantic.TABLE),
                            planeSemanticFilter = setOf(PlaneType.VERTICAL)
                        )

                        val movableComponent = session.createMovableComponent(
                            systemMovable = false,
                            scaleInZ = false,
                            anchorPlacement = setOf(anchorPlacement)
                        )
                        gltfEntity.addComponent(movableComponent)
                        val resizableComponent = session.createResizableComponent()
                        resizableComponent.minimumSize = Dimensions(177f, 100f, 1f)
                        resizableComponent.fixedAspectRatio = 16f / 9f //Specify a 16:9 aspect ratio
                        gltfEntity.addComponent(resizableComponent)
*/

                        val executor by lazy { Executors.newSingleThreadExecutor() }
                        val interactableComponent = session.createInteractableComponent(executor) {
                            //when the user disengages with the entity with their hands
                            if (it.source == InputEvent.SOURCE_HANDS && it.action == InputEvent.ACTION_UP) {
                                // increase size with right hand and decrease with left
                                if (it.pointerType == InputEvent.POINTER_TYPE_RIGHT) {
                                    gltfEntity.setScale(1.5f)
                                } else if (it.pointerType == InputEvent.POINTER_TYPE_LEFT) {
                                    gltfEntity.setScale(0.5f)
                                }
                            }
                        }
                        gltfEntity.addComponent(interactableComponent)



                } catch (e: Exception) {
                    Log.e("MainContent", "Error loading GLTF model", e)
                }
            }

        }) {
            Text(text = "ActivitySpace")
        }
        Button(onClick = {
            Log.d("MainActivity", "1")

            val THREED_MODEL_URL =
                "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/FlightHelmet/glTF/FlightHelmet.gltf"
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


        Button(onClick = {
            Log.d("MainActivity", "1")

            val preferenceResult = session.spatialEnvironment.setPassthroughOpacityPreference(0.8f)

            if (preferenceResult == SpatialEnvironment.SetPassthroughOpacityPreferenceChangeApplied()) {
                Log.d("MainActivity", "SetPassthroughOpacityPreferenceChangeApplied")

            } else if (preferenceResult == SpatialEnvironment.SetPassthroughOpacityPreferenceChangePending()) {
                Log.d("MainActivity", "SetPassthroughOpacityPreferenceChangePending")

            }

        }) {
            Text(text = "Passthrough")
        }
        Button(onClick = {
            Log.d("MainActivity", "1")
            val activity = context as? Activity
            PermissionHelper.requestPermission(activity!!,PermissionHelper.SCENE_UNDERSTANDING_PERMISSION, 0)        }) {
            Text(text = "Ask Permission")
        }

      //  ObjectInAVolume(session, true)

    }

}

@Composable
fun RightContent(session: Session, modifier: Modifier = Modifier, title: String) {
    var context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column() {

        Box(modifier = Modifier
            .height(100.dp)
            .width(100.dp)) {

            Text(text = title, modifier = modifier, color = Color.White)
        }

        Button(onClick = {

            Log.d("MainActivity", "1")


        }) {
            Text(text = "MyButton1")
        }




    }

}
@Composable
fun LeftContent(session: Session, modifier: Modifier = Modifier, title: String) {
    var context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column() {

        Box(modifier = Modifier
            .height(100.dp)
            .width(100.dp)) {

            Text(text = title, modifier = modifier, color = Color.White)
        }

        Button(onClick = {

            Log.d("MainActivity", "1")


        }) {
            Text(text = "MyButton1")
        }




    }

}

@Composable
fun ObjectInAVolume(session: Session, show3DObject: Boolean) {
    val xrCoreSession = checkNotNull(LocalSession.current)
    val scope = rememberCoroutineScope()

    Subspace {
        Volume(
            modifier = SubspaceModifier
                .offset(100.dp, 100.dp, 100.dp)
                .scale(1.2f) // Scale to 120% of the size

        ) { parent ->
            scope.launch {
                try {

                    Log.d("MainActivity", "2")
                    xrCoreSession.createGltfResourceAsync("models/glTF/FlightHelmet.gltf").let {
                        val model = it.get();
                        Log.d("MainActivity", "3")
                        val gltfEntity = xrCoreSession.createGltfEntity(model)
                        val newPosition = Vector3(0f, 0f, -2f)
                        val newOrientation = Quaternion.fromEulerAngles(0f, 0f, 180f)
                        gltfEntity.setHidden(false)
                        gltfEntity.setScale(2f)
                        gltfEntity.setPose(Pose(newPosition, newOrientation))
                        gltfEntity.addChild(gltfEntity)
                        Log.d("MainActivity", "4")
                    }


                } catch (e: Exception) {
                    Log.e("MainContent", "Error loading GLTF model", e)
                }
            }
        }
    }

}


@Composable
fun MyNavigationRail() {
    var selectedItem by remember { mutableStateOf(0) }

    NavigationRail {
        NavigationRailItem(
            icon = { Icon(ImageVector.vectorResource(id = android.R.drawable.ic_delete), contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedItem == 0,
            onClick = { selectedItem = 0 }
        )
        NavigationRailItem(
            icon = { Icon(ImageVector.vectorResource(id =  android.R.drawable.ic_menu_search), contentDescription = "Search") },
            label = { Text("Search") },
            selected = selectedItem == 1,
            onClick = { selectedItem = 1 }
        )
        NavigationRailItem(
            icon = { Icon(ImageVector.vectorResource(id =  android.R.drawable.sym_action_email), contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = selectedItem == 2,
            onClick = { selectedItem = 2 }
        )
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