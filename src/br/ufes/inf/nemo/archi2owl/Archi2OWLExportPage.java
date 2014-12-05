package br.ufes.inf.nemo.archi2owl;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import br.ufes.inf.nemo.archi2owl.utils.Utils;

import com.archimatetool.editor.model.viewpoints.IViewpoint;
import com.archimatetool.editor.model.viewpoints.ViewpointsManager;
import com.archimatetool.editor.ui.IArchimateImages;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IApplicationLayerElement;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IBusinessLayerElement;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IImplementationMigrationElement;
import com.archimatetool.model.IMotivationElement;
import com.archimatetool.model.IRelationship;
import com.archimatetool.model.ITechnologyLayerElement;



public class Archi2OWLExportPage extends WizardPage {

	private Archi2OWLExport fExporter;

	private List<IArchimateElement> elementsList;
	
	private Text 	fFolderTextField,
					fFolderNamespaceField;
	
	private Combo 	fComboViewpoint;
	
	private Tree 	businessTree, 
					applicationTree,
					technologyTree,
					motivationTree,
					implementationMigrationTree,
					relationsTree;
	
	private Button 	businessLayerButton,
					applicationLayerButton,
					technologyLayerButton,
					motivationLayerButton,
					implementationMigrationLayerButton,
					relationsLayerButton;
	
    String ARCHI2OWL_EXTENSION = ".owl"; //$NON-NLS-1$
    String ARCHI2OWL_EXTENSION_WILDCARD = "*.owl"; //$NON-NLS-1$
	
	public Archi2OWLExportPage(Archi2OWLExport exporter) {
		super("Archi2OWLExportPage");
		// TODO Auto-generated constructor stub
		fExporter = exporter;
		
        setTitle(Messages.Archi2OWLExportPage_0);
        setDescription(Messages.Archi2OWLExportPage_1);
        setImageDescriptor(IArchimateImages.ImageFactory.getImageDescriptor(IArchimateImages.ECLIPSE_IMAGE_EXPORT_DIR_WIZARD));
        
        elementsList = new ArrayList<IArchimateElement>();
        writeFolder(fExporter.getfModel().getFolder(FolderType.BUSINESS));
    	writeFolder(fExporter.getfModel().getFolder(FolderType.APPLICATION));
    	writeFolder(fExporter.getfModel().getFolder(FolderType.TECHNOLOGY));
    	writeFolder(fExporter.getfModel().getFolder(FolderType.MOTIVATION));
    	writeFolder(fExporter.getfModel().getFolder(FolderType.IMPLEMENTATION_MIGRATION));
    	writeFolder(fExporter.getfModel().getFolder(FolderType.RELATIONS));
    	
    	Collections.sort(elementsList, new EComparator());
    	
	}

