package de.codesourcery.kotlin.kminesweep

data class Vec2d(val x:Int,val y:Int)
{
    fun add(v:Vec2d) : Vec2d = Vec2d(x+v.x,y+v.y)
	
	fun add(dx:Int,dy:Int) : Vec2d = Vec2d(x+dx,y+dy)
}