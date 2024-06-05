package fr.eni.tp.filmotheque.configuration.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    protected final Log logger = LogFactory.getLog(getClass());
    private final static String SELECT_USER = "SELECT email, password, 1 from membre WHERE email=?";
    private final static String SELECT_USER_ROLES = "SELECT m.email, r.role from membre m INNER JOIN roles r ON r.is_admin = m.admin WHERE m.email=?";

    @Bean
    UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery(SELECT_USER);
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(SELECT_USER_ROLES);
        return jdbcUserDetailsManager;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/").permitAll();
            auth.requestMatchers(HttpMethod.GET, "/films").permitAll();
            auth.requestMatchers(HttpMethod.GET, "/films/detail").permitAll();
            auth.requestMatchers("/css/*").permitAll();
            auth.requestMatchers("/images/*").permitAll();
            auth.anyRequest().authenticated();
        });

        //Customiser le formulaire
        http.formLogin(form -> {
            form.loginPage("/login").permitAll();
            form.defaultSuccessUrl("/session").permitAll();
        });

//        http.formLogin(Customizer.withDefaults());

        // Logout --> vider la session et le contexte de sécurité
        http.logout(logout ->
                logout
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
                    .permitAll());

        return http.build();
    }
}
