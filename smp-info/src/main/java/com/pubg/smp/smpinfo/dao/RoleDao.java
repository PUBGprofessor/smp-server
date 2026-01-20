package com.pubg.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pubg.smp.smpinfo.entity.Role;

/**
 * @author itning
 */
public interface RoleDao extends JpaRepository<Role, String> {
}
