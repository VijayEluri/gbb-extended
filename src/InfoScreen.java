import java.util.Calendar;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

// �������ڣ���ʾ��ǰʱ����Ķ�����
public final class InfoScreen extends PopupScreen{
	
	public  InfoScreen(int offset, int size) {
		super(new VerticalFieldManager(FIELD_VCENTER | FIELD_HCENTER ));

		//�õ�ʱ��
		Calendar now = Calendar.getInstance();
		int hh = now.get(Calendar.HOUR_OF_DAY);
		int mm = now.get(Calendar.MINUTE);
		now = null;
		StringBuffer time = new StringBuffer("��ǰʱ��: ");
		if (hh < 10)
			time.append(0);
		time.append(hh).append(':');
		if (mm < 10)
			time.append(0);
		time.append(mm);

		add(new LabelField(time));
		add(new LabelField(calProgress(offset, size)));
		add(new LabelField("�ļ���С: " + String.valueOf(size /1024) + "KB"));
 	}

	/**
	 * ������Ȳ����ظ�ʽ������ַ���
	 * @param offset : ƫ����
	 * @param size : �ļ���С
	 * @return : ��ʽ����Ľ��Ȱٷֱ��ַ��� 
	 */
	public static String calProgress(int offset, int size) {
		StringBuffer ps = new StringBuffer("��ǰ����: ");
		if (size > 0) {
			long p = ((long)offset *  10000) / size;
			if (p < 10) 
				ps.append("00.0").append(p);
			else if (p < 100)
				ps.append("00.").append(p);
			else if (p < 9999) 
				ps.append(p / 100).append('.').append(p % 100);
			else if (p == 10000) 
				ps.append(100);
			ps.append('%');
		}
		return ps.toString();
	}
	
    protected boolean navigationClick(int status, int time) {
    	close();
    	return true;
    }

    public boolean keyChar(char c, int status, int time) {
   		close();
    	return true;
    }

}
