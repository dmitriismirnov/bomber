package com.example.bomber.game

import com.example.bomber.game.maps.FirstMap

data class GameState(
	val map: GameMap,
	val bomberman: Bomberman,
	val enemies: List<Enemy>,
	val playState: GamePlayState,
	val bombs: List<Bomb>
) {
	companion object {
		val INITIAL: GameState = GameState(
			map = FirstMap,
			bomberman = Bomberman(
				location = Location(x = 0f, y = 0f)
			),
			enemies = listOf(
				Enemy(
					location = Location(x = 7f, y = 4f),
					type = EnemyType.ALIEN,
				),
				Enemy(
					location = Location(x = 3f, y = 7f),
					type = EnemyType.ALIEN,
				),
				Enemy(
					location = Location(x = 2f, y = 3f),
					type = EnemyType.WITCH,
				),
				Enemy(
					location = Location(x = 9f, y = 9f),
					type = EnemyType.GHOST,
				)
			),
			playState = GamePlayState.RUNNING,
			bombs = emptyList()
		)
	}
}