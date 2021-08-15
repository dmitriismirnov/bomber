package com.example.bomber.game

import androidx.annotation.DrawableRes
import com.example.bomber.R

interface MapTile {

	val drawableRes: Int
	val afterExplode: MapTile?
}

enum class Grass(
	@DrawableRes override val drawableRes: Int,
	override val afterExplode: MapTile? = null,
) : MapTile {
	DRY(drawableRes = R.drawable.dry_grass),
	GREEN(drawableRes = R.drawable.green_grass, afterExplode = DRY),
}

enum class Obstacle(
	@DrawableRes override val drawableRes: Int,
	override val afterExplode: MapTile? = null,
) : MapTile {
	WEAK_WALL(drawableRes = R.drawable.ground_rocks, afterExplode = Grass.GREEN),
	STRONG_WALL(drawableRes = R.drawable.wall_rock),
}