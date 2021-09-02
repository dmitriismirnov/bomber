package com.example.bomber.game.maps

import com.example.bomber.game.GameMap
import com.example.bomber.game.Location
import com.example.bomber.game.MapCell
import com.example.bomber.game.MapTile
import com.example.bomber.game.Tiles.GREEN_GRASS as GG
import com.example.bomber.game.Tiles.DRY_GRASS as DG
import com.example.bomber.game.Tiles.STRONG_WALL as SW
import com.example.bomber.game.Tiles.WEAK_WALL as WW

val EmptyMap = GameMap(
	cells = arrayOf(
		line(0, GG, GG, GG, GG, GG, GG, GG, GG, GG, GG),
		line(1, GG, GG, GG, GG, GG, GG, GG, GG, GG, GG),
		line(2, GG, GG, GG, GG, GG, GG, GG, GG, GG, GG),
		line(3, GG, GG, GG, GG, GG, GG, GG, GG, GG, GG),
		line(4, GG, GG, GG, GG, GG, GG, GG, GG, GG, GG),
		line(5, GG, GG, GG, GG, GG, GG, GG, GG, GG, GG),
		line(6, GG, GG, GG, GG, GG, GG, GG, GG, GG, GG),
		line(7, GG, GG, GG, GG, GG, GG, GG, GG, GG, GG),
		line(8, GG, GG, GG, GG, GG, GG, GG, GG, GG, GG),
		line(9, GG, GG, GG, GG, GG, GG, GG, GG, GG, GG),
	)
)

val FirstMap = GameMap(
	cells = arrayOf(
		line(0, DG, DG, DG, DG, DG, SW, GG, GG, GG, GG),
		line(1, SW, WW, WW, WW, SW, SW, SW, WW, WW, WW),
		line(2, SW, GG, GG, GG, GG, SW, GG, GG, GG, GG),
		line(3, SW, GG, GG, GG, GG, WW, GG, GG, GG, GG),
		line(4, SW, GG, GG, GG, GG, SW, GG, GG, GG, GG),
		line(5, SW, WW, WW, WW, SW, SW, SW, WW, WW, WW),
		line(6, GG, GG, GG, GG, GG, SW, DG, DG, DG, DG),
		line(7, GG, GG, GG, GG, GG, WW, DG, DG, DG, DG),
		line(8, GG, GG, GG, GG, GG, SW, DG, DG, DG, DG),
		line(9, GG, GG, GG, GG, GG, SW, DG, DG, DG, DG),
	)
)

private fun line(y: Int, vararg tiles: MapTile): Array<MapCell> {
	var line = arrayOf<MapCell>()
	tiles.forEachIndexed { x, mapTile ->
		line += MapCell(
			location = Location(x = x.toFloat(), y = y.toFloat()),
			tile = mapTile,
		)
	}
	return line
}