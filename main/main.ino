// Libraries
#include "RTClib.h"
#include <SoftwareSerial.h>
#include <string.h>
#include <BH1750.h>

// components  
RTC_DS3231 rtc;
SoftwareSerial BTserial(2, 3);
BH1750 lightMeter;

// led
const int red = 9;
const int green = 10;
const int blue = 11;
int dimmervalue = 0, redval = 0, greenval = 0, blueval = 0;

// rtc and bh1750
const int shutters = 10;
const int day = 10;
int hmtf = 0, mmtf = 0, hw = 0, mw = 0;
bool autom = false;

void setup () {
  Serial.begin(57600);
  Wire.begin();
  lightMeter.begin();

  if (!rtc.begin()) {
    Serial.println("Couldn't find RTC");
    Serial.flush();
    while (1) delay(10);
  } else {rtc.adjust(DateTime(F(__DATE__), F(__TIME__)));}

  BTserial.begin(9600);
  delay(1000);
  pinMode(red, OUTPUT);
  pinMode(green, OUTPUT);
  pinMode(blue, OUTPUT);
}

void loop () {
  char data = '-';

  if(BTserial.available()){
    data = BTserial.read();
    Serial.print("data:");
    Serial.println(data);
  }

  // commands sent by the app
  switch (data){
    case 'B':
      for(int i = 0;i<3;i++){
        digitalWrite(blue, HIGH);
        delay(50);
        digitalWrite(blue, LOW);
        delay(50);}
      break;
    case 'F':
      changecolor("e", &redval, &greenval, &blueval);
      autom = false;
      break;
    case 'M':
      settingallarm(&hmtf, &mmtf);
      break;
    case 'W':
      settingallarm(&hw, &mw);
      break;
    case 'D':
      changedimmer(&dimmervalue, &redval, &greenval, &blueval);
      break;
    case 'b':case 'r':case 'g':case 'y':case 'o':case 's':
      changecolor(data, &redval, &greenval, &blueval);
      break;
    case '(':
      if(autom == false){autom = true;}
      else{autom = false;}
      break;
    case ')':
      break;
    default:
      break;
  }
  if(autom){automode(hw, mw, hmtf, mmtf, redval, greenval, blueval);}
  delay(100);
}

void changecolor(char color, int *redval, int *greenval, int *blueval){
  switch (color){
    case 'b':
      setcolor(0, 0, 255, redval, greenval, blueval);
      break;
    case 'r':
      setcolor(255, 0, 0, redval, greenval, blueval);
      break;
    case 'g':
      setcolor(0, 255, 0, redval, greenval, blueval);
      break;
    case 'y':
      setcolor(251, 252, 151, redval, greenval, blueval);
      break;
    case 'o':
      setcolor(252, 146, 15, redval, greenval, blueval);
      break;
    case 's':
      setcolor(93, 244, 252, redval, greenval, blueval);
      break;
    default:
      setcolor(0, 0, 0, redval, greenval, blueval);
      break;
  }
}

void setcolor(int r, int g, int b, int *redval, int *greenval, int *blueval){
  analogWrite(red, r);
  analogWrite(green, g);
  analogWrite(blue, b);
  
  *redval = r;
  *greenval = g;
  *blueval = b;
}

void changedimmer(int *value, int *redval, int *greenval, int *blueval){
  int prec = *value;
  int r, g, b;

  while(prec == *value){
    if(BTserial.available()){
      char data = BTserial.read();

      *value = data-48;
      if(*value == 0){
        break;
      }
    }
  }

  r = map(*value, 0, 10, 0, *redval);
  g = map(*value, 0, 10, 0, *greenval);
  b = map(*value, 0, 10, 0, *blueval);

  analogWrite(red, r);
  analogWrite(green, g);
  analogWrite(blue, b);
}

void settingallarm(int *h, int *m){
  String data;
  int index;
  bool part1 = true;
  
  data = BTserial.readString();
  index = data.indexOf(':');
  *h = (data.substring(0, index)).toInt();
  *m = (data.substring(index+1)).toInt();

  Serial.print(*h);
  Serial.print(":");
  Serial.println(*m);
}

void automode(int hw, int mw, int hmtf, int mmtf, int redval, int greenval, int blueval){
  DateTime now = rtc.now();
  int light = 0;
  int r, g, b;
  float lux = lightMeter.readLightLevel();
  Serial.println(lux);
  

  if((now.hour() == hw && now.minute() == mw && (now.day() == 6 || now.day() == 7)) 
    || (now.hour() == hmtf && now.minute() == mmtf && now.day() <= 5)){
    while(lux < shutters){
      Serial.println("mattino");
      analogWrite(red, 50);
      analogWrite(green, 29);
      analogWrite(blue, 3);
      lux = lightMeter.readLightLevel();
    }
  } else if((now.month() >= 9 || now.month() < 4) && now.hour() < 20){
    Serial.print("giorno");
    light = day-lux;
    if(light > 0){
      Serial.println(light);
      if(redval == 0 && greenval == 0 && blueval == 0){
        redval = 252;
        blueval = 15;
        greenval = 146;
      }
      r = map(light, 0, 10, 0, redval);
      g = map(light, 0, 10, 0, greenval);
      b = map(light, 0, 10, 0, blueval);    
      analogWrite(red, r);
      analogWrite(green, g);
      analogWrite(blue, b);
    } else{
      analogWrite(red, 0);
      analogWrite(green, 0);
      analogWrite(blue, 0);
    } 
  } else if(now.hour() >= 20){
    if(redval == 0 && greenval == 0 && blueval == 0){
      redval = 252;
      greenval = 146;
      blueval = 15;
    } else{
      r = map(3, 0, 10, 0, redval);
      g = map(3, 0, 10, 0, greenval);
      b = map(3, 0, 10, 0, blueval);
    }
    analogWrite(red, r);
    analogWrite(green, g);
    analogWrite(blue, b);
    Serial.print(redval);
    Serial.print("-");
    Serial.print(greenval);
    Serial.print("-");
    Serial.println(blueval);
  } else if((now.month() <= 9 && now.month() >= 4) && now.hour() < 20){
    analogWrite(red, 0);
    analogWrite(green, 0);
    analogWrite(blue, 0);
  }

}