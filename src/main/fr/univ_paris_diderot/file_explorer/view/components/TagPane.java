package fr.univ_paris_diderot.file_explorer.view.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.io.FilenameUtils;

import fr.univ_paris_diderot.utils.Util;

/**
 * Etiquette Permettant d'affiché un Label pouvant être supprimé
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 */
public class TagPane extends JPanel implements ActionListener{

	/** Le lien correspondant à l'étiquette */
	private final String path;
	/** Le bouton rattaché au bouton */
	private final JButton closeButton = new JButton();
	/** Le conteneur */
	private final TagContainerPane containerPane;
	/** L'icon de fermeture */
	private final String CLOSE_ICON = "close";
	/** Taille de l'icone de fermeture */
	private final int ICON_SIZE = 8;

	/**
	 * Constucteur
	 * 
	 * @param path chemin vers le fichier étiquetté
	 * @param win la fenetre prinpal rattaché à l'étiquette
	 */
	public TagPane(String path, TagContainerPane pan){

		this.path = path;
		this.containerPane = pan;

		init();
		setup();
	}

	/**
	 * 
	 * Obtenir le path contenu dans le tag
	 * 
	 * @return la valeur contenu par le tag
	 */
	public String getPath() {

		return path;
	}

	/**
	 * Gerer lorsque l'un evennement à été détecter
	 * 
	 * @param evt l'evennement detecter
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if (evt.getSource() == closeButton)
			handleCloseButton();
	}

	/**
	 * Gere le clique sur le bouton de fermeture
	 */
	private void handleCloseButton(){

		containerPane.performDeleteAction(this);
	}

	/**
	 * Inialisation de l'étiquette
	 */
	private void init(){

		setName("tag");
		closeButton.setName("tagClose");
		closeButton.addActionListener(this);
	}

	/**
	 * Construction et agencement des composant principal du panel
	 */
	private void setup(){

		setLayout(new GridBagLayout());
		var cst = new GridBagConstraints();

		var lab = new JLabel(FilenameUtils.getName(path));
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridx = 0; cst.gridy = 0;
		cst.weightx = 0; cst.weighty = 0;
		add(lab, cst);

		ImageIcon icn = new ImageIcon(Util.getIconPath(CLOSE_ICON, ICON_SIZE, "gray"));
		closeButton.setIcon(icn);
		cst.gridx = 1; cst.gridy = 0;
		cst.weightx = 0; cst.weighty = 0;
		add(closeButton, cst);
	}



}
