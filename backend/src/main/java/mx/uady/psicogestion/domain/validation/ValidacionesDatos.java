
package mx.uady.psicogestion.domain.validation;

import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ValidacionesDatos {

    private static final Pattern TELEFONO =
            Pattern.compile("^[0-9]{10}$");

    private static final Pattern CORREO =
            Pattern.compile(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$"
            );

    private static final Pattern CORREO_INSTITUCIONAL =
            Pattern.compile(
                "^[A-Za-z0-9._%+-]+@(correo\\.uady\\.mx|alumnos\\.uady\\.mx)$",
                Pattern.CASE_INSENSITIVE
            );

    private static final Pattern CEDULA =
            Pattern.compile("^[0-9]{7,9}$");

    private ValidacionesDatos() {
    }

    public static boolean telefonoValido(String telefono) {
        return telefono != null &&
                TELEFONO.matcher(telefono).matches();
    }

    public static boolean correoValido(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }

        String valor = correo.trim();

        if (valor.length() > 254 ||
                !CORREO.matcher(valor).matches()) {
            return false;
        }

        String[] partes = valor.split("@", -1);

        if (partes.length != 2 ||
                partes[0].length() > 64 ||
                partes[0].startsWith(".") ||
                partes[0].endsWith(".") ||
                partes[0].contains("..")) {
            return false;
        }

        for (String etiqueta : partes[1].split("\\.")) {
            if (etiqueta.isEmpty() ||
                    etiqueta.length() > 63 ||
                    etiqueta.startsWith("-") ||
                    etiqueta.endsWith("-")) {
                return false;
            }
        }

        return true;
    }

    public static boolean correoInstitucionalValido(String correo) {
        return correoValido(correo) &&
                CORREO_INSTITUCIONAL.matcher(
                    correo.trim()
                ).matches();
    }

    public static boolean cedulaValida(String cedula) {
        return cedula != null &&
                CEDULA.matcher(cedula).matches();
    }

    public static boolean edadMinima(
            LocalDate fechaNacimiento,
            int edadMinima
    ) {
        if (fechaNacimiento == null ||
                fechaNacimiento.isAfter(LocalDate.now())) {
            return false;
        }

        return Period.between(
            fechaNacimiento,
            LocalDate.now()
        ).getYears() >= edadMinima;
    }

    public static String normalizarCurp(String curp) {
        return curp == null
                ? null
                : curp.trim().toUpperCase(Locale.ROOT);
    }

    // Solo se valida que la CURP tenga exactamente 18 caracteres.
    // Se conservan los parámetros para no modificar Paciente.java.
    public static boolean curpValida(
            String curp,
            String nombreCompleto,
            LocalDate fechaNacimiento,
            String sexo
    ) {
        return curpValida(curp, fechaNacimiento, sexo);
    }

    // Conserva compatibilidad con las llamadas anteriores.
    public static boolean curpValida(
            String curp,
            LocalDate fechaNacimiento,
            String sexo
    ) {
        String valor = normalizarCurp(curp);

        return valor != null && valor.length() == 18;
    }
}
