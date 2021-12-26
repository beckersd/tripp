package lcd;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioInterruptCallback;

public class Interrupt_Listener implements GpioInterruptCallback{

    private final long debounceTime = 200;
    
    private long lastTime;
    
    public Interrupt_Listener() {
        System.out.println("Interruplistener initialized");
    }
    
    @Override
    public void callback(int pin) {
        long currentTime = System.currentTimeMillis();
        if(currentTime > lastTime+debounceTime){
            Gpio.digitalWrite(pin, Gpio.digitalRead(pin)==0?1:0);                
                        
            System.out.println("Button Pressed");
            
        } else {
            System.out.println("Discard event "+currentTime);
        }              
        lastTime=currentTime;
    }
    
}
