trait StorageComponent {
  type User <: UserLike

  def storeUser(user: User)
  def retrieveUser(id: Int): Option[User]


  trait UserLike {
    def id: Int
    def hash: Vector[Byte]
  }
}

trait ConfigComponent {
  type Config
  def config: Config
}

trait MySQLStorageComponent extends StorageComponent with ConfigComponent {
  type Config <: MySQLConfig

  override def storeUser(user: User) { }
  override def retrieveUser(id: Int): Option[User] = None


  case class User(id: Int, hash: Vector[Byte]) extends UserLike

  trait MySQLConfig {
    def mysqlHost: String
    def mysqlPort: Int
  }
}

trait GravatarComponent extends StorageComponent {
  type Config <: GravatarConfig

  def avatarURL(user: User): String = { "" }

  trait GravatarConfig {
    def token: String
  }
}

class RESTService extends MySQLStorageComponent with GravatarComponent {
  type Config = CConfig

  class CConfig extends MySQLConfig with GravatarConfig {
    val mysqlHost = "localhost"
    val mysqlPort = 3336

    val token = "1234cafebabe"

    User(1, Vector())
  }

  override def config = new CConfig


}

