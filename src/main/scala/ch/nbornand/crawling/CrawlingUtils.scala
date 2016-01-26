package ch.nbornand.crawling

import java.io.{IOException, FileOutputStream, InputStream}
import java.net.{HttpURLConnection, URL}

import org.apache.log4j.Logger

object CrawlingUtils{

  private lazy val logger = Logger.getLogger(this.getClass)

  def performHttpGet(url:String):Option[InputStream] = {

    try{
      val connection = new URL(url).openConnection().asInstanceOf[HttpURLConnection];
      connection.setRequestMethod("GET");
      connection.addRequestProperty("Cookie" ,"PHPSESSID=huqgel901l4spktmgh213tvdi6;")
      connection.setInstanceFollowRedirects(true)
      if(connection.getResponseCode / 100 == 2) Some(connection.getInputStream)
      else{
        logger.error("response code: "+connection.getResponseCode)
        None
      }
    } catch{
      case e:IOException => {
        logger.error(s"request failed: $url",e)
        None
      }
    }
  }

  def saveToFile(filePath:String, content:String):Unit = {

    println(s"write $filePath")
    val out = new FileOutputStream(filePath);
    //otherwise is saves with Charset.defaultCharset() that is Windows-1252
    out.write(content.getBytes("UTF-8"));
    out.close();
  }

}
