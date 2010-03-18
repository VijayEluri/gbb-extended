import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class SearchScreen extends PopupScreen {
	
	private BasicEditField keyWord ;
	private ButtonField btnSearch;
	private TextBook book;
	private int startOffset;
	public  SearchScreen(TextBook txtBook, int offset) {
		super(new VerticalFieldManager(FIELD_VCENTER | FIELD_HCENTER |VERTICAL_SCROLL | VERTICAL_SCROLLBAR), 
										DEFAULT_CLOSE | DEFAULT_MENU);
		add(new LabelField("�����ؼ��ر�", FIELD_HCENTER));
		keyWord = new BasicEditField("��������: ","");
		add(keyWord);
        add(new SeparatorField());
		btnSearch = new ButtonField("��ʼ����", FIELD_HCENTER | ButtonField.CONSUME_CLICK);
		btnSearch.setChangeListener(btnListener);
		add(btnSearch);
		book = txtBook;
		startOffset = offset;
 	}
	
	// ��ť���º�ʼ����
    FieldChangeListener btnListener = new FieldChangeListener() {
        public void fieldChanged(Field field, int context) {
        	String sKey = keyWord.getText().trim();
        	if (sKey.length() > 0) {
        		int result = book.search(sKey, startOffset);
        		if (result >= 0) {
        			TextScreen parent = (TextScreen)getScreenBelow();
        			book.jumpTo(result, TextBook.BUFFER_SIZE, false); //��ת���������
        			book.setCurrentLine(0);
        			parent.invalidate();
        			startOffset = sKey.length() + book.getCurrentOffset();
        		}
        		else
        			Dialog.alert("û���ҵ�");
        	}
        }
    };
}
