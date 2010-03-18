import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

public final class TextScreen extends MainScreen {
	private String bookName;  //������ȫ·��
	private String bookTitle; // ����������
	private TextBook txtBook;
	private Bitmap bmpBackground;  //����ͼƬ
	private boolean isFullScreen = false;  //ȫ�����
	private int TITLE_HEIGHT;// = 30; // title height
	private int TITLE_BG = Color.BLACK; // title background color
	private int TITLE_FG = Color.LIGHTGREY; // title font color
	private int TXT_BG;// = Color.WHITE; // book background color
	private int TXT_FG;// = Color.BLACK; // book font color
	private int LINE_FG;// = Color.LIGHTGREY;
	private int TOP_MARGIN;// = 0 ; //�ϱ߾�
	private int LINE_SPACE;// = 2;  //�о�
	private int MAX_W ;  //��Ļ���
	private int MAX_H ; // ��Ļ�߶�
	private int FONT_HEIGHT;  //����߶�
	private int LINE_PER_PAGE;  //ÿҳ��ʾ����
	private int IMAGE_W, IMAGE_H; //ͼƬ��ȼ��߶�
	private final static int LEFT_MARGIN = 3;  //��߾�
	private final static int RIGHT_MARGIN = 6; //�ұ߾�
	private Record bookRecord; //��¼
	private Font currentFont; //��ǰ����
	private int mode;
	private boolean useTrackBall;
	
	private DateFormat df = SimpleDateFormat.getInstance(SimpleDateFormat.TIME_SHORT);
	   
	/**
	 * ����Ĭ�ϲ˵���ȥ��Ĭ�ϵ�closeѡ��
	 */
	protected void makeMenu(Menu menu, int instance) { 
//		menu.add(_closeItem);
		menu.add(_jumpItem);
		menu.add(_ptItem);
		menu.add(_pbItem);
		menu.add(_fullItem);
		menu.add(_searchItem);
		menu.add(_addBookMarkItem);
		menu.add(_showBookMarkItem);
		menu.add(_copyItem);
		menu.add(_nextItem);
		menu.addSeparator();
		menu.add(_exitItem);
	}

    private MenuItem _fullItem = new MenuItem("ȫ ��(D)",130,20) {
    	public void run() {
    		toggleFullScreen();
    	}
    };

    private MenuItem _jumpItem = new MenuItem("�� ת(Z)",90,20){
    	public void run() {
    		pageJump(); 
    	}
    };
    private MenuItem _ptItem = new MenuItem("�� ʼ",70,20){
    	public void run() {
  			pageTop();
    	}
    };
    private MenuItem _pbItem = new MenuItem("�� β",75,20){
    	public void run() {
  			pageBottom();
    	}
    };
  
    private MenuItem _nextItem = new MenuItem("����һ��",141,20){
    	public void run() {
			String nextBook = getNextBook();
			if (nextBook != null) 
				openNextBook(nextBook);			
 			else 
 				Dialog.alert("�ѵ����һ��!");
    	}
    };

    private void search() {
		SearchScreen ss = new SearchScreen(txtBook, txtBook.getCurrentOffset());
		UiApplication.getUiApplication().pushModalScreen(ss);
		ss = null;
		}
    
	private MenuItem _searchItem = new MenuItem("�� ��(V)",132,20){
    	public void run() {
    		search();
   		}
    };
    
    private MenuItem _copyItem = new MenuItem("����ҳ��",140,20){
    	public void run() {
    		//�ѵ�ǰҳ���ݸ��Ƶ�������
    		Clipboard cb = Clipboard.getClipboard();	
			StringBuffer strToCopy = new StringBuffer();
			int startLine = txtBook.getCurrentLine();
   			int lastLine = getLastPageLine(startLine);

   			for (int i = startLine; i < lastLine; i++) 
   				strToCopy.append(txtBook.getLineText(i));
    		cb.put(strToCopy);
    		strToCopy = null;
    		cb = null;
    	}
    };

    private MenuItem _addBookMarkItem = new MenuItem("�����ǩ(A)",133,20){
    	public void run() {
    		addBookMark();
    	}
    };
    
    /**
     * �����ǩ
     */
    private void addBookMark() {
		BookMark bm = new BookMark();
		bm.offset = txtBook.getCurrentOffset();
		bm.bookName = bookTitle;
		bm.digest = txtBook.getLineText(txtBook.getCurrentLine()).trim(); // �õ�ǰҳ��ĵ�һ����ΪժҪ����ɾȥͷβ�Ŀո�
		
		Record bookMarkRecord = new Record(Record.BOOKMARK_NAME);
		bookMarkRecord.saveBookMark(bm, true);
		bookMarkRecord = null;
	}
    
