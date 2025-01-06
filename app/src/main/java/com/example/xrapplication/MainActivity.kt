package com.example.xrapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
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
import com.example.xrapplication.ui.theme.XRApplicationTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            XRApplicationTheme {
                val session = LocalSession.current
                if (LocalSpatialCapabilities.current.isSpatialUiEnabled) {
                    Subspace {
                        MySpatialContent(onRequestHomeSpaceMode = { session?.requestHomeSpaceMode() })
                    }
                } else {
                    My2DContent(onRequestFullSpaceMode = { session?.requestFullSpaceMode() })
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun MySpatialContent(onRequestHomeSpaceMode: () -> Unit) {
    SpatialRow(curveRadius = 0.dp) {

        SpatialPanel(
            SubspaceModifier
                .width(384.dp)
                .height(592.dp)

        ) {
            Surface {
            MainContent(
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
fun My2DContent(onRequestFullSpaceMode: () -> Unit) {
    Surface {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MainContent(modifier = Modifier.padding(48.dp), "2DContent")
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
fun MainContent(modifier: Modifier = Modifier, title: String) {
    Column {

        Text(text = title, modifier = modifier, color = Color.White)

        Button(onClick = {

        }) {
            Text(text = "MyButton")
        }

        /* SpatialDialog(

             onDismissRequest = {

             }
         ) {
             Box(modifier = Modifier.background(color = Color.Red).padding(20.dp)) {
                 Text(text = "Dialog Text", modifier = modifier)
             }

         }*/
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
    XRApplicationTheme {
        My2DContent(onRequestFullSpaceMode = {})
    }
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