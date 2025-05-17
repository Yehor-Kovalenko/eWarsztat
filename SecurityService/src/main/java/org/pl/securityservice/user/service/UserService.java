package org.pl.securityservice.user.service;

import org.pl.securityservice.exception.UserExistsException;
import org.pl.securityservice.exception.UserNotFoundException;
import org.pl.securityservice.user.dto.UserDto;

import java.util.List;


public interface UserService {

	/**
	 * @return
	 */
	List<UserDto> getUsers();

	/**
	 * @param id
	 * @return
	 * @throws UserNotFoundException
	 */
	UserDto findOne(long id) throws UserNotFoundException;

	/**
	 * @param id
	 */
	void delete(long id);

	/**
	 * @param userDto
	 * @return
	 * @throws UserExistsException
	 */
	UserDto createUser(UserDto userDto) throws UserExistsException;

}
