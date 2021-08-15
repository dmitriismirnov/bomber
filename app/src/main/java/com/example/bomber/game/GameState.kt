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
				location = Location(x = 0, y = 0)
			),
			enemies = listOf(
				Enemy(
					location = Location(1, 5),
					type = EnemyType.ALIEN,
				),
				Enemy(
					location = Location(2, 3),
					type = EnemyType.WITCH,
				),
				Enemy(
					location = Location(9, 9),
					type = EnemyType.GHOST,
				)
			),
			playState = GamePlayState.RUNNING,
			bombs = emptyList()
		)
	}
}