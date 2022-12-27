package com.team5.besthouse.interfaces;

import com.team5.besthouse.models.UserRole;

public interface DirectUICallback {

    void direct(boolean isCredentialCorrected, UserRole loginUserRole);
}
