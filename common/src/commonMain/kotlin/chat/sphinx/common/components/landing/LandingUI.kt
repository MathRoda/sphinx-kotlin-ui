package chat.sphinx.common.components.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.platform.imageResource
import CommonButton
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity

import kotlinx.coroutines.delay

@Composable
fun LandingUI() {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
//            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.secondary)
        ) { LeftPortion() }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ){ RightPortion()}
    }
}

@Composable
fun LeftPortion() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = "test", block ={
        delay(10)
        visible=true
    } )
    val density = LocalDensity.current
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(visible = visible,
            enter = slideInVertically {
                // Slide in from 40 dp from the top.
                with(density) { 10.dp.roundToPx() }
            } + fadeIn(
                // Fade in with the initial alpha of 0.3f.
                initialAlpha = 0.3f
            ),) {
            Image(
                painter = imageResource(Res.drawable.sphinx_logo),
                contentDescription = "Sphinx landing page graphic",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.paddingFromBaseline(top = 53.dp)
            )
        }
        AnimatedVisibility(visible = visible, enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { 20.dp.roundToPx() }
        } + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        )) {
            Image(
                painter = imageResource(Res.drawable.LANDING_WORD_MARK),
                contentDescription = "Sphinx landing page graphic",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.paddingFromBaseline(top = 16.dp)
//                    modifier = Modifier.height(150.dp).width(150.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(visible = visible, enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { 30.dp.roundToPx() }
        } + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        )) {
            Text(
                text = "LIGHTNING CHAT",
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.fillMaxWidth())
    }
    Box(

        modifier = Modifier
//            .fillMaxWidth()
            .paddingFromBaseline(bottom = 50.dp), contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.End) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically {
                    // Slide in from 40 dp from the top.
                    with(density) { 40.dp.roundToPx() }
                } + fadeIn(
                    // Fade in with the initial alpha of 0.3f.
                    initialAlpha = 0.3f
                ),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = imageResource(Res.drawable.landing_page_image),
                    contentDescription = "Sphinx landing page graphic",

                    modifier = Modifier.fillMaxWidth(),
//                       modifier = Modifier
//                           .,
                    contentScale = ContentScale.FillWidth,

                    )
                Spacer(modifier = Modifier.weight(1f))
            }


        }
    }


}

@Composable
fun RightPortion() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = "test", block ={
        delay(10)
        visible=true
    } )
    val density = LocalDensity.current
    AnimatedVisibility(visible = visible, enter = slideInVertically {
        // Slide in from 40 dp from the top.
        with(density) { -20.dp.roundToPx() }
    } + fadeIn(
        // Fade in with the initial alpha of 0.3f.
        initialAlpha = 0.3f
    )) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "WELCOME",
                textAlign = TextAlign.Center, color = Color.White, fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            CommonButton(text = "New User") {
                LandingScreenState.screenState(LandingScreenType.NewUser)
            }
            Spacer(modifier = Modifier.height(16.dp))
            CommonButton(text = "Existing user") {
                LandingScreenState.screenState(LandingScreenType.ExistingUser)
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    LandingScreenState.screenState(LandingScreenType.RestoreFromKeystore)
                }
            ) {
                Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = "Restore from Keychain",
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Image(
                        painter = imageResource(Res.drawable.IC_KEY),
                        contentDescription = "Key"
                    )
                }
            }
            Spacer(modifier = Modifier.height(42.dp))
        }
    }

}