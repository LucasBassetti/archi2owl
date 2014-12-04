package br.ufes.inf.nemo.archi2owl;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;


public class Archi2OWLExportWizard extends Wizard {

	Archi2OWLExportPage fPage;
	private Archi2OWLExport fExporter;
	
	public Archi2OWLExportWizard(Archi2OWLExport exporter){
		fExporter = exporter;
		setWindowTitle(Messages.Archi2OWLExporterWizard_0);
	}
	
	@Override
    public void addPages() {
        fPage = new Archi2OWLExportPage(fExporter);
        addPage(fPage);
    }
	
	@Override
	public boolean performFinish() {

        // Make sure the elements file does not already exist
        File file = new File(fPage.getExportFolderPath());
        if(file.exists()) {
            boolean result = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                    Messages.Archi2OWLExporterWizard_1,
                    Messages.Archi2OWLExporterWizard_2);
            if(!result) {
                return false;
            }
        }
        
        // Export
        try {
            fExporter.export(file, fPage);
        }
        catch(IOException ex) {
            ex.printStackTrace();
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
                    Messages.Archi2OWLExporterWizard_3,
                    Messages.Archi2OWLExporterWizard_3 + " " + ex.getMessage()); //$NON-NLS-1$
            return false;
        }

        return true;
	}

	
	
}
