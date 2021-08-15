package com.example.bomber.game

import androidx.annotation.DrawableRes
import com.example.bomber.R

data class Enemy(
	val location: Location,
	val type: EnemyType,
) {
	@DrawableRes
	val drawableRes = type.drawableRes
}

enum class EnemyType(@DrawableRes val drawableRes: Int) {
	GHOST(R.drawable.ghost),
	WITCH(R.drawable.witch),
	ALIEN(R.drawable.alien),

}