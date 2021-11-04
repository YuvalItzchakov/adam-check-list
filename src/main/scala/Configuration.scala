import sttp.client3.httpclient.zio.SttpClient
import zio.blocking.Blocking
import zio.clock.Clock
import zio.{Has, RIO, Task}

import java.time.Duration
import scala.annotation.nowarn

sealed trait Status

object Status {
  final case class Failed(t: Throwable) extends Status

  final case class Success[A](value: A) extends Status
}

case class HealthCheckResult(status: Status)

trait ConfigurationManager {
  def fetchStuff: RIO[Blocking, String]
}

@nowarn
case class ConfigurationManagerLive(sttpClient: SttpClient.Service) extends ConfigurationManager {
  override def fetchStuff: RIO[Blocking, String] = RIO("foo")
}

trait Check {
  def check: Task[HealthCheckResult]
}

class DataflowCheck(blocking: Blocking.Service, clock: Clock.Service) extends Check {
  override def check: Task[HealthCheckResult] =
    blocking.effectBlocking(HealthCheckResult(Status.Success("YAY!"))).delay(Duration.ofSeconds(10)).provide(Has(clock))
}

class FlowdataCheck(blocking: Blocking.Service, configurationManager: ConfigurationManager) extends Check {
  override def check: Task[HealthCheckResult] =
    configurationManager.fetchStuff.provide(Has(blocking)).map { res =>
      HealthCheckResult(Status.Success(res))
    }
}

object CheckExecutor {
  def runChecks(checks: List[Check]): Task[String] = ???
}
