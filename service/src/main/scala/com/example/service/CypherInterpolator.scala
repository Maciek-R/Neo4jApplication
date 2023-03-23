package com.example.service

object CypherInterpolator {

  implicit class CypherOps(context: StringContext) {
    def cypher(args: Any*): String = {
      // TODO maybe Integer values will require other type of handling - without ''
      val mappedArgs = args.map {
        case Some(v: String)  => s"'$v'"
        case Some(v: Int)     => s"$v"
        case Some(v: Boolean) => s"$v"
        case v: String        => s"'$v'"
        case v: Int           => s"$v"
        case v: Boolean       => s"$v"
        case None             => s"NULL"
      }
      context.s(mappedArgs: _*)
    }
  }
}
