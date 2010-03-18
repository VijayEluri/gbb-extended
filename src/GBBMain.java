//import net.rim.device.api.system.Sensor;
//import net.rim.device.api.system.SensorListener;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;

public final class GBBMain extends UiApplication // implements SensorListener
{
		public static int deviceName = 83;
		public static boolean isLightOn;
		public static boolean isTouchPad;
		public static boolean isTrackWheel;
		
        public static void main(String[] args) {
        	GBBMain app = new GBBMain();
            app.enterEventDispatcher();
        } 

        public GBBMain() {
//            Sensor.addListener(this,this,Sensor.HOLSTER);

        	// ��ʼ��GB2312���ű�
        	TextBook.initGB2312SymbolTable();
        	
        	TransitionScreen.MAX_W = Display.getWidth();
        	TransitionScreen.MAX_H = Display.getHeight();

        	// ��ȡ����
        	getDevicename(); 
        	
        	// ��������
     		Record setRecord = new Record(Record.SETTING_NAME);
    		setRecord.loadSetting();
    		setRecord = null;
    		
    		//��������
    		GBBMainScreen ms = new GBBMainScreen();
           	pushScreen(ms);
           	
           	//������Ķ���
    		if (Setting.openLastBook > 0) 
            	openLastBook();
         }

    	//ֱ�Ӵ��ϴ��Ķ��鼮
    	private void openLastBook() {
    		String book = Setting.lastBookName;
    		Record bookRecord = new Record(Record.HISTORY_NAME);
    		int id = bookRecord.findRecord(book);
    		bookRecord = null;
    		if (id < 0 || !Setting.bookExists(book))  // ����ʷ��¼���ѱ�ɾ��
    			return;
    		else {
   		    	TextScreen bookScreen = new TextScreen(book);
   				pushScreen(bookScreen);
   				bookScreen = null;
    		}
    	}

        // ��ȡ����  
        private void getDevicename() {
    		String dn = (DeviceInfo.getDeviceName()).substring(0,2);
    		int deviceID = Integer.parseInt(dn);
			deviceName = deviceID;
	    	if (deviceID == 85 || deviceID == 97)
	    		isTouchPad = true;
	    	else if (deviceID == 87)
	    		isTrackWheel = true;
	    		
        }
        
        //����ת���̨
        public void deactivate() {
        	isLightOn = false;
        	int defaultTime = Backlight.getTimeoutDefault();
        	Backlight.setTimeout(defaultTime);
        }
        
}

