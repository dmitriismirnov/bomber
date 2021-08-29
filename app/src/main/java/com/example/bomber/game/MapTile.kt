package com.example.bomber.game

import androidx.annotation.DrawableRes
import com.example.bomber.R

interface MapTile {

	val drawableRes: Int
	val passable: Boolean
	val afterExplode: MapTile?
}

enum class Tiles(
	@DrawableRes override val drawableRes: Int,
	override val passable: Boolean,
	override val afterExplode: MapTile? = null,
) : MapTile {
	DRY_GRASS(drawableRes = R.drawable.dry_grass, passable = true),
	GREEN_GRASS(drawableRes = R.drawable.green_grass, passable = true, afterExplode = DRY_GRASS),
	WEAK_WALL(drawableRes = R.drawable.ground_rocks, passable = false, afterExplode = GREEN_GRASS),
	STRONG_WALL(drawableRes = R.drawable.wall_rock, passable = false),
}