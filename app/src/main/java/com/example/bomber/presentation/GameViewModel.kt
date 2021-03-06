package com.example.bomber.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomber.domain.GameEngine
import com.example.bomber.game.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
	private val gameEngine: GameEngine,
) : ViewModel() {

	val gameState: StateFlow<GameState> = gameEngine.gameFlow.stateIn(viewModelScope, SharingStarted.Lazily, GameState.INITIAL)

	// TODO: use coroutines in proper way
	val moveUpdates = gameEngine.moveUpdates.launchIn(viewModelScope)
	val bombUpdates = gameEngine.bombsUpdates.launchIn(viewModelScope)

	fun pressUp() {
		gameEngine.goUp()
	}

	fun releaseUp() {
		gameEngine.stopMoving()
	}

	fun pressDown() {
		gameEngine.goDown()
	}

	fun releaseDown() {
		gameEngine.stopMoving()
	}

	fun pressLeft() {
		gameEngine.goLeft()
	}

	fun releaseLeft() {
		gameEngine.stopMoving()
	}

	fun pressRight() {
		gameEngine.goRight()
	}

	fun releaseRight() {
		gameEngine.stopMoving()
	}

	fun pressBomb() {
		gameEngine.placeBomb()
	}

	fun releaseBomb() = Unit

	fun pauseGame() {
		gameEngine.pauseGame()
	}

	fun runGame() {
		gameEngine.runGame()
	}

	fun restartGame() {
		gameEngine.restartGame()
	}
}