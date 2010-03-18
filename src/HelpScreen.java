import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.SeparatorField;

public final class HelpScreen extends TransitionScreen {
	public HelpScreen(boolean t){
        super(t, VERTICAL_SCROLL | VERTICAL_SCROLLBAR);               
		setTitle("GBBReader - ����");
        
		add(new NullField(FOCUSABLE)); // Ϊ���������ܹ���
        String s =  "�Ķ�ʱ����:\n��/�·�ҳ: H��S/�ո񡢻س���F��K \n��/�·���: E��U/X��N \n" +
        			"��ʾʱ�估����: C  ����Ŵ�/��С: Y/I\n����: O  ����: V  ���ⳤ��: P\n��ת: Z  ȫ��: D/J  ��ɫ: 0\n" + 
        			"ģʽ�л�: T  �����ǩ: A  ����ǩ��L\n�˳�: Q  ����: R  ����: M\n";
        add(new LabelField(s, NON_FOCUSABLE));
        add(new SeparatorField());
        s = "�����а���:\n�����ƶ�: E��U/X��N\nȷ��: �ո�/�س�/D/J \n������/β: T/B\n����: R\n";
        add(new LabelField(s, NON_FOCUSABLE));
        add(new SeparatorField());
        add(new LabelField("���й���/ͼƬ�������ĵ磡", NON_FOCUSABLE));
        add(new NullField(FOCUSABLE)); // Ϊ���������ܹ���
	}

// class GBBMainScreen ends
}
