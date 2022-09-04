package com.absence.auth.authentication;

import com.absence.auth.dtos.LoginRequestDto;
import com.absence.auth.exceptions.BadCredentialException;
import com.absence.auth.models.UserLoginHistory;
import com.absence.auth.models.UserRole;
import com.absence.auth.models.Users;
import com.absence.auth.repositories.UserLoginHistoryRepository;
import com.absence.auth.repositories.UserRoleRepository;
import com.absence.auth.repositories.UsersRepository;
import com.absence.auth.utilities.PasswordHashUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authentication;
    }

    public Authentication authenticate(LoginRequestDto loginRequestDto, UsersRepository usersRepository,
                                       UserLoginHistoryRepository userLoginHistoryRepository,
                                       UserRoleRepository userRoleRepository, JwtConfig jwtConfig)
            throws AuthenticationException, BadCredentialException {

        Users users = usersRepository.findByUsername(loginRequestDto.getUsername()).orElse(null);
        if (users == null) {
            throw new BadCredentialException("Invalid username or password!");
        }

        if (users.getStatus() != 1) {
            throw new BadCredentialException("User is inactive!");
        }

        String passwordHex = PasswordHashUtil.generate(loginRequestDto.getPassword());
        if (passwordHex.equals(users.getPassword())) {

            UserLoginHistory personLoginHistory = new UserLoginHistory();
            personLoginHistory.setLoginDate(new Date());
            personLoginHistory.setStatus(true);
            personLoginHistory.setUserId(users.getUserId());
            userLoginHistoryRepository.save(personLoginHistory);

            List<UserRole> userRoles = userRoleRepository.findByUserId(users.getUserId());
            List<String> authorities = userRoles.stream()
                    .map(userRole -> userRole.getRole().getRoleName()
            ).collect(Collectors.toList());

            if (authorities.size() > 0) {
                CustomAuthenticationToken authentication = new CustomAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword(),
                        authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                authentication.setDetails(users);
                authentication.setAuthenticated(true);
                return authentication;
            } else {
                throw new BadCredentialException("Invalid username or password!");
            }
        } else {
            UserLoginHistory userLoginHistory = new UserLoginHistory();
            userLoginHistory.setLoginDate(new Date());
            userLoginHistory.setStatus(false);
            userLoginHistory.setUserId(users.getUserId());
            userLoginHistoryRepository.save(userLoginHistory);

            LocalDateTime tenMinuteBefore = LocalDateTime.now(ZoneId.systemDefault());
            tenMinuteBefore = tenMinuteBefore.plusMinutes(-5);
            List<UserLoginHistory> userLoginHistories = userLoginHistoryRepository.findFailedUserLogin(users.getUserId(),
                    Date.from(tenMinuteBefore.atZone(ZoneId.systemDefault()).toInstant()));
            int totalFail = 0;
            for (UserLoginHistory loginHistory : userLoginHistories) {
                if (!loginHistory.getStatus()) {
                    totalFail++;
                } else {
                    break;
                }
            }
            if (totalFail == 2) {
                throw new BadCredentialException("You have one more attempt to login!");
            } else if (totalFail > 2) {
                userLoginHistory.setStatus(true);
                LocalDateTime localDateTime = LocalDateTime.now();
                localDateTime = localDateTime.plusMinutes(jwtConfig.getExpirationInMinutes());
                users.setLockedUntil(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
                usersRepository.save(users);
                throw new BadCredentialException("Your account is Locked!, try again in 60 minutes");
            }
            throw new BadCredentialException("Invalid username or password!");
        }

    }
}
