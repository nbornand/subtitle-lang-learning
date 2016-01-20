import java.io._
import java.net._
import java.util.zip.ZipInputStream

import ch.nbornand.crawling.{OpenSubtitlesDotOrg, ImdbDotCom, CrawlingUtils}
import ch.nbornand.subtitles.SrtParser
import org.apache.commons.lang3.StringUtils
import org.apache.spark.{SparkContext, SparkConf}

object Main {

  def main(args: Array[String]) = {

    val conf = new SparkConf().setMaster("local[4]")
          .setAppName("Subtitles analytics")
        System.setProperty("hadoop.home.dir", "C:\\hadoop-2.6.0") //only for windows => need winutils in path
        val sc = new SparkContext(conf)

        val replace = Seq("ß" -> "ss", "ü" -> "ue", "ï" -> "ie", "ä" -> "ae", "ë" -> "e:")
        def removeSpecialChars(s:String, substitutions:Seq[(String, String)]): String = substitutions match{
          case first :: rest => removeSpecialChars(s.replaceAll(first._1, first._2), rest)
          case Nil => s
        }
        val lines = sc.wholeTextFiles("subtitles/ger/")
          .flatMap(pair => SrtParser.parse(pair._2))
        val words = lines.map(s => removeSpecialChars(s.toLowerCase, replace))
          .flatMap(s => {
            val items = s.split("[^\\w:]+").filter(_.size > 0)
            //if(items.size > 1) items.zip(items.tail).map(pair => pair._1 + " " + pair._2) else Nil
            items
          })
        val counts = words.countByValue()

        CrawlingUtils.saveToFile("one-grams.txt", counts.toList.sortBy(-_._2).mkString("\r\n"))

//    OpenSubtitlesDotOrg.fetchSubtitlesFor(ImdbDotCom.fetchTop250MoviesNames.drop(50).take(10), "spa")
  }
}