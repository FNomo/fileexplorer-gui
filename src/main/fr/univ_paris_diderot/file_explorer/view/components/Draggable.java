package fr.univ_paris_diderot.file_explorer.view.components;

import java.awt.Container;
import java.awt.Point;

import javax.swing.SwingUtilities;

import fr.univ_paris_diderot.file_explorer.view.frame.CustomWindow;

/**
 * 
 * Interface pour un objet trainable
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 * 
 */
public interface Draggable {

	/**
	 * 
	 * Demmarage du trainage de l'objet
	 * 
	 */
	public void startDragging();

	/**
	 * 
	 * Reinitialisation de l'objet après le trainage
	 * 
	 */
	public void resetDragging();

	/**
	 * 
	 * Obtenir le point précedent de l'objet en fonction du trainage
	 * 
	 * @return le point précédent
	 * 
	 */
	public Point getPreviousLocation();

	/**
	 * 
	 * Modifier le point précedent de l'objet en fonction du trainage
	 * 
	 * @param p le nouveau point
	 * 
	 */
	public void setPreviousLocation(Point p);

	/**
	 * 
	 * Obtenir le Container racine de l'objet trainable
	 * 
	 * @return le Container racine
	 * 
	 */
	public Container getContent();

	/**
	 * 
	 * Obtenir le parent initiale de l'objet
	 * 
	 * @return le parent initiale de l'objet
	 * 
	 */
	public Container getDefaultParent();

	/**
	 * 
	 * Performer une traine d'un objet trainable à en fonction d'un point de la souris.
	 * 
	 * @param win la fenetre contenant l'objet
	 * @param drag l'objet à traner
	 * @param evtPoint le point ou se situe la souris
	 * 
	 */
	public static void drag(CustomWindow win, Draggable drag, Point evtPoint){

		var content = drag.getContent();

		var location = SwingUtilities.convertPoint(content, evtPoint, win.getMainLayer());
		drag.setPreviousLocation(location);

		content.setLocation( location);
		win.getMainLayer().repaint();
		
	}

}
