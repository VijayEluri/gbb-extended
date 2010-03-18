import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;

public final class Setting {
	private static Font font; // ����
	
	public static int useTrackBall;  //�������ҷ�ҳ 
	public static int fontSizeIndex;    //�����С����
	public static int fontSytleIndex;	//������������
	public static int[] fgColorIndex = {0,0};		//ǰ��ɫ����
	public static int[] bgColorIndex = {0,0};		//����ɫ����
	public static int[] lineColorIndex = {0,0};		//����ɫ����
	public static int fullScreen;       // =1ȫ��Ļ��=0��ȫ��
	public static int drawDashLine;		// =1 �����ߣ�=0������
	public static int mode;		// ��ҹģʽ  
	public static int openLastBook;     // =1���ϴζ��飻=0����
	public static int lastBookOffset;         //����򿪵���ƫ����
	public static int invertColor;    //=1 ��ɫ�� =0����ɫ
	public static int antiAliasIndex;    //���������
	public static int lineSpace;    //�о�
	public static int RecordID;
	public static int escToBackground; // esc�ź�̨��=0���ź�̨��=1�ź�̨
	public static int pageScroll; // =0 ��ҳ�ǲ�����һ�У�=1����һ��
	public static int[] bgType = {0,0}; // =0 ������ɫ��=1 ����ͼƬ
	public static int cycle;  // �б�ѭ�� =1ѭ��
//	public static int fgColorIndex2; //ͼƬ͸����
//	public static int bgColorIndex2;
//	public static int lineColorIndex2;
//	public static int bgType2;
	public static String nullStr = "string"; // �ַ�����ʼֵ
	public static String lastBookName; //����򿪵���ȫ·����
	public static String lastPath; //����򿪵���·��
	public static String reservedStr1; 
	public static String[] imageFileName = {nullStr, nullStr}; //����ͼƬȫ·����
	public static String lastImgPath; //����򿪵�ͼƬ·��
//	public static String imageFileName2;
	public static String reservedStr4;
	
