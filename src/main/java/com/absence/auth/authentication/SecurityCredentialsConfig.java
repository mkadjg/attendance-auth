package com.absence.auth.authentication;

import com.absence.auth.config.CorsFilter;
import com.absence.auth.factories.ResponseFactory;
import com.absence.auth.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityCredentialsConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtConfig jwtConfig;

    @Autowired
    CorsFilter corsFilter;

    @Autowired
    ApplicationRepository applicationRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> ResponseFactory
                        .sendUnAuthorizeErrorResponse(rsp))
            .and()
            .exceptionHandling().accessDeniedHandler((req, rsp, e) -> ResponseFactory
                        .sendAccessDeniedErrorResponse(rsp))
            .and()
            .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
            .authorizeRequests()
            .and()
            .addFilterAfter(jwtTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()
            .antMatchers(HttpMethod.GET, "/").permitAll()
            .antMatchers(HttpMethod.GET, "/user/logout/**").permitAll()
            .antMatchers("/auth/forgot-password/send-otp").permitAll()
            .antMatchers("/auth/forgot-password/validate-otp").permitAll()
            .antMatchers("/auth/forgot-password/new-password").permitAll()
            // only hrd
            .antMatchers(HttpMethod.POST, "/employee/**").hasAuthority("HRD")
            .anyRequest().authenticated();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter() {
        return new JwtTokenAuthenticationFilter(
                jwtConfig, applicationRepository);
    }

}
