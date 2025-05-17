package org.pl.securityservice.user.dao;

import org.pl.securityservice.user.model.TUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDao extends JpaRepository<TUser, Long> {

	/**
	 * @param username
	 */
	TUser findByUsername(String username);

	/**
	 * @param email
	 */
	TUser findByEmail(String email);

}
