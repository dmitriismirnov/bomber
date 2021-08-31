package com.example.bomber.game

data class Location(
	val x: Float,
	val y: Float,
) {
	fun explosionArea(): List<Location> {
		val area = mutableListOf(this)

		if (x > 0) area += Location(x - 1, y)
		if (x < 9) area += Location(x + 1, y)
		if (y > 0) area += Location(x, y - 1)
		if (y < 9) area += Location(x, y + 1)

		return area
	}
}