    private MenuItem _showBookMarkItem = new MenuItem("����ǩ(L)",134,20){
    	public void run() {
    		showBookMark();
     	}
    };

    /**
     * ����ǩ
     */
    private void showBookMark() {
		BookMarkScreen bs = new BookMarkScreen(bookTitle, (int) txtBook.getBookSize());
		UiApplication.getUiApplication().pushModalScreen(bs);

		int t = bs.offset ;
		bs = null;
		if (t >= 0) {
			txtBook.jumpTo(t, TextBook.BUFFER_SIZE, false);
			txtBook.setCurrentLine(0);
			invalidate();
		}
	}

    private MenuItem _exitItem = new MenuItem("�� ��(Q)",150,20){
    	public void run() {
    		quit();
    	}
    };

    /**
     * ֱ���˳�
     */
	public void quit() {
		windowClose();
		System.exit(0);
	}

	private int maxLineX; // �����Ҷ˵�Y����
	public  TextScreen(String filename) {
		super();
		MAX_W = Display.getWidth();
		MAX_H = Display.getHeight();
		bookName = filename;
		maxLineX = MAX_W - 1;
   		max_x = MAX_W - RIGHT_MARGIN;
   		max = max_x - LEFT_MARGIN ;
		txtBook = new TextBook(bookName);
		bookTitle = txtBook.getBookName();
		mode = Setting.mode > 0 ? 1 : 0;
		setPage();

		loadBackgroundImage();
		loadHistory();  //����¼
		int keytype = Keypad.getHardwareLayout();
		if (keytype == Keypad.HW_LAYOUT_REDUCED || keytype == Keypad.HW_LAYOUT_REDUCED_24) 
			isReducedKeyboard = true;
	}
	
	/**
	 * ����ҳ����ز��� 
	 */
	private int PB_HEIGHT; //�������߶�
	private int lineTopY; // ������ʼY����
	private int deltaY;  // �������ֵ�Y���� 
	private int topY; // ������ʼY����
	private int max; // ҳ������ȣ��۳����ұ߾�
	private int min; // �����е�����ȣ� Ϊmax��ȥһ����ŵĿ��
	private int max_x; // ���ұ��ַ����ұ߽� 
	private void setPage() {
		setColor(Setting.invertColor); 
		useTrackBall = (Setting.useTrackBall > 0) ? true : false;
		isFullScreen = (Setting.fullScreen > 0) ? true : false;
		currentFont = Setting.getFont();
		setFont(currentFont);
		min = max - currentFont.getAdvance("��");
		LINE_SPACE = Setting.lineSpace;
		FONT_HEIGHT = currentFont.getHeight();
		TITLE_HEIGHT = (isFullScreen) ? 0 : FONT_HEIGHT + 1;
		PB_HEIGHT = (isFullScreen) ? 0 : 5; 
		
		int lh = FONT_HEIGHT + LINE_SPACE;
		int m = MAX_H - TITLE_HEIGHT - PB_HEIGHT;
		LINE_PER_PAGE = m / lh;
		TOP_MARGIN = (m - lh * LINE_PER_PAGE + LINE_SPACE) >> 1;
		topY = TOP_MARGIN + TITLE_HEIGHT;
		lineTopY = topY + FONT_HEIGHT - 1;
		deltaY = FONT_HEIGHT + LINE_SPACE;  
		txtBook.setPage(currentFont, LINE_PER_PAGE, max);
	}

	/**
	 * �����ô��ڣ� ��������Ϻ�Ӧ���µ�����
	 */
	private void showSetting() {
//		delAutoQuitTimer(); //�ر��Զ��˳���ʱ
		SettingScreen setScr = new SettingScreen(false);
		UiApplication.getUiApplication().pushModalScreen(setScr);
		if (setScr.isDirty()) {
			loadBackgroundImage();
			setColor(Setting.invertColor); 
			refreshPage();
		}
		setScr = null;
//		startAutoQuitTimer(); // �����Զ��˳���ʱ
	}
	
	private void refreshPage() {
		setPage(); // ����ҳ���������
		txtBook.jumpTo(txtBook.getCurrentOffset(), TextBook.BUFFER_SIZE, false);
		txtBook.setCurrentLine(0);
		invalidate();
	}
	
