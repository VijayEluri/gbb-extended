
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;

public final class TextBook {
	// ������ʽ�й�
	private final static String ENC_UTF16BE = "UTF-16BE";
	private final static String ENC_UTF8 = "UTF-8";
	private final static String ENC_GB2312 = "GB2312";
	private final static int ID_UTF8 = 3; 
	private final static int ID_UTF16LE = 2; 
	private final static int ID_UTF16BE = 1; 
	private final static int ID_GB2312 = 0; 
	public final static int BUFFER_SIZE = 4096 ; // ��������С
	private int formatID;
	
	
	//���ļ������йصı���
	private String fullBookName;  // ȫ·������
	private String bookName;  // ��·������
	private long bookSize; // size of the book
	private FileConnection fconn;
    private InputStream di;	
    private String bookFormat; // �ļ���ʽ
	public boolean EOF; //��ǰ�������Ѿ������ļ�ĩβ 
	public boolean BOF; //��ǰ�������Ѿ������ļ���ʼ 
    //////////////////////////
    
    //��Щ�����뻺������������ǰ���ڵ�һ���й�
    private int[] line_offset; //ÿ��ƫ����
    private short[] line_width;  //ÿ�г���
    private byte[] bufferTxt; 
	public  int line_number;  //������
	private int currentOffset; // ��ǰ�ļ�ָ�룬ֻ��locate()��read()�б��޸�
	private int currentLine;  //��ǰ��ʾҳ���һ���ڻ��������������� 
	private int bufferHeadOffset; // �������׶�Ӧ���ļ�ƫ����
    private int BufferLen; // ������ʵ�ʳ���

	///////////////////////////
	
	//���Ű��йصı���
	private Font fontCurrent; //��ǰ������������
	private int gbWidth; //�����ֿ��
	private int pageWidth; // ��Ļ�ɻ����
	private int LINE_PER_PAGE; // ÿҳ����
	//////////////////////////
	
