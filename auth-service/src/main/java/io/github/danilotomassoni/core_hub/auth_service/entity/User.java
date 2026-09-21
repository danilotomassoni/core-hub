package io.github.danilotomassoni.core_hub.auth_service.entity;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString 
@NoArgsConstructor
public class User extends Audit implements UserDetails {

    @Column(nullable = false)
    private String username;

    @Column(nullable =  false, unique = true)
    private String email;

    @Column(nullable =  false)
    private String password;

    @Enumerated(EnumType.STRING) // Salva o nome do enum (ex: "ADMIN") no banco em vez do índice numérico
    private RoleType role;
    
    private Boolean active = true; // Define true como padrão

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    // --- MÉTODOS OBRIGATÓRIOS DA INTERFACE USERDETAILS ---

    @Override
    public boolean isAccountNonExpired() {
        return true; // Conta não expira por padrão
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Conta não bloqueia por padrão
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credenciais não expiram por padrão
    }

    @Override
    public boolean isEnabled() {
        return this.active != null && this.active; // Baseia-se no status do campo 'active'
    }
}
