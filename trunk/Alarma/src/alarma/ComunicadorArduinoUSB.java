/*
 * Proyecto final Analisis Sistemas
 * Jorge Castro
 * Saul Calderon
 * Rodolfo Arias
 */
package alarma;

//This class:
// - Starts up the communication with the Arduino.
// - Reads the data coming in from the Arduino and print's it out to terminal.
// - It reads and prints out LINES of data coming in

//Code builds upon this great example:
//http://www.csc.kth.se/utbildning/kth/kurser/DH2400/interak06/SerialWork.java

//Load Libraries
import java.io.*;
import java.util.TooManyListenersException;

//Load RXTX Library
import gnu.io.*;

// TODO: Auto-generated Javadoc
/**
 * The Class ComunicadorArduino.
 */
class ComunicadorArduinoUSB implements SerialPortEventListener{
   
   /** Recurso del puerto serial */
   SerialPort puertoSerial;   
   /** The flujo entrada. */
   InputStream flujoEntrada;   
   
   /** The lector buffer. */
   BufferedReader lectorBuffer;   
   
   
   ControladorAlarma controlador;
   /** The graficador. */
  // Graficador graficador;
   
   /** Indica si se debe detener la lectura de datos del arduino*/
   boolean detener = true;
   
   /**
    * Abre el flujo de entrada.
    *
    * @param nombrePuerto the nombre puerto
    * @param baudRate the baud rate
    */
   public void iniciar(int baudRate, ControladorAlarma controlador){
       this.controlador = controlador;
       String[] nombresPuertos = new String[2];
       nombresPuertos[0] = "COM4";
       nombresPuertos[1] = "COM3";
       boolean encontrado = false;
       for(int i = 0; i < 2 && !encontrado; ++i){
           String nombrePuerto = nombresPuertos[i];
        //detener = false; 
        try {
            //busca y abre el puerto serial
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(nombrePuerto);
            puertoSerial = (SerialPort)portId.open("my_java_serial" + nombrePuerto, 2000);
            System.out.println("Puerto serial encontrado y abierto");
            encontrado = true;
            //Configura el puerto
            try {
                puertoSerial.setSerialPortParams(baudRate,
                puertoSerial.DATABITS_8,
                puertoSerial.STOPBITS_1,
                puertoSerial.PARITY_NONE);            
            } 
            catch (UnsupportedCommOperationException e){
                System.out.println("Probablemente una velocidad no soportada");
            }

            //abre el flujo de entrada
            try{
                flujoEntrada = puertoSerial.getInputStream();
                //El lector permite leer una linea a la vez
                lectorBuffer = new BufferedReader(new InputStreamReader(flujoEntrada));
            } 
            catch (IOException e) { 
                System.out.println("No se obtuvieron los flujos");
            }

            // se lee en otro hilo
            try {
                puertoSerial.addEventListener(this);
                puertoSerial.notifyOnDataAvailable(true);
                System.out.println("Event listener added");
            } 
            catch (TooManyListenersException e){
                System.out.println("couldn't add listener");
            }
        }
        catch (Exception e) 
        { 
            System.out.println("Port in Use: "+e);
        }
       }
   }
   
   public void setLeerDatosArduino(boolean leer){
       this.detener = !leer;
   }

  
   /**
    * Cierra el puerto serial.
    */
   public void cerrarPuertoSerial(){
      try{
         flujoEntrada.close();
         detener = true; 
         puertoSerial.close();
         System.out.println("Serial port closed");
      }
      catch (Exception e){
    	  System.out.println(e);
      }
   }
   
   /**
    * Serial event.
    *
    * @param evento the evento
    */
   public void serialEvent(SerialPortEvent evento){ 
      //Lee datos linea por linea
      while (evento.getEventType() == SerialPortEvent.DATA_AVAILABLE ) {
         try{ 
            //Lee una linea
        	String line = lectorBuffer.readLine();
                if(detener == false)
                    this.controlador.agregarABitacora(line);
                System.out.println(line);
        	
         } 
         catch (IOException e){
         }
      }
   }
   
   /**
    * Sets the graficador.
    *
    * @param plotter the new graficador
    */
/*   public void setGraficador(Graficador plotter){
       this.graficador = plotter;
   }*/

   /**
    * Conmutar leer datos.
    * Si no esta leyendo los datos, empieza a hacerlo
    * Si estaba leyendo los datos, deja de hacerlo
    * @return true, si dejo de leer datos
    */
   public boolean conmutarLeerArduino(){
       this.detener = !this.detener;
       return this.detener;
   }
   
   public boolean leyendoArduino(){
       return !this.detener;
   }
   
   
}