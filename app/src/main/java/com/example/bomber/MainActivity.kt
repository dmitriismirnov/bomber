package com.example.bomber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bomber.game.Bomberman
import com.example.bomber.game.GamePlayState
import com.example.bomber.game.GameState
import com.example.bomber.game.Location
import com.example.bomber.presentation.GameViewModel
import com.example.bomber.ui.Controls
import com.example.bomber.ui.theme.BomberTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.min

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			BomberTheme(darkTheme = true) {

				val gameState: GameState by hiltViewModel<GameViewModel>().gameState.collectAsState()


				Surface(
					modifier = Modifier.fillMaxSize(),
					color = gameState.playState.backgroundColor(),
				) {
					Column {
						GameMap()
						Controls()
					}
				}
			}
		}
	}
}

@Composable
fun GamePlayState.backgroundColor(): Color = when (this) {
	GamePlayState.PAUSE -> Color.Gray
	GamePlayState.RUNNING -> MaterialTheme.colors.background
	GamePlayState.WIN -> Color.Green
	GamePlayState.LOOSE -> Color.Red
}

@Composable
fun GameMap() {
	val gameViewModel: GameViewModel = hiltViewModel()
	val context = LocalContext.current
	val gameState: GameState by gameViewModel.gameState.collectAsState()

	Canvas(
		modifier = Modifier
			.padding(16.dp)
			.fillMaxWidth()
			.fillMaxHeight(0.7f),
	) {
		gameState.map.cells.forEach { line ->
			line.forEach { cell ->
				val tile = ImageBitmap.imageResource(
					res = context.resources,
					id = cell.tile.drawableRes
				)
				drawImage(location = cell.location, image = tile)
			}
		}

		val bombermanImage = ImageBitmap.imageResource(
			res = context.resources,
			id = Bomberman.drawableRes
		)
		drawImage(location = gameState.bomberman.location, image = bombermanImage)

		val bombImage = ImageBitmap.imageResource(
			res = context.resources,
			id = R.drawable.bomb,
		)

		val fireImage = ImageBitmap.imageResource(
			res = context.resources,
			id = R.drawable.fire,
		)

		gameState.bombs.forEach { bomb ->
			if (bomb.secondsToExplode > 0) {
				drawImage(
					location = bomb.location,
					image = bombImage,
				)
			} else {
				bomb.location.explosionArea().forEach { fireLocation ->
					drawImage(
						location = fireLocation,
						image = fireImage,
					)
				}
			}
		}

		gameState.enemies.forEach { enemy ->
			drawImage(
				location = enemy.location,
				image = ImageBitmap.imageResource(
					res = context.resources,
					id = enemy.drawableRes
				)
			)
		}

	}
}

fun DrawScope.drawImage(
	location: Location,
	image: ImageBitmap,
) {
	val size = min(size.height, size.width)
	val step = size / 10

	drawImage(
		image = image,
		dstOffset = IntOffset(
			x = (location.x * step).toInt(),
			y = (location.y * step).toInt(),
		),
		dstSize = IntSize(
			width = step.toInt(),
			height = step.toInt()
		),
	)
}