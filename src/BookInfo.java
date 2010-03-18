import net.rim.device.api.util.Comparator;


// �鼮��Ϣ��
public final class BookInfo implements Comparator {

	public String bookFullName; //ȫ·������
	public String bookName;		//��·������
	public int bookOffset;		//��¼��ƫ����
	public int bookID;			//��¼��
	public long time;            //�ϴδ�ʱ��
	
	public String toString() {
		return bookName;
	}
	
	public int compare(Object o1, Object o2) {
		BookInfo x1 = (BookInfo)o1;
		BookInfo x2 = (BookInfo)o2;
		if (x1.time > x2.time)
			return -1;
		else if (x1.time < x2.time)
			return 1;
		else
			return 0;
	}

}