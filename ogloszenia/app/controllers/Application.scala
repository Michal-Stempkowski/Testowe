package controllers

import models._
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

/**
  * Zajmuje się obsługą strony z ogłoszeniami.
  * @author Michał Stempkowski
  * 
  */
object Application extends Controller {

  /** Formularz do obsługi dodawania nowych ogłoszeń. */
  val adForm = Form("text" -> nonEmptyText)

  /** Ilość ogłoszeń wyświetlanych na stronie */
  val results = 10

  /**
    * Metoda wyświetlająca 'results pierwszych ogłoszeń.
    * @return gotowa strona html
    */
  def announces = Action {
    Ok(views.html.index(Announcement all results, adForm))
  }
  
  /**
    * Metoda dokonująca przekierowania ze strony '/' na '/announces/.'
    * @return gotowa strona html (przekierowanie)
    */
  def index = Action {
   Redirect(routes.Application.announces)       
  }

  /**
    * Metoda dodająca nowe ogłoszenie, powoduje odświeżenie strony.
    * @return gotowa strona html, wyświetla na niej ewentualne błędy
    */
  def newAnnouncement = Action { implicit request =>
  	adForm.bindFromRequest.fold(
  			errors => BadRequest(views.html.index(Announcement.all(results), errors)),
  			label => {
          Announcement create label
  				Redirect(routes.Application.announces)
  			}
  	)
  }
  
}