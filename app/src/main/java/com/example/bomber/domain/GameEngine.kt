package com.example.bomber.domain

import com.example.bomber.game.Bomb
import com.example.bomber.game.Bomberman
import com.example.bomber.game.Enemy
import com.example.bomber.game.GameMap
import com.example.bomber.game.GamePlayState
import com.example.bomber.game.GameState
import com.example.bomber.game.Location
import com.example.bomber.game.MoveDirection
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class GameEngine @Inject constructor() {

	private var gameState: GameState = GameState.INITIAL

	private var heroMoveDirection = MoveDirection.IDLE

	val gameFlow = flow {
		while (true) {
			emit(gameState)
			delay(GAME_UPDATE_RATE)
		}
	}

	val updates = flow {
		while (true) {
			emit(Unit)
			delay(PERIODIC_UPDATE_RATE)
		}
	}
		.onEach { runUpdates() }

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

		bombs.add(
			Bomb(
				location = location
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

	private fun runUpdates() {
		if (gameState.playState != GamePlayState.RUNNING) return

		gameState = gameState.copy(
			bomberman = getUpdatedBomberman(),
			enemies = getUpdatedEnemies(),
		)

		checkBombs()
		checkExplosions()
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
		if (gameState.bomberman.location == this) {
			gameState = gameState.copy(
				playState = GamePlayState.LOOSE
			)
			return
		}

		val enemies = gameState.enemies.filterNot {
			it.location == this
		}

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
				enemyLocation == bombermanLocation
			}

		if (enemyMetBomberman != null) {
			gameState = gameState.copy(
				playState = GamePlayState.LOOSE
			)
		}
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
			MoveDirection.UP    -> y = max(0, y - 1)
			MoveDirection.DOWN  -> y = min(9, y + 1)
			MoveDirection.LEFT  -> x = max(0, x - 1)
			MoveDirection.RIGHT -> x = min(9, x + 1)
			MoveDirection.IDLE  -> Unit
		}

		return Location(x, y)
	}

	private companion object {
		const val GAME_UPDATE_RATE: Long = 100
		const val PERIODIC_UPDATE_RATE: Long = 1000
	}
}