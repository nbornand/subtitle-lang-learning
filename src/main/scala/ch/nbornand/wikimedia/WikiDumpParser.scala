package ch.nbornand.wikimedia

import scala.io.Source
import scala.xml.pull.{XMLEventReader, EvText, EvElemStart}


/**
 * Created by Nicolas on 21.01.2016.
 */
object WikiDumpParser {

  //TODO close the source at some point
  def collectWordsInDump(dumpPath:String) = {

    val source = Source.fromFile(dumpPath, "UTF-8")
    val xmlIterator = new scala.xml.pull.XMLEventReader(source)

    new Iterator[Word]{

      var nextWord:Option[Word] = computeNext

      def hasNext = nextWord.isDefined

      def next = {
        val temp = nextWord
        nextWord = computeNext
        temp.get
      }

      private def computeNext:Option[Word] = {
        if(xmlIterator.hasNext){
          var last = xmlIterator.next()
          while(xmlIterator.hasNext){
            val current = xmlIterator.next()
            (last, current) match {
              case (EvElemStart(_, "text", _, _), EvText(text)) => {
                val potential = parseMarkup(text)
                if(potential.isDefined) return potential
              }
              case _ =>
            }
            last = current
          }
        }
        source.close()
        None
      }
    }
  }

  private def parseMarkup(text:String):Option[Word] = {

    val word = "([^\\s]+\\w)\\s+\\([{]{2}Sprache\\|Deutsch".r
      .findFirstMatchIn(text).map(_.group(1))

    if(word.isDefined){
      "\\{\\{Wortart\\|([^|]+)\\|Deutsch\\}\\}.{0,5}([fmn])?".r.findFirstMatchIn(text) match {
        case None => None
        case Some(oneGroup) if oneGroup.group(2) == null => Some(new Word(word.get, oneGroup.group(1), None))
        case Some(twoGroups) => Some(new Word(word.get, twoGroups.group(1), Some(twoGroups.group(2))))
      }
    } else None
  }
}
