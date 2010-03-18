import net.rim.device.api.util.Comparator;


// �鼮��Ϣ��
public final class BookMark implements Comparator{

	public String bookName;		//��·������
	public String digest;		//��ǩժҪ
	public int offset;		//��¼��ƫ����
	public int id;  // Record ID
	
	public String toString() {
		return digest;
	}

	public int compare(Object o1, Object o2) {
		BookMark x1 = (BookMark)o1;
		BookMark x2 = (BookMark)o2;
		if (x1.offset > x2.offset)
			return 1;
		else if (x1.offset < x2.offset)
			return -1;
		else
			return 0;
	}
}