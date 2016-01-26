package ch.nbornand.wikimedia

import scala.collection.immutable.Stream.Empty

/**
 * Created by Nicolas on 22.01.2016.
 */

trait Token{
  def desc:String
  def value:String
}
case class WordT(value:String) extends Token{
  def desc = "word"
}
case class Delim(value:String) extends Token{
  def desc = value
}

object WikiMarkupLexerTokenizer {

  private val specialChars = Set('[', ']','|', ':', '{', '}')

  def transform(s:Stream[Char]):Stream[Token] = s match {
    case char #:: rest if char.toString.matches("[\\s\r\n\n\r]") => transform(s.tail)
    case char #:: rest if specialChars.contains(char) => {
      if(!rest.isEmpty && rest.head == char) Delim(List(char, char).mkString) #:: transform(s.drop(2))
      else Delim(char.toString) #:: transform(s.tail)
    }
    case char #:: rest => {
      val word = s.takeWhile(c => !specialChars.contains(c) && !c.toString.matches("[\\s\r\n\n\r]")).mkString
      if(word.trim.size > 0) WordT(word) #:: transform(s.drop(word.size)) else transform(s.drop(word.size))
    }
    case Empty => Empty
  }
}

case class Entity(text:String, pos:String)

object WikiMarkupParser {

//  private def check(t:Token, expected:String) ={
//    if(t.desc != expected) throw new Exception(s"expected $expected, got ${t.desc}")
//    t
//  }
//
//  def transform(s:Stream[Token]):Option[Entity] = s match {
//    case Delim("==") #:: rest => {
//      val value = check(rest.head, "word")
//      val link = check(rest.head, "word").asInstanceOf[Word].value
//      check(rest.tail.head, "]]")
//      Link(link) #:: transform(rest.tail)
//    }
//    case first #:: rest => transform(rest)
//    case Empty => Empty
//  }
//
//  private def parseTitle(s:Stream[Token]):String = {
//    check(s.head, "==")
//    val word = check(s.head, "word")
//    check(s(1), "(");check(s(1), "{{")
//    check(s.head, "word").
//    raus ({{Sprache|Deutsch}})
//  }

}
