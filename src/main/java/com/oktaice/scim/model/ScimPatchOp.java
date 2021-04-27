package com.oktaice.scim.model;

import com.oktaice.scim.model.ScimResource;

public class ScimPatchOp extends ScimResource {

    //The ScimPatchOp class has its own SCIM schema. The identifier is PatchOp.
    public static final String SCHEMA_PATCH_OP = "urn:ietf:params:scim:api:messages:2.0:PatchOp";
    public static final String SCHEMA_PATCH_OP_V1 = "urn:scim:schemas:core:1.0";
    public static final String OPERATION_REPLACE = "replace";
    public static final String OPERATION_ADD = "add";


}
