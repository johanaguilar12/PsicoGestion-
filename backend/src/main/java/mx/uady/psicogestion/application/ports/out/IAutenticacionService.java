package mx.uady.psicogestion.application.ports.out;

public interface IAutenticacionService {
    String cifrarContrasena(String contrasenaPlana);
    boolean compararContrasena(String contrasenaPlana, String hash);
}