	private static int[] style = {Font.PLAIN, Font.BOLD, Font.EXTRA_BOLD, Font.BOLD | Font.ITALIC , Font.ITALIC};
	private static int[] anti = {Font.ANTIALIAS_NONE, Font.ANTIALIAS_STANDARD, Font.ANTIALIAS_LOW_RES, Font.ANTIALIAS_SUBPIXEL};
	public static int[] colorList = {	
		Color.BLACK,
		Color.WHITE,
		Color.ANTIQUEWHITE,
		Color.NAVAJOWHITE,
		Color.SILVER,
		Color.SNOW,
		Color.WHEAT,
		Color.WHITESMOKE,
		Color.GHOSTWHITE,

		Color.LIGHTGREY,
		Color.DARKGRAY,
		Color.DARKSLATEGRAY,
		Color.DIMGRAY,
		Color.GRAY,
		Color.LIGHTSLATEGRAY,
		Color.SLATEGRAY,

		Color.DARKRED,
		Color.INDIANRED,
		Color.MEDIUMVIOLETRED,
		Color.ORANGERED,
		Color.PALEVIOLETRED,
		Color.RED,
		Color.MISTYROSE,
		Color.TOMATO,

		Color.GREEN,
		Color.DARKGREEN,
		Color.DARKSEAGREEN,
		Color.DARKOLIVEGREEN,
		Color.FORESTGREEN,
		Color.LIGHTGREEN,
		Color.LIGHTSEAGREEN,
		Color.LAWNGREEN,
		Color.PALEGREEN,
		Color.LIMEGREEN,
		Color.SPRINGGREEN,
		Color.MEDIUMSEAGREEN,
		Color.MEDIUMSPRINGGREEN,
		Color.SEAGREEN,
		Color.YELLOWGREEN,

		Color.ALICEBLUE,
		Color.BLUE,
		Color.CADETBLUE,
		Color.CORNFLOWERBLUE,
		Color.DARKBLUE,
		Color.DARKSLATEBLUE,
		Color.DEEPSKYBLUE,
		Color.LIGHTBLUE,
		Color.LIGHTSKYBLUE,
		Color.LIGHTSTEELBLUE,
		Color.MEDIUMBLUE,
		Color.MEDIUMSLATEBLUE,
		Color.MIDNIGHTBLUE,
		Color.POWDERBLUE,
		Color.ROYALBLUE,
		Color.SKYBLUE,
		Color.NAVY,
		Color.STEELBLUE,
		Color.SLATEBLUE,
		
		Color.GREENYELLOW,
		Color.LIGHTGOLDENRODYELLOW,
		Color.LIGHTYELLOW,
		Color.YELLOW,
		
		Color.CYAN,
		Color.DARKCYAN,
		Color.LIGHTCYAN,
	
		Color.BROWN,
		Color.ROSYBROWN,
		Color.SADDLEBROWN,
		Color.SANDYBROWN,

		Color.DEEPPINK,
		Color.HOTPINK,
		Color.LIGHTPINK,
		Color.PINK,

		Color.ORANGE,
		Color.DARKORANGE,

		Color.PURPLE,
		Color.MEDIUMPURPLE,


		Color.AQUA,
		Color.AQUAMARINE,
		Color.AZURE,
		Color.BEIGE,
		Color.BISQUE,
		Color.BLANCHEDALMOND,
		Color.BLUEVIOLET,
		Color.BURLYWOOD,
		Color.CHARTREUSE,
		Color.CHOCOLATE,
		Color.CORAL,
		Color.CORNSILK,
		Color.CRIMSON,
		Color.DARKGOLDENROD,

		Color.DARKKHAKI,
		Color.DARKMAGENTA,
		Color.DARKORCHID,
		Color.DARKSALMON,
		Color.DARKTURQUOISE,
		Color.DARKVIOLET,
		Color.FIREBRICK,
		Color.FLORALWHITE,
		Color.FUCHSIA,
		Color.GAINSBORO,
		Color.GOLD,
		Color.GOLDENROD,
		Color.HONEYDEW,
		Color.INDIGO,
		Color.IVORY,
		Color.KHAKI,
		Color.LAVENDER,
		Color.LAVENDERBLUSH,
		Color.LEMONCHIFFON,
		Color.LIGHTCORAL,
		Color.LIGHTSALMON,
		Color.LIME,
		Color.LINEN,
		Color.MAGENTA,
		Color.MAROON,
		Color.MEDIUMAQUAMARINE,
		Color.MEDIUMORCHID,
		Color.MEDIUMTURQUOISE,
		Color.MINTCREAM,
		Color.MOCCASIN,
		Color.OLDLACE,
		Color.OLIVE,
		Color.OLIVEDRAB,
		Color.ORCHID,
		Color.PALEGOLDENROD,
		Color.PALETURQUOISE,
		Color.PAPAYAWHIP,
		Color.PEACHPUFF,
		Color.PERU,
		Color.PLUM,
		Color.SALMON,
		Color.SEASHELL,
		Color.SIENNA,
		Color.TAN,
		Color.TEAL,
		Color.THISTLE,
		Color.TURQUOISE,
		Color.VIOLET
	};

	public static void init() {
//		fontFamilyIndex = 0;
		fontSizeIndex = 8;
//		fontSytleIndex = 0;
//		fgColorIndex = 0;
		fgColorIndex[1] = 1;
		bgColorIndex[0] = 1;
		drawDashLine = 1;
//		bgColorIndex2 = 0;
//		openLastBook = 0;
//		lastBookOffset = 0;
		fullScreen = 1;
//		invertColor = 0;
		lineSpace = 1;
//		antiAliasIndex = 0;
//		escToBackground = 0;
//		pageScroll = 0;
		lineColorIndex[0] = 9;
		lineColorIndex[1] = 9;
//		bgType = 0;
//		reservedInt4 = 0;
//		reservedInt5 = 0;
//		reservedInt6 = 0;
//		reservedInt7 = 0;
		cycle = 1;
		lastBookName = nullStr;
		lastPath = nullStr;
		reservedStr1 = nullStr;
		imageFileName[0] = nullStr;
		lastImgPath = nullStr;
		imageFileName[1] = nullStr;
		reservedStr4 = nullStr;
		
		RecordID = -1;
	}

//	public  static Font getFont(int familyID, int styleID, int sizeID, int antiAliasMode) {
	public  static Font getFont(int styleID, int sizeID, int antiAliasMode) {
		int sty = style[styleID];
		int aam = anti[antiAliasMode];
		int height = sizeID + 12;
//		Font f = fontFamily[familyID].getFont(sty, height, Ui.UNITS_px); 
		Font f = Font.getDefault(); 
		return f.derive(sty,height, Ui.UNITS_px, aam, 0);
	}

