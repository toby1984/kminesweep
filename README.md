A toy minesweeper implementation in Kotlin
==========================================

Kotlin seems to be getting some attention lately (since Google announced that Kotlin will be supported on Android) so I wanted to see how it feels compared to Scala.

TL;DR If you're already using Scala, there's no need to look at Kotlin. If you're not using Scala/don't want to use it and are looking for something to use until Java 10 comes around (with value types,type inference and pattern matching), Kotlin might be worth a look.

Here's my very short summary after playing around with it for 2-3 hours (so take it with a grain of salt).

The Good
========

- More expressive than Java 
- Non-nullable by default
- val/var
- some FP-style methods on their Collection API
- immutable collections available
- no primitive types
- type inference
- limited operator overloading (see below)

The Bad
=======

- limited operator overloading :-)

- Although a simpler language was one of their design goals they had to add quite a few additional keywords (like infix,inner,operator) to make it work

- Setting up multi-dimensional arrays is a PITA ...

class Board(val columns:Int,val rows:Int)
{
        val board : Array<Array<State>>

        enum class State(val hasMine:Boolean,val isDiscovered:Boolean) { ... }

        init
        {
            board = Array<Array<State>>(columns, { Array<State>( rows , { State.EMPTY } ) } )
        }
}


- Unlike Scala , assignment does not return Unit so is no expression

  func set(x:Int,y:Int,s:State) : Unit = board[x][y] = s  <<<<< won't compile 

- Noisy lambas (why the need for curly braces?) 

   val x : (Int) -> Int = { x -> x+1 }

- Inner classes need to be explicitly marked with "inner" keyword

- They got ridf of the 'new' keyword but now you have to write "object : xxxx" when you want to create an anonymous class

- property implementation feels clunky compared so Scala's unified namespace approach 
  - properties <-> backing properties 
  - being only able to declare getters setters only right after a property definition IMHO makes classes hard to read if your getters/setters are longer than one line

The Ugly
========

- A language without full-blown pattern matching in 2017 ...

- restrictive for() loop syntax

- Somewhat expected (they want to sell their IDE, after all) the Eclipse Kotlin plugin is quite stable but very basic, it's basically just a glorified text editor with syntax highlighting and auto-completion of identifiers in scope. 
  - no refactorings (not even rename...)
  - no auto-completion of method names when trying to override a function
  - no search callers etc.
  - no type hierarchy
  - no auto-formatting (not even indent...)

- No syntax for array-initialization like String[] x = { "a" , "b" , "c" };
  (though there are factory functions like arrayOf() etc. )
