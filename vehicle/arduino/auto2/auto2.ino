/*
 *  --- Auto 2.0 ---
 *
 * Controls stearing servo and ESC of a RC car according to 
 * incomming serial commands.
 * 
 * Accepted serial commands: "steering_int_value throttle_int_value" e.q. "123 87"
 * 
 * (c) 2016 Jaroslav Lžičař
 *
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <Jaroslav Lzicar> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.
 * ----------------------------------------------------------------------------
 */

#include <Servo.h>

int throttle;
int stearing;
int zeroThrottle = 90;
int zeroStearing = 90;
int servoPin = 3;
int escPin = 6;
int timer = 0;
bool timerActive = false;
Servo servo;
Servo esc;

void setup() {
  // Initialize servos
  servo.attach(servoPin);
  esc.attach(escPin);

  // Initialize serial 
  Serial.begin(115200);
  delay(20);
  emptySerial();

  // Set throttle and stearing to default values
  setDefaults();
  writeValues();
}

// Timer sets default values after no data it recieved for 100 msec
void loop() {
  if(timerActive){
    timer++;
  }
  if(timer > 50){
    timer = 0;
    timerActive = false;
    setDefaults();
    writeValues();
  }
  delay(2);
}

// This action happens when serial data is available
void serialEvent(){
  delay(2);
  int st = 0;
  int th = 0;
  if (Serial.available()){
    st = Serial.parseInt();
    th = Serial.parseInt();
  }
  emptySerial();
  if( st > 0 && st <= 181 && th > 0 && th <= 181 ){
    throttle = th - 1;
    stearing = st - 1;
    if(throttle != zeroThrottle || stearing != zeroStearing){
      timer = 0;
      timerActive = true;
    }
    writeValues();
  }
}

// Functions
// -------------------------------------------------------------

void writeValues(){
    servo.write(stearing);
    esc.write(throttle);
    Serial.print((String)"stearing: " + stearing);
    Serial.println((String)" throttle: " + throttle);
}

void setDefaults(){
  throttle = zeroThrottle;
  stearing = zeroStearing;
}

void emptySerial(){
  while(Serial.available()){
    Serial.read();  
  }
}



