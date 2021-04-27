package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ScimUserPatchOpV2 extends ScimUserPatchOp {
    private static final Logger logger = LoggerFactory.getLogger(ScimUserPatchOpV2.class);

    /**
     * The body of an HTTP PATCH request MUST contain the attribute "Operations",
     * whose value is an array of one or more PATCH operations.
     */
    @JsonProperty("Operations")
    List<Operation> operations = new ArrayList<>();

    public ScimUserPatchOpV2() {
        super();
        getSchemas().add(SCHEMA_PATCH_OP);
    }

    public List<Operation> getOperations() {
        return operations;
    }
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }


    public boolean updateUser(User user) {
        boolean updated = false;

        for (ScimUserPatchOp.Operation op : this.getOperations()) {

            if (op.getType() == Operation.PatchType.PASSWORD) {
                logger.warn("Password not Implemented in User Scheme: " + op.getValue().getPassword());
            } else if (op.getType() == Operation.PatchType.ACTIVE) {
                if (user.getActive() != op.getValue().getActive()) {
                    user.setActive(!user.getActive());
                    updated = true;
                }
            } else {
                logger.error("Should never get here Type: " + op.getOp());
                throw new RuntimeException("Invalid Patch Operation Type.");
            }
        }

        return updated;
    }

    public boolean validatePatchOp() {
        if (!getSchemas().get(0).contentEquals(SCHEMA_PATCH_OP)) {
            throw new RuntimeException("PatchOp must contain correct schema attribute.");
        }

        if (getOperations().size() < 1) {
            throw new RuntimeException("PatchOp must contain operations.");
        }

        // only replace is supported
        if (!ScimPatchOp.OPERATION_REPLACE.contentEquals(getOperations().get(0).getOp())) {
            throw new RuntimeException("Only 'replace' operation supported for PatchOp.");
        }

        return true;
    }

}