	/**
	 * ��������
	 * @return
	 */
	public static Font getFont() {
		int aam = anti[antiAliasIndex];
//		fontEffect = Font.COLORED_OUTLINE_EFFECT;
//		fontEffect = Font.DROP_SHADOW_RIGHT_EFFECT;
//		fontEffect = Font.EMBOSSED_EFFECT;
//		fontEffect = Font.ENGRAVED_EFFECT; 
		int sty = style[fontSytleIndex];
		int height = fontSizeIndex + 12;
//		Font f = fontFamily[fontFamilyIndex].getFont(sty, height, Ui.UNITS_px); 
		Font f = Font.getDefault();
		font =  f.derive(sty,height, Ui.UNITS_px, aam, 0); //���һ������0��glyph effect, ����û���ĵ�
		return font;
	}
	
	public static void FontSizeInc() {
		if (fontSizeIndex < 28)
			fontSizeIndex++;
	}
	
	public static void FontSizeDec() {
		if (fontSizeIndex > 0)
			fontSizeIndex--;
	}

	public static int returnColor(int id) {
		return colorList[id];
	}

	/**
	 * ����ǰ����ɫ
	 * @return
	 */
	public static int returnFGColor() {
		return colorList[fgColorIndex[mode]];
	}

	/**
	 * ���ر�����ɫ
	 * @return
	 */
	public static int returnBGColor() {
		return colorList[bgColorIndex[mode]];
	}

	/**
	 * ����������ɫ
	 * @return
	 */
	public static int returnLineFGColor() {
		return colorList[lineColorIndex[mode]];
	}

	/**
	 * ����ַ���Ϊ������Ϊstring,����д��RMS�����
	 * @param s - Ҫ�����ַ���
	 * @return - ��鲢�������ַ���
	 */
	public static String isNull(String s) {
		if (s == null || s.length() < 1)
			return nullStr;
		else
			return s;
	}
	
	/**
	 * ����ļ��Ƿ񻹴���
	 * @param bookName - �ļ���
	 * @return - true:�ļ�����, false:�ļ�������
	 */
	public static boolean bookExists(String bookName) {
		boolean result = false;
		FileConnection fconn;
		try {
			fconn = (FileConnection)Connector.open(bookName);
		    if (fconn.exists())  
		    	result = true;
	    	fconn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	fconn = null;
    	return result;
	}

	/**
	 * ���뱳��ͼƬ
	 */
	public static Bitmap loadBackgroundImage(String imgFile) {
		Bitmap bmp = null;;
		byte[] imgBuffer = null;
		FileConnection fconn = null;
		try {
			fconn = (FileConnection)Connector.open(imgFile);
			if ((fconn != null) && fconn.exists()) {
		        int size = (int) fconn.fileSize();
		        imgBuffer = new byte[size];
		        InputStream s;	
			    s = fconn.openInputStream();	
				s.read(imgBuffer);
			    s.close();
			    s = null;
			    fconn.close();
				EncodedImage image = EncodedImage.createEncodedImage(imgBuffer, 0, imgBuffer.length, "image/jpeg");
				bmp = image.getBitmap();
				imgBuffer = null;
				image = null;
			}
		} catch (IOException e) {
			bmp = null;
		}
	    fconn = null;
		return bmp;
	}

}
