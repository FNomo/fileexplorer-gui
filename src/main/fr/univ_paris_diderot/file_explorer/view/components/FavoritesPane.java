package fr.univ_paris_diderot.file_explorer.view.components;

import java.awt.Dimension;
import java.io.File;
import java.util.List;

import javax.swing.SwingUtilities;

import fr.univ_paris_diderot.file_explorer.controller.MutableLiveData;
import fr.univ_paris_diderot.file_explorer.view.frame.CustomWindow;
import fr.univ_paris_diderot.file_explorer.view.frame.MainWindow;
import fr.univ_paris_diderot.utils.Log;
import fr.univ_paris_diderot.utils.Util;

/**
 * Le panneau des favoris 
 * @author Franc Zobo Nomo
 * @version 1.0
 * @see FilesPane
 */
public class FavoritesPane extends FilesPane{

	// --------------------------------- ------------ Création ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Constructeur du panneau
	 * @param parentWindow la fenetre parent
	 */
	public FavoritesPane(CustomWindow parentWindow) {
		
		super(parentWindow, "favoritesFilesPane", "white", true, false);
		init();

		Log.d("Created " + Util.printObject(this));
	}

	/**
	 * Gestion des évennements du controlleur de l'application
	 */
	private void init(){

		setPreferredSize(new Dimension(MainWindow.getFavMenuWidth(), getPreferredSize().height));
		setMinimumSize(new Dimension(MainWindow.getFavMenuWidth(), getPreferredSize().height));

		var controller = parentWindow.getController();
		controller.currentFavoritesData.observe(new MutableLiveData.Observer<>((favList) -> update(favList) ));

		setup();
	}

	/**
	 * 
	 * Initialisation de l'affichage
	 *
	 */
	private void setup(){

		update(parentWindow.getController().currentFavoritesData.getValue());
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ---------- Mis à jour ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Mis à jour du panneau des favoris
	 * @param paths la liste des nouveaux favoris
	 */
	public void update(List<String> paths){

		SwingUtilities.invokeLater(() -> {

			File[] fs = new File[paths.size()];

			for (int i = 0; i < fs.length; i++) {
				fs[i] = new File(paths.get(i));
			}

			updateContent(fs);
			revalidate();
			repaint();
			Log.i(Util.printObject(this) + " has been updated");
		});

	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
}
