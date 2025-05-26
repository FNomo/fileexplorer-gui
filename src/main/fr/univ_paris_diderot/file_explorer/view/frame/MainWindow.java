package fr.univ_paris_diderot.file_explorer.view.frame;

// START
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.OverlayLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import fr.univ_paris_diderot.file_explorer.controller.MainController;
import fr.univ_paris_diderot.file_explorer.controller.MutableLiveData;
import fr.univ_paris_diderot.file_explorer.controller.MutableLiveData.Observer;
import fr.univ_paris_diderot.file_explorer.view.components.FavoritesPane;
import fr.univ_paris_diderot.file_explorer.view.components.NavigationPane;
import fr.univ_paris_diderot.file_explorer.view.components.ViewerPane;
import fr.univ_paris_diderot.utils.*;

/**
 * 
 */
public class MainWindow extends JFrame implements ActionListener, CustomWindow{

	// --------------------------------- ----------- Variables ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/** Taille par ddéfaut */
	private static final int[] DEFAULT_SIZE = {800, 500};
	/** Largeur du panel des favoris */
	private static final int FAV_MENU_WIDTH = 200;
	/** Largeur minimale  */
	private static final int MIN_LABEL_WIDTH = 150;
	/** Hauteur de la Top Bar */
	private static final int FIXED_TOP_HEIGHT = 50;
	/** Taille des titre */
	private static final int TITLE_SIZE = 20;
	/** Taille d'un bouton */
	private static final int BUTTON_SIZE = 20;
	/** L'icone des favoris */
	private static final String STAR_ICON = "star";
	/** L'icone du bouton de recherche */
	private static final String SEARCH_ICON = "search";
	/**L'icone du bouton de paramètre */
	private static final String SETTINGS_ICON = "settings";

	/** Nom Général de l'application */
	private final String globalName;
	/** Controlleurs de l'appplication */
	private final MainController controller;
	/** Langue de l'application */
	private final ResourceBundle labels;

	/** Étiquette contenant le nom du répertoire sélectioner */
	private final JLabel folderSelectionLabel = new JLabel();
	/** Étiquette contenant le fichier sélectioner courant */
	private final JLabel fileSelectionLabel = new JLabel();
	/** Bouton de paramétrage */
	private final JButton settingsButton = new JButton();
	/** Bouton de recherche */
	private final JButton searchButton = new JButton();

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





	// --------------------------------- -------- Setters & Getters ------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * @return {@code FAV_MENU_WIDTH}
	 */
	public static int getFavMenuWidth() {
		return FAV_MENU_WIDTH;
	}

	/**
	 * 
	 * @return {@code FIXED_TOP_HEIGHT}
	 */
	public static int getFixedTopHeight() {
		return FIXED_TOP_HEIGHT;
	}

	/**
	 * 
	 * @return {@code controller}
	 */
	public MainController getController() {
		return controller;
	}

	/**
	 * 
	 * @return {@code lables}
	 */
	public ResourceBundle getLabels() {
		return labels;
	}

	@Override
	public Container getContentContainer() {
		
		return this.getContentPane();
	}

	/**
	 * Changer le titre de la fenêtre
	 * @param s le nouveau sous-titre
	 */
	@Override
	public void setTitle(String s) {

		String newTitle = (s == null || s.equals(""))? globalName : s + " - " + globalName;
		super.setTitle(newTitle);
		Log.d(Util.printObject(this) + " title has changed to " + newTitle);
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




	// --------------------------------- ---------- Création ------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Constructeur d'une fenêtre
	 * 
	 * @param globalName le titre principale
	 * @param controller le controlleur de la fenêtre
	 * @param labels le dictionnaire à utiliser
	 * 
	 */
	public MainWindow(MainController controller, ResourceBundle labels){

		this.globalName = labels.getString("title");
		this.controller = controller;
		this.labels = labels;
		init();

		Log.d("Created " + Util.printObject(this));
	}

	/**
	 * 
	 * Initialisation de la fenêtre principale
	 * 
	 */
	private void init(){

		setSize(DEFAULT_SIZE[0], DEFAULT_SIZE[1]);
		setMinimumSize(new Dimension(DEFAULT_SIZE[0], DEFAULT_SIZE[1]));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		layerPane.setOpaque(true);

		setLayout(new GridBagLayout());
		setTitle(controller.currentFolderData.getValue());

		addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(WindowEvent e){
				controller.isOpenLiveData.setValue(false);
			}
		});

		controller.currentFolderData.observe(new MutableLiveData.Observer<String>((newFolderPath) -> {

			SwingUtilities.invokeLater(() -> {

				folderSelectionLabel.setText(newFolderPath);
				folderSelectionLabel.repaint();
				Log.d(Util.printObject(MainWindow.this) + " folder selected [" + newFolderPath + "]");
				setTitle(newFolderPath);
			});
		}));

		controller.currentFileData.observe(new Observer<>((curFile) -> {

			if (curFile != null){

				fileSelectionLabel.setText(curFile.getName());
				Log.d(Util.printObject(MainWindow.this) + " folder selected [" + curFile.getAbsolutePath() + "]");
			}

		}));