	/**
	 * ������ɫ
	 */
	private void setColor(int invert) {
		int mask = invert * 0xFFFFFFFF; 
		TXT_BG = Setting.returnBGColor() ^ mask;
		TXT_FG = Setting.returnFGColor() ^ mask;
		LINE_FG = Setting.returnLineFGColor() ^ mask;
	}
	
	/**
	 * ���뱳��ͼƬ
	 */
	private void loadBackgroundImage() {
		bmpBackground = null;
		if (Setting.bgType[mode] > 0) {
			bmpBackground = Setting.loadBackgroundImage(Setting.imageFileName[mode]);
			if (bmpBackground != null) {
				IMAGE_W = Math.min(MAX_W, bmpBackground.getWidth());
				IMAGE_H = Math.min(MAX_H, bmpBackground.getHeight());
			}
			else
				Setting.bgType[mode] = 0;
		}
	}
	
	/**
	 * ��ʾ��ǰʱ��ͽ���
	 */
	private void showInfo() {
		InfoScreen bid = new InfoScreen((int) getOffset(), (int)txtBook.getBookSize());
		UiApplication.getUiApplication().pushModalScreen(bid);
		bid = null;
	}
	
	/**
	 * ������ؼ�¼
	 */
	private void loadHistory() {
		bookRecord = new Record(Record.HISTORY_NAME);
		int id = bookRecord.findRecord(bookName);
		int offset = (id < 0) ? 0 : bookRecord.bookOffset;
		bookRecord = null;

		txtBook.jumpTo(offset, TextBook.BUFFER_SIZE, false);
		txtBook.setCurrentLine(0);
	}
	
	/**
	 * �����Ķ�����
	 */
	private void saveHistory() {
		bookRecord = new Record(Record.HISTORY_NAME);
		bookRecord.findRecord(bookName);
		bookRecord.bookName = this.bookName;
		bookRecord.bookOffset = txtBook.getCurrentOffset() ;
		bookRecord.saveBookHistory();

		Setting.fullScreen = (isFullScreen) ? 1 : 0;
		Setting.lastBookName = bookName;
		Setting.lastBookOffset = txtBook.getCurrentOffset();
		
		bookRecord.setStoreName(Record.SETTING_NAME);
		bookRecord.saveSetting();
		bookRecord = null;
	}
	
	/**
	 * �л�ȫ��/��ȫ��
	 * @param on - true��ʾȫ��
	 */
	private void toggleFullScreen() {
		isFullScreen = !isFullScreen;
		Setting.fullScreen = (isFullScreen) ? 1 : 0;;
		setPage();
		invalidate();	
	}
	
	/**
	 * �������
	 * @param g - Graphics����
	 */
	protected void clearBackground(Graphics g){
		if (Setting.bgType[mode] == 0) {
			g.setBackgroundColor(TXT_BG);
			g.clear();
		}
		else if (Setting.bgType[mode] > 0){
			g.drawBitmap(0, TITLE_HEIGHT, IMAGE_W, IMAGE_H - TITLE_HEIGHT, bmpBackground, 0, 0);
			g.setGlobalAlpha(255);
		}
	}

	/**
	 * ��������
	 * @param g - Graphics����
	 * @param title - ��������
	 */
	private void drawTitle(Graphics g) {

		// ���ñ���������ɫ
		g.setBackgroundColor(TITLE_BG);
		g.clear(0, 0, MAX_W, TITLE_HEIGHT);
		
		// ������������ 
		g.setColor(TITLE_FG);
		g.drawText(bookTitle + "   " + df.format(new Date()), 2, 1);
		
		// ���ײ�������
		int y = MAX_H - PB_HEIGHT ;
		long l = getOffset() * MAX_W / txtBook.getBookSize(); 
		int w = (int)l;
		g.setColor(Color.NAVY);
		g.fillRect(0, y, w, PB_HEIGHT);
		g.setColor(Color.LIGHTSKYBLUE);
		g.fillRect(w, y, MAX_W - w , PB_HEIGHT);
	}

