import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;


public final class ColorSelScreen extends PopupScreen{
	private ColorListField colorList;
	public int selColor;
	
	public ColorSelScreen(int orig_color) {
		super(new VerticalFieldManager(NO_VERTICAL_SCROLL), DEFAULT_CLOSE);
		add(new LabelField("������ѡ����ɫ,������ȡ��",Field.FIELD_HCENTER));
		add(new SeparatorField());
		
        VerticalFieldManager vfm = new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR) {
            protected void sublayout(int width, int height) {
            	height = Display.getHeight() / 2;  // �б�߶�ȡ��Ļ�߶ȵ�һ��
                super.sublayout(width, height);
                setExtent(width, height);
            }
        };
		
        colorList = new ColorListField();
		colorList.setSelectedIndex(orig_color);
		vfm.add(new SeparatorField());
		vfm.add(colorList);    
        add(vfm);
		add(new SeparatorField());
		selColor = -1;
	}

	public boolean keyChar(char c, int status, int time) {
   		switch(c) {
   			case Characters.ENTER:
			case Characters.LATIN_SMALL_LETTER_J:
			case Characters.LATIN_SMALL_LETTER_D:
				navigationClick(status, time);
     			return true;
   		}
		return super.keyChar(c, status, time);
    }

    protected boolean navigationClick(int status, int time) {
		selColor = colorList.getSelectedIndex();
   		close();
    	return true;
    }
}

//��ɫ�б�ؼ�
class ColorListField extends MyObjectListField implements ListFieldCallback{
	public ColorListField(){
		super(ELLIPSIS);
		// ������ɫ����
		int len = Setting.colorList.length;
		for(int i = 0; i < len; i++) 
			insert(i); //������ɫֵ
	}

    public int moveFocus(int amount, int status, int time) {
        invalidate();
        return super.moveFocus(amount, status, time);
    }
    
    // Invoked when this field receives the focus.
    public void onFocus(int direction) {
        super.onFocus(direction);
        invalidate();
    }

    // Invoked when a field loses the focus.
    public void onUnfocus() {
        super.onUnfocus();
        invalidate();
    }    
    
    public void drawListRow(ListField list, Graphics graphics, int index, int y, int w) {
        int h = getRowHeight();
        //���߿�
        graphics.setColor(Color.WHITE);
        if (getSelectedIndex() == index)  
            graphics.fillRect(4, y+4, w-8, h-8); //ѡ����
        else 
            graphics.fillRect(0, y, w, h);  //��ѡ����
        
        //��ʵ����ɫ����
        graphics.setColor(Setting.colorList[index]);
        graphics.fillRect(6, y+6, w-12, h-12);
    }
}