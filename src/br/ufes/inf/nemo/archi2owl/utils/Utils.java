package br.ufes.inf.nemo.archi2owl.utils;

import java.text.Normalizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.archimatetool.editor.utils.StringUtils;

public class Utils {
	
	public static String formatString(String s) {  
	    String temp = Normalizer.normalize(s, java.text.Normalizer.Form.NFD);  
	    temp = temp.replaceAll("[^\\p{ASCII}]","");
	    
	    char[] str = temp.toCharArray();

	    for(int i = 0; i <  str.length; i++){
	    	
	    	if(str[i] == ' '){
	    		str[i+1] = Character.toUpperCase(str[i+1]);
	    	}
	    	
	    }
	    
	    temp = new String(str); 
	    temp = temp.replaceAll(" ", "");
	    return temp;
	}  
	
	public static void conformSingleTextControl(Text textControl) {
        if(textControl == null) {
            return;
        }
        
        if((textControl.getStyle() & SWT.SINGLE) != 0) {
            textControl.addListener(SWT.Verify, new Listener() {
                public void handleEvent(Event event) {
                    if(StringUtils.isSet(event.text)) {
                        event.text = event.text.replaceAll("(\\r\\n|\\r|\\n)", " "); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
            });
        }
    }
}

