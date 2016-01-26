package ch.nbornand.wikimedia

/**
 * @param text
 * @param pos part of speech
 */

case class Word(text:String, pos:String, gender:Option[String])