	protected void paint(Graphics g){
		drawPage(g, txtBook.getCurrentLine());
		if (!isFullScreen) {
			drawTitle(g);
		}
	}

//	private int optimize;
	/**
	 * ��ָ���п�ʼ���һҳ
	 * @param g - Graphis����
	 * @param start - ��ҳ�ڵ�һ�е��к�
	 * @return - ��ҳ��������
	 */
    private void drawPage(Graphics g, int start) {
		clearBackground(g);
		g.setDrawingStyle(DrawStyle.TOP | DrawStyle.LEFT, true);
		int lastLine = getLastPageLine(start);
		
		if (Setting.drawDashLine > 0) { //������ 
			g.setColor(LINE_FG);  //��������ɫ
			g.setStipple(0xCCCCCCCC);  //������Ϊ���� 
			int ly = lineTopY;  // ���ߵ�Y����
			for (int i = start; i < lastLine ; i++) {
				g.drawLine(0, ly, maxLineX, ly);
				ly += deltaY;
			}
		}
		int ty = topY;
   		g.setColor(TXT_FG);
		for (int i = start; i < lastLine ; i++) {
		   		//��ʾ����
   			String txtLine = txtBook.getLineText(i); // ���ݱ���תΪ�ַ���
   			int w = currentFont.getAdvance(txtLine); //�õ����п������ֵ
   			if (w < min || w >= max) 
   				g.drawText(txtLine, LEFT_MARGIN, ty); // ����������ֱ�ӻ�����
   			else { 
//   	   			Dialog.alert(txtLine+ String.valueOf(w));
	   			int len = txtLine.length(); 
	   			int leftPixel = max - w;
	   			int cs = leftPixel / (len  - 1); //�ַ����
	   			leftPixel %= (len  - 1); // �۳��ַ�������������
   				int x0 = LEFT_MARGIN;
				for (int t = 0; t < len; t++) { 
	   				int dx = g.drawText(txtLine.charAt(t), x0, ty, 0, 50); //һ���ַ���Ȳ�Ӧ�ó���50
	   				if (t < leftPixel) // ���ݶ���left_w���أ���ǰleft_w���ַ�������cs+1
	   					dx++; 
		   				x0 += cs + dx;
	   			}
   			}
   			txtLine = null;
   			ty += deltaY;  
		}
		g.setGlobalAlpha(255);
    }

    /////////////���±���/////////////////////////
    /**
     * �Ѿ�����β�ˣ�����һ��
     */
   private String[] filter = {"*.txt","*.TXT","*.Txt"};
   private String getNextBook() {
 		Vector v = new Vector();
    	int i = bookName.lastIndexOf('/') + 1;
    	String path = bookName.substring(0, i);
		String bookFilename = bookName.substring(i);
    	//�г���ǰ������Ŀ¼�з��Ϻ�׺Ҫ����ļ�
    	try {
			int len = filter.length;
			FileConnection fc = (FileConnection)Connector.open(path);
			for (int t = 0; t < len; t++) {
				Enumeration list = fc.list(filter[t], false);
				while (list.hasMoreElements())
					v.addElement(list.nextElement());
				list = null;
			}
		} catch (IOException e) {
			return null;
		}
		String nextBook;
		if (v.size() == 0)
			nextBook = null;
		else {
			//���ļ��б��������
			String[] books = new String[v.size()];
			v.copyInto(books);
			v.removeAllElements();
			v = null;
			Arrays.sort(books, new bookCompare());
			//�ҵ��б��е���һ���飬�����ļ���
			i = Arrays.getIndex(books, bookFilename);
			if (i < books.length - 1) 
				nextBook = path + books[i + 1];
			else 
				nextBook = null;
			books = null;
		}
		return nextBook;
   }

    final class bookCompare implements Comparator {
	   	public int compare(Object o1, Object o2) {
	   		String x1 = (String)o1;
	   		String x2 = (String)o2;
   			return StringUtilities.compareToIgnoreCase(x1, x2);
	   	}
   }

    /**
     * ����
     * @param nextBook - ����ļ���
     */
    private void openNextBook(String nextBook) {
		saveHistory();
		bookName = nextBook;
		txtBook.clear();
		txtBook = null;
		txtBook = new TextBook(bookName);
		txtBook.setPage(currentFont, LINE_PER_PAGE, MAX_W - LEFT_MARGIN - RIGHT_MARGIN);
		bookTitle = txtBook.getBookName();
		loadHistory();  //����¼
		invalidate();
    }
    /////////////���±������/////////////////////////
   
