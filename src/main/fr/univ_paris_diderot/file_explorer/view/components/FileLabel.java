package fr.univ_paris_diderot.file_explorer.view.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;

import fr.univ_paris_diderot.file_explorer.ApplicationGUI;
import fr.univ_paris_diderot.file_explorer.controller.MainController;
import fr.univ_paris_diderot.file_explorer.view.frame.CustomWindow;
import fr.univ_paris_diderot.file_explorer.view.frame.EditWindow;
import fr.univ_paris_diderot.file_explorer.view.frame.MainWindow;
import fr.univ_paris_diderot.file_explorer.view.frame.PermissionsWindow;
import fr.univ_paris_diderot.utils.Log;
import fr.univ_paris_diderot.utils.Util;

/**
 * Panneau servant à répresentant un fichier dans une fenetre
 * @author Franc Zobo Nomo
 * @version 1.0
 */
public class FileLabel extends JLabel implements MouseListener, MouseMotionListener, Draggable, ActionListener{

	// --------------------------------- --------- Variables ------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/** Le chemein du fichier */
	private final String path;
	/** La fenetre associé */
	private final CustomWindow parentWindow;
	/** Le nom de l'icone de fichier normal */
	private final static String DEFAULT_FILE_ICON = "file";
	/** Le nom de l'icone de fichier texte */
	private final static String TEXT_FILE_ICON = "text_file";
	/** Le nom de l'icone de fichier image */
	private final static String IMAGE_FILE_ICON = "img_file";
	/** Le nom de l'icone de dossier */
	private final static String FOLDER_ICON = "folder";
	/** Le nom de l'icone de dossier home */
	private final static String HOME_ICON = "home";
	/** Le nom de l'icone de dossier poubelle*/
	private final static String TRASH_ICON = "trash";
	/** La taille de l'icone */
	private final static int ICON_SIZE = 15;
	/** La couleur de l'icone */
	private final String icnColor;
	/** Le point initial */
	private Point initialPoint = null;
	/** Le parent initial */
	private Container parentContainer = null;
	/** La position dans le parent */
	private int initialZorder = -1;
	/** Le point précédent pour le dragging */
	private Point previousPoint = null;
	/** Savoir si l'oobjet a été dragged */
	private boolean isDragged = false;

	/** Le menu contextuel*/
	private final JPopupMenu popupMenu = new JPopupMenu();
	/** L'item d'ajout de favoris */
	private JMenuItem favoritetItem = null;
	/** L'item de renommage */
	private JMenuItem editItem = null;
	/** L'item de droit */
	private JMenuItem rightsItem = null;
	/** L'item de suppression */
	private JMenuItem deleteItem = null;

	/** Le cursor de selection */
	private static Cursor selectedCursor = new Cursor(Cursor.HAND_CURSOR);
	/** Icone pour l'item EDIT */
	private static String ICON_FAVORITE = "star";
	/** Icone pour l'item EDIT */
	private static String ICON_EDIT = "edit";
	/** Icone pour l'item RIGHTS */
	private static String ICON_RIGHTS = "rights";
	/** Icone pour l'item DELETE */
	private static String ICON_TRASH = "trash";

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- ----------- Création ------------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Constructeur du panneau 
	 * @param parentWindow la fenetre associer
	 * @param f le fichier à représenter
	 * @param relative Savoir si le nom est relatif au répertoire courant
	 */
	public FileLabel(CustomWindow parentWindow, File f, String color, boolean relative){

		this.parentWindow = parentWindow;
		this.icnColor = color;
		this.path = f.getAbsolutePath();

		init(relative);
		
		Log.d("Created " + Util.printObject(this));
	}

