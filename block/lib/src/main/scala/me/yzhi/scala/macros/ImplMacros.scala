package me.yzhi.scala.macros

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

class FillDefs extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro ImplMacros.addDefs
}

object ImplMacros {
  def addDefs(c: Context)(annottees: c.Expr[Any]*) = {
    impl(c)(false, annottees: _*)
  }

  def impl(c: Context)(addSuper: Boolean, annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val inputs = annottees.map(_.tree).toList
    // create the definitions we're going to add
    val newDefDefs = List(
      DefDef(Modifiers(), TermName("x"), List(), List(), TypeTree(), Literal(Constant(5))),
      DefDef(Modifiers(), TermName("y"), List(), List(), TypeTree(), Literal(Constant(7.0f)))
    )

    // pattern match on the inputs
    val modDefs = inputs map { tree => tree match {
      case ClassDef(mods, name, something, template) =>
        println(s"DEBUG: $mods | $name | $something | $template")
        val q = template match {
          case Template(superMaybe, emptyValDef, defs) =>
            Template(superMaybe, emptyValDef, defs ++ newDefDefs)
          case ex =>
            throw new IllegalArgumentException(s"Invalid template: $ex")
        }
        ClassDef(mods, name, something, q)
      case ModuleDef(mods, name, template) =>
        println(s"DEBUG Module: $mods | $name | $template")
        val q = template match {
          case Template(superMaybe, emptyValDef, defs) =>
            println(s"DEBUG Template: $superMaybe | $emptyValDef | $defs")
            Template(superMaybe, emptyValDef, defs ++ newDefDefs)
          case ex =>
            throw new IllegalArgumentException(s"Invalid template: $ex")
        }
        ModuleDef(mods, name, q)
      case ex =>
        throw new IllegalArgumentException(s"Invalid macro input: $ex")
    }
    }
    // wrap the result up in an Expr, and return it
    val result = c.Expr(Block(modDefs, Literal(Constant())))
    result
  }
}