	/**
	 * ���·���
	 * @param lines - ��Ҫ���¹��������� 
	 */
    private void pageDown(int lines) {
		if (txtBook.EOF && (txtBook.line_number - txtBook.getCurrentLine() <= LINE_PER_PAGE)) {
			//�����Ի��� 
			String nextBook = getNextBook();
			if (nextBook != null) {
				String prompt = "��: " + nextBook.substring(nextBook.lastIndexOf('/')+1);
				if (Dialog.ask(Dialog.D_YES_NO, prompt) == Dialog.YES) 
					openNextBook(nextBook);					
			}
 			else 
				return ;
		}
		else {
			txtBook.scrollDown(lines);
			invalidate();	
		}
	}

	/**
	 * ���Ϸ���
	 * @param lines - ��Ҫ���Ϲ��������� 
	 */
    private void pageUp(int lines) {
		if (txtBook.getCurrentLine() == 0 && txtBook.BOF)
			return;
		txtBook.scrollUp(lines);
		invalidate();
	}
	
	/**
	 * ��ת���ļ���β
	 */
    private void pageBottom() {
		txtBook.jumpToBottom();
		invalidate();
	}

	/**
	 * ��ת���ļ���ʼ
	 */
    private void pageTop() {
		txtBook.jumpTo(0, TextBook.BUFFER_SIZE, false);
		txtBook.setCurrentLine(0);
		invalidate();
	}

    private int getLastPageLine(int start) {
    	int lastLine = txtBook.getCurrentLine() + LINE_PER_PAGE;
		int ln = txtBook.line_number;
		lastLine = (lastLine > ln ) ? ln : lastLine;
		return lastLine;
    }
    /**
     * ���㵱ǰ���� 
     * @return - ��ǰ����=��ǰҳ�����һ���ֵ�ƫ����
     */
    private long getOffset() {
    	int pageLastLine = getLastPageLine(txtBook.getCurrentLine());
    	long offset;
		if (pageLastLine == txtBook.line_number && txtBook.BOF && txtBook.EOF)
			offset = txtBook.getBookSize(); // С�ļ�, һҳ�ھ�����ʾ��
		else {
	    	if (pageLastLine == txtBook.line_number)
	    		pageLastLine--;
			offset = txtBook.getLineOffset(pageLastLine);
		}
		return offset;
    }
    
	/**
	 * �Ӹ���ƫ������ת
	 * @param value - ƫ����
	 */
    private void pageJump() {
		JumpScreen js = new JumpScreen((int) getOffset(), (int) txtBook.getBookSize());
        UiApplication.getUiApplication().pushModalScreen(js);
		int v = js.getValue();
		js = null;
		if (v >= 0) {
			if (!(txtBook.BOF && txtBook.EOF)) {
				long  x =  v * txtBook.getBookSize() / 10000;
				txtBook.jumpTo((int)x, TextBook.BUFFER_SIZE, true);
			}
			txtBook.setCurrentLine(0);
			invalidate();
		}
	}
	
    private boolean doScroll(int keycode) {
		int pn = LINE_PER_PAGE - Setting.pageScroll;
		char key = Keypad.map(keycode);
		switch(key) {
		case Characters.LATIN_SMALL_LETTER_D :
			if (isReducedKeyboard)
   				pageUp(pn);
			else
				toggleFullScreen();
			return true;
		case Characters.LATIN_SMALL_LETTER_J :
			if (isReducedKeyboard)
   				pageDown(pn);
			else
				toggleFullScreen();
			return true;
		case Characters.SPACE: 
		case Characters.ENTER: 
		case Characters.LATIN_SMALL_LETTER_F:
		case Characters.LATIN_SMALL_LETTER_K:
			//��һҳ
			pageDown(pn);
			return true;
		case Characters.LATIN_SMALL_LETTER_H:
		case Characters.LATIN_SMALL_LETTER_S:
				//��һҳ 
			pageUp(pn);
			return true;
		case Characters.LATIN_SMALL_LETTER_E:
		case Characters.LATIN_SMALL_LETTER_U:
			// ��һ��
			pageUp(1);
			return true;
		case Characters.LATIN_SMALL_LETTER_X:
		case Characters.LATIN_SMALL_LETTER_N:
			// ��һ��
			pageDown(1);
			return true;
		}
		return false;
    }
    
    protected boolean keyDown(int keycode, int time) {
    	if (doScroll(keycode))
    		return true;
   		return super.keyDown(keycode, time);
    }
    
    protected boolean keyRepeat(int keycode, int time) {
    	doScroll(keycode);
		return super.keyRepeat(keycode, time);
    }
    
