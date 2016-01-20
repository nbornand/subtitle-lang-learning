package ch.nbornand.crawling

import org.apache.commons.io.IOUtils
import org.apache.log4j.Logger

object ImdbDotCom{

  def fetchTop250MoviesNames:List[String] = {

    val top250Url = "http://www.imdb.com/chart/top"
    val content = IOUtils.toString(CrawlingUtils.performHttpGet(top250Url).get)

    val movieTitle = "<td class=\"titleColumn\">\\s*\\d+.\\s*<a [^<]+?title=[^<]+?>([^<]+?)</a>".r
    movieTitle.findAllIn(content).matchData
      .map(_.group(1)).toList
  }
}
