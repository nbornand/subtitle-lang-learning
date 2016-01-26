import java.io._
import java.net._
import java.util.zip.ZipInputStream

import ch.nbornand.crawling.{SrtParser, OpenSubtitlesDotOrg, ImdbDotCom, CrawlingUtils}
import ch.nbornand.wikimedia.{Word, WikiMarkupParser, WikiMarkupLexerTokenizer, WikiDumpParser}
import opennlp.tools.postag.{POSTaggerME, POSModel}
import opennlp.tools.sentdetect.{SentenceDetectorME, SentenceModel}
import opennlp.tools.tokenize.{TokenizerME, TokenizerModel}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import collection.mutable._

import scala.io.Source
import scala.reflect.ClassTag
import scala.xml.pull._

object Main {

  def main(args: Array[String]):Unit = {

//    val conf = new SparkConf().setMaster("local[4]")
//      .setAppName("Subtitles analytics")
//    System.setProperty("hadoop.home.dir", "C:\\hadoop-2.6.0") //only for windows => need winutils in path
//    val sc = new SparkContext(conf)
//
//    def checkpoint[T:ClassTag](path:String, computation:() => RDD[T]):RDD[T] = {
//      val file = new File(path)
//      val list = if (file.exists()) {
//        sc.objectFile(path)
//      } else {
//        val result = computation()
//        result.saveAsObjectFile(path).asInstanceOf[T]
//        result
//      }
//      list
//    }
//
//    val lines = sc.wholeTextFiles("subtitles/ger/se7en.srt")
//      .flatMap(pair => SrtParser.parse(pair._2))
//
//    val words = lines.mapPartitions(it => {
//      val pathPrefix = "nlp-models/de-"
//      val splitter = new SentenceDetectorME(new SentenceModel(new FileInputStream(pathPrefix + "sent.bin")))
//      val tokenizer = new TokenizerME(new TokenizerModel(new FileInputStream(pathPrefix + "token.bin")))
//      val posTager = new POSTaggerME(new POSModel(new FileInputStream(pathPrefix + "pos-maxent.bin")))
//      it.flatMap(splitter.sentDetect)
//        .flatMap(line => {
//        val tokens = tokenizer.tokenize(line)
//        val res = tokens.zip(posTager.tag(tokens))
//        res
//      })
//    })
//    //words.count()
//
//    val counts = checkpoint[(String, String, Int)]("checkpoints/srt-stats" ,() => words.groupBy(w => w).map(pair => (pair._1._1, pair._1._2, pair._2.size)))
//    //counts.filter(_._1.toLowerCase == "komm").foreach(println)
//
//    val wikt = checkpoint[Word]("checkpoints/wiktionary-pos" ,() => {
//      sc.parallelize[Word](WikiDumpParser.collectWordsInDump("wiki/dewiktionary-20160111-pages-meta-current.xml").toList)
//    }).filter(_.gender.isDefined).take(30).foreach(println)
//
//    counts.join(wikt).sortBy(-_._2._1).take(50).foreach(println)

    OpenSubtitlesDotOrg.fetchSubtitlesFor(ImdbDotCom.fetchTop250MoviesNames.drop(50).take(10), "ger")
  }
}