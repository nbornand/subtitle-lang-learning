package ch.nbornand.subtitles

object SrtParser{

  /**
   * Parse a srt file, format:
   *    1287 [id of the subtitle item, increment by 1]
   *    01:31:18,177 --> 01:31:22,176
   *    Ich sage Ihnen,
   *    ich hab alle Fakten auf meiner Seite. [can be several lines]
   *    [completely empty line as delimiter]
   * @param fileContent, the full file as a string. Expected to be relatively small for .sbt files
   * @return one line per subtitle group (even if they span multiple lines)
   */
  def parse(fileContent:String) = {

    val rn = "\r\n|[\r\n]"
    val timeRegex = "\\d{2}:\\d{2}:\\d{2},\\d{3}" //one 01:31:18,177 block

    def extractGroups(lines:List[String]):List[String] = lines match {
      case id :: timePair :: rest if id.matches("\\d+") && timePair.matches(s"$timeRegex --> $timeRegex") => {
        val subtitleLines = rest.takeWhile(_.trim().length > 0)
        subtitleLines.mkString(" ") :: extractGroups(rest.drop(subtitleLines.length + 1))
      }
      case head :: rest => extractGroups(rest)
      case Nil => Nil
    }

    extractGroups(fileContent.split(rn).toList)
  }
}