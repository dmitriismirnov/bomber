package com.example.bomber.game

data class GameMap(
	val cells: Array<Array<MapCell>>,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as GameMap

		if (!cells.contentDeepEquals(other.cells)) return false

		return true
	}

	override fun hashCode(): Int {
		return cells.contentDeepHashCode()
	}

	companion object {
		val EMPTY: GameMap = generateEmptyMap()

		private fun generateEmptyMap(): GameMap {
			var map = arrayOf<Array<MapCell>>()

			for (y in 0..9) {
				var line = arrayOf<MapCell>()
				for (x in 0..9) {
					line += MapCell(
						location = Location(x = x, y = y),
						tile = Grass.GREEN,
					)
				}
				map += line
			}

			return GameMap(map)
		}

	}
}