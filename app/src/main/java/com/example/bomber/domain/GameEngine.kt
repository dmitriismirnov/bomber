package com.example.bomber.domain

import android.util.Range
import com.example.bomber.game.Bomb
import com.example.bomber.game.Bomberman
import com.example.bomber.game.Enemy
import com.example.bomber.game.GameMap
import com.example.bomber.game.GamePlayState
import com.example.bomber.game.GameState
import com.example.bomber.game.Location
import com.example.bomber.game.MoveDirection
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
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
			x = location.x.roundToInt().toFloat(),
			y = location.y.roundToInt().toFloat()
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

		val y = y.roundToInt()
		val x = x.roundToInt()

		val map = gameState.map.cells
		val tileAfterExplode = map[y][x].tile.afterExplode
		if (tileAfterExplode != null) {
			map[y][x] = map[y][x].copy(
				tile = tileAfterExplode
			)
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
		val xCross = Range.create(first.x - HALF_TILE_SIZE, first.x + HALF_TILE_SIZE).contains(second.x)
		val yCross = Range.create(first.y - HALF_TILE_SIZE, first.y + HALF_TILE_SIZE).contains(second.y)

		return xCross && yCross
	}

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

		when (direction) {
			MoveDirection.UP    -> y = max(0f, y - MOVE_INCREMENT_VALUE)
			MoveDirection.DOWN  -> y = min(9f, y + MOVE_INCREMENT_VALUE)
			MoveDirection.LEFT  -> x = max(0f, x - MOVE_INCREMENT_VALUE)
			MoveDirection.RIGHT -> x = min(9f, x + MOVE_INCREMENT_VALUE)
			MoveDirection.IDLE  -> Unit
		}

		return Location(x, y)
	}

	private companion object {
		const val GAME_UPDATE_RATE: Long = 1000 / 60
		const val MOVE_UPDATE_RATE: Long = 100
		const val BOMBS_UPDATE_RATE: Long = 1000
		const val MOVE_INCREMENT_VALUE = 0.1f
		const val TILE_SIZE = 1f
		const val HALF_TILE_SIZE = TILE_SIZE / 2
	}
}