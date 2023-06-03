package ntlong.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtTokenProvider jwtTokenProvider;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // Disable CSRF (cross site request forgery)
    http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and().authorizeRequests();

    // No session will be created or used by spring security
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // Entry points
    http.authorizeRequests()//
            .antMatchers("/users/signin").permitAll()//
            .antMatchers("/users/signup").permitAll()//
            .antMatchers("/users/account-verification/{.*}").permitAll()
            .antMatchers("/course/course-overall/{.*}").permitAll()
            .antMatchers("/course/course-information-structure/{.*}").permitAll()
            .antMatchers("/course/high-rating-courses").permitAll()
            .antMatchers("/public/document").permitAll()//
            .antMatchers("/admin/landing-page").permitAll()
            .antMatchers("/h2-console/**/**").permitAll()
            .antMatchers("/contactInfo").permitAll()//
            .antMatchers("/topic/question").permitAll()
            .antMatchers("/terms").permitAll()
            .antMatchers("/payment/save-payment").permitAll()
            .antMatchers("/category/get-courses").permitAll()
            .antMatchers("/news/{.*}").permitAll()
            .antMatchers("/news/list").permitAll()
            .antMatchers("/news/hot-news").permitAll()
            .antMatchers("/cart/*").permitAll()
            .antMatchers("/users/login-google").permitAll()
            .antMatchers("/users/client-login-google").permitAll()
            .antMatchers(HttpMethod.POST,"/rating/").hasAnyRole("ROLE_CLIENT","ROLE_ADMIN")
            // Disallow everything else..
            .anyRequest().authenticated();

    // If a user try to access a resource without having enough permissions
    http.exceptionHandling().accessDeniedPage("/login");

    // Apply JWT
    http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));

    // Optional, if you want to test the API from a browser
//     http.httpBasic();
  }

  @Override
  public void configure(WebSecurity web) {
    // Allow swagger to be accessed without authentication
    web.ignoring().antMatchers("/v2/api-docs")//
        .antMatchers("/swagger-resources/**")//
        .antMatchers("/swagger-ui.html")//
        .antMatchers("/configuration/**")//
        .antMatchers("/webjars/**")//
        .antMatchers("/public/document")//
        .antMatchers("/public")
        
        // Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
        .and()
        .ignoring()
        .antMatchers("/h2-console/**/**");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

}
