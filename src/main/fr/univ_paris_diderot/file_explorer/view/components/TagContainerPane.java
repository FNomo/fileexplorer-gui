package fr.univ_paris_diderot.file_explorer.view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.univ_paris_diderot.file_explorer.view.frame.CustomWindow;
import fr.univ_paris_diderot.utils.Util;

/**
 * Conteneur d'étiquette 
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 */
public class TagContainerPane extends JPanel implements ActionListener{
	

	/** Action à effectuer lors de la suppression d'une étquette */
	private final Consumer<String> onDeteleAction;
	/** Action à effectuer lors de l'ajout d'une étquette */
	private final Consumer<String> onAddAction;
	/** Le panel ou se trouve tous les tag */
	private final JPanel tagsPane = new JPanel(new FlowLayout());
	/** Bouton de création du formulaire d'ajout */
	private final JButton createTagButton = new JButton();
	/** Le champs de text d'ajout */
	private final JTextField txtField = new JTextField();
	/** Bouton de confirmation du formulaire d''ajout */
	private final JButton okTagButton = new JButton();
	/** Bouton de'annulation du formulaire d''ajout */
	private final JButton noTagButton = new JButton();
	/** Icone de fermerture */
	private static final ImageIcon CLOSE_ICON = new ImageIcon(Util.getIconPath("close", 20, "white")) ;
	/** Icone de validation */
	private static final ImageIcon OK_ICON = new ImageIcon(Util.getIconPath("check", 20, "white"));
	/** Icone d'ajout */
	private static final ImageIcon ADD_ICON = new ImageIcon(Util.getIconPath("plus", 20, "white"));
	

	/**
	 * 
	 * Constructeur du Conteneur d'étiquette
	 * 
	 * @param win la fenetre parent
	 * @param titl le titre de la fentre
	 * @param lbls le dictionnaire lié à la fenetre
	 * @param onAdd l'action à effectue en cas d'ajout d'une étiquette
	 * @param onDel l'action à effectue en cas de suppression d'une étiquette
	 */
	public TagContainerPane(CustomWindow win, String titl, List<String> lbls, Consumer<String> onAdd, Consumer<String> onDel){

		this.onDeteleAction = onDel;
		this.onAddAction = onAdd;

		init();
		setup(titl, lbls);
	}

	/**
	 * Initialisation de l'état du controleur
	 */
	private void init(){

		setName("tagContainer");
		okTagButton.setName("confirmButton");
		noTagButton.setName("cancelButton");
		createTagButton.addActionListener(this);
		okTagButton.addActionListener(this);
		noTagButton.addActionListener(this);
	}

	/** 
	 * Mis à jour du conteneur
	 * 
	 * @param tgs la liste de nouvelle etiquette
	 */
	public void update(List<String> tgs){

		tagsPane.removeAll();

		for (var tg: tgs)
			tagsPane.add(new TagPane(tg, this));

		tagsPane.revalidate();
		tagsPane.repaint();
	}

	/**
	 * Inialisation des composants du conteneur
	 * 
	 * @param titl le titre du composant
	 * @param tgs la liste des étiquettes
	 */
	private void setup(String titl, List<String> tgs){

		var noInsets = new Insets(0, 0, 0, 0);

		setLayout(new GridBagLayout());
		var cst = new GridBagConstraints();

		var titleLabel = new JLabel(titl);
		titleLabel.setName("tagTitle");
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.weightx = 1; cst.weighty = 0;
		cst.gridwidth = 3; cst.insets = noInsets;
		cst.gridx = 0; cst.gridy = 0;
		add(titleLabel, cst);

		tagsPane.setMaximumSize(new Dimension(350, getMaximumSize().height));
		update(tgs);
		cst.weightx = 1; cst.weighty = 0;
		cst.gridwidth = 3;
		cst.gridx = 0; cst.gridy = 1;
		cst.insets = new Insets(20, 10, 20, 10);
		add(tagsPane, cst);

		createTagButton.setIcon(ADD_ICON);
		cst.weightx = 1; cst.weighty = 0;
		cst.gridwidth = 3;
		cst.gridx = 0; cst.gridy = 2;
		cst.insets = noInsets;
		add(createTagButton, cst);

		txtField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		txtField.setName("textField");
		cst.fill = GridBagConstraints.BOTH;
		cst.weightx = 1; cst.weighty = 0;
		cst.gridwidth = 1;
		cst.gridx = 0; cst.gridy = 3;
		add(txtField, cst);

		okTagButton.setIcon(OK_ICON);
		cst.weightx = 0; cst.weighty = 0;
		cst.gridwidth = 1;
		cst.gridx = 1; cst.gridy = 3;
		cst.insets = new Insets(0, 7, 0, 7);
		add(okTagButton, cst);

		noTagButton.setIcon(CLOSE_ICON);
		cst.weightx = 0; cst.weighty = 0;
		cst.gridwidth = 1;
		cst.gridx = 2; cst.gridy = 3;
		cst.insets = noInsets;
		add(noTagButton, cst);

		hideAddAction();
	}

	/**
	 * 
	 * Gestion lorsque le clique d'un bouton a été detecté
	 * 
	 * @param evt l'evenement détecter
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {

		if (evt.getSource() == createTagButton)
			showAddAction();

		else if (evt.getSource() == noTagButton)
			hideAddAction();

		else if (evt.getSource() == okTagButton){

			performAddAction(txtField.getText());
			hideAddAction();
		}
	}

	/**
	 * Rends visible les options d'ajout d'une nouvelle étiquette
	 */
	private void showAddAction(){

		createTagButton.setVisible(false);
		txtField.setText("");
		txtField.setVisible(true);
		okTagButton.setVisible(true);
		noTagButton.setVisible(true);
		revalidate();
		repaint();
	}

	/**
	 * Cache les options d'ajout d'une nouvelle étiquette
	 */
	private void hideAddAction(){

		createTagButton.setVisible(true);
		txtField.setText("");
		txtField.setVisible(false);
		okTagButton.setVisible(false);
		noTagButton.setVisible(false);
		revalidate();
		repaint();
	}

	/**
	 * 
	 * Appliquer l'action de suppression
	 * 
	 * @param tag l'étiquette declencheuse de l'action
	 * 
	 */
	public void performDeleteAction(TagPane tag){

		onDeteleAction.accept(tag.getPath());
	}

	/**
	 * Appliquer l'action d'ajout
	 * 
	 * @param path le nouveau chemin à ajouter
	 * 
	 */
	public void performAddAction(String path){

		onAddAction.accept(path);
	}
	

}
