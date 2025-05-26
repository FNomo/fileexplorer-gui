package fr.univ_paris_diderot.utils;

import java.time.LocalDateTime;

/**
 * 
 * Notification perméttant de relié un moteur graphique et une application.
 * [Non implémenté]
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 * 
 */
public class Notification {
	
	/** Le titre de la notification  */
	private final String title;
	/** Le message contenu dans la notification */
	private final String message;
	/** Le temps de la notification */
	private final LocalDateTime time;

	/**
	 * 
	 * Création d'une nouvelle notification
	 * 
	 * @param title le titre de la notification
	 * @param message le contenu de la notification
	 * 
	 */
	public Notification(String title, String message){

		this.title = title;
		this.message = message;
		this.time = LocalDateTime.now();
	}

	/** 
	 * 
	 * Obtenir le titre de la notification
	 * 
	 * @return le titre la notification
	 * 
	 */
	public String getTitle() {
		return title;
	}

	/** 
	 * 
	 * Obtenir le message de la notification
	 * 
	 * @return le message la notification
	 * 
	 */
	public String getMessage() {
		return message;
	}

	/** 
	 * 
	 * Obtenir le temps de création de la notification
	 * 
	 * @return le temps la notification
	 * 
	 */
	public LocalDateTime getTime() {
		return time;
	}

}
