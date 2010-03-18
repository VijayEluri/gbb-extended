import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class OpenBookScreen extends TransitionScreen {
	FileSelField _olf;       //Lists fields and directories.
	LabelField pathField;//
	
	public OpenBookScreen(String startPath){
		super(true, NO_VERTICAL_SCROLL);   // no transition effect        

		setTitle("GBBReader - ѡ���鼮");
   	
        String[] extentions = {"txt","TXT","Txt"};
        _olf = new FileSelField(extentions, startPath);
		Setting.lastPath = Setting.isNull(_olf._currentPath);
        pathField = new LabelField((_olf._currentPath == null) ? "/" : "/" + _olf._currentPath, NON_FOCUSABLE);

        VerticalFieldManager vfm = new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR);
        vfm.add(_olf);
        add(pathField);
        add(new SeparatorField());
        add(vfm);
        addMenuItem(_delItem);
        addMenuItem(_renameItem);
 	}

    private MenuItem _delItem = new MenuItem("ɾ��",10,20){
    	public void run() {
    		modify(true);
    	}
    };

    private MenuItem _renameItem = new MenuItem("������",20,20){
    	public void run() {
    		modify(false);
    	}
    };

    /**
     * �ļ�����
     * * @param choice - true: ɾ��; false: ������ 
     */
    private void modify(boolean choice) {
		String currentPath = _olf._currentPath;
		int i = _olf.getSelectedIndex();
		String s = _olf.get(_olf, i).toString();
		if (currentPath != null && !s.equals("..")) {
			String newName = null;
			boolean delete = false;
			if (choice) 
				delete = Dialog.ask(Dialog.D_DELETE, "ȷ��ɾ��?") == Dialog.DELETE;
			else {
    			ModifyScreen rs = new ModifyScreen("������(����׺��): ");
        		UiApplication.getUiApplication().pushModalScreen(rs);
        		newName = rs.value; //�õ�Ҫ�޸ĵ��ļ���
        		rs = null;
			}
  			try {
  				String f = "file:///" + currentPath + s;
  				FileConnection fconn = (FileConnection)Connector.open(f);
  				if (choice && delete)
  					fconn.delete(); //ɾ���ļ�
  				else if (!choice && newName != null && newName.length() > 0)
  					fconn.rename(newName); //�޸��ļ���
  		    	fconn.close();
  		    	fconn = null;
  		    	_olf.updateList(currentPath);
  		    	if (i == _olf.getSize())
  		    		i--;
  		    	_olf.setSelectedIndex(i);
  			} catch (IOException e) {
  				Dialog.alert("�޷���ɲ���!");
  			}
		}
    }
    	
	//Handles a user picking an entry in the ObjectListField.
    private void doSelection() {
		//Determine the current path.
    	boolean sel = _olf.fileSelected();
		pathField.setText((_olf._currentPath == null) ? "/" : "/" + _olf._currentPath);
		Setting.lastPath = Setting.isNull(_olf._currentPath);
    	if (sel) {
    		String filename = "file:///" + _olf._selectedFile;
			TextScreen bookScreen = new TextScreen(filename);
			UiApplication.getUiApplication().pushModalScreen(bookScreen);
			bookScreen = null;
		}
    }
    
    //Handle trackball clicks.  
    protected boolean navigationClick(int status, int time) {
 		if(!GBBMain.isTrackWheel) {
	    	doSelection();
	    	return true;
    	}
 		else return super.navigationClick(status, time);
    }
    
    public boolean keyChar(char c, int status, int time) {
    	//Close this screen if escape is selected.
    	switch(c) {
	    	case Characters.ENTER:
	    	case Characters.SPACE:
			case Characters.LATIN_SMALL_LETTER_J:
			case Characters.LATIN_SMALL_LETTER_D:
		    	doSelection();
				return true;
    	}
   		return super.keyChar(c, status, time);
    }
    
    
// class GBBMainScreen ends
}