	class EComparator implements Comparator<IArchimateElement> {
	    public int compare(IArchimateElement eobject, IArchimateElement eobject2) {
	    	
	    	if((eobject instanceof IRelationship) && !(eobject2 instanceof IRelationship)){
	    		return  eobject.eClass().getName().compareTo(eobject2.getName());
	    	}
	    	else if(!(eobject instanceof IRelationship) && (eobject2 instanceof IRelationship)){
	    		return  eobject.getName().compareTo(eobject2.eClass().getName());
	    	}
	    	else if((eobject instanceof IRelationship) && (eobject2 instanceof IRelationship)){
	    		return  eobject.eClass().getName().compareTo(eobject2.eClass().getName());
	    	}
	    	
	        return  eobject.getName().compareTo(eobject2.getName());
	    }
	}

	
	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout());
        setControl(container);
        
        //Namespace
        
        Group namespaceGroup = new Group(container, SWT.NULL);
        namespaceGroup.setText("Ontology Namespace");
        namespaceGroup.setLayout(new GridLayout(3, false));
        namespaceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Label namespaceLabel = new Label(namespaceGroup, SWT.NULL);
        namespaceLabel.setText("Namespce");
        
        fFolderNamespaceField = new Text(namespaceGroup, SWT.BORDER | SWT.SINGLE);
        fFolderNamespaceField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fFolderNamespaceField.setText("http://www.nemo.inf.ufes.br/ontology#");
        
        //Output folder
        
        Group exportGroup = new Group(container, SWT.NULL);
        exportGroup.setText(Messages.Archi2OWLExportPage_2);
        exportGroup.setLayout(new GridLayout(3, false));
        exportGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Label label = new Label(exportGroup, SWT.NULL);
        label.setText(Messages.Archi2OWLExportPage_3);
        
        fFolderTextField = new Text(exportGroup, SWT.BORDER | SWT.SINGLE);
        fFolderTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        // Single text control so strip CRLFs
        Utils.conformSingleTextControl(fFolderTextField);
          
        String path = System.getProperty("user.home");
        path += "/Untitled.owl";
        fFolderTextField.setText(path);
        
        Button folderButton = new Button(exportGroup, SWT.PUSH);
        folderButton.setText(Messages.Archi2OWLExportPage_4);
        folderButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String folderPath = chooseFolderPath();
                if(folderPath != null) {
                    fFolderTextField.setText(folderPath);
                }
            }
        });

        //Layers 
        
        Group layerGroup = new Group(container, SWT.NULL);
        layerGroup.setText("Choose the Layers");
        layerGroup.setLayout(new GridLayout(6, false));
        layerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));     

		businessLayerButton = new Button(layerGroup, SWT.CHECK);
		businessLayerButton.setSelection(true);
		businessLayerButton.setText("Business");
		
		businessLayerButton.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {	  
		    	  for(int i = 0; i < businessTree.getItems().length; i++){
		    		  if(businessLayerButton.getSelection() == false){
		    			  businessTree.getItem(i).setChecked(false);
		    		  }
		    		  else{
		    			  businessTree.getItem(i).setChecked(true);
		    		  }
		    	  }
		      } 
	      });
        
		applicationLayerButton = new Button(layerGroup, SWT.CHECK);
		applicationLayerButton.setSelection(true);
		applicationLayerButton.setText("Application");
        
		applicationLayerButton.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {	  
		    	  for(int i = 0; i < applicationTree.getItems().length; i++){
		    		  if(applicationLayerButton.getSelection() == false){
		    			  applicationTree.getItem(i).setChecked(false);
		    		  }
		    		  else{
		    			  applicationTree.getItem(i).setChecked(true);
		    		  }
		    	  }
		      } 
	      });
		
		technologyLayerButton = new Button(layerGroup, SWT.CHECK);
		technologyLayerButton.setSelection(true);
		technologyLayerButton.setText("Technology");
		
		technologyLayerButton.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {	  
		    	  for(int i = 0; i < technologyTree.getItems().length; i++){
		    		  if(technologyLayerButton.getSelection() == false){
		    			  technologyTree.getItem(i).setChecked(false);
		    		  }
		    		  else{
		    			  technologyTree.getItem(i).setChecked(true);
		    		  }
		    	  }
		      } 
	      });
		
		motivationLayerButton = new Button(layerGroup, SWT.CHECK);
		motivationLayerButton.setSelection(true);
		motivationLayerButton.setText("Motivation");
		
		motivationLayerButton.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {	  
		    	  for(int i = 0; i < motivationTree.getItems().length; i++){
		    		  if(motivationLayerButton.getSelection() == false){
		    			  motivationTree.getItem(i).setChecked(false);
		    		  }
		    		  else{
		    			  motivationTree.getItem(i).setChecked(true);
		    		  }
		    	  }
		      } 
	      });
		
		implementationMigrationLayerButton = new Button(layerGroup, SWT.CHECK);
		implementationMigrationLayerButton.setSelection(true);
		implementationMigrationLayerButton.setText("Implementation and Migration");
		
		implementationMigrationLayerButton.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {	  
		    	  for(int i = 0; i < implementationMigrationTree.getItems().length; i++){
		    		  if(implementationMigrationLayerButton.getSelection() == false){
		    			  implementationMigrationTree.getItem(i).setChecked(false);
		    		  }
		    		  else{
		    			  implementationMigrationTree.getItem(i).setChecked(true);
		    		  }
		    	  }
		      } 
	      });
		
		relationsLayerButton = new Button(layerGroup, SWT.CHECK);
		relationsLayerButton.setSelection(true);
		relationsLayerButton.setText("Relations");
		
		relationsLayerButton.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {	  
		    	  for(int i = 0; i < relationsTree.getItems().length; i++){
		    		  if(relationsLayerButton.getSelection() == false){
		    			  relationsTree.getItem(i).setChecked(false);
		    		  }
		    		  else{
		    			  relationsTree.getItem(i).setChecked(true);
		    		  }
		    	  }
		      } 
	      });
		
		/* ================================================================================
	     * Create a selection by Viewpoints according with ArchiMate Specification:
	     * http://pubs.opengroup.org/architecture/archimate2-doc/chap08.html#_Toc371945230
	     ================================================================================== */
		
		Group viewpointGroup = new Group(container, SWT.NULL);
		viewpointGroup.setText("Choose the Viewpoint");
		viewpointGroup.setLayout(new GridLayout(1, false));
		viewpointGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));     
        
		fComboViewpoint = new Combo(viewpointGroup, SWT.READ_ONLY);

		String items[] = new String[ViewpointsManager.INSTANCE.getAllViewpoints().size()];
		
		for(int i = 0; i < items.length; i++){
			items[i] = ViewpointsManager.INSTANCE.getViewpoint(i).getName();
		}
		
		fComboViewpoint.setItems(items);
		fComboViewpoint.select(0);

	    /* ================================================================================
	     * Create the Groups of the Trees separate by layers 
	     * 		Business; Application; Technology; 
	     * 		Motivation; Implementation&Migration; Relation 
	     ================================================================================ */

		TabFolder folder = new TabFolder(container, SWT.NULL);
		folder.setLayout(new GridLayout(1, true));
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true)); 
		
		//Business	
        Group businessGroup = createTreeGroup(folder);
        TabItem businessTab = createTabItem(folder, "Business");
        businessTab.setControl(businessGroup);
            
	    //Application	    
        Group applicationGroup = createTreeGroup(folder);
        TabItem applicationTab = createTabItem(folder, "Application");
        applicationTab.setControl(applicationGroup);
        
	    //Technology    
        Group technologyGroup = createTreeGroup(folder);
        TabItem technologyTab = createTabItem(folder, "Technology");
        technologyTab.setControl(technologyGroup);

	    //Motivation
        Group motivationGroup = createTreeGroup(folder);
        TabItem motivationTab = createTabItem(folder, "Motivation");
        motivationTab.setControl(motivationGroup);
	    
	    //Implementation & Migration
        Group implementationMigrationGroup = createTreeGroup(folder);
        TabItem implementationMigrationTab = createTabItem(folder, "Implementation and Migration");
        implementationMigrationTab.setControl(implementationMigrationGroup);
	    
	    //Relation   
        Group relationsGroup = createTreeGroup(folder);
        TabItem relationsTab = createTabItem(folder, "Relations");
        relationsTab.setControl(relationsGroup);
	    
	    /* ================================================================================
	     * Create a Tree of elements separate by layers 
	     * 		Business; Application; Technology; 
	     * 		Motivation; Implementation&Migration; Relation 
	     ================================================================================ */
	    
        businessTree = createTree(businessGroup);
        applicationTree = createTree(applicationGroup);
        technologyTree = createTree(technologyGroup);
        motivationTree = createTree(motivationGroup);
        implementationMigrationTree = createTree(implementationMigrationGroup);
        relationsTree = createTree(relationsGroup);
		
		for(EObject eObject : elementsList){
			
			if(eObject instanceof IBusinessLayerElement){
				IBusinessLayerElement element = (IBusinessLayerElement) eObject;
				TreeItem item = new TreeItem(businessTree, SWT.CHECK);   		
	    		item.setChecked(true);
	    		item.setText(element.getName() + " (" + element.eClass().getName() + ")");
			}
			else if(eObject instanceof IApplicationLayerElement){
				IApplicationLayerElement element = (IApplicationLayerElement) eObject;
				TreeItem item = new TreeItem(applicationTree, SWT.CHECK);   		
	    		item.setChecked(true);
	    		item.setText(element.getName() + " (" + element.eClass().getName() + ")");
			}
			else if(eObject instanceof ITechnologyLayerElement){
				ITechnologyLayerElement element = (ITechnologyLayerElement) eObject;
				TreeItem item = new TreeItem(technologyTree, SWT.CHECK);   		
	    		item.setChecked(true);
	    		item.setText(element.getName() + " (" + element.eClass().getName() + ")");
			}
			else if(eObject instanceof IMotivationElement){
				IMotivationElement element = (IMotivationElement) eObject;
				TreeItem item = new TreeItem(motivationTree, SWT.CHECK);   		
	    		item.setChecked(true);
	    		item.setText(element.getName() + " (" + element.eClass().getName() + ")");
			}
			else if(eObject instanceof IImplementationMigrationElement){
				IImplementationMigrationElement element = (IImplementationMigrationElement) eObject;
				TreeItem item = new TreeItem(implementationMigrationTree, SWT.CHECK);   		
	    		item.setChecked(true);
	    		item.setText(element.getName() + " (" + element.eClass().getName() + ")");
			}
			else if(eObject instanceof IRelationship) {
				IRelationship element = (IRelationship) eObject;
				TreeItem item = new TreeItem(relationsTree, SWT.CHECK);   		
	    		item.setChecked(true);
	    		item.setText(element.eClass().getName() + " (" + element.getSource().getName() + " -> " + element.getTarget().getName() + ")");
			}

		}
		
        //Viewpoint selection
		fComboViewpoint.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {
		    	  changeSelectedTree();
		      } 
	      });
        
		//Set initial tab item
        folder.setSelection(relationsTab);
		
        //Set full screen
        this.getShell().setMaximized(true);
	}
	
	public Group createTreeGroup(TabFolder folder){
		
		Group treeGroup = new Group(folder, SWT.NULL);
		treeGroup.setLayout(new GridLayout(1, true));
		treeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		return treeGroup;
	}

	public TabItem createTabItem(TabFolder folder, String text){
		
		TabItem tabItem = new TabItem(folder, SWT.NULL);
		tabItem.setText(text);
		
		return tabItem;
	}
	
	/**
	 * Method to create Tree
	 * @author Lucas Bassetti
	 */
	
	public Tree createTree(Group group){
		
		Tree tree = new Tree(group, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		return tree;
	}
	
	
	/**
	 * Method to auto select element on Tree by Viewpoint
	 * @author Lucas Bassetti
	 */
	
	private void changeSelectedTree(){

		IViewpoint viewpoint = ViewpointsManager.INSTANCE.getViewpoint(fComboViewpoint.getSelectionIndex());

		int indexB = 0, indexA = 0, indexT = 0, indexM = 0, indexI = 0, indexR = 0;
		for(IArchimateElement eObject : elementsList){

			if(eObject instanceof IBusinessLayerElement){
				IBusinessLayerElement element = (IBusinessLayerElement) eObject;
				if(!viewpoint.isAllowedType(((IArchimateElement)element).eClass())) {
					businessTree.getItem(indexB).setChecked(false);
				}
				else{
					businessTree.getItem(indexB).setChecked(true);
				}
				indexB++;
			}
			else if(eObject instanceof IApplicationLayerElement){
				IApplicationLayerElement element = (IApplicationLayerElement) eObject;
				if(!viewpoint.isAllowedType(((IArchimateElement)element).eClass())) {
					applicationTree.getItem(indexA).setChecked(false);
				}
				else{
					applicationTree.getItem(indexA).setChecked(true);
				}
				indexA++;
			}
			else if(eObject instanceof ITechnologyLayerElement){
				ITechnologyLayerElement element = (ITechnologyLayerElement) eObject;
				if(!viewpoint.isAllowedType(((IArchimateElement)element).eClass())) {
					technologyTree.getItem(indexT).setChecked(false);
				}
				else{
					technologyTree.getItem(indexT).setChecked(true);
				}
				indexT++;
			}
			else if(eObject instanceof IMotivationElement){
				IMotivationElement element = (IMotivationElement) eObject;
				if(!viewpoint.isAllowedType(((IArchimateElement)element).eClass())) {
					motivationTree.getItem(indexM).setChecked(false);
				}
				else{
					motivationTree.getItem(indexM).setChecked(true);
				}
				indexM++;
			}
			else if(eObject instanceof IImplementationMigrationElement){
				IImplementationMigrationElement element = (IImplementationMigrationElement) eObject;
				if(!viewpoint.isAllowedType(((IArchimateElement)element).eClass())) {
					implementationMigrationTree.getItem(indexI).setChecked(false);
				}
				else{
					implementationMigrationTree.getItem(indexI).setChecked(true);
				}
				indexI++;
			}
			else  if(eObject instanceof IRelationship) {
				IRelationship element = (IRelationship) eObject;
				IArchimateElement source = ((IRelationship)element).getSource();
				IArchimateElement target = ((IRelationship)element).getTarget();
				if(!viewpoint.isAllowedType(source.eClass()) || !viewpoint.isAllowedType(target.eClass())) {
					relationsTree.getItem(indexR).setChecked(false);
				}
				else{
					relationsTree.getItem(indexR).setChecked(true);
				}
				indexR++;
			}

		}

	}
	
	/**
	 * Method to add all elements in a list
	 * @author Lucas Bassetti
	 */
	
	private void writeFolder(IFolder folder) {
        
        for(EObject eObject : folder.getElements()) {
        	if(eObject instanceof IArchimateElement){
        		elementsList.add((IArchimateElement) eObject);
        	}
        }
       
    }

	
    /**
     * Update the page status
     */
    @Override
    public void setErrorMessage(String message) {
        super.setErrorMessage(message);
        setPageComplete(message == null);
    }
	
	
	private String chooseFolderPath() {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		
        dialog.setText(Messages.Archi2OWLExportPage_0);
        dialog.setFilterExtensions(new String[] { ARCHI2OWL_EXTENSION_WILDCARD, "*.*" } ); //$NON-NLS-1$
        String path = dialog.open();
        if(path == null) {
            return null;
        }
        
        // Only Windows adds the extension by default
        if(dialog.getFilterIndex() == 0 && !path.endsWith(ARCHI2OWL_EXTENSION)) {
            path += ARCHI2OWL_EXTENSION;
        }
        
        File file = new File(path);
       
        return file.getPath();
    }
	
	String getExportFolderPath() {
        return fFolderTextField.getText();
    }

	public Button getBusinessLayerButton() {
		return businessLayerButton;
	}

	public void setBusinessLayerButton(Button businessLayerButton) {
		this.businessLayerButton = businessLayerButton;
	}

	public Button getApplicationLayerButton() {
		return applicationLayerButton;
	}

	public void setApplicationLayerButton(Button applicationLayerButton) {
		this.applicationLayerButton = applicationLayerButton;
	}

	public Button getTechnologyLayerButton() {
		return technologyLayerButton;
	}

	public void setTechnologyLayerButton(Button technologyLayerButton) {
		this.technologyLayerButton = technologyLayerButton;
	}

	public Button getMotivationLayerButton() {
		return motivationLayerButton;
	}

	public void setMotivationLayerButton(Button motivationLayerButton) {
		this.motivationLayerButton = motivationLayerButton;
	}

	public Button getImplementationMigrationLayerButton() {
		return implementationMigrationLayerButton;
	}

	public void setImplementationMigrationLayerButton(
			Button implementationMigrationLayerButton) {
		this.implementationMigrationLayerButton = implementationMigrationLayerButton;
	}

	public Button getRelationsLayerButton() {
		return relationsLayerButton;
	}

	public void setRelationsLayerButton(Button relationsLayerButton) {
		this.relationsLayerButton = relationsLayerButton;
	}

	public List<IArchimateElement> getElementsList() {
		return elementsList;
	}

	public void setElementsList(List<IArchimateElement> elementsList) {
		this.elementsList = elementsList;
	}


	public Archi2OWLExport getfExporter() {
		return fExporter;
	}


	public void setfExporter(Archi2OWLExport fExporter) {
		this.fExporter = fExporter;
	}


	public Text getfFolderNamespaceField() {
		return fFolderNamespaceField;
	}


	public void setfFolderNamespaceField(Text fFolderNamespaceField) {
		this.fFolderNamespaceField = fFolderNamespaceField;
	}


	public Text getfFolderTextField() {
		return fFolderTextField;
	}


	public void setfFolderTextField(Text fFolderTextField) {
		this.fFolderTextField = fFolderTextField;
	}


	public Combo getfComboViewpoint() {
		return fComboViewpoint;
	}


	public void setfComboViewpoint(Combo fComboViewpoint) {
		this.fComboViewpoint = fComboViewpoint;
	}


	public Tree getBusinessTree() {
		return businessTree;
	}


	public void setBusinessTree(Tree businessTree) {
		this.businessTree = businessTree;
	}


	public Tree getApplicationTree() {
		return applicationTree;
	}


	public void setApplicationTree(Tree applicationTree) {
		this.applicationTree = applicationTree;
	}


	public Tree getTechnologyTree() {
		return technologyTree;
	}


	public void setTechnologyTree(Tree technologyTree) {
		this.technologyTree = technologyTree;
	}


	public Tree getMotivationTree() {
		return motivationTree;
	}


	public void setMotivationTree(Tree motivationTree) {
		this.motivationTree = motivationTree;
	}


	public Tree getImplementationMigrationTree() {
		return implementationMigrationTree;
	}


	public void setImplementationMigrationTree(Tree implementationMigrationTree) {
		this.implementationMigrationTree = implementationMigrationTree;
	}


	public Tree getRelationsTree() {
		return relationsTree;
	}


	public void setRelationsTree(Tree relationsTree) {
		this.relationsTree = relationsTree;
	}

	
	
}
