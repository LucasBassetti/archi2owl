package br.ufes.inf.nemo.archi2owl;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.archimatetool.model.IApplicationLayerElement;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IBusinessLayerElement;
import com.archimatetool.model.IImplementationMigrationElement;
import com.archimatetool.model.IMotivationElement;
import com.archimatetool.model.IRelationship;
import com.archimatetool.model.ITechnologyLayerElement;

public class Archi2OWLExport {


    
    //private OutputStreamWriter writer;
    private ConvertArchi2OWL archi2owl;
       
    private IArchimateModel fModel;
    
    public Archi2OWLExport(IArchimateModel model) {
        fModel = model;
    }
    
    public void export(File file, Archi2OWLExportPage fPage) throws IOException {
    	
    	this.archi2owl = new ConvertArchi2OWL(fPage.getfFolderNamespaceField().getText());

    	List<IArchimateElement> selectedList = getSelectedList(fPage);  
    	archi2owl.convert(selectedList);

    	// Save OWL Ontology
    	try {
    		//archi2owl.getManager().saveOntology(archi2owl.getOntology(), new SystemOutDocumentTarget());
    		archi2owl.getManager().saveOntology(archi2owl.getOntology(), IRI.create(file.toURI()));
    	} catch (OWLOntologyStorageException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	
    }
    
    private List<IArchimateElement> getSelectedList(Archi2OWLExportPage fPage){
    	
    	List<IArchimateElement> selectedList = new ArrayList<IArchimateElement>();
    	
    	int indexB = 0, indexA = 0, indexT = 0, indexM = 0, indexI = 0, indexR = 0;
		for(IArchimateElement element : fPage.getElementsList()){
		
			if(element instanceof IBusinessLayerElement){
				if(fPage.getBusinessTree().getItem(indexB).getChecked() == true){
					selectedList.add(element);
				}
				indexB++;
			}
			
			else if(element instanceof IApplicationLayerElement){
				if(fPage.getApplicationTree().getItem(indexA).getChecked() == true){
					selectedList.add(element);
				}
				indexA++;
			}
			
			else if(element instanceof ITechnologyLayerElement){
				if(fPage.getTechnologyTree().getItem(indexT).getChecked() == true){
					selectedList.add(element);
				}
				indexT++;
			}
			
			else if(element instanceof IMotivationElement){
				if(fPage.getMotivationTree().getItem(indexM).getChecked() == true){
					selectedList.add(element);
				}
				indexM++;
			}
			
			else if(element instanceof IImplementationMigrationElement){
				if(fPage.getImplementationMigrationTree().getItem(indexI).getChecked() == true){
					selectedList.add(element);
				}
				indexI++;
			}
			
			else if(element instanceof IRelationship){
				if(fPage.getRelationsTree().getItem(indexR).getChecked() == true){
					selectedList.add(element);
				}
				indexR++;
			}
		}
    	
    	return selectedList;
    }
     

	public IArchimateModel getfModel() {
		return fModel;
	}

	public void setfModel(IArchimateModel fModel) {
		this.fModel = fModel;
	}
    
    

}


