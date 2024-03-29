package com.oktaice.scim.model.scim11;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import com.oktaice.scim.model.scim11.ScimEnterpriseUser;

import static com.oktaice.scim.model.scim11.ScimEnterpriseUser.SCHEMA_USER_ENTERPRISE;
import static com.oktaice.scim.model.scim11.ScimOktaIceUser.SCHEMA_USER_OKTA_ICE;

@JsonPropertyOrder({ "schemas", "id", "externalId", "active", "userName", "name", "emails", "groups", SCHEMA_USER_ENTERPRISE, SCHEMA_USER_OKTA_ICE, "meta" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScimOktaIceUser extends com.oktaice.scim.model.scim11.ScimEnterpriseUser {

    /**
     *  The ScimOktaIceUser class extends ScimEnterpriseUser class.
     *  It contains the SCHEMA_USER_OKTA_ICE string to store the custom schema for ICE Research.
     */
    public static final String SCHEMA_USER_OKTA_ICE = "urn:scim:schemas:extension:ice:1.0";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(SCHEMA_USER_OKTA_ICE)
    private OktaIceAttributes oktaIceAttributes;

    public ScimOktaIceUser() {
        super();
        getSchemas().add(SCHEMA_USER_OKTA_ICE);
    }

    public OktaIceAttributes getOktaIceAttributes() {
        return oktaIceAttributes;
    }

    public void setOktaIceAttributes(OktaIceAttributes oktaIceAttributes) {
        this.oktaIceAttributes = oktaIceAttributes;
    }

    public static class OktaIceAttributes {

        private String iceCream;

        public String getIceCream() {
            return iceCream;
        }

        public void setIceCream(String iceCream) {
            this.iceCream = iceCream;
        }
    }


    public static void main(String[] args) {

        ScimUser scimUser = new ScimUser();
        System.out.println("scimUser schemas: " + scimUser.getSchemas());

        com.oktaice.scim.model.scim11.ScimEnterpriseUser scimEnterpriseUser = new ScimEnterpriseUser();
        System.out.println("scimEnterpriseUser schemas: " + scimEnterpriseUser.getSchemas());

        ScimOktaIceUser scimOktaIceUser = new ScimOktaIceUser();
        System.out.println("scimOktaIceUser schemas: " + scimOktaIceUser.getSchemas());
    }
}
