package com.pubg.smp.smpclass.security;

import java.lang.annotation.*;

/**
 * @author itning
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MustLogin(role = MustLogin.ROLE.COUNSELOR)
public @interface MustCounselorLogin {
}
