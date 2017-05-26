package de.codesourcery.kotlin.kminesweep

import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.Graphics
import java.awt.Color
import java.awt.Point
import java.awt.geom.Point2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.Graphics2D
import java.awt.Dimension
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

fun main(args:Array<String>)
{
    Main()
}

class Main : JFrame("Kminesweep")
{	
	companion object Main
	{
	    val ROWS = 10
	    val COLUMNS = 10		
	}
	
	private val board = Board(COLUMNS,ROWS)
	private var gameOver = false
	
	init {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
		val panel = MyPanel()
	    panel.setPreferredSize( Dimension(640,400))
		getContentPane().setLayout( BorderLayout() )
		getContentPane().add( panel , BorderLayout.CENTER )
		pack();
		setLocationRelativeTo( null )
		setVisible(true)
		panel.setFocusable(true)
		panel.requestFocus()
	}
		
	inner class MyPanel : JPanel()
	{
		var xinc = 0
		var yinc = 0
		
		init
		{
			addKeyListener( object : KeyAdapter()
			{
				override fun keyTyped(ev:KeyEvent)
				{
					if ( gameOver ) {
						gameOver = false;
						board.init()
						repaint()
					}
				}
			})
			addMouseListener( object: MouseAdapter()
			{
			    override fun mouseClicked(e: MouseEvent)
				{
					val vec = viewToModel( e.getPoint() )
					if ( board.contains( vec ) )
					{
						if ( e.getButton() == MouseEvent.BUTTON3 ) {
							board.toggleMarked( vec )
						} else if ( e.getButton() == MouseEvent.BUTTON1 ) {
    						if ( board[vec].hasMine ) {
    							gameOver = true
    						}
    						else
    						{
    							board.discover( vec )
    						}
						}
    				    repaint()						
					}
			    }				
			})
		}
		
		//     protected void paintComponent(Graphics g) {
		override fun paintComponent(g : Graphics)
		{
			super.paintComponent(g)
			
			val w = getWidth()
			val h = getHeight()
			
			xinc = Math.max( 1, w/COLUMNS )
			yinc = Math.max( 1 , h/ROWS )
			g.setFont( getFont().deriveFont(24.0f))						
			for ( x in 0..COLUMNS-1 )
			{
				for ( y in 0..ROWS-1 )
				{
					val v = Vec2d(x,y)
					val state = board[v]
					
					if ( state.isMarked ) {
					    g.setColor(Color.RED)
						printMarked(v,g)
					}					
					else if ( gameOver)
					{
						if ( state.hasMine ) {
							fillRect( v , Color.RED , g )
						} else {
							if ( state.isDiscovered ) {
							    fillRect( v , Color.WHITE , g)
							} else {
							    fillRect( v , Color.GRAY, g)
							}
						}	
					}
					else if ( state.isDiscovered )
					{
						if ( state.hasMine ) {
							fillRect( v , Color.RED , g )
						} else {
						    fillRect( v , Color.WHITE , g)
						}
					 } else {
						if ( board.hasDiscoveredNeighbours( v ) ) {
						    val count = board.getMineCount( v )
							if ( count > 0 ) {
						        fillRect( v , Color.WHITE , g)								
							    printMineCount(v , count ,g as Graphics2D )
							}
						}						
					} 
				}
			}
			
			renderGrid(g)
			
			if ( gameOver ) {
				g.setColor(Color.BLACK)
				val oldFont = g.getFont()
				g.setFont( oldFont.deriveFont( 48.toFloat() ) )
				print(Point(0,0),Point(w,h),"Game Over !!!",g)
				g.setFont(oldFont)
			}
		}
		
		private fun renderGrid(g:Graphics)
		{
			g.setColor(Color.BLACK)
			val w = getWidth()
			val h = getHeight()
			for ( x in 0..w step xinc )
			{
				g.drawLine(x,0,x,h)
			}
			for ( y in 0..h step yinc )
			{
				g.drawLine(0,y,w,y)
			}			
		}
		
		private fun printMarked(cell:Vec2d,g:Graphics) {
			print( cell, "!" , g )
		}
		
		private fun printMineCount(cell:Vec2d,count:Int,g:Graphics2D) {
			
			when(count) {
			    1 -> g.setColor(Color.BLUE)
				2 -> g.setColor(Color.GREEN)
				else -> {
				    g.setColor(Color.RED)
				}
			}
			print(cell,count.toString(),g)
		}
		
		private fun print(cell:Vec2d,msg:String,g:Graphics) {
			val p0 = modelToView( cell )
			val p1 = modelToView( cell.add(1,1) )
			print( p0 , p1 , msg , g )
		}
		
		private fun fillRect(cell:Vec2d,color:Color,g:Graphics)
		{
			val p0 = modelToView( cell )
			val p1 = modelToView( cell.add(1,1) )
			g.setColor(color)
			g.fillRect( p0.x , p0.y , p1.x - p0.x , p1.y - p0.y )
		}		
		
		private fun print(p0:Point,p1:Point,msg:String,g:Graphics) {
		    val x : (Int,Int) -> Int = { x,y -> x + y }
			val cx = (p1.x+p0.x)/2
			val cy = (p1.y+p0.y)/2
			val strHeight = g.getFontMetrics().height
			val strWidth = g.getFontMetrics().stringWidth(msg)
			val px = cx - strWidth/2
			val py = cy - strHeight/2 + g.getFontMetrics().ascent
			g.drawString( msg , px , py )
		}		
		
		fun modelToView( v : Vec2d ) : Point
		{
		    return Point( v.x * xinc , v.y * yinc )
		}
		
		fun viewToModel(p : Point ) : Vec2d
		{
			val x = p.getX().toInt() /xinc
			val y = p.getY().toInt() /yinc
			return Vec2d(x,y)
		}
	}
}