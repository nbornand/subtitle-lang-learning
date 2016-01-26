package ch.nbornand.crawling

import java.io.InputStream
import java.net.URLEncoder
import java.util.zip.ZipInputStream

import org.apache.commons.io.IOUtils
import org.apache.log4j.Logger

/**
 * API to crawl the http://opensubtitles.org/ website
 * /!\ The site has a limit of requests per IP per 24h that is pretty low
 * and sometimes use Captacha that would make batches of requests fail
 */
object OpenSubtitlesDotOrg {

  private lazy val logger = Logger.getLogger(this.getClass)

  def cleanMovieName(movieName:String) = movieName.toLowerCase.toCharArray
    .filter(c => c.isLetterOrDigit || c == ' ')
    .mkString.trim().replaceAll("\\s+", " ")

  /**
   * Perform a full text search with a movie name to obtain its id. When there is no ambiguity a single
   * movie is advertised (and we get a subtitle id as well), otherwise we pick the first one in the list
   * and only get the id.
   * TODO: for movie series (ex: machete kills again in space), the best match is not always first
   * @param movieName, no pre processing needed
   * @param languageCode, 3 letters
   * @return (movie id, subtitle id, normalized movie name)
   */
  def getMovieInformationFor(movieName: String, languageCode: String): Option[(List[Int], String)] = {

    val movieForSearch = cleanMovieName(movieName)
    val movieSearchUrl = s"http://www.opensubtitles.org/en/search2/sublanguageid-$languageCode/moviename-" + movieForSearch

    def lookForInfosAt(url: String): (Option[Int], Option[List[Int]]) = {
      CrawlingUtils.performHttpGet(url) match {
        case None => {
          logger.error(s"request failed: $movieSearchUrl")
          (None, None)
        }
        case Some(stream) => {
          val content = IOUtils.toString(stream)
          val movieIdOption = s"search/sublanguageid-$languageCode/idmovie-(\\d+)".r
            .findFirstMatchIn(content)
            .map(_.group(1).toInt)
          val subtitlesIdsOption = inspectHtmlForSubtitleLinks(content)
          (movieIdOption, if (subtitlesIdsOption.isDefined) subtitlesIdsOption
          else "dl.opensubtitles.org/\\w{2}/download/sub/(\\d+)".r
            .findFirstMatchIn(content)
            .map(mat => List(mat.group(1).toInt)))
        }
      }
    }

    lookForInfosAt(movieSearchUrl) match {
      case (_, Some(subtitleIds)) => {
        logger.warn(s"subtitle ids $subtitleIds found directly for $movieName")
        Some(subtitleIds, movieForSearch)
      }
      case (Some(movieId), _) => {
        logger.warn(s"movie id for $movieName is $movieId")
        val singleMovie = s"http://www.opensubtitles.org/en/search/sublanguageid-$languageCode/idmovie-$movieId"
        lookForInfosAt(singleMovie) match {
          case (_, Some(subtitleIds)) => {
            logger.warn(s"subtitles $subtitleIds for $movieName")
            Some(subtitleIds, movieForSearch)
          }
          case _ => {
            logger.warn(s"no subtitles found for $movieName")
            None
          }
        }
      }
      case _ => {
        logger.warn(s"no movie id found for $movieName")
        None
      }
    }
  }

  /**
   * Return the id of the most downloaded subtitle file. For some movies, a special page is displayed
   * with a single download link instead of the full list (else clause).
   * @param htmlPage resulting from [[getMovieInformationFor]]
   * @return the list of subtitle ids, if any
   */
  private def inspectHtmlForSubtitleLinks(htmlPage:String, takeTopNDownload:Int = 3):Option[List[Int]] = {

     val re = """href="subtitleserve/sub/(\d+)"[^>]*?>(\d+)x""".r

     //find all the links for subtitles
     val subtitlesLinks = re.findAllIn(htmlPage).matchData
       .map(mat => (mat.group(2).toInt, mat.group(1)))
       .toList.sortBy(-_._1)
       .map(_._2.toInt)

     val subtitleId = if (!subtitlesLinks.isEmpty){
       Some(subtitlesLinks.take(Math.max(subtitlesLinks.length, takeTopNDownload)))
     } else {
       "dl.opensubtitles.org/\\w{2}/download/sub/(\\d+)".r
         .findFirstMatchIn(htmlPage)
         .map(mat => List(mat.group(1).toInt))
     }

     subtitleId
  }

  /**
   * Download zips corresponding to the subtitles ids until a .srt file is found (if any, Option).
   * So far the other subtitle formats are ignored, as they cumulatively represent a minority of the subtitles.
   * TODO:target another subtitles files when no match or support those formats
   * @param subtitleIds, already target subtitles in a specific language
   * @return (fileNameInZip:String, fileContent:String)
   */
  def getSubtitleFileFor(subtitleIds: List[Int]):Option[(String,String)] = {

    def getFirstSrtInZipAt(sbtZipUrl:String) = {

      def collectAllSrtInStream(zip: ZipInputStream): List[(String, String)] = zip.getNextEntry match {
        case null => Nil
        case entry => (entry.getName, IOUtils.toString(zip)) :: collectAllSrtInStream(zip)
      }

      CrawlingUtils.performHttpGet(sbtZipUrl) match {
        case Some(stream) => {

          val sbtFiles = collectAllSrtInStream(new ZipInputStream(CrawlingUtils.performHttpGet(sbtZipUrl).get))
            .filter(_._1.endsWith(".srt"))

          if (sbtFiles.isEmpty) {
            logger.warn(s"no sbt file in the zip at $sbtZipUrl");
            None
          }
          else {
            logger.warn(s"sbt file found: ${sbtFiles.head._1}");
            Some(sbtFiles.head)
          }
        }
        case None => logger.error(s"request failed for $sbtZipUrl"); None
      }
    }

    def trySrtIdsUntilMatch(ids:List[Int]):Option[(String,String)] = ids match {
      case firstId :: restOfIds => {
        val sbtZipUrl = s"http://dl.opensubtitles.org/en/download/sub/vrf-108d030f/$firstId"
        val srt = getFirstSrtInZipAt(sbtZipUrl)
        if(srt.isDefined) srt else trySrtIdsUntilMatch(restOfIds)
      }
      case Nil => None
    }

    trySrtIdsUntilMatch(subtitleIds)
  }

  // TODO: specify language, add comment
  def fetchSubtitlesFor(movieList:Seq[String], language:String) = {

    val knownLanguageCodes = Seq("ger", "eng", "spa")
    assert(language.size == 3, "language codes have length 3")
    if(!knownLanguageCodes.contains(language)){
      logger.warn(s"language '$language' not in white list: [${knownLanguageCodes.mkString(",")}]")
    }

    val srtRoot = s"subtitles/$language/"
    println("fetching subtitles for: "+movieList)

    val subtitleFiles = movieList
      .flatMap(movieName => OpenSubtitlesDotOrg.getMovieInformationFor(movieName, language))
      .flatMap{
      case (subtitlesIds, normalizedMovieName) => {
        OpenSubtitlesDotOrg.getSubtitleFileFor(subtitlesIds)
          .map((_, normalizedMovieName))
      }
    }

    subtitleFiles.foreach{
      case ((_, content), name) => {
        val filePath = srtRoot+name.replaceAll(" ", "-")+".srt"
        CrawlingUtils.saveToFile(filePath, content)
      }
    }
  }
}
