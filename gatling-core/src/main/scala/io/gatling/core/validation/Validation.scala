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
package io.gatling.core.validation

abstract class Validation[+T] {
  def map[A](f: T => A): Validation[A]
  def flatMap[A](f: T => Validation[A]): Validation[A]
  def mapError(f: String => String): Validation[T]
  def foreach[A](f: T => A): Unit
  def withFilter(p: T => Boolean): Validation[T] = filter(p)
  def filter(p: T => Boolean): Validation[T]
  def onSuccess(f: T => Any): Unit
  def onFailure(f: String => Any): Unit
  def recover[A >: T](v: => A): Validation[A]
  def get: T
}

case class Success[+T](value: T) extends Validation[T] {
  def map[A](f: T => A): Validation[A] = Success(f(value))
  def flatMap[A](f: T => Validation[A]): Validation[A] = f(value)
  def mapError(f: String => String): Validation[T] = this
  def foreach[A](f: T => A) { f(value) }
  def filter(p: T => Boolean): Validation[T] = if (p(value)) this else Failure("Predicate does not hold for " + value)
  def onSuccess(f: T => Any) { f(value) }
  def onFailure(f: String => Any) {}
  override def recover[A >: T](v: => A): Validation[A] = this
  def get: T = value
}

case class Failure(message: String) extends Validation[Nothing] {
  def map[A](f: Nothing => A): Validation[A] = this
  def flatMap[A](f: Nothing => Validation[A]): Validation[A] = this
  def mapError(f: String => String): Validation[Nothing] = Failure(f(message))
  def foreach[A](f: Nothing => A) {}
  def filter(p: Nothing => Boolean) = this
  def onSuccess(f: Nothing => Any) {}
  def onFailure(f: String => Any) { f(message) }
  override def recover[A >: Nothing](v: => A): Validation[A] = v.success
  def get: Nothing = throw new UnsupportedOperationException(s"Can't call get on $this")
}
