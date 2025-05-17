package org.pl.securityservice.user.dao;

import java.util.List;
import java.util.Set;

import org.pl.securityservice.user.model.TRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface RoleDao extends JpaRepository<TRole, Long> {

	@Query(value = "SELECT * from T_ROLES where ROLE_NAME IN (:roles)", nativeQuery = true)
	Set<TRole> find(@Param("roles") List<String> roles);

}
