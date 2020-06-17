package com.pk.mentoria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@SpringBootApplication
public class MentoringApplication {

	//set cifrar/descifrar las contraseñas
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	@Autowired
	PasswordEncoder passwordEncoder;
	public static void main(String[] args) {
		SpringApplication.run(MentoringApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData (PlayerRepository playerRepository, GameRepository gameRepository , GamePlayerRepository gamePlayerRepository ) {
	return (args) -> {

		Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
		Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
		Player player3 = new Player("kim_bauer@gmail.com",  passwordEncoder().encode("kb"));
        Player player4 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));
		playerRepository.save(player1);
		playerRepository.save(player2);
		playerRepository.save(player3);
        playerRepository.save(player4);

		Game game1  = new Game();
		Game game2  = new Game();
		gameRepository.save(game1);
		gameRepository.save(game2);


		GamePlayer gamePlayer1 = new GamePlayer(player1,game1);
		GamePlayer gamePlayer2 = new GamePlayer(player2,game1);
		GamePlayer gamePlayer3 = new GamePlayer(player3,game2);
		GamePlayer gamePlayer4 = new GamePlayer(player4,game2);
		gamePlayerRepository.save(gamePlayer1);
		gamePlayerRepository.save(gamePlayer2);
		gamePlayerRepository.save(gamePlayer3);
		gamePlayerRepository.save(gamePlayer4);

	};
  }
}

//Spring Security Authentication Filter
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository personRepository;

	@Override
	//Spring Security Authentication Manager
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = personRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER")); // el Rol del usuario para la autorización
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

//set la Autorización
@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
						.antMatchers("/rest/**").hasAuthority("ADMIN")
						.antMatchers("/api/game_view/*", "/api/logout", "/api/games/*/players").hasAuthority("USER")
						.antMatchers("/api/login").permitAll()
						.antMatchers("/api/games").permitAll()
						.antMatchers("/api/players").permitAll()
						.anyRequest().denyAll();

		http.formLogin()
						.usernameParameter("username")
						.passwordParameter("password")
						.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}
	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);}
	}
}