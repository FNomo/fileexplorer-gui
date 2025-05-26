package fr.univ_paris_diderot.file_explorer.view.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import fr.univ_paris_diderot.file_explorer.controller.MutableLiveData;
import fr.univ_paris_diderot.file_explorer.view.frame.CustomWindow;
import fr.univ_paris_diderot.utils.Log;
import fr.univ_paris_diderot.utils.Util;

/**
 * Panneau pour la naviagation de fichier
 * @author Franc Zobo Nomo
 * @version 1.0
 */
public class NavigationPane extends JPanel {

	// -------------------------------- ------------ Variables ------------ --------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/** Fenetre parent */
	private final CustomWindow parentWindow;

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ----------- Création ------------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Constructeur du panneau
	 * @param parentWindow fenêtre parent
	 */
	public NavigationPane(CustomWindow parentWindow) {
		
		super(new GridBagLayout());
		this.parentWindow = parentWindow;

		init();
		setup();
		
		Log.d("Created " + Util.printObject(this));
	}

	/**
	 * Gestion des évenenements passés par le controlleur rattaché à la fenêtre
	 */
	private void init(){

		setName("navigation");

		var controller = parentWindow.getController();

		controller.currentFolderData.observe(new MutableLiveData.Observer<String>((newFolderPath) -> {

			SwingUtilities.invokeLater(() -> {

				Log.d(Util.printObject(NavigationPane.this) + " folder label new value [" + newFolderPath + "]");
			});
		}));

		controller.filesTreeData.observe(new MutableLiveData.Observer<>((filesList) -> {
			update(filesList);
		} ));

	}

	/**
	 * 
	 * Création de l'affichage du parent
	 * 
	 * @param filesList la liste de tableau de fichiers
	 * 
	 */
	private void setup(){

		update(parentWindow.getController().filesTreeData.getValue());
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// -------------------------------- ---------- Mis à jour -------------- -------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Mis à jour à partir d'une liste de fichier actualiser
	 * 
	 * @param newFilesList la nouvelle liste
	 * @param controller le controlleur qui gère la fenetre parent
	 * 
	 */
	private void update(List<File[]> newFilesList){

		removeAll();

		int index = 0;
		var cst = new GridBagConstraints();
		
		for (File[] files : newFilesList) {
			
			var filesPane = new FilesPane(parentWindow);
			
			filesPane.updateContent(files);
			
			cst.fill = GridBagConstraints.VERTICAL;
			cst.gridx =index; cst.weightx = 0.0;
			cst.weighty = 1.0;
			add(filesPane, cst);
			index++;
		}

		var pane = new JPanel();
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx =index; cst.weightx = 1.0;
		cst.weighty = 1.0;
		add(pane, cst);

		revalidate();
		repaint();

		SwingUtilities.invokeLater(() -> {

			var parent = getParent();
			if (parent != null) parent = parent.getParent();

			if (parent instanceof JScrollPane){
			
				var horizBar = ((JScrollPane) parent).getHorizontalScrollBar();
				horizBar.setValue(horizBar.getMaximum() - 1);
			}
		});

		

		

	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

}
