
int numReadings = 20;
int thInputPin = A0;
int stInputPin = A1;
int readIndex = 0;
int total = 0;
int average = 0;

void setup() {
  Serial.begin(115200);
}

void loop() {
  total += analogRead(thInputPin);
  readIndex ++;

  if(readIndex == numReadings){
    average = total / numReadings;
    readIndex = 0;
    total = 0;
    Serial.println(average);
  }
  
  delay(1);
}
