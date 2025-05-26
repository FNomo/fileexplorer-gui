package fr.univ_paris_diderot.file_explorer.view.frame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 * Fenetre pour apercevoir les permissions d'un fichier
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 * 
 */
public class PermissionsWindow extends JDialog implements ActionListener {

	
	/** Fenetre parent */
	private final CustomWindow parentWindow;
	/** Le chemin du fichier */
	private final String path;
	/** Le selecteur d'accès pour le propriétaire du dossier */
	private JComboBox<String> ownerAccessBox = null;
	/** Le selecteur d'accès pour le groupe du dossier */
	private JComboBox<String> groupAccessBox = null;
	/** Le selecteur d'accès pour les autres du dossier */
	private JComboBox<String> othersAccessBox = null;
	/** Liste des droits pour le fichier rattaché à la fenetre */
	private Set<PosixFilePermission> permissions = null;

	/**
	 * 
	 * Le constructeur de la fenetre
	 * 
	 * @param win Fenetre parent
	 * @param path Chemin du fichier
	 * 
	 */
	public PermissionsWindow(CustomWindow win, String path){

		this.parentWindow = win;
		this.path = path;
		init();
	}


	/**
	 * 
	 * L' initialisation de la fenetre
	 * 
	 */
	private void init(){

		setTitle(FilenameUtils.getName(path) + " "+ parentWindow.getLabels().getString("rightsFile") );

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(400, 400));
		setResizable(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {

				super.windowClosed(e);
				savePermission();
			}
		});

		setup();
	}

	/**
	 * 
	 * Inialisation de l'affichage de la fenetre
	 * 
	 * @see PermissionsWindow#setup_file()
	 * @see PermissionsWindow#setup_folder()
	 * 
	 */
	private void setup(){

		var dics = parentWindow.getLabels();
		var root = new JPanel(new GridBagLayout());
		root.setName("permissionRoot");
		var f = new File(path);

		if (f.isDirectory()){

			String[] access1 = {dics.getString("folderAccessRead"), dics.getString("folderAccessWrite"), dics.getString("folderAccessReadAndWrite")};
			String[] access2 = {dics.getString("accessNone"), dics.getString("folderAccessRead"), dics.getString("folderAccessWrite"), dics.getString("folderAccessReadAndWrite")};
			setup_combobox(f, root, access1, access2);
		}
			

		else {

			String[] access1 = {dics.getString("fileAccessRead"), dics.getString("fileAccessWrite"), dics.getString("fileAccessReadAndWrite")};
			String[] access2 = {dics.getString("accessNone"), dics.getString("fileAccessRead"), dics.getString("fileAccessWrite"), dics.getString("fileAccessReadAndWrite")};
			setup_combobox(f, root, access1, access2);
		}

		//var scroll = new JScrollPane(root);
		setContentPane(root);
	}

	/**
	 * 
	 * Inialisation de l'affichage de la fenetre pour un répertoire
	 * 
	 * @param root le panneau principal
	 * 
	 */
	private void setup_combobox(File f, JPanel root, String[] access1, String[] access2){

		try{

			permissions = Files.getPosixFilePermissions(f.toPath());
			var attr = Files.getFileAttributeView(f.toPath(), PosixFileAttributeView.class).readAttributes();
			var dics = parentWindow.getLabels();
			var cst = new GridBagConstraints();
			var insetsLeft = new Insets(0, 0, 10, 10);
			var insetsRight = new Insets(0, 0, 10, 0);
			
			var ownerAccessInd = 0;
			var groupAccessInd = 0;
			var othersAccessInd = 0;


			for (var p : permissions) {
				
				if (p == PosixFilePermission.OWNER_READ)
					ownerAccessInd += 1;

				else if (p == PosixFilePermission.GROUP_READ)
					groupAccessInd += 1;

				else if (p == PosixFilePermission.OTHERS_READ)
					othersAccessInd += 1;

				if (p == PosixFilePermission.OWNER_WRITE)
					ownerAccessInd += 2;

				else if (p == PosixFilePermission.GROUP_WRITE)
					groupAccessInd += 2;

				else if (p == PosixFilePermission.OTHERS_WRITE)
					othersAccessInd += 2;
			}

			ownerAccessInd = (ownerAccessInd -1 > -1)? ownerAccessInd - 1 : 0;

			var comboBoxRenderer = new DefaultListCellRenderer(){

				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					
					var c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					c.setName("comboBoxRenderer");
					return c;
				}
			};

			JLabel lab = new JLabel(dics.getString("owner"));
			lab.setName("permissionTitle");
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 0; cst.gridy = 0;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsLeft;
			root.add(lab, cst);

			lab = new JLabel( System.getProperty("user.name").equals( attr.owner().getName() )? dics.getString("me"): attr.owner().getName());
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 1; cst.gridy = 0;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsRight;
			root.add(lab, cst);
			
			lab = new JLabel(dics.getString("access"));
			lab.setName("permissionTitle");
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 0; cst.gridy = 1;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsLeft;
			root.add(lab, cst);

			ownerAccessBox = new JComboBox<String>(access1);
			ownerAccessBox.setSelectedIndex(ownerAccessInd);
			ownerAccessBox.setRenderer(comboBoxRenderer);
			ownerAccessBox.addActionListener(this);
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 1; cst.gridy = 1;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsRight;
			root.add(ownerAccessBox, cst);

			lab = new JLabel(dics.getString("group"));
			lab.setName("permissionTitle");
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 0; cst.gridy = 2;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsLeft;
			root.add(lab, cst);

			lab = new JLabel(attr.group().getName());
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 1; cst.gridy = 2;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsRight;
			root.add(lab, cst);
			
			lab = new JLabel(dics.getString("access"));
			lab.setName("permissionTitle");
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 0; cst.gridy = 3;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsLeft;
			root.add(lab, cst);

			groupAccessBox = new JComboBox<String>(access2);
			groupAccessBox.setSelectedIndex(groupAccessInd);
			groupAccessBox.setRenderer(comboBoxRenderer);
			groupAccessBox.addActionListener(this);
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 1; cst.gridy = 3;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsRight;
			root.add(groupAccessBox, cst);

			lab = new JLabel(dics.getString("others"));
			lab.setName("permissionTitle");
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 0; cst.gridy = 4;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsLeft;
			root.add(lab, cst);
			
			lab = new JLabel(dics.getString("access"));
			lab.setName("permissionTitle");
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 0; cst.gridy = 5;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsLeft;
			root.add(lab, cst);

			othersAccessBox = new JComboBox<String>(access2);
			othersAccessBox.setSelectedIndex(othersAccessInd);
			othersAccessBox.setRenderer(comboBoxRenderer);
			othersAccessBox.addActionListener(this);
			cst.fill = GridBagConstraints.BOTH;
			cst.gridx = 1; cst.gridy = 5;
			cst.weightx = 0; cst.weighty = 0;
			cst.insets = insetsRight;
			root.add(othersAccessBox, cst);
		}

		catch (IOException e){

		}

		
	}

	@Override
	public void actionPerformed(ActionEvent evt) {

		if (permissions == null)
			return;

		var src = evt.getSource();

		if ( src == ownerAccessBox ){

			var selectedIndex = ownerAccessBox.getSelectedIndex();

			if (selectedIndex == 0){

				permissions.add(PosixFilePermission.OWNER_READ);
				permissions.remove(PosixFilePermission.OWNER_WRITE);
			}

			else if (selectedIndex == 1){

				permissions.add(PosixFilePermission.OWNER_WRITE);
				permissions.remove(PosixFilePermission.OWNER_READ);
			}

			else {

				permissions.add(PosixFilePermission.OWNER_READ);
				permissions.add(PosixFilePermission.OWNER_WRITE);
			}
		}

		else if ( src == groupAccessBox ){

			var selectedIndex = groupAccessBox.getSelectedIndex();

			if (selectedIndex == 1){

				permissions.add(PosixFilePermission.GROUP_READ);
				permissions.remove(PosixFilePermission.GROUP_WRITE);
			}

			else if (selectedIndex == 2){

				permissions.add(PosixFilePermission.GROUP_WRITE);
				permissions.remove(PosixFilePermission.GROUP_READ);
			}

			else if (selectedIndex == 3) {

				permissions.add(PosixFilePermission.GROUP_READ);
				permissions.add(PosixFilePermission.GROUP_WRITE);
			}

			else {

				permissions.remove(PosixFilePermission.GROUP_READ);
				permissions.remove(PosixFilePermission.GROUP_WRITE);
			}
		}

		else {

			var selectedIndex = othersAccessBox.getSelectedIndex();

			if (selectedIndex == 1){

				permissions.add(PosixFilePermission.OTHERS_READ);
				permissions.remove(PosixFilePermission.OTHERS_WRITE);
			}

			else if (selectedIndex == 2){

				permissions.add(PosixFilePermission.OTHERS_WRITE);
				permissions.remove(PosixFilePermission.OTHERS_READ);
			}

			else if (selectedIndex == 3) {
				
				permissions.add(PosixFilePermission.OTHERS_READ);
				permissions.add(PosixFilePermission.OTHERS_WRITE);
			}

			else {

				permissions.remove(PosixFilePermission.OTHERS_READ);
				permissions.remove(PosixFilePermission.OTHERS_WRITE);
			}
		}
		
	}


	/**
	 * 
	 * Mettre à jour les permissions du fichier.
	 * 
	 */
	private void savePermission(){

		if (permissions == null) return;

		try {
			
			File f = new File(path);
			Files.setPosixFilePermissions(f.toPath(), permissions);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
