package study.loginstudy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import study.loginstudy.auth.MyAccessDeniedHandler;
import study.loginstudy.auth.MyAuthenticationEntryPoint;
import study.loginstudy.domain.UserRole;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Form Login에 사용하는 Security Config
 */
/* Security Config2(Jwt Token Login에서 사용)와 같이 사용하면 에러 발생
Security Form Login 진행하기 위해서는 이 부분 주석 제거 후 Security Config2에 주석 추가
@Configuration
@EnableWebSecurity
*/
/* 다른 인증, 인가 방식 적용을 위한 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true)
*/
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                // 인증
                .antMatchers("/security-login/info").authenticated()
                // 인가
                .antMatchers("/security-login/admin/**").hasAuthority(UserRole.ADMIN.name())
                .anyRequest().permitAll()
                .and()
                // Form Login 방식 적용
                .formLogin()
                // 로그인 할 때 사용할 파라미터들
                .usernameParameter("loginId")
                .passwordParameter("password")
                .loginPage("/security-login/login")     // 로그인 페이지 URL
                .defaultSuccessUrl("/security-login")   // 로그인 성공 시 이동할 URL
                .failureUrl("/security-login/login")    // 로그인 실패 시 이동할 URL
                .and()
                .logout()
                .logoutUrl("/security-login/logout")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID");
        http
                .exceptionHandling()
                .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler());
                /* 익명 클래스 사용 예제
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        response.sendRedirect("/security-login/authentication-fail");
                     }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        response.sendRedirect("/security-login/authorization-fail");

                    }
                });*/
    }
}