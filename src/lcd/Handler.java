package lcd;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Lcd;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Handler {
    
    private final static int LCD_ROWS = 2;
    private final static int LCD_COLUMNS = 16;
    private final static int LCD_BITS = 4;
    
    private final static int GPIO_PIN = 26;
    
    private String version;
    public int lcdHandle;
    private final SimpleDateFormat formatter;
    
    public Interrupt_Listener interrupt_Listener;
    
    public Handler(String version) throws InterruptedException {
        this.version = version;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                int lcdHandle= getLcdHandle();
                 Lcd.lcdClear(lcdHandle); 
            }
        });
        
        try {
            setupWiringPi();
            lcdHandle = initLcd();  
        } catch (Exception ex) {}
        
        Gpio.pinMode(GPIO_PIN, Gpio.INPUT);
        Gpio.pullUpDnControl(GPIO_PIN, Gpio.PUD_DOWN);
        Gpio.delay(10);
        formatter = new SimpleDateFormat("HH:mm:ss");
        
        interrupt_Listener = new Interrupt_Listener();
        Gpio.wiringPiISR(GPIO_PIN, Gpio.INT_EDGE_FALLING, interrupt_Listener);
        
    }
    
    
    public static String formatTextToFit1Line(String value) {
        if (value.length()>=16) {
            return value.substring(0, 16);
        } else {
            if ((value.length() % 2) != 0) {
                value = value + "!";
            }
            Integer numberOfSparePos = 16 - value.length();
            Integer halfOfNumberOfSparePostitions = numberOfSparePos / 2;
            if (halfOfNumberOfSparePostitions != 0) {
                value = " " + value + " ";
            }
            for (int i=2; i<= halfOfNumberOfSparePostitions; i++ ) {
                value = "-" + value + "-";
            }
            return value;
        }
    }
    
    public void writeLineWithDate(String value) throws InterruptedException{
        Lcd.lcdClear(lcdHandle);
        Lcd.lcdPosition (lcdHandle, 0, 0) ;
        Lcd.lcdPuts (lcdHandle, "--- " + formatter.format(new Date()) + " ---");
        Lcd.lcdPosition (lcdHandle, 0, 1) ;
        Lcd.lcdPuts (lcdHandle, value);
        Thread.sleep(1000);
    }
    
    public void write2Lines(String value1, String value2) throws InterruptedException {
        Lcd.lcdClear(lcdHandle);
        Lcd.lcdPosition (lcdHandle, 0, 0) ;
        Lcd.lcdPuts (lcdHandle, value1);
        Lcd.lcdPosition (lcdHandle, 0, 1) ;
        Lcd.lcdPuts (lcdHandle, value2);
        Thread.sleep(2000);
        
    }
    
    public void writeMenuOption(String value) throws InterruptedException {
        writeOneLineWithMinusBelow(value);
    }
    
    public void writeWelcome() throws InterruptedException {
        // clear LCD
        Lcd.lcdClear(lcdHandle);
        
        write2Lines(formatTextToFit1Line("Tripp's Test"), formatTextToFit1Line(version));
        
        Lcd.lcdClear(lcdHandle);
    }
    
    private void writeOneLineWithMinusBelow(String text) throws InterruptedException {
        // write line 1 to LCD
        Lcd.lcdHome(lcdHandle);
        //Lcd.lcdPosition (lcdHandle, 0, 0) ; 
        Lcd.lcdPuts (lcdHandle, text) ;
        
        // write line 2 to LCD        
        Lcd.lcdPosition (lcdHandle, 0, 1) ; 
        Lcd.lcdPuts (lcdHandle, "----------------") ;
        Thread.sleep(2000);
    }
    
    
    
    public void clearScreen() throws InterruptedException {
        // clear LCD
        Lcd.lcdClear(lcdHandle);
    }
    
    private static void setupWiringPi() throws Exception {
        if (Gpio.wiringPiSetup() == -1) {
            System.out.println(" ==>> GPIO SETUP FAILED");
            Exception e = new Exception("GPIO SETUP FAILED");
            throw e;
        } else {
            System.out.println(" ==>> GPIO SETUP OK");        
            
        }
    }
    
    private static int initLcd() throws Exception {
        int lcdHandle = getLcdHandle();
        // verify initialization
        if (lcdHandle == -1) {
            System.out.println(" ==>> LCD INIT FAILED");
            Exception e = new Exception("LCD INIT FAILED");
            throw e;
        } else {
            System.out.println(" ==>> LCD INIT OK");        
        }
        return lcdHandle;
    }
    
    private static int getLcdHandle() {
        int lcdHandle= Lcd.lcdInit(LCD_ROWS,     // number of row supported by LCD
                                   LCD_COLUMNS,  // number of columns supported by LCD
                                   LCD_BITS,     // number of bits used to communicate to LCD 
                                   11,           // LCD RS pin
                                   10,           // LCD strobe pin
                                   0,            // LCD data bit 1
                                   1,            // LCD data bit 2
                                   2,            // LCD data bit 3
                                   3,            // LCD data bit 4
                                   0,            // LCD data bit 5 (set to 0 if using 4 bit communication)
                                   0,            // LCD data bit 6 (set to 0 if using 4 bit communication)
                                   0,            // LCD data bit 7 (set to 0 if using 4 bit communication)
                                   0);           // LCD data bit 8 (set to 0 if using 4 bit communication)
        return lcdHandle;
    }
}