		setup();
	}


	/**
	 * 
	 * Création du de panneau principal
	 * 
	 */
	private void setup(){

		Log.d(Util.printObject(this) + " initializing ...");

		layerPane.setLayout(new OverlayLayout(layerPane));
		mainLayer.setLayout(new BorderLayout());
		
		popupLayer.setLayout(new CardLayout());
		dragLayer.setLayout(null);

		var noInsets = new Insets(0, 0, 0, 0);
		var customInsets = new Insets(10, 10, 10, 10);

		var root = new JPanel();
		root.setName("mainPane");
	
		root.setLayout(new GridBagLayout());
		GridBagConstraints cst = new GridBagConstraints();

		var url = Util.getIconPath(STAR_ICON, TITLE_SIZE, "white");
		var favTitle = new JLabel(labels.getString("titleOfFavPanel"), SwingConstants.CENTER);
		favTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.decode("#871436")));
		favTitle.setPreferredSize(new Dimension(FAV_MENU_WIDTH, MainWindow.getFixedTopHeight()));
		favTitle.setMinimumSize(new Dimension(FAV_MENU_WIDTH, MainWindow.getFixedTopHeight()));
		favTitle.setName("favTitle");
		favTitle.setIcon(new ImageIcon(url));

		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 0; cst.weightx = 0; cst.gridwidth = 1;
		cst.gridy = 0;  cst.weighty = 0;  cst.gridheight = 1;
		cst.insets = noInsets;
		root.add(favTitle, cst);

		folderSelectionLabel.setPreferredSize(new Dimension(MIN_LABEL_WIDTH, MainWindow.getFixedTopHeight()));
		folderSelectionLabel.setText(controller.currentFolderData.getValue());
		folderSelectionLabel.setName("folderSelector");
		folderSelectionLabel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#871436")),
			BorderFactory.createEmptyBorder(0, 10, 0, 10)
			));
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 1; cst.weightx = .8; cst.gridwidth = 1;
		cst.gridy = 0;  cst.weighty = 0;  cst.gridheight = 1;
		cst.insets = customInsets;
		root.add(folderSelectionLabel, cst);

		url = Util.getIconPath(SEARCH_ICON, BUTTON_SIZE, "white");
		searchButton.addActionListener(this);
		searchButton.setName("searchButton");
		searchButton.setIcon(new ImageIcon(url));

		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 2; cst.weightx = 0; cst.gridwidth = 1;
		cst.gridy = 0;  cst.weighty = 0;  cst.gridheight = 1;
		cst.insets = customInsets;
		root.add(searchButton, cst);

		fileSelectionLabel.setPreferredSize(new Dimension(MIN_LABEL_WIDTH, MainWindow.getFixedTopHeight()));
		fileSelectionLabel.setName("fileSelector");
		fileSelectionLabel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#871436")),
			BorderFactory.createEmptyBorder(0, 10, 0, 10)
			));
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 3; cst.weightx = .2; cst.gridwidth = 1;
		cst.gridy = 0;  cst.weighty = 0;  cst.gridheight = 1;
		cst.insets = customInsets;
		root.add(fileSelectionLabel, cst);

		url = Util.getIconPath(SETTINGS_ICON, BUTTON_SIZE, "white");
		settingsButton.setName("settingsButton");
		settingsButton.addActionListener(this);
		settingsButton.setIcon(new ImageIcon(url));

		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 4; cst.weightx = 0; cst.gridwidth = 1;
		cst.gridy = 0;  cst.weighty = 0;  cst.gridheight = 1;
		cst.insets = customInsets;
		root.add(settingsButton, cst);

		var favpane = new FavoritesPane(this);
		cst.fill = GridBagConstraints.VERTICAL;
		cst.gridx = 0; cst.weightx = 0; cst.gridwidth = 1;
		cst.gridy = 1;  cst.weighty = 1;  cst.gridheight = 1;
		cst.insets = noInsets;
		root.add(favpane, cst);

		var scrollPane = new JScrollPane(new NavigationPane(this));
		scrollPane.setName("navigationScrollPane");
		scrollPane.getHorizontalScrollBar().setName("navigationScrollBar");
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 1; cst.weightx = 0; cst.gridwidth = 2;
		cst.gridy = 1;  cst.weighty = 1;  cst.gridheight = 1;
		root.add(scrollPane, cst);

		var vpane = new ViewerPane(this);
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 3; cst.weightx = 0; cst.gridwidth = 2;
		cst.gridy = 1;  cst.weighty = 1;  cst.gridheight = 1;
		root.add(vpane, cst);

		mainLayer = root;
		
		//mainLayer.add(root, BorderLayout.CENTER);
		//layerPane.add(mainLayer);
		//layerPane.add(popupLayer);
		//layerPane.add(dragLayer);

		cst.fill = GridBagConstraints.BOTH;
		cst.gridx =0; cst.gridy = 0;
		cst.weightx = 1; cst.weighty = 1;
		cst.gridheight = 1; cst.gridwidth = 1;
		setContentPane(mainLayer);

		for (var c: mainLayer.getComponents())
			c.setFocusable(false);

		Log.d("... " + Util.printObject(this) + " initialization succeed");
	}

	
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ------ Gestion des actions ------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Gestions du click sur un bouton
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent event){

		if (event.getSource() == settingsButton) handleSettingsButton();
		else if (event.getSource() == searchButton) handleSearchButton();
	}

	/**
	 * 
	 * Gestion du bouton de recherche
	 * 
	 */
	private void handleSearchButton(){
		
		var win = new SearchWindow(this, globalName);
		win.setVisible(true);
	}

	/**
	 * 
	 * Gestion du bouton de paramètre
	 * 
	 */
	private void handleSettingsButton(){

		var win = new SettingsWindow(this);
		win.setVisible(true);
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
}
