package br.ufes.inf.nemo.archi2owl;

import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.archimatetool.editor.model.IModelExporter;
import com.archimatetool.model.IArchimateModel;

public class Archi2OWLExportProvider implements IModelExporter {

	@Override
	public void export(IArchimateModel model) throws IOException {
		Archi2OWLExport exporter = new Archi2OWLExport(model);
		
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), new Archi2OWLExportWizard(exporter)){ //$NON-NLS-1$
            
            @Override
            protected void createButtonsForButtonBar(Composite parent) {
                super.createButtonsForButtonBar(parent); // Change "Finish" to "Save"
                Button b = getButton(IDialogConstants.FINISH_ID);
                b.setText("Save");
            }
        };
        
    	dialog.open(); 
		
	}

}
