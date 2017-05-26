package de.codesourcery.kotlin.kminesweep

import java.util.Random

class Board(val columns:Int,val rows:Int)
{
	val board : Array<Array<State>>
		
	public enum class State(val hasMine:Boolean,val isDiscovered:Boolean,val isMarked:Boolean)
	{
	    MINE(true,false,false),
		EMPTY(false,false,false),
		MINE_DISCOVERED(true,true,false),
		EMPTY_DISCOVERED(false,true,false),
	    MINE_MARKED(true,false,true),
		EMPTY_MARKED(false,false,true),
		MINE_DISCOVERED_MARKED(true,true,true),
		EMPTY_DISCOVERED_MARKED(false,true,true);		
		
		fun discovered() : State
		{
		    when(this) {
				MINE -> return MINE_DISCOVERED
				EMPTY -> return EMPTY_DISCOVERED
				MINE_MARKED -> return MINE_DISCOVERED_MARKED
				EMPTY_MARKED -> return EMPTY_DISCOVERED_MARKED
			    else -> return this
			}
		}
		
		fun marked() : State
		{
		    when(this) {
				MINE -> return MINE_MARKED
				EMPTY -> return EMPTY_MARKED
				MINE_DISCOVERED -> return MINE_DISCOVERED_MARKED
				EMPTY_DISCOVERED -> return EMPTY_DISCOVERED_MARKED
			    else -> return this
			}			
		}
		
		fun unmarked() : State
		{
		    when(this) {
				MINE_MARKED -> return MINE
				EMPTY_MARKED -> return EMPTY
				MINE_DISCOVERED_MARKED -> return MINE_DISCOVERED
				EMPTY_DISCOVERED_MARKED -> return EMPTY_DISCOVERED
			    else -> return this
			}			
		}		
		
		fun isEmpty() : Boolean = hasMine == false
	}
	
	data class Field(val pos:Vec2d,val state:State)
	{
		fun isEmpty() : Boolean = state.isEmpty()
	}

	init	
	{
	    board = Array<Array<State>>(columns, { Array<State>( rows , { State.EMPTY } ) } )
		init();
	}
	
	fun init()
	{
		val rnd = Random(System.currentTimeMillis())
		for ( x in 0..columns-1)
		{
			for ( y in 0..rows-1 ) {
				if ( rnd.nextFloat() > 0.7 ) {
					board[x][y] = State.MINE
				} else {
					board[x][y] = State.EMPTY
				}
			}
		}
		
		while( true ) {
		    val x = rnd.nextInt( columns )
		    val y = rnd.nextInt( rows )
			if ( getMineCount( Vec2d(x,y) ) == 0 ) {
			    discover( Vec2d(x,y) )
				break;
			}
		}		
	}
	
	operator fun get(v:Vec2d) : State = board[v.x][v.y]
	
	operator fun set(v:Vec2d,s:State) {
	    board[v.x][v.y] = s
	}
	
	private fun markDiscovered(v:Vec2d) {
		this[v] = this[v].discovered()
	}

	fun markAllDiscovered()
	{
		for (x in 0..columns-1 )
		{
			for (y in 0..rows-1 ) {
			    markDiscovered(Vec2d(x,y))
			}
		}	
	}
	
	fun toggleMarked(v:Vec2d) {
	  if ( this[v].isMarked ) {
		this[v] = this[v].unmarked()  
	  } else {
	      this[v] = this[v].marked()  
	  }
    }
	
	private fun mark(v:Vec2d) {
		this[v] = this[v].marked()
	}
	
	private fun unmark(v:Vec2d) {
		this[v] = this[v].unmarked()
	}	
	
	fun hasDiscoveredNeighbours(v:Vec2d) : Boolean
	{
		return neighbours(v).any { it.state.isDiscovered }
	}
	
	fun hasUndiscoveredNeighbours(v:Vec2d) : Boolean
	{
		return neighbours(v).any { ! it.state.isDiscovered }
	}	
	
	private fun discover(v:Vec2d,visited:MutableSet<Vec2d>)  
	{
		if ( get(v).hasMine || visited.contains( v ) ) {
			return
		}
		visited.add(v)
		markDiscovered(v)
		neighbours(v).filter { getMineCount( it.pos ) == 0 }.forEach( { discover(it.pos,visited) } )		
	}
	
	fun discover(v:Vec2d)
	{
		val visited = mutableSetOf<Vec2d>()
		discover(v,visited)
	}
	
	fun getMineCount(field:Vec2d) : Int
	{
		val minecount : (Field) -> Int = { v -> if (v.isEmpty() ) 0 else 1 }
		return minecount(Field(field,this[field])) + neighbours(field).map( minecount ).sum()
	}
	
	private fun neighbours(v:Vec2d) : List<Field> 
	{
		val result = mutableListOf<Field>()
        for ( delta in arrayOf( Vec2d(1,0),Vec2d(-1,0),Vec2d(0,1),Vec2d(0,-1) ) )
		{
			    val newPos = v.add( delta )
			    if ( contains(newPos) ) {
			        result.add( Field( newPos , this[newPos] ) )
				}
		}
		return result
	}
	
	fun contains(v:Vec2d) : Boolean = v.x >= 0 && v.y >= 0 && v.x < columns && v.y < rows
}