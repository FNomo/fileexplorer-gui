package fr.univ_paris_diderot.file_explorer.view.components;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;


import fr.univ_paris_diderot.file_explorer.controller.MutableLiveData.Observer;
import fr.univ_paris_diderot.file_explorer.view.frame.CustomWindow;
import fr.univ_paris_diderot.utils.Log;
import fr.univ_paris_diderot.utils.Util;

/**
 * Panneau de visualisation de fichier
 * @author Franc Zobo Nomo
 * @version 1.0
 */
public class ViewerPane extends JPanel {

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	
	/** Taille minimal du pnneau */
	private static final int MIN_WIDTH = 300;

	/** Contenue principale */
	private JPanel contentView;
	/** Layout du contenu principale */
	private CardLayout contentLayout;
	/** Layout du panneau */
	private CardLayout layout;
	/** Visualiseur de Texte */
	private JTextPane vpaneTxt;
	/** Visualiseur d'image */
	private ImagePane vpaneImg;
	/** Etiquette décrivant la taille d'un fichier */
	private JLabel sizeLabel;
	/** Etiquette décrivant la date de création d'un fichier */
	private JLabel creationLabel;
	/** Les clés pour le type de contenu */
	private static final String[] keys = {"nothing", "content"};
	/** Les clés pour le type de visualiseur */
	private static final String[] contentKeys = {"img", "txt", "nothing"};
	/** Parent de la fenêtre */
	private final CustomWindow parentWindow;

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- ----------- Création ------------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Contructeur du panneau de visualisation
	 * @param parentWindow la fenêtre auquel est rattaché le panneau
	 */
	public ViewerPane(CustomWindow parentWindow) {
		
		this.parentWindow = parentWindow;
		setPreferredSize(new Dimension(MIN_WIDTH, super.getPreferredSize().height));
		setMinimumSize(new Dimension(MIN_WIDTH, super.getPreferredSize().height));
		initComponents();
		observeMainController();

		Log.d("Created " + Util.printObject(this));
	}

	/**
	 * Initialisation des composants du contenu du panneau
	 */
	private void initComponents(){

		var labels = parentWindow.getLabels();

		layout = new CardLayout();
		setLayout(layout);

		var vpaneNothing = new JLabel();
		add(keys[0], vpaneNothing);
		
		var paneContent = new JPanel(new GridBagLayout());
		paneContent.setName("viewerGlobal");
		contentLayout = new CardLayout();

		contentView = new JPanel(contentLayout);
		vpaneImg = new ImagePane();
		vpaneTxt = new JTextPane();
		var icn = new ImageIcon(Util.getIconPath("no_viewer", 50, ""));
		var vpaneNothingContent = new JLabel(icn);
		vpaneNothing.setAlignmentX(CENTER_ALIGNMENT);
		vpaneNothing.setAlignmentY(CENTER_ALIGNMENT);
		contentView.add(contentKeys[0], vpaneImg);
		contentView.add(contentKeys[1], vpaneTxt);
		contentView.add(contentKeys[2], vpaneNothingContent);

		var cst = new GridBagConstraints();

		var color = Color.decode("#871436");
		
		contentView.setName("contentView");
		contentView.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(3, 3, 3, 3, color), 
			BorderFactory.createEmptyBorder(5, 5, 5, 5) ));
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx =0; cst.weightx = 1.0; cst.gridwidth = 2;
		cst.gridy =0; cst.weighty = 1.0;
		cst.insets = new Insets(0, 0, 10, 0);
		paneContent.add(contentView, cst);


		var pane1 = new JLabel(labels.getString("sizeLabel") + ":");
		pane1.setName("contentViewLeft");
		cst.fill = GridBagConstraints.BOTH; cst.gridwidth = 1;
		cst.gridx =0; cst.weightx = 0.0;
		cst.gridy =1; cst.weighty = 0.0;
		cst.insets = new Insets(0, 0, 5, 0);
		paneContent.add(pane1, cst);

		sizeLabel = new JLabel();
		sizeLabel.setAlignmentX(CENTER_ALIGNMENT);
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx =1; cst.weightx = 1.0;
		cst.gridy =1; cst.weighty = 0.0;
		cst.insets = new Insets(0, 0, 5, 0);
		paneContent.add(sizeLabel, cst);

		var pane2 = new JLabel(labels.getString("creationLabel") + ":");
		pane2.setName("contentViewLeft");
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx =0; cst.weightx = 0.0;
		cst.gridy =2; cst.weighty = 0.0;
		cst.insets = new Insets(0, 0, 0, 0);
		paneContent.add(pane2, cst);

		creationLabel = new JLabel();
		creationLabel.setAlignmentX(CENTER_ALIGNMENT);
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx =1; cst.weightx = 1.0;
		cst.gridy =2; cst.weighty = 0.0;
		paneContent.add(creationLabel, cst);

		add(keys[1], paneContent);
		layout.show(this, keys[0]);
	}

	/**
	 * Gestion des évennements passées par le controlleur rattaché à la fenêtre.
	 */
	private void observeMainController(){

		var controller = parentWindow.getController();
		var labels = parentWindow.getLabels();

		controller.currentFileData.observe(new Observer<>((curFile) -> {

			if (curFile != null){

				var fcontent = controller.currentFileContentData.getValue();
				sizeLabel.setText(Util.getSizeOf(curFile) + labels.getString("bytes"));
				creationLabel.setText(String.format(labels.getString("dateFormat"), Util.getCreationDate(curFile)));

				if (fcontent != null){

					if (fcontent instanceof Image){

						vpaneImg.setImg((Image) fcontent);
						contentLayout.show(contentView, contentKeys[0]);
					}
	
					else if (fcontent instanceof String){
	
						vpaneTxt.setText((String) fcontent);
						contentLayout.show(contentView, contentKeys[1]);
					}
				}

				else 
					contentLayout.show(contentView, contentKeys[2]);

				layout.show(this, keys[1]);
			}

			else 
				layout.show(this, keys[0]);
		}));

	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

}
