package lila.socket

import play.api.libs.json._
import scala.concurrent.Promise
import ornicar.scalalib.Zero

object Socket extends Socket {

  case class Uid(value: String) extends AnyVal

  val uidIso = lila.common.Iso.string[Uid](Uid.apply, _.value)
  implicit val uidFormat = lila.common.PimpedJson.stringIsoFormat(uidIso)

  case class Uids(uids: Set[Uid])

  case class SocketVersion(value: Int) extends AnyVal with IntValue with Ordered[SocketVersion] {
    def compare(other: SocketVersion) = value compare other.value
    def inc = SocketVersion(value + 1)
  }

  val socketVersionIso = lila.common.Iso.int[SocketVersion](SocketVersion.apply, _.value)
  implicit val socketVersionFormat = lila.common.PimpedJson.intIsoFormat(socketVersionIso)
  implicit val socketVersionZero = Zero.instance[SocketVersion](SocketVersion(0))

  case class GetVersion(promise: Promise[SocketVersion])

  val initialPong = makeMessage("n")
}

private[socket] trait Socket {

  def makeMessage[A](t: String, data: A)(implicit writes: Writes[A]): JsObject =
    JsObject(new Map.Map2("t", JsString(t), "d", writes.writes(data)))

  def makeMessage(t: String): JsObject = JsObject(new Map.Map1("t", JsString(t)))
}
