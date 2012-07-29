/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alarma;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author Saul Calderon
 */
public class ControladorAlarma {
    /** El comunicador arduino. */
    private ComunicadorArduinoUSB comunicadorArduino;
    
    private boolean alarmaActivada = false;
    
    /** Ventana principal*/
    private VentanaPrincipal ventana;
    
    public ControladorAlarma(){
        this.comunicadorArduino = new ComunicadorArduinoUSB();
        
    }
    
    public void iniciar(VentanaPrincipal ventana){
        this.ventana = ventana;
        this.comunicadorArduino.iniciar(9600, this);    
        this.comunicadorArduino.setLeerDatosArduino(false);
        
    }
    
    public void agregarABitacora(String entrada){
        if(entrada.compareTo("---") == 0){
            this.activarAlarma();
        }
        this.ventana.agregarABitacora(entrada);
    }
    
    public void conmutarAlarma(){
       if(!alarmaActivada){
           
            boolean armada = this.comunicadorArduino.leyendoArduino();
            //Esta armada
            if(armada){//Desarmar la alarma
                String password = this.ventana.getCampoPassword();
                if(password.compareTo("123")== 0){
                    this.ventana.cambiarEtiquetaAlarma("Armar alarma");                    
                    this.ventana.mostrarMensaje("Contraseña correcta, alarma desarmada");
                    this.comunicadorArduino.setLeerDatosArduino(false);
                }
                else{
                    this.ventana.mostrarMensaje("Codigo incorrecto");
                }
            }
            else{//si no estaba armada, arma la alarma
                this.ventana.cambiarEtiquetaAlarma("Desarmar alarma");
                this.ventana. setMensajeBarraEstado("Alarma armada");
                this.comunicadorArduino.setLeerDatosArduino(true);
            }
       }
       else{
           String password = this.ventana.getCampoPassword();
           if(password.compareTo("123")== 0){
               this.alarmaActivada = false;
               this.ventana.cambiarEtiquetaAlarma("Armar alarma");               
               this.ventana.mostrarMensaje("Contraseña correcta, alarma desactivada");
               this.comunicadorArduino.setLeerDatosArduino(false);
           }
           else{
                this.ventana.mostrarMensaje("Contraseña incorrecta");
           }
       
       }
           
    }
    
    public static synchronized void playSound(final String url) {
        new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing, see comments
        public void run() {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(VentanaPrincipal.class.getResourceAsStream("" + url));
                clip.open(inputStream);
                
                clip.start(); 
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        }).start();
    }

    
    public void activarAlarma(){
        boolean armada = this.comunicadorArduino.leyendoArduino();
        if(armada){
            this.alarmaActivada = true;
            this.ventana.cambiarEtiquetaAlarma("Desactivar Alarma");
            playSound("warning.wav");
            this.ventana.setMensajeBarraEstado("Alarma activada!!");
           // this.ventana.mostrarMensaje("Alarma activada!!!");
        }
    }
    
}