	public TextBook(String fileName) {
		setFullBookName(fileName);
		byte[] headOfBook = new byte[3];
		try {
		    fconn = (FileConnection)Connector.open(fileName);
	        bookSize = fconn.fileSize();
	        InputStream s;	
		    s = fconn.openInputStream();	
			s.read(headOfBook); //���ļ�ͷ�����ֽڣ��ж��ļ������ʽ
		    s.close();
		    s = null;
		    fconn.close();
		    fconn = null;
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		// �жϱ����ʽ
		if ((headOfBook[0] == -2) && (headOfBook[1] == -1)) {
			bookFormat =  ENC_UTF16BE;
			formatID = ID_UTF16BE;
		}
		else if ((headOfBook[0] == -1) && (headOfBook[1] == -2)) {
			bookFormat =  ENC_UTF16BE;
			formatID = ID_UTF16LE;
		}
		else if ((headOfBook[0] == -17) && (headOfBook[1] == -69) &&(headOfBook[2] == -65))
		{
			bookFormat = ENC_UTF8;  // UTF8 with BOM
			formatID = ID_UTF8;
		}
		else {
			bookFormat = ENC_GB2312;
			formatID = ID_GB2312;
		}
		
		line_offset = new int[1024]; //��ʼ����ƫ�������飬���Ҽٶ�4K���ݲ���ֳ�1024��
		line_width = new short[1024];  //��ʼ���г�������
//		line_offset[0] = 0;
//		line_number = 0;
//		currentLine = 0;
//		currentOffset = 0;
		BOF = true;
//		EOF = false;
	}

	public void setPage(Font f, int line, int width) {
		fontCurrent = f;
		gbWidth = f.getAdvance('��');
		LINE_PER_PAGE = line;
		pageWidth = width;
	}

	/**
	 * ���õ�ǰҳ���һ�ж�Ӧ�ڻ������ڵ�����
	 * @param line - �к�
	 */
	public void setCurrentLine(int line) {
		currentLine = line;
	}
	
	/**
	 * ���ص�ǰҳ���һ�ж�Ӧ�ڻ������ڵ�����
	 * @return - ��ǰҳ���һ�ж�Ӧ�ڻ������ڵ�����
	 */
	public int getCurrentLine() {
		return currentLine;
	}
	
	/**
	 * ��������
	 * @param fileName ȫ·������
	 */
	public void setFullBookName(String fileName) {
		fullBookName = fileName;
		int i = fileName.lastIndexOf('/') + 1;
		int t = fileName.lastIndexOf('.');
		bookName = fileName.substring(i, t);
	}

	/**
	 * ��ȡ��·������
	 * @return - ��·������
	 */
	public String getBookName() {
		return bookName;
	}

	/**
	 * ��ȡ�ļ���С
	 * @return - �ļ���С
	 */
	public long getBookSize() {
		return bookSize;
	}

	public long getLineOffset(int line) {
		return bufferHeadOffset + line_offset[line] + line_width[line];
	}
	
	private void Error() {
		Dialog.alert("�ļ����ʴ���!");
	}
	
	/**
	 * ���ļ�
	 */
	private void open() {
        try {
		    fconn = (FileConnection)Connector.open(fullBookName);
		    di = fconn.openInputStream();
        } catch (IOException ioe) {
//            ioe.printStackTrace();
        	Error();
        }
    }

    /**
     * �ر��ļ�
     */
	private void close() {
        if (fconn != null)
	    	try {
	        	fconn.close();
	        	if (di != null)
	        		di.close();
	            di = null;
	            fconn = null;
	        } catch (IOException ioe) {
//	            ioe.printStackTrace();
	        	Error();
	        }
    }
    
    /**
     * �ر�ʱ�������������������� 
     */
    public void clear() {
    	close();
    	line_offset = null;
    	line_width = null;
    	bufferTxt = null;
    }

    /**
     * �õ���ǰҳ���һ�����ļ��е�ƫ���� 
     * @return
     */
    public int getCurrentOffset() {
    	return line_offset[currentLine] + bufferHeadOffset;
    }
    
    /**
     * �������ݿ��е�һ���س���λ�� 
     * @param b - ���ݿ�
     * @param tail - ���ݿ�β��
     * @return - ��һ���س���λ��
     */
    /*
    private int firstReturn(byte[] b, int tail) {
        int t = 0; 
        int bLen = tail;
        while (t < bLen) {
        	if (b[t] == 10)  //��������
           		break;
            t++;
        }
        if (t == bLen) { //û��һ���س���,�쳣���
//        	System.out.println("No return in buffer, STRANGE!!");
        	return 0;
        }
    	return t;
    }*/
 
    /**
     * ��λ���ĵ�ƫ�ƴ�
     * @param i int ƫ�Ƶ�λ��
     */
    private void locate(int i) {
    	if (i >= bookSize)
    		return;
    	if (i <= 0 )  
            i = 0;
    	BOF = ( i == 0) ? true : false;
        close();
        open();
        currentOffset = i;
        try {
			di.skip(i);
		} catch (IOException e) {
//			e.printStackTrace();
        	Error();
		}
    }

    /**
     * �ӵ�ǰ�ļ�ƫ��λ�ö���len��byte
     * @param len - ָ����ȡ����
     * @return - ��ȡ�����ݿ�  
     */
    private byte[] read(int LENGTH) {
        byte b[];

        int buf_len = (int) Math.min(bookSize - currentOffset, LENGTH);
        EOF = (buf_len < LENGTH) ? true : false;
        b = new byte[buf_len];
        try {
            di.read(b);
            currentOffset += b.length;
        } catch (IOException ioe) {
        	Error();
            return null;
        }
        if (formatID == ID_UTF16LE) { //�����UTF16LE,����������ʹ֮��ΪUTF16BE
          	int len = b.length;
           	for (int i = 0; i < len; i+=2) {
           		byte c = b[i];
          		b[i] = b[i+1];
           		b[i+1] = c;
           	}
        }
        return b;
    }
    
    /**
     * ��ָ��ƫ��������ʼ��������������ͷβ�Ļس������ش����Ļ�������һ���к�
     * @param start - ָ��ƫ����
     * @param length - ������������ݳ���
     * @param flag - true��ʾҪɾ��ͷ���س�
     */
  	  public void jumpTo(int start, int length, boolean flag) {
  		  	bufferTxt = null;
  		  	if (formatID == ID_UTF16LE || formatID == ID_UTF16BE)
  		  		start = start & 0xFFFFFFFE;
  		  	locate(start);
  		  	int offset = currentOffset;
  		    byte[] b = read(length); 
  		    
  		    //������Ƕ����ļ�ͷ���� ɾ���������е�һ���س���ǰ���ַ��������س�����
  		    int t = 0;
  		    int s = 0;
  		    
  		    byte ret = 10;
  		    if (flag && !BOF) {
// 		    	t = firstReturn(b, b.length);
  		    	t = Arrays.getIndex(b, ret); //�õ���һ���س���λ��
  		    	if (t > 0)
  		    		s = t + 1;
  		    }
  		    bufferHeadOffset  = offset + s; 

  		    int len = b.length - s;
  		    int m = 0; // �����UTF16��ʽ���򻺳�����С���䣬��ΪUTF16ÿ���ַ�����2�ֽ�
  		    if (formatID == ID_UTF8) // �����utf8��ʽ���򻺳�����2�����⽨������ʱ�����ȱ��
  		    	m = 2;
  		    else if (formatID == ID_GB2312) // �����GB2312��ʽ���򻺳�����1
  		    	m = 1;
  		    BufferLen = m + len;
  		    bufferTxt = new byte[BufferLen];
  		    System.arraycopy(b, s, bufferTxt, 0, len);
  		    createIndex();
  		    b = null;
  	  }

  	   /**
       * ��startָ������ʼ�����Ϲ���linesָ�������� 
       * @param start - ָ������ʼ��(��ǰ��������)
       * @param window - �����ڿ���ʾ������ 
       * @param lines - ���¹��������� 
       * @return - ������������кţ������Ҫ׷�����ݣ��򷵻��кŶ�Ӧ���µĻ�����
       */
      public void scrollUp(int lines) {
       	//�ж��Ƿ���Ҫ�����µ�����
      	if (currentLine < lines ) { 
      		if (!BOF) {//��ǰ�������Ƿ��Ѿ��ﵽ�ļ��� 
      	    	int page_bottom_line = Math.min(line_number - 1, currentLine + LINE_PER_PAGE - 1);
      	    	int last = bufferHeadOffset + line_offset[page_bottom_line];
      	    	int offset = (last < BUFFER_SIZE) ? 0 : last - BUFFER_SIZE;
      			int len = last - offset;
      	    	jumpTo(offset, len , true);
      			currentLine = line_number - (LINE_PER_PAGE + lines) + 1;
      		}
      		else
      			currentLine = 0; // ��ǰ�����ϵ�����ĿС��Ҫ�Ϲ��������� �������Ѿ������ļ��ף�����0
      	}
      	else
      		currentLine = currentLine - lines;
      }

    /**
     * ��startָ������ʼ�����¹���linesָ�������� 
     * @param start - ָ������ʼ��(��ǰ��������)
     * @param window - �����ڿ���ʾ������ 
     * @param lines - ���¹��������� 
     * @return - ������������кţ������Ҫ׷�����ݣ��򷵻��кŶ�Ӧ���µĻ�����
     */
    public void scrollDown(int lines) {
    	//�ж��Ƿ���Ҫ�����µ�����
    	if (currentLine + LINE_PER_PAGE + lines >= line_number) {
    		//�ӻ����������� 
    		if (!EOF ) {//�жϵ�ǰ�������Ƿ��Ѿ��ﵽ�ļ�ĩβ 
    			jumpTo(bufferHeadOffset + line_offset[currentLine], BUFFER_SIZE, false);
    			createIndex();
    			currentLine = lines;
    		}
    		else { // �����ļ�β
    			int last_line = Math.min(line_number , currentLine + LINE_PER_PAGE);
    			int rest = line_number - last_line;
    			currentLine = (rest <= lines) ? currentLine + rest : currentLine + lines ;
     		}
    	}
    	else
    		currentLine = currentLine + lines;
    }

     /**
     * ��ת���ļ���β
     * @return - ���һҳ�����к�
     */
    public void jumpToBottom() {
    	int offset; 
    	if (EOF) // ����������Ѿ����ļ�β��ֱ�ӷ����к�
    		currentLine =Math.max(0, line_number - LINE_PER_PAGE);
    	else {
    		offset = (int) ((bookSize < BUFFER_SIZE) ? 0 : (bookSize - BUFFER_SIZE));
    		jumpTo(offset,  BUFFER_SIZE, false);
    		EOF = true;
            currentLine = line_number - LINE_PER_PAGE ;
    	}
    }

    private void createIndex() {
    	switch (formatID) {
	    	case ID_GB2312 :
	    		createIndexGB2312();
	    		break;
	    	case ID_UTF16LE :
	    	case ID_UTF16BE :
				createIndexUTF16BE();
	    		break;
	    	case ID_UTF8: 
	    		createIndexUTF8();
	    		break;
    	}
    }
    
     /**
     * ����UTF8����ÿ�е�ƫ�������г���(�ֽ���)����
     */
    private void createIndexUTF8() {
        if (bufferTxt == null) 
            return; 

        line_number = 0; // ��ʼʱ������=0
    	byte[] localBufferTxt = bufferTxt;
		Font f = fontCurrent;
		int pw = pageWidth;

    	int c = 0; // ����������ָ��
        int w = 0; // �����ؿ�� 
        short l = 0; // ��ռ���ֽ���
        int nextW; // ������һ���ַ�����п��
        int len = BufferLen - 2 ;
        
		while (c < len) {
	      	int c1 = localBufferTxt[c] & 0x00ff ;
	       	if (c1 == 0x0A) { //\r ����
	       		c++;
                l++;
	            addIndex(c, l);
	           	w = l = 0;
	           	continue;
	       	} 
	       	else if (c1 == 0x0D) { //\r ����
	       		c++;
                l++;
	           	continue;
	       	}
	       	else if (c1 < 0x80) { //ascii 0xxxxxxx
	       		char x = (char)c1;
      			int inc = 1;
       			if (isLetter(x)) {
       				int t = getWord(c);
       				inc += (t - c);
       				nextW = w + getStringWidth(c, t);
       			}
       			else
       				nextW = w + f.getAdvance(x);
	           	if (nextW > pw) { //��ǰ���޷���ʾ����
	               	//����������
	           		addIndex(c, l);
	                w = l = 0;
	                continue; 
	           	} 
	           	else {
	               	c += inc;
	                l += inc;
	                w = nextW; 
	           	}
	       	}
		   	else {  //��ascii����
		   		int b;
		   		if (c1 < 0xE0) { //˫�ֽ� 110xxxxx 10xxxxxx 
		   			int c2 = localBufferTxt[c+1] & 0x00ff;
		   			b = ((c1 & 0x1F) << 6) | (c2 & 0x3F) ;
  	       			nextW = w + f.getAdvance((char)b) ; // ��CJKͳһ����
	           		if (nextW > pw) { //��ǰ���޷���ʾ����
	          	        addIndex(c, l);
	           			w = l = 0;
	           			continue;
	                } 
	           		else {
	                    c += 2;
	                    l += 2;
	                    w = nextW;
	                }
		   		}
		   		else { //1110xxxx 10xxxxxx 10xxxxxx
		   			int c2 = localBufferTxt[c+1] & 0x00ff;
		   			int c3 = localBufferTxt[c+2] & 0x00ff;
		   			b = (c1 & 0x1F) << 12;
		   			b |= (c2 & 0x3F) << 6;
		   			b |= (c3 & 0x3F);	       			
		   			if (b >= 0x4E00 && b <= 0x9FA5)
		           		// CJKͳһ������Unicode�зֲ���0x4E00 - 0x9FA5֮�䣬�ٶ�һ��С˵������Ƨ�� 
    	       			nextW = w + gbWidth;  
    	       		else
    	       			nextW = w + f.getAdvance((char)b) ; // ��CJKͳһ����
 	           		if (nextW > pw) { //��ǰ���޷���ʾ����
	          	        addIndex(c, l);
	           			w = l = 0;
	           			continue;
	                } 
	           		else {
	                    c += 3;
	                    l += 3;
	                    w = nextW;
	                }
		   		}
	   		}
		}
       	addIndex(c,l);
       	localBufferTxt = null;
    }

    /**
     * ����UTF16BE����ÿ�е�ƫ�������г���(�ֽ���)����
     * @throws UnsupportedEncodingException 
     */
    private void createIndexUTF16BE(){
        if (bufferTxt == null) {
            return; 
        } 
    	line_number = 0; // ��ʼʱ������=0
    	byte[] localBufferTxt = bufferTxt;
		Font f = fontCurrent;
		int pw = pageWidth;

    	int c = 0; // ����������ָ��
        int w = 0; // �����ؿ�� 
        short l = 0; // ��ռ���ֽ���
        int nextW = 0; // ������һ���ַ�����п��
        int len = BufferLen - 1;

        while (c < len) {
	      	int c1 = localBufferTxt[c] & 0x00ff ;
	      	int c2 = localBufferTxt[c+1] & 0x00ff ;
	      	int b = (c1 << 8) | c2;
	       	if (b == 10) { //\r ����
	       		c += 2;
                l += 2;
	            addIndex(c, l);
	           	w = l = 0;
	           	continue;
	       	}
	       	else {
	       		int inc = 2;
	       		if (c1 >= 0x004E && c1 <= 0x009F) // CJKͳһ������Unicode�зֲ���0x4E00 - 0x9FA5֮�䣬�ٶ�һ��С˵������Ƨ�� 
	       			nextW = w + gbWidth;  

	       		else {
	       			char x = (char)b;
	       			if (isLetter(x)) { //��ĸ
	       				int t = getWordUTF16(c);
	       				inc += (t - c);
	       				nextW = w + getStringWidthUTF16(c, t);
	       			}
	       			else
	       				nextW = w + f.getAdvance(x) ; // ��CJKͳһ����
	       		}
           		if (nextW > pw) { //��ǰ���޷���ʾ����
          	        addIndex(c, l);
           			w = l = 0;
           			continue;
                } 
           		else {
                    c += inc;
                    l += inc;
                    w = nextW;
                }
	       	} 
		}
   		addIndex(c, l);
    	localBufferTxt = null;
    }

    /**
     * ����GB2312����ÿ�е�ƫ�������г���(�ֽ���)����
     */
    private void createIndexGB2312() {
        if (bufferTxt == null) {
            return; 
        } 
    	line_number = 0; // ��ʼʱ������=0
    	
    	byte[] localBufferTxt = bufferTxt;
        int len = BufferLen - 1;
		Font f = fontCurrent;
		int pw = pageWidth;
		int gw = gbWidth;

		// GBK ����A1-A9, ���õ���A1-A3
		// GBK2 B0-F7
		// GBK3 81 - A0
		// GBK4 AA - FE
    	int c = 0; // ����������ָ��
        int w = 0; // �����ؿ�� 
        short l = 0; // ��ռ���ֽ���
        int nextW = 0; // ������һ���ַ�����п��
	    while (c < len) {
	    	int b = localBufferTxt[c] & 0x00ff;
	       	if (b == 10) { // 0x0A ����
	       		c++;  
	       		l++;
	           //����������
	       		addIndex(c, l);
	            w = l = 0;
	            continue;
	       	}
	            //�ַ�����
	       	if (b > 0x0080) { //����
	          	if (b > 0x00A0 && b < 0x00A4) {// ֻ�����ж�GB2312 A1 - A3��
	          		char sym = GB2312SymToUnicode(localBufferTxt[c], localBufferTxt[c+1]);
	          		nextW = w + f.getAdvance(sym);  //��������ŵĿ��
	          	}
	          	else
	          		nextW = w + gw ; // ����
	          	if (nextW > pw) { //��ǰ���޷���ʾ����
	        	//����������
	                addIndex(c, l);
	        		w = l = 0;
	        		continue;
	            } 
	        	else {
	        		c += 2;
	                l += 2;
	                w = nextW;
	            }
	       	} else { //ascii
	       		if (b == 13) { //������0x0D
	            	c++;
	            	l++;
	            	continue;
	            }
       			char x = (char)b;
       			int inc = 1;
       			if (isLetter(x)) {
       				int t = getWord(c);
       				inc += (t - c);
       				nextW = w + getStringWidth(c, t);
       			}
       			else
       				nextW = w + f.getAdvance(x);
	           	if (nextW > pw) { //��ǰ���޷���ʾ����
	               	//����������
	           		addIndex(c, l);
	                w = l = 0;
	                continue; 
	           	} 
	           	else {
	               	c += inc;
	                l += inc;
	                w = nextW; 
	           	}
	       	}
	    }
       	addIndex(c, l );
    	localBufferTxt = null;
    }
   
    /**
     * �ж��Ƿ�Ϊ��ĸ
     * @param x - �ַ�
     * @return - true:����ĸ
     */
    private static boolean isLetter(char x) {
		return (x >= 'a' && x <= 'z') || (x >= 'A' && x <= 'Z');
    }
    
    private static boolean isContinue(char x) {
    	return (x == '\'') || (x == '.') || (x == ',') || (x == '"')|| (x == '@') || (x == '-');
    }
    
    /**
     * ȡ��һ��Ӣ�ĵ���, ����GB2312��UTF8����ascii����
     * @param start - �������ڿ�ʼλ��
     * @return - ���ʽ���λ��
     */
    private int getWord(int start) {
     	int i = start;
    	int len = BufferLen;
//    	int w = 0;
//    	while(i < len && w < pageWidth) {
    	while(i < len) {
    		char x = (char)bufferTxt[i];
    		if (isLetter(x) || isContinue(x)) {
//        		w += fontCurrent.getAdvance(x);
     			i++;
    		}
    		else
    			return i - 1;
    	}
    	return Math.min(len - 1, i);
    }
    
    /**
     * �õ�һ��ascii�ַ��ĳ���
     * @param start ��������ʼλ��
     * @param end ����������λ��
     * @return �ַ��ܳ���
     */
    private int getStringWidth(int start, int end) {
    	int w = 0;
    	for(int i = start; i <= end; i++) {
    		char b = (char)bufferTxt[i];
    		w += fontCurrent.getAdvance(b);
    	}
    	return w;
    }

    /**
     * ȡ��һ��Ӣ�ĵ���, ����UTF16����
     * @param start - �������ڿ�ʼλ��
     * @return - ���ʽ���λ��
     */
        
	private int getWordUTF16(int start) {
     	int i = start;
    	int len = BufferLen - 1;
//    	int w = 0;
//    	while(i < len && w < pageWidth) {
    	while(i < len) {
    		char x = (char) (((bufferTxt[i] & 0x00ff) << 8) | (bufferTxt[i+1] & 0x00ff));
    		if (isLetter(x) || isContinue(x)) {
//        		w += fontCurrent.getAdvance(x);
     			i += 2;
    		}
    		else
    			return i - 2;
    	}
    	return Math.min(len - 1, i);
    }
    
    /**
     * �õ�һ��UTF16��Ӣ���ַ��ĳ���
     * @param start ��������ʼλ��
     * @param end ����������λ��
     * @return �ַ��ܳ���
     */
   private int getStringWidthUTF16(int start, int end) {
    	int w = 0;
    	for(int i = start; i <= end; i+=2) {
    		char b = (char) (((bufferTxt[i] & 0x00ff) << 8) | (bufferTxt[i+1] & 0x00ff));
    		w += fontCurrent.getAdvance(b);
    	}
    	return w;
    }
    

     /**
     * ����ָ���е��ַ���
     * @param line - �к�
     * @return - ���ص��ַ���
     */
    public String getLineText(int line){
    	try {
			String txtLine = new String(bufferTxt, line_offset[line], line_width[line], bookFormat);
			return txtLine;
    	} catch (UnsupportedEncodingException e) {
    		return null;
    	}
    }
    
    /**
     * �������
     * @param offset - ��������һ��ĩβָ��
     * @param num - һ�г����ֽ��� 
     */
    private void addIndex(int offset, short num) {
        line_offset[line_number] = offset - num;
        line_width[line_number++] = num;
    }
    
    private static char[] symbol_table;
    private static int symbolLen;
    /**
     * ���ݵ�ǰ���彨��GB2312������תUnicode�ַ���
     */
    public static void initGB2312SymbolTable(){
     	byte[] code = new byte[600];
    	int c = 0;
    	for (int i = 0x00A1; i< 0x00A4; i++)
    		for(int j = 0x00A1; j < 0x00FF; j++) {
    			code[c++] = (byte) i;
    			code[c++] = (byte) j;
    		}
    	String cs = null; 
    	try {
			cs = new String(code, 0, c, ENC_GB2312);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		code = null;
		symbol_table = cs.toCharArray();
		symbolLen = cs.length();
		cs = null;
   }
    
    /**
     * GB2312������תΪUNICODE�ַ�
     * @param i ��һ�ֽ�
     * @param j �ڶ��ֽ�
     * @return  Unicode�ַ�
     */
    private static char GB2312SymToUnicode(byte i, byte j) {
    	int a = ((i  - 0xA1) & 0xff) << 1;
    	int b = j & 0xff - 0xA1;
//   	c = a * 94 + b = a*64 + a*32 - a*2 + b
		int c = (a << 5) + (a << 4) - a + b;
		if (c >= 0 && c < symbolLen)
			return symbol_table[c];
		else
			return '?';
    }
    
    /****************������������صĲ���************************/

    private int lastOffset;
    private byte[] sBuf;

    /**
     * �����ַ���
     * @param key - �����Ĺؼ����ַ���
     * @param startOffset - ��ʼ�������ļ�ƫ����
     * @return - ����0������������ƥ��λ��,���ļ�ƫ������=-1��ʾδ������
     */
    public int search(String keyString, int startOffset) {
    	byte[] key;// = null;
    	try {
			key = keyString.getBytes(bookFormat); //��Ҫ���ҵ��ַ���ת��Ϊ��ԭʼ��ʽһ�µ����� 
		} catch (UnsupportedEncodingException e) {
        	Error();
//			e.printStackTrace();
			return -1;
		}
	    int SEARCH_BUFFER_SIZE = 16384;
	    sBuf = new byte[SEARCH_BUFFER_SIZE];
		SEARCH_EOF = false;
		int start = startOffset;
		while (!SEARCH_EOF) {
			searchLocate(start);
			searchRead(SEARCH_BUFFER_SIZE); 
			// ɾ�������������һ���س��������ַ�
			getLastReturn();
			int offsetInBuffer = searchBuf(key);
			if (offsetInBuffer > 0) {
				// �����ɹ�����ת
				sBuf = null;
				return searchOffset + offsetInBuffer;
			}
			start += lastOffset;
		}
		sBuf = null;
		return -1;
    }
   
    /**
     * �ڻ�����������
     * @param key - �ؼ�������
     * @return : ƥ��λ�õĻ�������ƫ����, -1��ʾû��ƥ�� 
     */
 // ����ı���û�лس��������п��ܲ�׼ȷ���ر��ǿ绺���������
 // һ�ֻ���İ취�����󻺳��������ǿ��ǵ�һ��С˵����û�лس�����������㷨Ӧ�����þ��󲿷���� 
    private int searchBuf(byte[] key) {
    	int offset = 0;
    	byte first = key[0];
    	int keyLen = key.length;
    	int end = lastOffset - keyLen; // ����ı���û�лس��������п��ܲ�׼ȷ���ر��ǿ绺���������
    	while (offset < end) {
    		offset = getFirst(first, offset);
    		if (offset < 0)
    			return -1;
    		else if (Arrays.equals(sBuf, offset, key, 0, keyLen)) 
    			return offset;
    		else
    			offset++;
    	}
    	return -1;
    }
    
    /**
     * �����������ڷ��ϵ�һ���ؼ����ֽڵ�λ��
     * @param k - �ؼ��ֵĵ�һ���ֽ�
     * @return >0����������ƫ����, =-1��ʾδƥ��
     */
    private int getFirst(byte k, int startOffset) {
    	for(int i = startOffset; i < lastOffset; i++) {
    		if (k == sBuf[i])
    			return i;
    	}
    	return -1;
    }
    
    private boolean SEARCH_EOF;
    private int searchOffset;
    
    /**
     * ��ת���ļ�ָ��ƫ����
     * @param i - ƫ����
     */
    private void searchLocate(int i) {
    	if (i >= bookSize)
    		return;
    	else if (i <= 0 )  
            i = 0;
        searchOffset = i;
        close();
        open();
        try {
			di.skip(i);
		} catch (IOException e) {
//			e.printStackTrace();
        	Error();
		}
    }
    
    /**
     * �ӵ�ǰ�ļ�ƫ��λ�ö���len��byte
     * @param len - ָ����ȡ����
     * @return - ��ȡ�����ݿ�  
     */
    private int sBufLen;
    private void searchRead(int LENGTH) {
        try {
        	sBufLen = di.read(sBuf);
            SEARCH_EOF = (sBufLen < LENGTH) ? true : false;
            close();
        } catch (IOException ioe) {
        	Error();
//			ioe.printStackTrace();
            return;
        }
    }

    /**
     * �������������������һ���س���λ�� 
     * @return - ���һ���س���λ��
     */
    private int getLastReturn() {
        int t = sBufLen - 1;
        while (t > 0) {
        	byte c = sBuf[t];
            if (c == 10)  //��������
            	break;
            t--;
        }
        if (t == 0)  //û��һ���س���
        	t = sBufLen ;
        lastOffset = t;
        return t;
    }
 /**************************Class End************************************/
 }
