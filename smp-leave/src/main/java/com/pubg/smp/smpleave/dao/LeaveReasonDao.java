package com.pubg.smp.smpleave.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pubg.smp.smpleave.entity.LeaveReason;

/**
 * @author itning
 */
public interface LeaveReasonDao extends JpaRepository<LeaveReason, String> {
}
