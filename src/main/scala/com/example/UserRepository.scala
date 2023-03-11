package com.example

import com.example.User.User

class UserRepository(appConfig: AppConfig) {

  def readAll(): List[User] = {
    val script = s"MATCH (user:Users) RETURN user.name as name, user.lastName as lastName"

    val results = Query(script, appConfig).execute{result =>
      QueryResult.list[User](result)
    }

    results
  }
}
