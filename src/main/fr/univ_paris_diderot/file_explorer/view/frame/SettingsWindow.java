package fr.univ_paris_diderot.file_explorer.view.frame;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.univ_paris_diderot.file_explorer.controller.MainController;
import fr.univ_paris_diderot.file_explorer.controller.MutableLiveData.Observer;
import fr.univ_paris_diderot.file_explorer.view.components.TagContainerPane;
import fr.univ_paris_diderot.utils.Log;
import fr.univ_paris_diderot.utils.Util;

/**
 * Fenetre destiner à generer les paramètre de l'explorateur de fichier
 * 
 * @author Franc Z.Nomo
 * @version 1.0
 */
public class SettingsWindow extends JDialog implements ItemListener, CustomWindow{

	// --------------------------------- ----------- Variables ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/** La taille par défaut de la fenetre */
	private static final int[] DEFAULT_SIZE = {400, 400};
	/** Le conteneur des favoris */
	private final TagContainerPane favPane;
	/** Le conteneur des extensions d'image */
	private final TagContainerPane imgPane;
	/** Le conteneur des extensions de texte */
	private final TagContainerPane txtPane;
	/** La fentre contenant la nouvelle fenetre */
	private final MainWindow parentWindow;
	/** Le checkbox pour les fichiers caché */
	private final JCheckBox chckbx;
	/** La taille de l'icone */

	/** La taille de l'icone */
	private final static int ICON_SIZE = 14;
	/** L'image de l'icone pour un checkbox */
	private final static  String CHECK_ICON_NAME = "check";
	/** L'image de l'icone pour un checkbox 2 */
	private final static String UNCHECK_ICON_NAME = "uncheck";
	/** L'icone des icone validé */
	private final static ImageIcon CHECK_ICON;
	/** L'icone des icone non validé */
	private final static ImageIcon UNCHECK_ICON;

	/** Le paneau de layer **/
	JLayeredPane layerPane = new JLayeredPane();
	/** Le layer par défaut */
	JPanel mainLayer = new JPanel();
	/** Le layer pour les notifications */
	JPanel popupLayer = new JPanel();
	/** Le layer pour les notifications */
	JPanel dragLayer = new JPanel();

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	




	// --------------------------------- ------------ Création ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	static{
		
