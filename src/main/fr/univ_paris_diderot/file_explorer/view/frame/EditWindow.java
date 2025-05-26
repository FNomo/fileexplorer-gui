package fr.univ_paris_diderot.file_explorer.view.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FilenameUtils;

import fr.univ_paris_diderot.utils.Util;

/**
 * 
 * Fenêtre crée pour la modification d'un nom de fichier
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 * 
 */
public class EditWindow extends JDialog implements ActionListener{

	/** Le chemin du fichier à modifier */
	private String path;

	/** Le nom du champs de texte de renommage */
	private final JTextField renameField;
	/** Le bouton de confirmation */
	private final JButton renameButton = new JButton(RENAME_ICON);
	/** Le bouton d'annulation */
	private final JButton cancelButton = new JButton(CANCEL_ICON);
	/** La fenetre parent */
	private final CustomWindow parentWindow;

	/** L'icone pour le bouton de renommage */
	private static final ImageIcon RENAME_ICON;
	/** L'icone pour le bouton d'annulation */
	private static final ImageIcon CANCEL_ICON;

	static {

		RENAME_ICON = new ImageIcon(Util.getIconPath("edit", 20, "white"));
		CANCEL_ICON = new ImageIcon(Util.getIconPath("close", 20, "white"));
	}
	
	/**
	 * 
	 * Le contructeur de la fenetre. Crée la fenetre et l'initailise.
	 * 
	 * @param win la fenetre parent
	 * @param path le chemin du fichier à rennomer
	 * 
	 */
	public EditWindow(CustomWindow win, String path){

		this.parentWindow = win;
		this.path = path;
		this.renameField = new JTextField(FilenameUtils.getName(this.path));

		init();
	}

	/**
	 * 
	 * Initialise les paramètre par défaut de la fenetre
	 * 
	 */
	private void init(){

		setTitle(FilenameUtils.getName(path) + " " + parentWindow.getLabels().getString("renameFile") );

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(400, 100));
		setResizable(false);

		renameButton.addActionListener(this);
		cancelButton.addActionListener(this);

		setup();
	}

	/**
	 * 
	 * Organise l'affichage principal de la fenêtre
	 * 
	 */
	private void setup(){

		var root = new JPanel(new GridBagLayout());
		root.setName("customWindow");
		var cst = new GridBagConstraints();

		renameField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		cst.fill = GridBagConstraints.BOTH;
		cst.gridx = 0; cst.gridy = 0;
		cst.weightx = 1; cst.weighty = 0;
		cst.gridwidth = 1;
		root.add(renameField, cst);

		cst.gridx = 1;
		cst.weightx = 0;
		cst.insets = new Insets(0, 10, 0, 0);
		root.add(renameButton, cst);

		cst.gridx = 2;
		root.add(cancelButton, cst);
		setContentPane(root);
	}

	/**
	 * 
	 * Gere la confirmation du renommage.
	 * Soumets la valeur du champs de rennomage au controlleur de la fenêtre parent. 
	 * 
	 */
	private void handleConfirmation(){

		var newName = renameField.getText();
		var controller = parentWindow.getController();

		if (newName.equals(""))
			return;

		else {
			controller.fileToRenameData.setValueNoAlert(path);
			controller.newFileNameData.setValue(newName);
			dispose();
		}
	}

	/**
	 * 
	 * Gere l'annulation du rennomage.
	 * Vide le champs de rennomage et le reremplie par la valeur par défaut.
	 * 
	 */
	private void handleCancel(){

		renameField.setText(FilenameUtils.getName(this.path));
	}

	/**
	 * 
	 * Gestion de l'appuie sur un bouton de la fenetre.
	 * Lorsque qu'il s'agit du bouton de confirmation , on confirme le renommage.
	 * S'il s'agit du bouton d'annulaton, on annule le renommage.
	 * 
	 * @param event l'evennement generer par le bouton
	 * 
	 * @see EditWindow#handleConfirmation()
	 * @see EditWindow#handleCancel()
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		if (event.getSource() == renameButton)
			handleConfirmation();
		else if (event.getSource() == cancelButton)
			handleCancel();
	}

}
