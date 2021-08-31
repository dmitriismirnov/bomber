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
}