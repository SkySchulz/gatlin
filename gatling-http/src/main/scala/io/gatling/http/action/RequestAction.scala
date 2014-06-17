/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.http.action

import io.gatling.core.action.{ Failable, Interruptable }
import io.gatling.core.result.message.KO
import io.gatling.core.result.writer.DataWriterClient
import io.gatling.core.session.{ Expression, Session }
import io.gatling.core.util.TimeHelper.nowMillis
import io.gatling.core.validation.Validation

object RequestAction extends DataWriterClient {

  def reportUnbuildableRequest(requestName: String, session: Session, errorMessage: String): Unit = {
    val now = nowMillis
    writeRequestData(session, requestName, now, now, now, now, KO, Some(errorMessage))
  }
}

abstract class RequestAction extends Interruptable with Failable {

  def requestName: Expression[String]
  def sendRequest(requestName: String, session: Session): Validation[Unit]

  def executeOrFail(session: Session): Validation[Unit] =
    requestName(session).flatMap { resolvedRequestName =>

      val outcome = sendRequest(resolvedRequestName, session)

      outcome.onFailure(errorMessage => RequestAction.reportUnbuildableRequest(resolvedRequestName, session, errorMessage))

      outcome
    }
}
