#include <Arduino.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <WiFiClientSecure.h>
#include <ArduinoJson.h>
#include <DHT.h>
 
// DHT11 sensör ayarları
#define DHTPIN 4      // DHT11'in bağlı olduğu GPIO pin
#define DHTTYPE DHT11 // Sensör tipi
DHT dht(DHTPIN, DHTTYPE);
 
// WiFi bilgileriniz
const char* ssid = "Muharrem";
const char* password = "askimmeryemmm";
 
// Kullanıcı kimlik bilgileri
const char* userEmail = "basicuser@gmail.com";
const char* userPassword = "123Pa$$word!";
 
// Auth endpoint ve update endpoint
const char* authEndpoint = "https://192.168.73.226:9001/api/Account/authenticate";
const char* updateEndpoint = "/api/v1/ElectronicCard/update-data";
 
// Bearer token
String bearerToken = "";
 
// Veri gönderme aralığı (30 saniye)
const unsigned long sendInterval = 30000;
unsigned long previousMillis = 0;
 
// Hata durumu için değişken
String errorState = "";
 
void setup() {
  Serial.begin(115200);
  Serial.println("Başlatılıyor...");
 
  // DHT11 sensörünü başlat
  dht.begin();
 
  WiFi.begin(ssid, password);
  Serial.print("WiFi bağlanıyor");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
 
  Serial.println("\nWiFi bağlandı");
  Serial.print("IP adresi: ");
  Serial.println(WiFi.localIP());
 
  // Önce token al
  getAuthToken();
}
 
void loop() {
  unsigned long currentMillis = millis();
 
  // Her 30 saniyede bir veri gönder
  if (currentMillis - previousMillis >= sendInterval) {
    previousMillis = currentMillis;
   
    // Token kontrolü - eğer boşsa yeniden al
    if (bearerToken.length() == 0) {
      getAuthToken();
    }
   
    // Sensör verilerini oku ve gönder
    readSensorAndSendData();
  }
}
 
void getAuthToken() {
  WiFiClientSecure client;
  client.setInsecure();  // Sertifika doğrulamasını devre dışı bırak (test için)
 
  HTTPClient https;
  Serial.print("[HTTPS] Bağlanıyor: ");
  Serial.println(authEndpoint);
 
  if (https.begin(client, authEndpoint)) {
    https.addHeader("Content-Type", "application/json");
   
    DynamicJsonDocument jsonDoc(200);
    jsonDoc["email"] = userEmail;
    jsonDoc["password"] = userPassword;
   
    String requestBody;
    serializeJson(jsonDoc, requestBody);
   
    Serial.println("[HTTPS] POST gönderiliyor...");
    int httpCode = https.POST(requestBody);
    Serial.printf("[HTTPS] POST cevap kodu: %d\n", httpCode);
   
    if (httpCode > 0) {
      String payload = https.getString();
      Serial.println("Yanıt:");
      Serial.println(payload);
     
      DynamicJsonDocument responseDoc(1024);
      DeserializationError error = deserializeJson(responseDoc, payload);
     
      if (!error) {
        bearerToken = responseDoc["jwToken"].as<String>();
        Serial.println("Token alındı: " + bearerToken);
      } else {
        Serial.print("JSON ayrıştırma hatası: ");
        Serial.println(error.c_str());
        errorState = "auth_error";
      }
    } else {
      Serial.printf("[HTTPS] POST hatası: %s\n", https.errorToString(httpCode).c_str());
      errorState = "network_error";
    }
   
    https.end();
  } else {
    Serial.println("[HTTPS] Bağlantı kurulamadı");
    errorState = "connection_error";
  }
}
 
void readSensorAndSendData() {
  // DHT11'den okuma yap
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();
 
  // Sensör değerlerini kontrol et
  if (isnan(temperature) || isnan(humidity)) {
    Serial.println("DHT11 sensöründen okuma yapılamadı!");
    errorState = "sensor_error";
    temperature = 0;
    humidity = 0;
  } else {
    errorState = "";  // Okuma başarılıysa hata durumunu normal yap
  }
 
  Serial.print("Sıcaklık: ");
  Serial.print(temperature);
  Serial.print("°C, Nem: ");
  Serial.print(humidity);
  Serial.println("%");
 
  // Veri gönder
  sendUpdateData(String(temperature), String(humidity), errorState);
}
 
void sendUpdateData(String temperature, String humidity, String errorState) {
  if (bearerToken.length() == 0) {
    Serial.println("Token yok, önce kimlik doğrulama yapın");
    return;
  }
 
  WiFiClientSecure client;
  client.setInsecure();  // Sertifika doğrulaması kapalı (test için)
 
  HTTPClient https;
 
  String url = "https://192.168.73.226:9001";
  url += updateEndpoint;
 
  Serial.print("[HTTPS] PUT isteği yapılıyor: ");
  Serial.println(url);
 
  if (https.begin(client, url)) {
    https.addHeader("Authorization", "Bearer " + bearerToken);
    https.addHeader("Content-Type", "application/json");
    https.addHeader("accept", "*/*");
   
    // Body verisi
    DynamicJsonDocument jsonDoc(256);
    jsonDoc["id"] = 2;
    jsonDoc["temperature"] = temperature;
    jsonDoc["humidity"] = humidity;
    jsonDoc["errorState"] = errorState;
   
    String jsonBody;
    serializeJson(jsonDoc, jsonBody);
   
    int httpCode = https.PUT(jsonBody);
   
    Serial.printf("[HTTPS] PUT cevap kodu: %d\n", httpCode);
   
    if (httpCode > 0) {
      String payload = https.getString();
      Serial.println("Yanıt:");
      Serial.println(payload);
    } else {
      Serial.printf("[HTTPS] PUT hatası: %s\n", https.errorToString(httpCode).c_str());
      // Token süresi dolmuş olabilir, yeniden almayı dene
      if (httpCode == -1) {
        bearerToken = "";  // Token'ı temizle ki yeniden alınabilsin
      }
    }
   
    https.end();
  } else {
    Serial.println("[HTTPS] Bağlantı kurulamadı");
  }
}
 