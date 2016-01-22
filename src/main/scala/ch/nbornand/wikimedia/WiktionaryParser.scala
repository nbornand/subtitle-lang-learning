package ch.nbornand.wikimedia

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.xml.pull.{EvText, EvElemStart}

/**
 * Created by Nicolas on 21.01.2016.
 */
object WiktionaryParser {

  def collectWordsInDump(dumpPath:String) = {

    val source = Source.fromFile(dumpPath, "UTF-8")
    val xmlStream = new scala.xml.pull.XMLEventReader(source)

    val buffer = new ListBuffer[(String, String)]
    while(xmlStream.hasNext){
      xmlStream.next() match {
        case EvElemStart(_, "text", _, _) if xmlStream.hasNext => xmlStream.next() match {
          case EvText(text) => {
            "([^\\s]+\\w)\\s+\\([{]{2}Sprache\\|Deutsch[}]{2}\\)[\\s=\r\n\n\r]+\\{\\{\\w+\\|(\\w+)".r
              .findFirstMatchIn(text)
              .foreach(m => buffer.append((m.group(1), m.group(2))))
          }
          case _ =>
        }
        case _ =>
      }
    }
    source.close()

    buffer.toList
  }
}
