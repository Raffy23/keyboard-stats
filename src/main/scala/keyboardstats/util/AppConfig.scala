package keyboardstats.util

import com.typesafe.config.{ConfigFactory, Config => TypesafeConfig}

import scala.io.Source

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.07.2018
  */
object AppConfig {

  case class Config(language: Option[String], configDirectory: String, statisticsDirectory: String,
                    keyboardLayout: String)

  val conf: Config = {
    val confString: String = Source.fromFile("./application.conf").getLines().mkString("\n")
    val hocon: TypesafeConfig = ConfigFactory.parseString(confString).resolve()

    import pureconfig._
    loadConfigOrThrow[Config](hocon)
  }
}
