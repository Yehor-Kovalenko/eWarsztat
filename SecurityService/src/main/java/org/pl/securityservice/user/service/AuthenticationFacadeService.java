package org.pl.securityservice.user.service;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacadeService {

	Authentication getAuthentication();

}
