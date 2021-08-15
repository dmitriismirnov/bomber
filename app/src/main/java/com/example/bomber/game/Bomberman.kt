package com.example.bomber.game

import androidx.annotation.DrawableRes
import com.example.bomber.R

class Bomberman(
    val location: Location,
) {
	companion object {
		@DrawableRes
		val drawableRes = R.drawable.bomberman
	}
}