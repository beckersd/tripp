package trippexamen;

import lcd.Handler;

public class TrippExamen {

    public static void main(String[] args) throws InterruptedException {
        Handler lcd_gpio_Handler = new Handler("v1.0");
        lcd_gpio_Handler.writeWelcome();
        String text;
        
        int i = 20;
        while (i >0) {
            text = "Test " + i;
            System.out.println(text);
            //lcd_gpio_Handler.writeLineWithDate(text);
            i -= 1;
        }
        Thread.sleep(4000);
    }
    
}
