/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alarma;

/**
 *
 * @author Saul Calderon
 */
public abstract class InterfazComunicador {
   /** Indica si se debe detener la lectura de datos del arduino*/
   boolean detener = true;
   
    public void setLeerDatosArduino(boolean leer){
       this.detener = !leer;
   }
    
    public boolean conmutarLeerArduino(){
       this.detener = !this.detener;
       return this.detener;
   }
   
   public boolean leyendoArduino(){
       return !this.detener;
   }
   
     public abstract void  iniciar(int baudRate, ControladorAlarma controlador);
     
     public abstract void escribirArduino(String dato);
     public abstract void escribirDato(int dato);
    
}