    private boolean isReducedKeyboard; // �ж��Ƿ���ST����
    protected boolean keyChar(char key, int status, int time) {	
		switch(key) {
    		case Characters.LATIN_SMALL_LETTER_Z:
    			// ҳ����ת
     			pageJump(); 
     			return true;
    		case Characters.LATIN_SMALL_LETTER_O:
    			// ����
     			showSetting(); 
     			return true;
    		case Characters.LATIN_SMALL_LETTER_C:
    			//��ʾʱ��ͽ���
    			showInfo();
     			return true;
      		case Characters.DIGIT_ZERO:
       			Setting.invertColor = (~Setting.invertColor) & 0x00000001;
    			setColor(Setting.invertColor); 
       			invalidate();
     			return true;
    		case Characters.LATIN_SMALL_LETTER_A:
    			//�����ǩ
    			addBookMark();   
     			return true;
    		case Characters.LATIN_SMALL_LETTER_L:
    			//����ǩ
    			showBookMark();   
     			return true;
    		case Characters.LATIN_SMALL_LETTER_V:
    			//������
    			search();   
     			return true;
    		case Characters.LATIN_SMALL_LETTER_G: //ΪST�������G�л�ȫ��
    			if (isReducedKeyboard)
    				toggleFullScreen();
    			return true;
     		case Characters.LATIN_SMALL_LETTER_M:
    			//������
    			HelpScreen hs = new HelpScreen(false);   
    			UiApplication.getUiApplication().pushModalScreen(hs);
    			hs = null;
     			return true;
    		case Characters.LATIN_SMALL_LETTER_I:
    			Setting.FontSizeDec(); //�����С
    			refreshPage() ;
    			return true;
    		case Characters.LATIN_SMALL_LETTER_Y :
    			Setting.FontSizeInc(); //��������
    			refreshPage() ;
    			return true;
    		case Characters.LATIN_SMALL_LETTER_T:
    			//�رմ��ڣ������ϼ�����
    			if (mode == 0)
    				mode = 1;
    			else
    				mode = 0;
    			Setting.mode = mode;
    			loadBackgroundImage();
    			refreshPage();
     			return true;
    		case Characters.LATIN_SMALL_LETTER_R:
    			//�رմ��ڣ������ϼ�����
    			windowClose();
     			return true;
    		case Characters.LATIN_SMALL_LETTER_P:
    			//�л����ⳣ��
    			if (!GBBMain.isLightOn) {
    				GBBMain.isLightOn = true;
    				Dialog.alert("���ⳤ��(2����)����");
    				Backlight.setTimeout(120);
    			}
    			else {
    				Dialog.alert("���ⳤ���ر�");
    				RestoreBackLight();    			
    			}
    			return true;
    		case Characters.ESCAPE : // �����ؼ�ʱ�ѳ���ת���̨
    			if (Setting.escToBackground > 0) {
    				saveHistory();
    				UiApplication.getUiApplication().requestBackground();
    			}
    			else 
    				windowClose();
    			return true;
    		case Characters.LATIN_SMALL_LETTER_Q:
    			//�˳�����
    			quit();   
    			break;
		}
		return super.keyChar(key, status, time);
     }
    
    private void RestoreBackLight() {
		GBBMain.isLightOn = false;
    	int defaultTime = Backlight.getTimeoutDefault();
    	Backlight.setTimeout(defaultTime);
    }
    
    protected boolean navigationMovement(int dx,int dy,int status,int time) {
//    	enableBackLight();
    	int pn = LINE_PER_PAGE - Setting.pageScroll;
    	if (dx > 0 && useTrackBall) {
			pageDown(pn);
			return true;
    	} else if (dx < 0 && useTrackBall) {
			pageUp(pn);
			return true;
    	} else if (dy > 0) {
    		if (GBBMain.isTrackWheel)
   				pageDown(pn);
    		else 
    			pageDown(dy);
			return true;
    	} else if (dy < 0) {
    		if (GBBMain.isTrackWheel)
   				pageUp(pn);
    		else
    			pageUp(-dy);
			return true;
    	}
    	return super.navigationMovement(dx, dy, status, time);
    }

    protected boolean navigationClick(int status, int time) {
    	if(!GBBMain.isTrackWheel) {
			showInfo();
			return true;
    	}
		return super.navigationClick(status, time);
    }

    private void windowClose() {
		saveHistory();
		RestoreBackLight();
		txtBook.clear();
		txtBook = null;
		close();
    }
    
////TextScreen Class ends////////////////////////////////////////
}