	/**
	 * 
	 * Initialisation du panneau
	 * 
	 */
	private void setup(){

		var labels = parentWindow.getLabels();
		ImageIcon icon;

		popupMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true) );

		icon = new ImageIcon(Util.getIconPath(ICON_FAVORITE, ICON_SIZE, "diderot"));
		favoritetItem = new JMenuItem(labels.getString("favoriteFile"), icon);
		favoritetItem.setCursor(selectedCursor);
		favoritetItem.setName("filePopUpFavoriteItem");
		favoritetItem.addActionListener(this);
		popupMenu.add(favoritetItem);

		icon = new ImageIcon(Util.getIconPath(ICON_EDIT, ICON_SIZE, ""));
		editItem = new JMenuItem(labels.getString("renameFile"), icon);
		editItem.setCursor(selectedCursor);
		editItem.setName("filePopUpItem");
		editItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, KeyEvent.CTRL_DOWN_MASK));
		editItem.addActionListener(this);
		popupMenu.add(editItem);

		icon = new ImageIcon(Util.getIconPath(ICON_RIGHTS, ICON_SIZE, ""));
		rightsItem = new JMenuItem(labels.getString("rightsFile"), icon);
		rightsItem.setCursor(selectedCursor);
		rightsItem.setName("filePopUpItem");
		rightsItem.addActionListener(this);
		rightsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK));
		popupMenu.add(rightsItem);

		icon = new ImageIcon(Util.getIconPath(ICON_TRASH, ICON_SIZE, "red"));
		deleteItem = new JMenuItem(labels.getString("deleteFile"), icon);
		deleteItem.setCursor(selectedCursor);
		deleteItem.setName("filePopUpItemDelete");
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK));
		deleteItem.addActionListener(this);
		popupMenu.add(deleteItem);

		setComponentPopupMenu(popupMenu);

		update();
	}

	/**
	 * 
	 * Initialisation des écouteurs d'évennements
	 * 
	 * @param relative Savoir si le nom est relatif par rapport à la racine
	 * 
	 */
	private void init(boolean relative){

		var controller = parentWindow.getController();
		var lbl = parentWindow.getLabels();
		var fname = FilenameUtils.getName(path);

		if (path.equals(ApplicationGUI.getTrashfolder().getAbsolutePath()))
			fname = lbl.getString("trashFolder");
		else if (path.equals(ApplicationGUI.getHomefolder().getAbsolutePath()))
			fname = lbl.getString("homeFolder");
		else if (relative)
			fname = path.replaceFirst(Matcher.quoteReplacement(controller.currentFolderData.getValue()), ".");
		
		setText(fname);

		popupMenu.setOpaque(true);
		setOpaque(true);

		setCursor(selectedCursor);
		setName("fileLabel");

		addMouseListener(this);
		addMouseMotionListener(this);

		setup();
	}

	/**
	 * 
	 * Obtenir la taille préféré par l'objet
	 * 
	 * @return la valeur de l'icone
	 * 
	 */
	@Override
	public Dimension getPreferredSize() {

		var dim = super.getPreferredSize();
		return new Dimension(MainWindow.getFavMenuWidth(), dim.height);
	}

	@Override
	public Container getContent() {
		
		return this;
	}

	@Override
	public Container getDefaultParent() {
		
		return parentContainer;
	}

	@Override
	public Point getPreviousLocation() {
		
		return previousPoint;
	}

	@Override
	public void setPreviousLocation(Point p) {
		
		previousPoint = p;
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- ------------ Gestion ------------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Sélection du fichier
	 */
	private void selectNewFile(){

		var controller = parentWindow.getController();
		var txt = path;
		File f = new File(txt);

		if (! f.exists()) return;
		if (f.isDirectory()) controller.selectedFolderData.setValue(txt);
		else controller.selectedFileData.setValue(txt);
	}

	/**
	 * Mis à jour de l'affichage des fichiers
	 */
	private void update(){

		MainController ctlr = parentWindow.getController();
		var f = new File(path);
		URL icn_url;

		if (path.equals(ApplicationGUI.getHomefolder().getAbsolutePath()))
			icn_url = Util.getIconPath(HOME_ICON, ICON_SIZE, icnColor);
		else if (path.equals(ApplicationGUI.getTrashfolder().getAbsolutePath()))
			icn_url = Util.getIconPath(TRASH_ICON, ICON_SIZE, icnColor);
		else if (f.isDirectory())
			icn_url = Util.getIconPath(FOLDER_ICON, ICON_SIZE, icnColor);
		else if (Util.inList(ctlr.currentImagesExtensionsData.getValue(), Util.getFileExtension(f.getName())))
			icn_url = Util.getIconPath(IMAGE_FILE_ICON, ICON_SIZE, icnColor);
		else if (Util.inList(ctlr.currentTextExtensionsData.getValue(), Util.getFileExtension(f.getName())))
			icn_url = Util.getIconPath(TEXT_FILE_ICON, ICON_SIZE, icnColor);
		else
			icn_url = Util.getIconPath(DEFAULT_FILE_ICON, ICON_SIZE, icnColor);
		
		ImageIcon icn = new ImageIcon(icn_url);
		setIcon(icn);
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ---- -- Drag and Drop ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Gestion d'un clique de la souris sur le composant.
	 * 
	 * @param e Evennement de souris
	 * 
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1 && (e.getComponent() instanceof FileLabel)){
			Log.d(Util.printObject(FileLabel.this) + " mouse click");
			selectNewFile();
		}
		
	}

	/**
	 * 
	 * Gestion lorsque la souris est trainé dans le composant
	 * 
	 * @param e Evennement de souris
	 * 
	 */
	@Override
	public void mouseDragged(MouseEvent e) {

		if (initialPoint == null)
			return;
		
		isDragged = true;
		Draggable.drag(parentWindow, this, e.getPoint());
	}

	/**
	 * 
	 * Gestion lorsque la souris entre dans le comosant
	 * 
	 * @param e Evennement de souris
	 * 
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}

	/**
	 * 
	 * Gestion lorsque la souris sort du composant
	 * 
	 * @param e Evennement de souris
	 * 
	 */
	@Override
	public void mouseExited(MouseEvent e) {}

	/**
	 * 
	 * Gestion lorsque la souris bouge dans le composant
	 * 
	 * @param e Evennement de souris
	 * 
	 */
	@Override
	public void mouseMoved(MouseEvent e) {}

	/**
	 * 
	 * Gestion lorsque la souris est appuyé dans le composant
	 * 
	 * @param e Evennement de souris
	 * 
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		if (e.isPopupTrigger()){

			popupMenu.show(e.getComponent(), e.getX(), e.getY());
			return;
		}

		if (e.getButton()  != MouseEvent.BUTTON1)
			return;
		
		parentContainer = getParent();

		var location = SwingUtilities.convertPoint(this, e.getPoint(), parentContainer) ;
		var p = SwingUtilities.convertPoint(this, e.getPoint(), parentWindow.getMainLayer());
		previousPoint = p;
		
		CustomWindow.startDragAndDrop(parentWindow, this);
		
		location.x = p.x - location.x;
		location.y = location.y - p.y;
		setLocation( location);
	}

	/**
	 * 
	 * Gestion lorsque la souris est relaché dans le composant
	 * 
	 * @param e Evennement de souris
	 * 
	 */
	@Override
	public void mouseReleased(MouseEvent e) {

		CustomWindow.stopDragAndDrop(parentWindow, this);

		if (isDragged)
			handleDrop(previousPoint);
		
		isDragged = false;
	}

	@Override
	public void startDragging() {

		initialPoint = getLocation();

		if (parentContainer != null){

			initialZorder = parentContainer.getComponentZOrder(this);
			parentWindow.getMainLayer().setComponentZOrder(this, 0);
			setOpaque(true);
			setBackground(parentContainer.getBackground());
			setForeground(parentContainer.getForeground());
		}
		
	}

	@Override
	public void resetDragging() {

		setOpaque(false);

		if (initialZorder > -1) parentContainer.setComponentZOrder(this, initialZorder);
		if (initialPoint != null) setLocation(initialPoint);

		initialPoint = null;
		initialZorder = -1;
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- -------- Geestion du drop ------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Gestion lorqu'un dépose l'étiquette sur un point de la fenetre
	 * 
	 * @param p Point de la fenetre
	 * 
	 */
	private void handleDrop(Point p){

		var c = parentWindow.getMainLayer().findComponentAt(previousPoint);

		if (c == null|| c.getName() == null)
			return;

		else if ( c.getName().equals("fileSelector"))
			handleDropOnFileSelector();

		else if (c.getName().equals("folderSelector"))
			handleDropOnFolderSelector();

		else if (c.getName().equals("fileLabel"))
			handleDropOnFileLabel((FileLabel) c);
	}

	/**
	 * 
	 * Gestion lorqu'un dépose l'étiquette sur le selecteur de fichier.
	 * On transmet au au controlleur de la fenetre parent la valeur du nouveau fichier.
	 * 
	 */
	private void handleDropOnFileSelector(){

		File f = new File(path);

		if (( !f.exists()) || f.isDirectory())
			return;
		
		parentWindow.getController().selectedFileData.setValue(path);
	}

	/**
	 * 
	 * Gestion lorqu'un dépose l'étiquette sur le selecteur de dossier.
	 * On transmet au au controlleur de la fenetre parent la valeur du nouveau dossier.
	 * 
	 */
	private void handleDropOnFolderSelector(){

		File f = new File(path);

		if ( (!f.exists()) ||(!f.isDirectory()) )
			return;

		parentWindow.getController().selectedFolderData.setValue(path);
	}

	/**
	 * 
	 * Gestion lorqu'un dépose l'étiquette sur une autre étiquette.
	 * On transmet au au controlleur de la fenetre parent la valeur du dossier de destination et du fichier actuel.
	 * 
	 */
	private void handleDropOnFileLabel(FileLabel lab){

		File d = new File(lab.path);
		File f = new File(path);

		if (f.exists() && d.exists() && d.isDirectory()){

			var controller = parentWindow.getController();
			controller.destinationFolderData.setValueNoAlert(lab.path);
			controller.fileToMovedData.setValue(path);
		}
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// ------------------------- -------- Gestion des action des menus --------- ---------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Gere le déclenchement d'un item de l'étiquette.
	 * 
	 * @param action Action déclencher par l'item
	 * 
	 * @see FileLabel#handleFavoriteItem()
	 * @see FileLabel#handleEditItem()
	 * @see FileLabel#handleRightsItem()
	 * @see FileLabel#handleDeleteItem()
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent action) {
		
		if (action == null)
			return;

		else if (action.getSource() == favoritetItem)
			handleFavoriteItem();

		else if (action.getSource() == editItem)
			handleEditItem();

		else if (action.getSource() == rightsItem)
			handleRightsItem();

		else if (action.getSource() == deleteItem)
			handleDeleteItem();
	}

	/**
	 * 
	 * Gestion du déclenchement de l'item de renommage
	 * 
	 */
	private void handleEditItem(){
		
		var win = new EditWindow(parentWindow, path);
		win.setVisible(true);
	}

	/**
	 * 
	 * Gestion du déclenchement de l'item de permission
	 * 
	 */
	private void handleRightsItem(){

		var win = new PermissionsWindow(parentWindow, path);
		win.setVisible(true);
	}

	/**
	 * 
	 * Gestion du déclenchement de l'item de suppression
	 * 
	 */
	private void handleDeleteItem(){

		parentWindow.getController().fileToDeleteData.setValue(path);
	}

	/**
	 * 
	 * Gestion du déclenchement de l'item de favoris
	 * 
	 */
	private void handleFavoriteItem(){

		var controller = parentWindow.getController();

		if ( controller.currentFavoritesData.getValue().contains(path) )
			controller.delFavoritesData.setValue(path);

		else 
			controller.newFavoritesData.setValue(path);
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

}
