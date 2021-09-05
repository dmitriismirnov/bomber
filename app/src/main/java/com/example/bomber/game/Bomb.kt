package com.example.bomber.game

data class Bomb(
	val location: Location,
	val secondsToExplode: Int = 4,
)