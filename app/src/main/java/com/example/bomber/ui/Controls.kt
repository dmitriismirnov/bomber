package com.example.bomber.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bomber.R
import com.example.bomber.game.GamePlayState
import com.example.bomber.presentation.GameViewModel

@Composable
fun Controls() {

	val gameViewModel: GameViewModel = hiltViewModel()
	val gameState = gameViewModel.gameState.collectAsState()

	val arrowUp = Icons.Default.KeyboardArrowUp
	val arrowDown = Icons.Default.KeyboardArrowDown
	val arrowLeft = Icons.Default.KeyboardArrowLeft
	val arrowRight = Icons.Default.KeyboardArrowRight

	val play = Icons.Outlined.PlayArrow
	val pause = Icons.Default.Pause

	val bombSize = 100.dp
	val playSize = 80.dp
	val arrowSize = 50.dp

	ConstraintLayout(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp)
	) {
		val (upRef, leftRef, rightRef, downRef, bombRef, playStateRef) = createRefs()

		Image(
			modifier = Modifier
				.constrainAs(upRef) {
					top.linkTo(parent.top)
					start.linkTo(leftRef.end)
					bottom.linkTo(leftRef.top)
					end.linkTo(rightRef.start)
				}
				.size(arrowSize)
				.pointerInput(Unit) {
					detectTapGestures(
						onPress = {
							gameViewModel.pressUp()
							tryAwaitRelease()
							gameViewModel.releaseUp()
						}
					)
				}
				.indication(
					interactionSource = remember { MutableInteractionSource() },
					indication = rememberRipple(),
				),
			imageVector = arrowUp,
			colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
			contentDescription = "go up",
		)

		Image(
			modifier = Modifier
				.constrainAs(leftRef) {
					top.linkTo(upRef.bottom)
					start.linkTo(parent.start)
					bottom.linkTo(downRef.top)
					end.linkTo(upRef.start)
				}
				.size(arrowSize)
				.pointerInput(Unit) {
					detectTapGestures(
						onPress = {
							gameViewModel.pressLeft()
							tryAwaitRelease()
							gameViewModel.releaseLeft()
						}
					)
				},
			imageVector = arrowLeft,
			colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
			contentDescription = "go left",
		)

		Image(
			modifier = Modifier
				.constrainAs(rightRef) {
					top.linkTo(upRef.bottom)
					bottom.linkTo(downRef.top)
					start.linkTo(upRef.end)
				}
				.size(arrowSize)
				.pointerInput(Unit) {
					detectTapGestures(
						onPress = {
							gameViewModel.pressRight()
							tryAwaitRelease()
							gameViewModel.releaseRight()
						}
					)
				},
			imageVector = arrowRight,
			colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
			contentDescription = "go right",
		)

		Image(
			modifier = Modifier
				.constrainAs(downRef) {
					top.linkTo(leftRef.bottom)
					start.linkTo(upRef.start)
					bottom.linkTo(parent.bottom)
					end.linkTo(upRef.end)
				}
				.size(arrowSize)
				.pointerInput(Unit) {
					detectTapGestures(
						onPress = {
							gameViewModel.pressDown()
							tryAwaitRelease()
							gameViewModel.releaseDown()
						}
					)
				},
			imageVector = arrowDown,
			colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
			contentDescription = "go down",
		)

		Image(modifier = Modifier

			.constrainAs(playStateRef) {
				bottom.linkTo(upRef.top)
//                bottom.linkTo(parent.bottom)
				start.linkTo(upRef.end)
				end.linkTo(bombRef.start)
			}

			.size(playSize)
			.pointerInput(Unit) {
				detectTapGestures(
					onPress = {
						when (gameState.value.playState) {
							GamePlayState.RUNNING -> gameViewModel.pauseGame()
							GamePlayState.PAUSE   -> gameViewModel.runGame()
							else                  -> gameViewModel.restartGame()

						}
					}
				)
			},
			imageVector = if (gameState.value.playState == GamePlayState.RUNNING) pause else play,
			colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
			contentDescription = if (gameState.value.playState == GamePlayState.RUNNING) "pause" else "play"
		)

		Image(
			modifier = Modifier
				.constrainAs(bombRef) {
					top.linkTo(upRef.top)
					bottom.linkTo(downRef.bottom)
					end.linkTo(parent.end)
				}
				.size(bombSize)
				.pointerInput(Unit) {
					detectTapGestures(
						onPress = {
							gameViewModel.pressBomb()
							tryAwaitRelease()
							gameViewModel.releaseBomb()
						}
					)
				},
			painter = painterResource(id = R.drawable.bomb),
			colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
			contentDescription = "place bomb"
		)

	}
}