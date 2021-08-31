package com.example.bomber.game

data class GameState(
	val map: GameMap,
	val bomberman: Bomberman,
	val enemies: List<Enemy>,
	val playState: GamePlayState,
	val bombs: List<Bomb>
) {
	companion object {
		val INITIAL: GameState = GameState(
			map = GameMap.EMPTY,
			bomberman = Bomberman(
				location = Location(x = 0f, y = 0f)
			),
			enemies = listOf(
				Enemy(
					location = Location(1f, 5f),
					type = EnemyType.ALIEN,
				),
				Enemy(
					location = Location(2f, 3f),
					type = EnemyType.WITCH,
				),
				Enemy(
					location = Location(9f, 9f),
					type = EnemyType.GHOST,
				)
			),
			playState = GamePlayState.RUNNING,
			bombs = emptyList()
		)
	}
}