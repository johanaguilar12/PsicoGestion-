package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.infrastructure.persistence.JpaSecretariaRepository;
import mx.uady.psicogestion.infrastructure.persistence.SecretariaEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CuentaUserDetailsService implements UserDetailsService {

    private final JpaSecretariaRepository secretariaRepository;

    public CuentaUserDetailsService(JpaSecretariaRepository secretariaRepository) {
        this.secretariaRepository = secretariaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identificador) {
        SecretariaEntity secretaria = secretariaRepository.findByIdentificador(identificador)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
        String rol = secretaria.getRol() == null || secretaria.getRol().isBlank()
                ? "USUARIO"
                : secretaria.getRol().trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));

        return new CuentaUserDetails(
                secretaria.getIdSecretaria(),
                secretaria.getNombreCompleto(),
                secretaria.getTelefono(),
                secretaria.getCorreoElectronico(),
                secretaria.getContrasena(),
                secretaria.isActivo(),
                secretaria.getFotoPerfil(),
                secretaria.getRol(),
                authorities
        );
    }

    public record CuentaUserDetails(
            Integer id,
            String nombreCompleto,
            String telefono,
            String correoElectronico,
            String passwordHash,
            boolean activo,
            String fotoPerfil,
            String rol,
            List<GrantedAuthority> authorities
    ) implements UserDetails {

        @Override
        public List<GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return passwordHash;
        }

        @Override
        public String getUsername() {
            return correoElectronico;
        }

        @Override
        public boolean isEnabled() {
            return activo;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
    }
}
