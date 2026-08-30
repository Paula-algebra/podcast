package camelpodcast.config;

import org.apache.camel.Exchange;
import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;

@Configuration
public class CamelSecurityPolicies {

    public static final String USER_POLICY = "userPolicy";
    public static final String ADMIN_POLICY = "adminPolicy";

    @Bean(USER_POLICY)
    public SpringSecurityAuthorizationPolicy userPolicy(
            AuthenticationManager authManager) {
        SpringSecurityAuthorizationPolicy policy =
                new SpringSecurityAuthorizationPolicy();
        policy.setId(USER_POLICY);
        policy.setAuthenticationManager(authManager);

        AuthorizationManager<Exchange> authz =
                AuthorityAuthorizationManager.hasRole("USER");
        policy.setAuthorizationManager(authz);

        return policy;
    }

    @Bean(ADMIN_POLICY)
    public SpringSecurityAuthorizationPolicy adminPolicy(
            AuthenticationManager authManager) {
        SpringSecurityAuthorizationPolicy policy =
                new SpringSecurityAuthorizationPolicy();
        policy.setId(ADMIN_POLICY);
        policy.setAuthenticationManager(authManager);

        AuthorizationManager<Exchange> authz =
                AuthorityAuthorizationManager.hasRole("ADMIN");
        policy.setAuthorizationManager(authz);

        return policy;
    }
}