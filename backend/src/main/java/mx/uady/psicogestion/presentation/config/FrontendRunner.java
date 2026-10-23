package mx.uady.psicogestion.presentation.config;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class FrontendRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(FrontendRunner.class);
    private Process frontendProcess;

    @Value("${app.frontend.auto-start:true}")
    private boolean autoStart;

    @Override
    public void run(ApplicationArguments args) {
        if (!autoStart) {
            log.info("Autoarranque de frontend deshabilitado por configuración (app.frontend.auto-start=false).");
            return;
        }

        // Buscar directorio frontend (sea ejecutado desde root o desde backend/)
        File frontendDir = new File("../frontend");
        if (!frontendDir.exists() || !new File(frontendDir, "package.json").exists()) {
            frontendDir = new File("frontend");
        }

        if (!frontendDir.exists() || !new File(frontendDir, "package.json").exists()) {
            log.info("Directorio frontend no encontrado. No se iniciará el servidor de Vite.");
            return;
        }

        log.info("Iniciando Frontend (Vite) en {}...", frontendDir.getAbsolutePath());

        try {
            String npmCmd = "npm";
            if (new File("/opt/homebrew/bin/npm").exists()) {
                npmCmd = "/opt/homebrew/bin/npm";
            } else if (new File("/usr/local/bin/npm").exists()) {
                npmCmd = "/usr/local/bin/npm";
            }

            ProcessBuilder pb = new ProcessBuilder(npmCmd, "run", "dev");
            pb.directory(frontendDir);

            String currentPath = pb.environment().getOrDefault("PATH", "");
            pb.environment().put("PATH", "/opt/homebrew/bin:/usr/local/bin:" + currentPath);

            // Redirigir la salida del frontend a un archivo frontend.txt
            File rootDir = frontendDir.getParentFile() != null ? frontendDir.getParentFile() : new File(".");
            File logFile = new File(rootDir, "frontend.txt");

            pb.redirectOutput(ProcessBuilder.Redirect.to(logFile));
            pb.redirectError(ProcessBuilder.Redirect.to(logFile));

            frontendProcess = pb.start();

            Runtime.getRuntime().addShutdownHook(new Thread(this::stopFrontend));

            log.info("Frontend (Vite) corriendo en segundo plano: http://localhost:5173");
            log.info("Logs del frontend guardándose en: {}", logFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error al iniciar el frontend automáticamente: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void stopFrontend() {
        if (frontendProcess != null && frontendProcess.isAlive()) {
            log.info("Deteniendo proceso de frontend (Vite)...");
            try {
                frontendProcess.descendants().forEach(ProcessHandle::destroy);
            } catch (Exception ignored) {}
            frontendProcess.destroy();
        }
    }
}
