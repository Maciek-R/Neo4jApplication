package com.example

object CypherInterpolator {

  implicit class CypherOps(context: StringContext) {
    def cypher(args: Any*): String = {
      // TODO maybe Integer values will require other type of handling - without ''
      val mappedArgs = args.map {
        case Some(v) => s"'$v'"
        case None    => s"NULL"
        case v       => s"'$v'"
      }
      context.s(mappedArgs: _*)
    }
  }
}
