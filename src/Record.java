import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.*;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class Record {
    private String name;  // ��¼����   �Ķ���ʷ��gbbhistory  ����:gbbset  ����:gbbBookmark
    private RecordStore rs;
    private boolean open;
	public static int MAX_NUMBER = 100;  // ����������ʷ��¼��Ŀ

	public static String HISTORY_NAME = "gbbhistory";
	public static String SETTING_NAME = "gbbset";
	public static String BOOKMARK_NAME = "gbbBookmark";
	
    public String bookName;
    public String bookFormat;
    public int bookOffset;
    public static int bookID = -1;
    
    public int totalBook ;
    public BookInfo[] bookList;
    
    public Record(String recordName) { 
        name = recordName;
        bookID = -1;
//        bookName = null;
//        bookOffset = 0;
//        totalBook = 0;
    }
    
    public void setStoreName(String storeName) {
    	name = storeName;
    }
    
    private boolean open() {
        open = true;
        rs = null;
        try {
            rs = RecordStore.openRecordStore(name, true);
        } catch (RecordStoreFullException ex) {
            open = false;
            RecordFull();
       } catch (RecordStoreException ex) {
            open = false;
            RecordError();
        }
        return open;
    }

    private void close() {
        open = false;
        try {
            rs.closeRecordStore();
        } catch (RecordStoreException ex) {
        	RecordError();
        }
        rs = null;
    }

    private int getNumRecords() {
        int num = -1;
        if (open) {
            try {
                num = rs.getNumRecords();
            } catch (RecordStoreNotOpenException ex) {
            	RecordError();
                num = -1;
            }
        }
        return num;
    }

    /**
     * ��Ӷ����¼, ͬʱ���õ�ǰ���record ID
     * @param b - ���ݿ�
     * @return false�������ʧ�ܣ�true�����ɹ�
     */
    private boolean addRecord(byte[] b) {
        boolean flag = true;
        if (b == null) {
            return false;
        }
        try {
            bookID = rs.addRecord(b, 0, b.length);
    	} catch (RecordStoreException ex) {
    		RecordError();
    		flag = false;
    	}
       return flag;
    }

    /**
     * �޸ĵ�ǰ�����¼
     * @param i - ��¼��
     * @param b - ���ݿ�
     * @return - true�޸ĳɹ���false�޸�ʧ��
     */
    private boolean setRecord(int i, byte[] b) {
        boolean flag = true;
        if (b == null) {
            return false;
        }
        try {
            rs.setRecord(i, b, 0, b.length);
		}catch (RecordStoreFullException e){
			RecordFull();
			flag = false;
//			e.printStackTrace();
        } catch (RecordStoreException ex) {
			RecordError();
            flag = false;
        }
        return flag;
    }

   public  void deleteRecord(int id) {
        open();
    	try {
            rs.deleteRecord(id);
        } catch (RecordStoreException ex) {
			RecordError();
//			ex.printStackTrace();
        }
        close();
    }

    /**
     * �����ļ����ڼ�¼�в�������
     * @param s - �����ҵ�ȫ·������
     * @return	- ����鵽���򷵻ظ���ļ�¼�ţ����򷵻�-1
     */
    public int findRecord(String s) {
    	String fieldBookName;// = null; // �����ֶ�
    	int fieldBookOffset;// = 0; // ��ƫ�����ֶ�

    	open();
		totalBook = getNumRecords();
		if (totalBook <= 0) {
			bookID = -1;
			close();
			return bookID;
		}
    	try {
    		RecordEnumeration re = rs.enumerateRecords(null, null, false);
   			for (int i = 0; i < totalBook; i++) {
   				int id = re.nextRecordId(); 
   				byte[] b = rs.getRecord(id);
   		        ByteArrayInputStream bais = new ByteArrayInputStream(b);
   		        DataInputStream dis = new DataInputStream(bais);
   		       	fieldBookName = dis.readUTF();
   		       	fieldBookOffset = dis.readInt();
   		       	bais.close();
   		       	dis.close();
   		       	b = null;
   		       	bais = null;
   		       	dis = null;
   				if (s.equals(fieldBookName)) {
   					bookName = fieldBookName;
   					bookOffset = fieldBookOffset;
   					bookID = id;
   					close();
   					re.destroy();
   					re = null;
   					return bookID;
   				}
   			}
   			re.destroy();
   			re = null;
	    } catch (EOFException  e) {
	    	e.printStackTrace();
	    } catch (IOException  e) {
	    	e.printStackTrace();
	    } catch (RecordStoreException e) {
			RecordError();
//			e.printStackTrace();
   		}
		close();
		bookID = -1;
		return bookID;
    }

    /**
     * ������ʷ��¼
     * @return - true��ʾ����ɹ���false��ʾ��¼��̫��
     */
    public boolean saveBookHistory() {
        byte[] STB = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(bookName);
            dos.writeInt(bookOffset);
            long time = System.currentTimeMillis();
            dos.writeLong(time);
            STB = baos.toByteArray();
            baos.close();
            dos.close();
            baos = null;
            dos = null;
        } catch (IOException e) {
        	e.printStackTrace();
        }
		open();
	    if (bookID < 0) { //�¼�¼
	    	int num = getNumRecords();
	    	if ((num >= 0) & (num < MAX_NUMBER))
	    		addRecord(STB);
	    	else {
	    		STB = null;
	    		close();
	    		return false;
	    	}
	    } else 
	    	setRecord(bookID, STB);
		close();
		STB = null;
		return true;
    }
    
    public BookInfo[] loadHistoryList() {
    	String fieldBookName;// = null; // �����ֶ�
    	int fieldBookOffset;// = 0; // ��ƫ�����ֶ�
    	long fieldLastCloseTime;// = 0;
    	
    	open();
		totalBook = getNumRecords();
		if (totalBook <= 0) {
			close();
			return null;
		}
		bookList = new BookInfo[totalBook];
    	try {
    		RecordEnumeration re = rs.enumerateRecords(null, null, false);
   			for (int i = 0; i < totalBook; i++) {
   				int id = re.previousRecordId();
   				byte[] b = rs.getRecord(id);
   		        ByteArrayInputStream bais = new ByteArrayInputStream(b);
   		        DataInputStream dis = new DataInputStream(bais);
 	        	fieldBookName = dis.readUTF();
 	        	fieldBookOffset = dis.readInt();
 	        	fieldLastCloseTime = dis.readLong();
 	        	bais.close();
 	        	dis.close(); 
 	        	bais = null;
 	        	dis = null;
 	        	b = null;
   		        bookList[i] = new BookInfo();
	        	bookList[i].bookFullName = fieldBookName;
		       	bookList[i].bookOffset = fieldBookOffset;
	        	bookList[i].bookName = getBookName(fieldBookName);
	        	bookList[i].time = fieldLastCloseTime;
	        	bookList[i].bookID = id;
   			}
   			re.destroy();
   			re = null;
   		} catch (EOFException  e) {
            e.printStackTrace();
        } catch (IOException  e) {
            e.printStackTrace();
        } catch (RecordStoreException e) {
//			e.printStackTrace();
			RecordError();
    	}
		close();
		return bookList;
    }
    
	/**
	 * ��ȡ��·������
	 */
	private String getBookName(String fullName) {
		int i = fullName.lastIndexOf('/');
		return fullName.substring(i+1);
	}

	/**
	 * ��������
	 */
	public  void saveSetting() {
        byte[] STB;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            //���������ֶ�
            dos.writeInt(Setting.useTrackBall);
            dos.writeInt(Setting.fontSizeIndex);
            dos.writeInt(Setting.fontSytleIndex);
            dos.writeInt(Setting.fgColorIndex[0]);
            dos.writeInt(Setting.bgColorIndex[0]);
            dos.writeInt(Setting.fullScreen);
            dos.writeInt(Setting.drawDashLine);
            dos.writeInt(Setting.mode);
            dos.writeInt(Setting.openLastBook);
            dos.writeInt(Setting.invertColor);
            dos.writeInt(Setting.lastBookOffset);
            dos.writeInt(Setting.lineSpace);
            dos.writeInt(Setting.antiAliasIndex);
            dos.writeInt(Setting.escToBackground);
            dos.writeInt(Setting.pageScroll);
            dos.writeInt(Setting.lineColorIndex[0]);
            dos.writeInt(Setting.bgType[0]);
            dos.writeInt(Setting.cycle);
            dos.writeInt(Setting.fgColorIndex[1]);
            dos.writeInt(Setting.bgColorIndex[1]);
            dos.writeInt(Setting.lineColorIndex[1]);
            dos.writeInt(Setting.bgType[1]);
            //�����ַ����ֶ�
            dos.writeUTF(Setting.lastBookName);
            dos.writeUTF(Setting.lastPath);
            dos.writeUTF(Setting.reservedStr1);
            dos.writeUTF(Setting.imageFileName[0]);
            dos.writeUTF(Setting.lastImgPath);
            dos.writeUTF(Setting.imageFileName[1]);
            dos.writeUTF(Setting.reservedStr4);
            STB = baos.toByteArray();
            baos.close();
            dos.close();
            baos = null;
            dos = null;
        } catch (IOException e) {
//        	e.printStackTrace();
        	return;
        }
		open();
		try {
			if (Setting.RecordID < 0)
				Setting.RecordID = rs.addRecord(STB, 0, STB.length);
			else 
	            rs.setRecord(Setting.RecordID, STB, 0, STB.length);
		}catch (RecordStoreFullException e) {
			RecordFull();
//			e.printStackTrace();
		}catch (RecordStoreException e){
			RecordError();
//			e.printStackTrace();
		}
	    STB = null;
		close();
    }
    
	/**
	 * ��������
	 */
    public void  loadSetting() {
		Setting.init(); // ��ʼ��
    	open();
		int num = getNumRecords(); // ��¼���� ��Ӧ��=1
		if (num <= 0) {// û�м�¼, ��˰ѳ�ʼֵд��RMS
			close();
			saveSetting(); 
			return;
		}
    	try {
    		RecordEnumeration re = rs.enumerateRecords(null, null, false);
   			Setting.RecordID = re.nextRecordId();
  	        re.destroy();
   	        re = null;
   			byte[] b = rs.getRecord(Setting.RecordID);
   	        ByteArrayInputStream bais = new ByteArrayInputStream(b);
   	        DataInputStream dis = new DataInputStream(bais);
            // �������ֶ�
   	        Setting.useTrackBall = dis.readInt();
            Setting.fontSizeIndex = dis.readInt();
            Setting.fontSytleIndex = dis.readInt();
	        Setting.fgColorIndex[0] = dis.readInt();
   		    Setting.bgColorIndex[0] = dis.readInt();
   		    Setting.fullScreen = dis.readInt();
   		    Setting.drawDashLine = dis.readInt();
   	        Setting.mode = dis.readInt();
   	        Setting.openLastBook = dis.readInt();
   	        Setting.invertColor = dis.readInt();
   	        Setting.lastBookOffset = dis.readInt();
   	        Setting.lineSpace = dis.readInt();
   	        Setting.antiAliasIndex = dis.readInt();
            Setting.escToBackground = dis.readInt();
            Setting.pageScroll = dis.readInt();
            Setting.lineColorIndex[0] = dis.readInt();
            Setting.bgType[0] = dis.readInt();
            Setting.cycle = dis.readInt();
            Setting.fgColorIndex[1] = dis.readInt();
            Setting.bgColorIndex[1] = dis.readInt();
            Setting.lineColorIndex[1] = dis.readInt();
            Setting.bgType[1] = dis.readInt();
            
            // ���ַ����ֶ�
   	        Setting.lastBookName = dis.readUTF();
            Setting.lastPath = dis.readUTF();
            Setting.reservedStr1 = dis.readUTF();
            Setting.imageFileName[0] = dis.readUTF();
            Setting.lastImgPath = dis.readUTF();
            Setting.imageFileName[1] = dis.readUTF();
            Setting.reservedStr4 = dis.readUTF();

            bais.close();
   	        dis.close(); 
   	        bais = null;
   	        dis = null;
   	        b = null;
    	} catch (EOFException  e) {
   	        e.printStackTrace();
    	} catch (IOException  e) {
   	       	e.printStackTrace();
   	    } catch (RecordStoreException e) {
			RecordError();
//       	e.printStackTrace();
   	    }
		close();
    }
    
    /**
     * ������ǩ
     * @param bm
     * @param create - true:������¼  false:�޸ļ�¼
     */
    public void saveBookMark(BookMark bm, boolean create) {
        byte[] STB;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            //���������ֶ�
            dos.writeUTF(bm.bookName);
            dos.writeUTF(bm.digest);
            dos.writeInt(bm.offset);
            STB = baos.toByteArray();
            baos.close();
            dos.close();
            baos = null;
            dos = null;
        } catch (IOException e) {
        	e.printStackTrace();
        	return;
        }
		open();
		try {
			if (create) {
				rs.addRecord(STB, 0, STB.length);
				Success su = new Success();
				UiApplication.getUiApplication().pushModalScreen(su);
				su = null;
			}
			else
				rs.setRecord(bm.id, STB, 0, STB.length);
		    STB = null;
		}catch (RecordStoreFullException e) {
			RecordFull();
//			e.printStackTrace();
		}catch (RecordStoreException e){
			RecordError();
//			e.printStackTrace();
		}
		close();
    }
  
    public void RecordError() {
    	Dialog.alert("���ʼ�¼����!");
    }

    public void RecordFull() {
    	Dialog.alert("��¼�ռ�����������!");
    }
    
    public BookMark[] loadBookMarkList(String name) {
    	String bookName; // �����ֶ�
    	
    	open();
		int totalBookMark = getNumRecords();
		if (totalBookMark <= 0) {
			close();
			return null;
		}
		Vector v = new Vector();
    	try {
    		RecordEnumeration re = rs.enumerateRecords(null, null, false);
   			for (int i = 0; i < totalBookMark; i++) {
   				int id = re.nextRecordId();
   				byte[] b = rs.getRecord(id);
   		        ByteArrayInputStream bais = new ByteArrayInputStream(b);
   		        DataInputStream dis = new DataInputStream(bais);
   		       	bookName = dis.readUTF();
   		       	if (name.equals(bookName)) {
   		       		BookMark bm = new BookMark();
   		       		bm.bookName = bookName;
   		       		bm.digest = dis.readUTF();
   		       		bm.offset = dis.readInt();
   		       		bm.id = id;
   		       		v.addElement(bm);
   		       	}
		       	bais.close();
		       	dis.close(); 
		       	bais = null;
		       	dis = null;
		       	b = null;
   			}
   			re.destroy();
   			re = null;
    	} catch (IOException  e) {
	            e.printStackTrace();
	            return null;
    	} catch (RecordStoreException e) {
			RecordError();
            return null;
//			e.printStackTrace();
    	}
		close();
		// vector תΪBookMark����
		int len = v.size();
		BookMark[] bookMarkList = null;
		if (len > 0) {
			bookMarkList = new BookMark[len];
			for (int i = 0 ; i < len; i++)
				bookMarkList[i] = (BookMark) v.elementAt(i);
			v.removeAllElements();
			v = null;
		}
		return bookMarkList;
    }
 
    public void delAllBookMark(){
    	open();
    	close();
    	try {
			RecordStore.deleteRecordStore(BOOKMARK_NAME);
		} catch (RecordStoreException e) {
			RecordError();
//			e.printStackTrace();
		}
    }
    
    public void deleteBookMark(String name) {
    	String bookName;// = null; // �����ֶ�
    	open();
		int totalBookMark = getNumRecords();
		if (totalBookMark <= 0) {
			close();
			return;
		}
    	try {
    		RecordEnumeration re = rs.enumerateRecords(null, null, false);
   			for (int i = 0; i < totalBookMark; i++) {
   				int id = re.nextRecordId();
   				byte[] b = rs.getRecord(id);
   		        ByteArrayInputStream bais = new ByteArrayInputStream(b);
   		        DataInputStream dis = new DataInputStream(bais);
   	        	bookName = dis.readUTF();
   	        	if (name.equals(bookName)) 
   	        		rs.deleteRecord(id);
	        	bais.close();
   	            dis.close(); 
   	            bais = null;
   	            dis = null;
   	            b = null;
   			}
  			re.destroy();
   			re = null;
        } catch (IOException  e) {
        	e.printStackTrace();
        } catch (RecordStoreException e) {
        	RecordError();
//			e.printStackTrace();
    	}
		close();
    }

}

final class Success extends PopupScreen{
	public Success() {
		super(new VerticalFieldManager(FIELD_VCENTER | FIELD_HCENTER ));
		add(new LabelField("�����ǩ�ɹ�"));
	}
    protected boolean navigationClick(int status, int time) {
    	close();
    	return true;
    }
    protected boolean keyChar(char c, int status, int time) {
   		close();
    	return true;
    }
}
