package com.example.bomber.game

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.bomber.R

data class Enemy(
	val location: Location,
	val type: EnemyType,
) {
	@DrawableRes
	val drawableRes = type.drawableRes
	val tint = type.tint
}

enum class EnemyType(@DrawableRes val drawableRes: Int, val tint: Color? = null) {
	GHOST(R.drawable.ghost, Color.Gray),
	WITCH(R.drawable.witch, Color.LightGray),
	ALIEN(R.drawable.alien, Color.Black),

}