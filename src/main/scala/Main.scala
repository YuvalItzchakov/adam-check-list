import sttp.client3.SttpBackendOptions
import sttp.client3.SttpBackendOptions.Default.connectionTimeout
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.client3.httpclient.zio.SttpClient
import zio._
import zio.blocking.Blocking
import zio.clock.Clock

import scala.concurrent.duration.DurationInt

object Main extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    (for {
      (blocking, clock) <- ZIO.services[Blocking.Service, Clock.Service]
      sttpClient <- ZIO.service[SttpClient.Service]
      dataflowCheck = new DataflowCheck(blocking, clock)
      flowDataCheck = new FlowdataCheck(blocking, ConfigurationManagerLive(sttpClient))
      _ <- CheckExecutor.runChecks(List(dataflowCheck, flowDataCheck))
    } yield ()).provideCustomLayer(AsyncHttpClientZioBackend
      .layer(options = new SttpBackendOptions(connectionTimeout = 30.seconds, proxy = None))
      .orDie
    ).exitCode

}

