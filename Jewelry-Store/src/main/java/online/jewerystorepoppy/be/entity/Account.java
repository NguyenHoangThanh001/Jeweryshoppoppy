package online.jewerystorepoppy.be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import online.jewerystorepoppy.be.enums.AccountStatus;
import online.jewerystorepoppy.be.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String fullName;

    @Column(unique = true)
    String email;

    @Column(unique = true)
    String phone;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    @Enumerated(EnumType.STRING)
    Role role;

    int point = 0;

    @OneToMany(mappedBy = "staff")
    @JsonIgnore
    List<Shift> shifts;

    @OneToMany(mappedBy = "createBy")
    @JsonIgnore
    List<Orders> orders = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    List<Orders> ordersOfCustomer;

    @OneToMany(mappedBy = "createBy")
    List<Voucher> vouchers;

    @Enumerated(EnumType.STRING)
    AccountStatus accountStatus = AccountStatus.ACTIVE;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonIgnore
    List<OrderBuy> orderBuys;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.phone;
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}
