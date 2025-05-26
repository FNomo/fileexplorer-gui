package fr.univ_paris_diderot.file_explorer.view.components;

import java.awt.Dimension;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import fr.univ_paris_diderot.file_explorer.controller.MutableLiveData.Observer;
import fr.univ_paris_diderot.file_explorer.view.frame.CustomWindow;
import fr.univ_paris_diderot.file_explorer.view.frame.MainWindow;
import fr.univ_paris_diderot.utils.Log;
import fr.univ_paris_diderot.utils.Util;

/**
 * 
 * Panneau de visualisation d'une liste de Fichier
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 * 
 */
public class FilesPane extends JScrollPane {

	// --------------------------------- ---------- Variables ------------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/** Le panneau principale */
	protected final JPanel mainPane = new JPanel();
	/** La fenêtre associé */
	protected final CustomWindow parentWindow;
	/** Le nom du style de l'élément */
	private final String styleNameString;
	/** La couleur de l'icone des label */
	private final String icnColor;
	/** Forcer l'affichage des fichiers caché */
	private final boolean forceHiddenFiles;
	/** Afficher le chement relatif des fichiers */
	private final boolean relativePath;

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- ----------- Création ------------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Construire un panneau de fichiers
	 * 
	 * @param parentWindow la fenetre associé
	 * 
	 */
	public FilesPane(CustomWindow parentWindow) {

		this(parentWindow, "filesPane", "", false, false);
	}

	/**
	 * 
	 * Construire un panneau de fichiers
	 * 
	 * @param parentWindow la fennetre associé
	 * @param force forcé l'afficahge de fichier caché
	 * @param relativePath affiché le chemin relatif des fichiers
	 * 
	 */
	public FilesPane(CustomWindow parentWindow, boolean force, boolean relativePath){

		this(parentWindow, "filesPane", "", force, relativePath);
	}

	/**
	 * 
	 * Construire un panneau de fichiers
	 * 
	 * @param parentWindow la fenetre associé
	 * @param styleName le nom du style
	 * @param icnColor la couleur de l'icone
	 * @param force force l'affihcage de fichier caché
	 * 
	 */
	protected FilesPane(CustomWindow parentWindow, String styleName, String icnColor, boolean force, boolean relativePath){

		this.parentWindow = parentWindow;
		this.styleNameString = styleName;
		this.icnColor = icnColor;
		this.forceHiddenFiles = force;
		this.relativePath = relativePath;

		init();
		setup();

		Log.d("Created " + Util.printObject(this));
	}

	/**
	 * 
	 * Inialisation des composants principaux
	 * 
	 */
	private void setup(){

		setPreferredSize(new Dimension(MainWindow.getFavMenuWidth() + 20, getPreferredSize().height));
		mainPane.setMaximumSize(new Dimension(MainWindow.getFavMenuWidth(), mainPane.getMaximumSize().height));

		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));
		setViewportView(mainPane);
	}

	/**
	 * 
	 * Initialise le contenu
	 * 
	 */
	private void init(){

		setName(styleNameString);
		mainPane.setName("filesPaneContent");
		getVerticalScrollBar().setName(styleNameString + "ScrollBar");
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		parentWindow.getController().isHiddenVisibleData.observe(new Observer<>((t) -> updateHiddenFile()));
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//







	// --------------------------------- ---------- Mis à jour ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//


	/** 
	 * 
	 * La mise à jour de la visibilité des fichier cachés
	 * 
	 */
	private void updateHiddenFile(){

		boolean showHidden = parentWindow.getController().isHiddenVisibleData.getValue() || forceHiddenFiles;
	
		for (var comp: mainPane.getComponents()){

			if (comp instanceof FileLabel){

				var f = (FileLabel) comp;

				if (f.getText().startsWith(".") && !showHidden)
					f.setVisible(false);

				else 
					f.setVisible(true);
			}
		}

		mainPane.revalidate();
		mainPane.repaint();
		revalidate();
		repaint();
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- --------- Mis à jour ---------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Supprésion de tous le contenu
	 * 
	 */
	public void clear(){

		mainPane.removeAll();
	}

	/**
	 * 
	 * Mis à jour du contenu du panneau
	 * 
	 * @param files La liste de nouveau fichier à afficher
	 * @param showHiddenFiles Affiché ou non les fichiers cachés
	 * 
	 */
	public void updateContent(File[] files){

		SwingUtilities.invokeLater(() -> {

			clear();

			for (var f: files){

				var filePane = new FileLabel(parentWindow, f, icnColor, relativePath);
				mainPane.add(filePane);

				Log.d(Util.printObject(this) + " has added " + Util.printObject(filePane));
			}

			Log.i(Util.printObject(this) + " has been updated");

			updateHiddenFile();

		});
	}

	/**
	 * 
	 * Obtenir le panneau principal du visualiseur
	 * 
	 * @return Panneau du visualiseur
	 * 
	 */
	public JPanel getMainPane() {
		return mainPane;
	}

	/**
	 * 
	 * La fenetre parent rattaché au composant
	 * 
	 * @return Fenetre parent
	 * 
	 */
	public CustomWindow getParentWindow() {
		return parentWindow;
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

}
