package fr.univ_paris_diderot.file_explorer.view.frame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;

import fr.univ_paris_diderot.file_explorer.controller.MainController;
import fr.univ_paris_diderot.file_explorer.controller.MutableLiveData.Observer;
import fr.univ_paris_diderot.file_explorer.view.components.FilesPane;
import fr.univ_paris_diderot.utils.Util;

/**
 * 
 * Fenetre destiner à generer la recherche
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 * 
 */
public class SearchWindow extends JDialog implements ActionListener, CustomWindow {
	
	// --------------------------------- ----------- Variables ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/** La taille par défaut de la fenetre */
	private static final int[] DEFAULT_SIZE = {400, 400};
	/** La fentre contenant la nouvelle fenetre */
	private final MainWindow parentWindow;
	/** Le champs de texte de recherche */
	private final JTextField searchField = new JTextField();
	/** Le bouton de recherche */
	private final JButton searchButton = new JButton(SEARCH_ICON);
	/** Le bouton d'annulation */
	private final JButton cancelButton = new JButton(CANCEL_ICON);
	/** Le champs de fichier */
	private final FilesPane filesPane;
	/** La taille des icones */
	private final static int ICON_SIZE = 20;
	/** Le nom de l'icone de recherche */
	private final static String SEARCH_ICON_NAME = "search";
	/** Le nom de l'icone d'annulation */
	private final static String CANCEL_ICON_NAME = "close";
	/** L'icone de recherche */
	private final static ImageIcon SEARCH_ICON;
	/** L'icone d'annulation */
	private final static ImageIcon CANCEL_ICON;

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

	static {

		SEARCH_ICON = new ImageIcon(Util.getIconPath(SEARCH_ICON_NAME, ICON_SIZE, "white"));
		CANCEL_ICON = new ImageIcon(Util.getIconPath(CANCEL_ICON_NAME, ICON_SIZE, "white"));
	}

	/**
	 * 
	 * Constructeur
	 * 
	 * @param parentWindow Le fenetre parent
	 * @param globalName Le nom principale
	 * 
	 */
	public SearchWindow(MainWindow parentWindow, String globalName){

		this.parentWindow = parentWindow;
		this.filesPane = new FilesPane(this, true, true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(DEFAULT_SIZE[0], DEFAULT_SIZE[1]);
		setResizable(false);
		setTitle(parentWindow.getLabels().getString("searchTitle")  + " - " + globalName);

		init();
		setup();
	}

	/**
	 * 
	 * L'initialisation des composants
	 */
	private void init(){

		var controller = parentWindow.getController();

		setLayout(new BorderLayout());
		mainLayer.setName("customWindow");
		searchButton.setName("confirmButton");
		cancelButton.setName("cancelButton");
		searchField.addActionListener(this);
		searchButton.addActionListener(this);
		cancelButton.addActionListener(this);

		controller.searchRequestData.setValueNoAlert(null);
		
		controller.searchResponseData.observe(new Observer<>((files) -> {
			filesPane.updateContent(files);
		}));
	}

	/**
	 * 
	 * L'initialisation de l'affichage
	 */
	private void setup(){

		layerPane.setLayout(new OverlayLayout(layerPane));
		mainLayer.setLayout(new BorderLayout());
		popupLayer.setLayout(new CardLayout());
		dragLayer.setLayout(null);

		var root = new JPanel(new GridBagLayout());

		var cst = new GridBagConstraints();

		searchField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 0; cst.gridy = 0;
		cst.weightx = 1; cst.weighty = 0;
		cst.gridwidth = 1;
		root.add(searchField, cst);

		cst.gridx = 1;
		cst.weightx = 0;
		cst.insets = new Insets(0, 10, 0, 0);
		root.add(searchButton, cst);

		cst.gridx = 2;
		root.add(cancelButton, cst);

		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 0; cst.gridy = 1;
		cst.weightx = 1; cst.weighty = 1;
		cst.gridwidth = 3;
		cst.insets = new Insets(0, 0, 0, 0);
		root.add(filesPane, cst);

		mainLayer.add(root, BorderLayout.CENTER);
		layerPane.add(mainLayer);
		layerPane.add(popupLayer);
		layerPane.add(dragLayer);
		add(layerPane, BorderLayout.CENTER);
		
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





	// --------------------------------- ----- Gestion des boutons ------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Gestion de l'action
	 * 
	 * @param evt l'evenement detecter
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if (evt.getSource() == cancelButton)
			clearTextField();
		else if (evt.getSource() == searchButton)
			submitTextField();
		
	}

	/**
	 * 
	 * Efface le contenu du champs de texte
	 * 
	 */
	private void clearTextField(){

		searchField.setText("");
		filesPane.clear();
	}

	/**
	 * 
	 * Soumet la recherche au controlleur
	 * 
	 */
	private void submitTextField(){

		filesPane.clear();
		parentWindow.getController().searchRequestData.setValue(searchField.getText());
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

}
