package fr.univ_paris_diderot.file_explorer.view.frame;

import java.awt.Container;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import fr.univ_paris_diderot.file_explorer.controller.MainController;
import fr.univ_paris_diderot.file_explorer.view.components.Draggable;

/**
 * 
 * Fenetre personalisé 
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 * 
 */
public interface CustomWindow {
	
	/**
	 * 
	 * Obtenir le controlleur de la fenetre
	 * 
	 * @return le controlleur gérant la fenetre
	 * 
	 */
	public MainController getController();

	/**
	 * 
	 * Obtenir le dictionnaire de la fenetre
	 * 
	 * @return le dictionnaire de la fenetre
	 * 
	 */
	public ResourceBundle getLabels();

	/***
	 * 
	 * Obtenir le conteneur racine de la fenetre
	 * 
	 * @return le conteneur racine de la fenetre
	 * 
	 */
	public Container getContentContainer();

	/**
	 * 
	 * Obtenir la couche principale de la fenetre
	 * 
	 * @return la couche princiapale de la fenetre
	 * 
	 */
	public JPanel getMainLayer();

	/**
	 * 
	 * Obtenir la couche de notification de la fenetre
	 * 
	 * @return la couche de notification de la fenetre
	 * 
	 */
	public JPanel getPopUpLayer();

	/**
	 * 
	 * Obtenir la couche de trainage de la fenetre
	 * 
	 * @return la couche de trainage
	 * 
	 */
	public JPanel getDragLayer();

	/**
	 * 
	 * Débuter le trainage d'un objet trainable sur une fenetre
	 * 
	 * @param win Fenetre personnalisé
	 * @param drag Objet trainable
	 * 
	 */
	public static void startDragAndDrop(CustomWindow win, Draggable drag){

		drag.startDragging();
	}

	/**
	 * 
	 * Arreter le trainage d'un objet trainable sur une fenetre
	 * 
	 * @param win Fenetre personnalisé
	 * @param drag Objet trainable
	 */
	public static void stopDragAndDrop(CustomWindow win, Draggable drag){

		win.getDragLayer().remove(drag.getContent());
		drag.resetDragging();
		win.getMainLayer().validate();
		win.getMainLayer().repaint();
	}
}
