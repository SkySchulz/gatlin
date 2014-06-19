/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.core.check.extractor.css

import jodd.lagarto.LagartoParser
import jodd.lagarto.dom.{ LagartoDomBuilderConfig, LagartoDOMBuilder }
import jodd.log.LoggerFactory
import jodd.log.impl.Slf4jLoggerFactory

object Jodd {

  LoggerFactory.setLoggerFactory(new Slf4jLoggerFactory)

  val JoddConfig = new LagartoDomBuilderConfig()
    .setParsingErrorLogLevelName("INFO")
    .setEnableConditionalComments(false)
    .setCaseSensitive(false)

  def getJoddConfig(ieVersion: Option[Float]): LagartoDomBuilderConfig = {
    ieVersion match {
      case Some(version) => JoddConfig.setEnableConditionalComments(true).setCondCommentIEVersion(version)

      case None          => JoddConfig
    }
  }

  def newLagartoDomBuilder: LagartoDOMBuilder = {
    val domBuilder = new LagartoDOMBuilder
    domBuilder.setConfig(JoddConfig)
    domBuilder
  }

  def newLagartoParser(chars: Array[Char], ieVersion: Option[Float]): LagartoParser = {
    val lagartoParser = new LagartoParser(chars, false)
    lagartoParser.setConfig(getJoddConfig(ieVersion))
    lagartoParser
  }
}
