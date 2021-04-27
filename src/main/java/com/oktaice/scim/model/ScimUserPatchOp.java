package com.oktaice.scim.model;


public abstract class ScimUserPatchOp extends ScimPatchOp {

    /**
     * The Operation object contains the string attribute op,
     * and the value attribute that currently only used to set the active status for a user.
     */
    public static class Operation {
        enum PatchType {ACTIVE, PASSWORD}
        private String op;
        private Value value;

        public String getOp() {
            return op;
        }
        public void setOp(String op) {
            this.op = op;
        }
        public Value getValue() {
            return value;
        }
        public void setValue(Value value) {
            this.value = value;
        }

        public PatchType getType() {
            if (value.active != null)
                return PatchType.ACTIVE;
            else if (value.password != null)
                return PatchType.PASSWORD;
            else
                throw new RuntimeException("Invalid Patch Operation Type: " + op);
        }

        public static class Value {
            private Boolean active;
            public boolean getActive() {
                return active;
            }
            public void set(boolean active) {
                this.active = active;
            }

            private String password;
            public String getPassword() {
                return password;
            }
            public void setPassword(String password) {
                this.password = password;
            }
        }
    }

    abstract public boolean updateUser(User user);
    abstract public boolean validatePatchOp();
}
