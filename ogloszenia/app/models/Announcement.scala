package models

import anorm._
import anorm.SqlParser._
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import play.api.db._
import play.api.Play.current

/**
  * Klasa przechowująca informacje nt. ogłoszeń
  * @author Michał Stempkowski
  * @constructor Tworzy obiekt ogłoszenia o podanych danych
  * @param id Unikalny identyfikator w bazie danych
  * @param date Data w postaci milisekund od początku epoki
  * @param text Treść ogłoszenia o maksymalnej długości 160 znaków
  */
case class Announcement(id: Int, date: Long, text: String) {

  /**
    * Metoda konwertująca datę z Long na format zależny od kraju
    * @param lang identyfikator kraju
    * @param dateFormat format daty w danym kraju
    * @return sformatowana data zgodna z formatem
    */
  def printDate(lang: String, dateFormat: String) = {
    if(!(Announcement.dateMap contains lang))
    {
      Announcement.dateMap + (lang -> new SimpleDateFormat(dateFormat))
    }

    Announcement.dateMap get lang getOrElse new SimpleDateFormat(dateFormat) format new Date(date)
  }
}

object Announcement {
  /** Mapa powiązań kod kraju - obiekt formatu daty. */
  val dateMap: Map[String, SimpleDateFormat] = Map()

  /**
    * Parser odpowiedzi bazy danych.
    * @return gotowy obiekt typu Announcement
    */
  val parser = {
    get[Int]("id") ~
    get[String]("txt") ~
    get[Long]("time") map {
      case id ~ txt ~ time => Announcement(id, time, txt)
    }
  }

  /**
    * Metoda zwracająca 'ammount ostatnich ogłoszeń z bazy, uporządkowanych od najnowszego.
    * @param ammount ilość wyników, które mają być zwrócone
    * @return Lista (List[Announcement]) z dziesięcioma, uporządkowanymi ogłoszeniami
    */
  def all(ammount: Int): List[Announcement] = DB.withConnection { implicit c =>
    SQL("select top {ammount} * from announces order by time desc").on(
        'ammount -> ammount
      ).as(parser *)
  }

  /**
    * Metoda dodająca do bazy o nowe ogłoszenie.
    * @param text tekst ogłoszenia
    * @return Unit
    */
  def create(text: String) {
    DB.withConnection { implicit c =>
      SQL("insert into announces (txt, time) values ({txt}, {time})").on(
        'txt -> text,
        'time -> Calendar.getInstance().getTimeInMillis()
      ).executeUpdate()
    }
  }
}