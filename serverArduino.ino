
#include <SPI.h> //INCLUSÃO DE BIBLIOTECA
#include <Ethernet.h> //INCLUSÃO DE BIBLIOTECA
#include <Thermistor.h>

Thermistor temp(1);
byte mac[] = { 0x90, 0xA2, 0xDA, 0x0D, 0x32, 0x54 }; //ATRIBUIÇÃO DE ENDEREÇO MAC AO ETHERNET SHIELD W5100
byte ip[] = { 192, 168, 202,  200}; //FAIXA DE IP DISPONÍVEL DO ROTEADOR.
byte gateway[] = {192, 168, 202, 1}; //GATEWAY DE CONEXÃO
byte subnet[] = {255, 255, 255, 0}; //MASCARA DE REDE
EthernetServer server(80); //PORTA EM QUE A CONEXÃO SERÁ FEITA

String readString = String(50);
int msgCount = 0;
void setup(){
  Ethernet.begin(mac, ip, gateway, subnet); //PASSA OS PARÂMETROS PARA A FUNÇÃO QUE VAI FAZER A CONEXÃO COM A REDE
  server.begin(); //INICIA O SERVIDOR PARA RECEBER DADOS NA PORTA 80
  Serial.begin(9600);
  
}
void loop(){
EthernetClient client = server.available();
String information = "";
  if (client) {
    Serial.println("Client connected!");
    msgCount = 0;
    readString = "";
    while (client.connected()) {//ENQUANTO EXISTIR CLIENTE CONECTADO
        if (client.available()) { //SE O CLIENTE ESTÁ HABILITADO
            
            char c = client.read();
            readString += c; // "readstring" VAI RECEBER OS CARACTERES LIDO
            if (c == ':' && msgCount == 0){
               if(checkMethod(readString)){
                  client.print("400 Bad Request:-1:SDTP/0.9");
                  client.stop();
                  break;
               }
               readString = "";
               msgCount++;
            }else if(c == ':' && msgCount == 1){

              information = readString;
              readString = "";
              msgCount++;
            
            }else if (c == '/'){
               if(checkProtocol(readString)){
                  
                  if(!(sendInformation(client,information)))
                     client.print("500 Internal Server Error:-1:SDTP/0.9");
                  readString = "";
                    client.stop();    
                    break;
                  
               }else
                  client.print("400 Bad Request:-1:SDTP/0.9");
              
              client.stop();
              break;
            
            }
            
            
          }
      }
      Serial.println("Fechando conexão");
  }
}

boolean sendInformation(EthernetClient client,String information){
  if(information == "brightness:"){          
    Serial.println("Enviando luminosidade");
    client.print("100 Ok:");
    client.print(String((1023-analogRead(0))/float(1023)));
    client.print(":SDTP/0.9");     
    return true;
  }
  else if(information == "temperature:"){
    
    Serial.println("Enviando temperatura");
    client.print("100 Ok:");
    client.print(String(temp.getTemp()));
    client.print(":SDTP/0.9");         
    return true;
  }
  else if(information == "humidity:"){
    
    Serial.println("Enviando umidade");
    client.print("100 Ok:");
    client.print("80");
    client.print(":SDTP/0.9");      
    return true;
  }
  else{
    Serial.println("Sensor não disponível");
    Serial.println(information);
    client.print("300 Sensor Unavailable:");
    client.print(":SDTP/0.9");       
    return true;
  }

 return false;
  
}
boolean checkMethod(String msg){
  return msg != "GET:";
}
boolean checkProtocol(String msg){
  return msg == "SDTP/";
}
