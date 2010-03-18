import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.SeparatorField;

public final class AboutScreen extends TransitionScreen {
	public AboutScreen(){
		super(true, VERTICAL_SCROLL | VERTICAL_SCROLLBAR);                

		setTitle("GBBReader - ����");
        
		add(new NullField(FOCUSABLE)); // Ϊ���������ܹ���
        Font _font = (this.getFont()).derive(Font.BOLD);
        LabelField _name = new LabelField("GBBReader 1.0.2",FIELD_HCENTER | FIELD_VCENTER | NON_FOCUSABLE);
        _name.setFont( _font);
        add(_name);
        
        add(new LabelField("��ݮ�ֻ��ı��Ķ�",FIELD_HCENTER | FIELD_VCENTER | NON_FOCUSABLE));
        add(new SeparatorField());
        LabelField _author = new LabelField("���ߣ�wick",FIELD_HCENTER | FIELD_VCENTER | NON_FOCUSABLE);
        _author.setFont(_font);
        add(_author);
        add(new SeparatorField());
        add(new LabelField("���������GPL 2.0��",FIELD_HCENTER | FIELD_VCENTER | NON_FOCUSABLE));
        add(new SeparatorField());
        add(new LabelField("��ϵ��loubingyong@gmail.com",FIELD_HCENTER | FIELD_VCENTER | NON_FOCUSABLE));
		add(new NullField(FOCUSABLE)); // Ϊ���������ܹ���
	}
	
}