		var url = Util.getIconPath(CHECK_ICON_NAME, ICON_SIZE, "diderot");
		CHECK_ICON = new ImageIcon(url);
		url = Util.getIconPath(UNCHECK_ICON_NAME, ICON_SIZE, "diderot");
		UNCHECK_ICON = new ImageIcon(url);
	}

	/**
	 * Constructeur de la fenetre de creation.
	 * @param globalName le nom générale 
	 * @param parentWindow la fenêtre associé
	 */
	public SettingsWindow(MainWindow parentWindow){

		var clr = parentWindow.getController();
		var lbls = parentWindow.getLabels();

		Consumer<String> onAddFav = (t) -> handleNewFavorite(t);
		Consumer<String> onDelFav = (t) -> handleRemoveFavorite(t);
		Consumer<String> onAddImg = (t) -> handleNewImageExtension(t);
		Consumer<String> onDelImg = (t) -> handleRemoveImageExtension(t);
		Consumer<String> onAddTxt = (t) -> handleNewTextExtension(t);
		Consumer<String> onDelTxt = (t) -> handleRemoveTextExtension(t);

		this.parentWindow = parentWindow;
		this.chckbx = new JCheckBox(lbls.getString("textShowHidden"), clr.isHiddenVisibleData.getValue());
		this.favPane = new TagContainerPane(this, lbls.getString("favoritesTitle"), clr.currentFavoritesData.getValue(), onAddFav, onDelFav);
		this.imgPane = new TagContainerPane(this, lbls.getString("imagesExensionTitle"), clr.currentImagesExtensionsData.getValue(), onAddImg, onDelImg);
		this.txtPane = new TagContainerPane(this, lbls.getString("textsExtensionTitle"), clr.currentTextExtensionsData.getValue(), onAddTxt, onDelTxt);

		init();

		Log.d("Created " + Util.printObject(this));
	}

	/**
	 * 
	 * Inialisation de la fenêtre principale
	 * 
	 */
	private void init(){

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(DEFAULT_SIZE[0], DEFAULT_SIZE[1]);
		setResizable(false);
		setTitle(parentWindow.getLabels().getString("settingsTitle")  + " - " + parentWindow.getLabels().getString("title"));

		chckbx.addItemListener(this);
		var ctlr = parentWindow.getController();
		ctlr.currentFavoritesData.observe(new Observer<>((l) -> updateFavoritesList(l)));
		ctlr.currentImagesExtensionsData.observe(new Observer<>((l) -> updateImageExtensionList(l)));
		ctlr.currentTextExtensionsData.observe(new Observer<>((l) -> updateTextExtensionList(l)));

		setup();
	}

	/**
	 * 
	 * Gestion du changement de la liste de favoris
	 * 
	 * @param l la nouvelle liste de favoris
	 */
	private void updateFavoritesList(List<String> l){

		favPane.update(l);
	}

	/**
	 * 
	 * Gestion du changement de la liste de des extensions d'image
	 * 
	 * @param l la nouvelle liste d'extension
	 */
	private void updateImageExtensionList(List<String> l){

		imgPane.update(l);
	}

	/**
	 * 
	 * Gestion du changement de la liste de des extensions de texte
	 * 
	 * @param l la nouvelle liste d'extension
	 */
	private void updateTextExtensionList(List<String> l){

		txtPane.update(l);
	}

	/**
	 * Inialisation de l'affichage du contenu de la fenêtre
	 */
	private void setup(){

		var mainPane = new JPanel(new GridBagLayout());
		
		mainPane.setName("settingsPane");
		GridBagConstraints cst = new GridBagConstraints();
		
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 0; cst.gridy = 0;
		cst.weightx = 0; cst.weighty = 0;
		cst.gridheight = 1;
		mainPane.add(favPane, cst);

		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 0; cst.gridy = 1;
		cst.weightx = 0; cst.weighty = 0;
		cst.gridheight = 1;
		cst.insets = new Insets(10, 0, 10, 0);
		mainPane.add(imgPane, cst);

		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 0; cst.gridy = 2;
		cst.weightx = 1; cst.weighty = 0;
		cst.gridheight = 1;
		cst.insets = new Insets(0, 0, 10, 0);
		mainPane.add(txtPane, cst);

		updateCheckBox();
		cst.gridx = 0; cst.gridy = 3;
		cst.weightx = 0; cst.weighty = 0;
		cst.gridwidth = 1; cst.gridheight = 1;
		cst.insets = new Insets(10, 0, 0, 0);
		mainPane.add(chckbx, cst);

		var leftOverpane = new JPanel();
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 0; cst.gridy = 4;
		cst.weightx = 0; cst.weighty = 1;
		cst.insets = new Insets(0, 0, 0, 0);
		mainPane.add(leftOverpane, cst);

		var root = new JScrollPane(mainPane);
		setContentPane(root);
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- ------ Setters & Getters -------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	@Override
	public MainController getController() {
		return parentWindow.getController();
	}

	@Override
	public ResourceBundle getLabels() {
		return parentWindow.getLabels();
	}

	@Override
	public Container getContentContainer() {
		
		return this.getContentPane();
	}

	@Override
	public JPanel getDragLayer() {

		return dragLayer;
	}

	@Override
	public JPanel getMainLayer() {

		return mainLayer;
	}

	@Override
	public JPanel getPopUpLayer() {

		return popupLayer;
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//






	// --------------------------------- ---- Gestions des étiquettes ---- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Gere l'ajout d'un nouveau favoris
	 * 
	 * @param path le chemin du favoris
	 */
	public void handleNewFavorite(String path){

		parentWindow.getController().newFavoritesData.setValue(path);
	}

	/**
	 * Gere l'ajout d'une nouvelle exension d'image pour la visualisation
	 * 
	 * @param ext le nom de l'extension
	 */
	public void handleNewImageExtension(String ext){

		parentWindow.getController().newImageExtData.setValue(ext);
	}

	/**
	 * Gere l'ajout d'une nouvelle exension de texte pour la visualisation
	 * 
	 * @param ext le nom de l'extension
	 */
	public void handleNewTextExtension(String ext){

		parentWindow.getController().newTxtExtData.setValue(ext);
	}

	/**
	 * Gere la suppression d'un favoris
	 * 
	 * @param fav le nom du favoris
	 */
	public void handleRemoveFavorite(String fav){

		parentWindow.getController().delFavoritesData.setValue(fav);
	}

	/**
	 * Gere la suppression d'une exetension d'image
	 * 
	 * @param ext le nom de l'extension
	 */
	public void handleRemoveImageExtension(String ext){

		parentWindow.getController().delImageExtData.setValue(ext);
	}

	/**
	 * Gere la suppression d'une exetension de texte
	 * 
	 * @param ext le nom de l'extension
	 */
	public void handleRemoveTextExtension(String ext){

		parentWindow.getController().delTxtExtData.setValue(ext);
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ---- La Gestion des boutons ----- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Gestion du clique sur 
	 * 
	 */
	@Override
	public void itemStateChanged(ItemEvent evt) {
		
		if (evt.getSource() == chckbx)
			handleCheckboxSwitch(evt.getStateChange() == 1);
		
	}

	/**
	 * La gestion lorsque le bouton checkbox est clique
	 * 
	 * @param state le nouveau statut
	 */
	private void handleCheckboxSwitch(boolean state){

		parentWindow.getController().isHiddenVisibleData.setValue(state);
		updateCheckBox();
	}

	/**
	 * 
	 * Met à jour l'icone du checkbox
	 * 
	 */
	private void updateCheckBox(){

		if (chckbx.isSelected())
			chckbx.setIcon(CHECK_ICON);
		else 
			chckbx.setIcon(UNCHECK_ICON);
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

}
