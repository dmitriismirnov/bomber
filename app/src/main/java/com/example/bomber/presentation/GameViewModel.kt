package com.example.bomber.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomber.data.GameRepository
import com.example.bomber.game.Bomb
import com.example.bomber.game.Bomberman
import com.example.bomber.game.Enemy
import com.example.bomber.game.GameMap
import com.example.bomber.game.GamePlayState
import com.example.bomber.game.GameState
import com.example.bomber.game.Location
import com.example.bomber.game.MoveDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@HiltViewModel
class GameViewModel @Inject constructor(
	private val gameRepository: GameRepository,
) : ViewModel() {

	private val _gameState: MutableStateFlow<GameState> = MutableStateFlow(GameState.INITIAL)
	val gameState: StateFlow<GameState> = _gameState

	private var heroMoveDirection = MoveDirection.IDLE

	@ExperimentalTime
	private val gameFlow = flow {
		while (true) {
			emit(Unit)
			delay(Duration.seconds(1))
		}
	}
		.onEach { runUpdates() }
		.launchIn(viewModelScope)

	private fun runUpdates() {
		if (_gameState.value.playState != GamePlayState.RUNNING) return

		_gameState.value = _gameState.value.copy(
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
				_gameState.value.bomberman.location
			)
		)

	private fun getUpdatedEnemies(): List<Enemy> =
		_gameState.value
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
		val bombs = _gameState.value.bombs
			.asSequence()
			.map { it.copy(secondsToExplode = it.secondsToExplode - 1) }
			.filter { it.secondsToExplode >= 0 }
			.toList()

		_gameState.value = _gameState.value.copy(
			bombs = bombs
		)
	}

	private fun checkExplosions() {
		_gameState.value
			.bombs
			.filter { it.secondsToExplode == 0 }
			.map(Bomb::location)
			.flatMap { it.explosionArea() }
			.forEach { it.explode() }
	}

	private fun Location.explode() {
		if (_gameState.value.bomberman.location == this) {
			_gameState.value = _gameState.value.copy(
				playState = GamePlayState.LOOSE
			)
			return
		}

		val enemies = _gameState.value.enemies.filterNot {
			it.location == this
		}

		val map = _gameState.value.map.cells
		val tileAfterExplode = map[y][x].tile.afterExplode
		if (tileAfterExplode != null) {
			map[y][x] = map[y][x].copy(
				tile = tileAfterExplode
			)
		}

		_gameState.value = _gameState.value.copy(
			map = GameMap(map),
			enemies = enemies,
		)
	}

	private fun checkCollisions() {
		val bombermanLocation = _gameState.value.bomberman.location
		val enemyMetBomberman =
			_gameState.value.enemies.map { it.location }.firstOrNull { enemyLocation ->
				enemyLocation == bombermanLocation
			}

		if (enemyMetBomberman != null) {
			_gameState.value = _gameState.value.copy(
				playState = GamePlayState.LOOSE
			)
		}
	}

	private fun checkWin() {
		if (_gameState.value.enemies.isEmpty()) {
			_gameState.value = _gameState.value.copy(
				playState = GamePlayState.WIN
			)
		}
	}

	private fun calculateNewLocation(direction: MoveDirection, location: Location): Location {
		var x = location.x
		var y = location.y

		when (direction) {
			MoveDirection.UP -> y = max(0, y - 1)
			MoveDirection.DOWN -> y = min(9, y + 1)
			MoveDirection.LEFT -> x = max(0, x - 1)
			MoveDirection.RIGHT -> x = min(9, x + 1)
			MoveDirection.IDLE -> Unit
		}

		return Location(x, y)
	}

	fun pressUp() {
		heroMoveDirection = MoveDirection.UP
	}

	fun releaseUp() {
		heroMoveDirection = MoveDirection.IDLE
	}

	fun pressDown() {
		heroMoveDirection = MoveDirection.DOWN
	}

	fun releaseDown() {
		heroMoveDirection = MoveDirection.IDLE
	}

	fun pressLeft() {
		heroMoveDirection = MoveDirection.LEFT
	}

	fun releaseLeft() {
		heroMoveDirection = MoveDirection.IDLE
	}

	fun pressRight() {
		heroMoveDirection = MoveDirection.RIGHT
	}

	fun releaseRight() {
		heroMoveDirection = MoveDirection.IDLE
	}

	fun pressBomb() {
		val bombs = _gameState.value.bombs.toMutableList()
		val location = _gameState.value.bomberman.location

		bombs.add(
			Bomb(
				location = location
			)
		)
		_gameState.value = _gameState.value.copy(
			bombs = bombs
		)
	}

	fun releaseBomb() {
		Log.e("QQQ", "releaseBomb")
	}

	fun pauseGame() {
		_gameState.value = _gameState.value.copy(
			playState = GamePlayState.PAUSE
		)
	}

	fun runGame() {
		_gameState.value = _gameState.value.copy(
			playState = GamePlayState.RUNNING
		)
	}

	fun restartGame() {
		_gameState.value = GameState.INITIAL
	}
}