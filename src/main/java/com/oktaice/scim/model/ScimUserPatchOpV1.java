package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ScimUserPatchOpV1 extends ScimUserPatchOp {
    private static final Logger logger = LoggerFactory.getLogger(ScimUserPatchOpV1.class);

    @JsonProperty("active")
    Boolean active;
    @JsonProperty("password")
    String password;

    public ScimUserPatchOpV1() {
        super();
        getSchemas().add(SCHEMA_PATCH_OP_V1);
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean updateUser(User user) {
        boolean updated = false;

        if (getPassword() != null) {
            logger.warn("Password not Implemented in User Scheme: " + getPassword());
        } else if (active != null) {
            if (user.getActive() != isActive()) {
                user.setActive(!user.getActive());
                updated = true;
            }
        } else {
            logger.error("Invalid patch type");
            throw new RuntimeException("Invalid Patch Type.");
        }

        return updated;
    }

    public boolean validatePatchOp() {
        logger.error(getSchemas().get(0));
        if (!getSchemas().get(0).contentEquals(SCHEMA_PATCH_OP_V1)) {
            throw new RuntimeException("PatchOp must contain correct schema attribute.");
        }

        if (active == null && password == null) {
            throw new RuntimeException("PatchOp must contain operations.");
        }

        return true;
    }
}
