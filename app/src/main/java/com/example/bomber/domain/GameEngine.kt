package com.example.bomber.domain

import android.util.Range
import com.example.bomber.game.Bomb
import com.example.bomber.game.Bomberman
import com.example.bomber.game.Enemy
import com.example.bomber.game.GameMap
import com.example.bomber.game.GamePlayState
import com.example.bomber.game.GameState
import com.example.bomber.game.Location
import com.example.bomber.game.MapCell
import com.example.bomber.game.MoveDirection
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.math.roundToInt

class GameEngine @Inject constructor() {

	private var gameState: GameState = GameState.INITIAL

	private var heroMoveDirection = MoveDirection.IDLE

	val gameFlow = flow {
		while (currentCoroutineContext().isActive) {
			emit(gameState)
			delay(GAME_UPDATE_RATE)
		}
	}

	val moveUpdates = flow {
		while (currentCoroutineContext().isActive) {
			emit(Unit)
			delay(MOVE_UPDATE_RATE)
		}
	}
		.onEach { updateLocations() }

	val bombsUpdates = flow {
		while (currentCoroutineContext().isActive) {
			emit(Unit)
			delay(BOMBS_UPDATE_RATE)
		}
	}
		.onEach {
			checkBombs()
			checkExplosions()
		}

	//player controls
	fun goUp() {
		heroMoveDirection = MoveDirection.UP
	}

	fun goDown() {
		heroMoveDirection = MoveDirection.DOWN
	}

	fun goLeft() {
		heroMoveDirection = MoveDirection.LEFT
	}

	fun goRight() {
		heroMoveDirection = MoveDirection.RIGHT
	}

	fun stopMoving() {
		heroMoveDirection = MoveDirection.IDLE
	}

	fun placeBomb() {
		val bombs = gameState.bombs.toMutableList()
		val location = gameState.bomberman.location
		val tileLocation = location.copy(
			x = location.x,
			y = location.y,
		)

		bombs.add(
			Bomb(
				location = tileLocation
			)
		)
		gameState = gameState.copy(
			bombs = bombs
		)
	}

	fun pauseGame() {
		gameState = gameState.copy(
			playState = GamePlayState.PAUSE
		)
	}

	fun runGame() {
		gameState = gameState.copy(
			playState = GamePlayState.RUNNING
		)
	}

	fun restartGame() {
		gameState = GameState.INITIAL
	}

	private fun updateLocations() {
		if (gameState.playState != GamePlayState.RUNNING) return

		gameState = gameState.copy(
			bomberman = getUpdatedBomberman(),
			enemies = getUpdatedEnemies(),
		)

		checkCollisions()
		checkWin()
	}

	private fun getUpdatedBomberman(): Bomberman =
		Bomberman(
			calculateNewLocation(
				heroMoveDirection,
				gameState.bomberman.location
			)
		)

	private fun getUpdatedEnemies(): List<Enemy> =
		gameState
			.enemies
			.map {
				it.copy(
					location = calculateNewLocation(
						direction = MoveDirection.values().random(),
						location = it.location
					)
				)
			}

	private fun checkBombs() {
		val bombs = gameState.bombs
			.asSequence()
			.map { it.copy(secondsToExplode = it.secondsToExplode - 1) }
			.filter { it.secondsToExplode >= 0 }
			.toList()

		gameState = gameState.copy(
			bombs = bombs
		)
	}

	private fun checkExplosions() {
		gameState
			.bombs
			.filter { it.secondsToExplode == 0 }
			.map(Bomb::location)
			.flatMap { it.explosionArea() }
			.forEach { it.explode() }
	}

	private fun Location.explode() {
		if (areLocationsMet(gameState.bomberman.location, this)) {
			gameState = gameState.copy(
				playState = GamePlayState.LOOSE
			)
			return
		}

		val enemies = gameState.enemies.filterNot { enemy ->
			areLocationsMet(enemy.location, this)
		}

		val map = gameState.map.cells

		cells().forEach { cell ->
			cell.tile.afterExplode?.let { tile ->
				val x = cell.location.x.roundToInt()
				val y = cell.location.y.roundToInt()
				map[y][x] = map[y][x].copy(tile = tile)
			}
		}

		gameState = gameState.copy(
			map = GameMap(map),
			enemies = enemies,
		)
	}

	private fun checkCollisions() {
		val bombermanLocation = gameState.bomberman.location
		val enemyMetBomberman =
			gameState.enemies.map { it.location }.firstOrNull { enemyLocation ->
				areLocationsMet(enemyLocation, bombermanLocation)
			}

		if (enemyMetBomberman != null) {
			gameState = gameState.copy(
				playState = GamePlayState.LOOSE
			)
		}
	}

	private fun areLocationsMet(first: Location, second: Location): Boolean {
		val xCross = first.x.unitRange().contains(second.x - UNIT_RADIUS) || first.x.unitRange().contains(second.x + UNIT_RADIUS)
		val yCross = first.y.unitRange().contains(second.y - UNIT_RADIUS) || first.y.unitRange().contains(second.y + UNIT_RADIUS)

		return xCross && yCross
	}

	private fun Float.unitRange() = Range.create(this - UNIT_RADIUS, this + UNIT_RADIUS)

	private fun checkWin() {
		if (gameState.enemies.isEmpty()) {
			gameState = gameState.copy(
				playState = GamePlayState.WIN
			)
		}
	}

	private fun calculateNewLocation(direction: MoveDirection, location: Location): Location {
		var x = location.x
		var y = location.y
		var xAdjustment = 0f
		var yAdjustment = 0f

		when (direction) {
			MoveDirection.UP    -> {
				y = (y - MOVE_INCREMENT_VALUE).coerceIn(0f, 9f)
				yAdjustment = -UNIT_RADIUS
			}
			MoveDirection.DOWN  -> {
				y = (y + MOVE_INCREMENT_VALUE).coerceIn(0f, 9f)
				yAdjustment = UNIT_RADIUS
			}
			MoveDirection.LEFT  -> {
				x = (x - MOVE_INCREMENT_VALUE).coerceIn(0f, 9f)
				xAdjustment = -UNIT_RADIUS
			}
			MoveDirection.RIGHT -> {
				x = (x + MOVE_INCREMENT_VALUE).coerceIn(0f, 9f)
				xAdjustment = UNIT_RADIUS
			}
			MoveDirection.IDLE  -> Unit
		}

		val checkX = (x + xAdjustment).coerceIn(0f, 9f)
		val checkY = (y + yAdjustment).coerceIn(0f, 9f)

		if (Location(checkX, checkY).cells().any {
				!it.tile.passable
		}) {
			x = location.x
			y = location.y
		}

		return Location(x, y)
	}

	private fun Location.cells(): List<MapCell> =
		gameState.map.cells.flatMap { line ->
			line.filter { cell ->
				areLocationsMet(this, cell.location)
			}
		}

	private companion object {
		const val GAME_UPDATE_RATE: Long = 1000 / 60
		const val MOVE_UPDATE_RATE: Long = 100
		const val BOMBS_UPDATE_RATE: Long = 1000
		const val MOVE_INCREMENT_VALUE = 0.1f
		const val UNIT_RADIUS = 0.35f
	}
}