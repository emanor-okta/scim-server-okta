package com.oktaice.scim.model.scim11;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import com.oktaice.scim.model.scim11.ScimUser;

import static com.oktaice.scim.model.scim11.ScimEnterpriseUser.SCHEMA_USER_ENTERPRISE;


@JsonPropertyOrder({ "schemas", "id", "externalId", "active", "userName", "name", "emails", "groups", SCHEMA_USER_ENTERPRISE, "meta" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScimEnterpriseUser extends ScimUser {

    /**
     * The ScimEnterpriseUser class extends ScimUser class.
     * It contains the SCHEMA_USER_ENTERPRISE string to store the SCIM Enterprise User Schema.
     */
    public static final String SCHEMA_USER_ENTERPRISE = "urn:scim:schemas:extension:enterprise:1.0";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(SCHEMA_USER_ENTERPRISE)
    private EnterpriseAttributes enterpriseAttributes;

    public ScimEnterpriseUser() {
        super();
        getSchemas().add(SCHEMA_USER_ENTERPRISE);
    }

    public EnterpriseAttributes getEnterpriseAttributes() {
        return enterpriseAttributes;
    }

    public void setEnterpriseAttributes(EnterpriseAttributes enterpriseAttributes) {
        this.enterpriseAttributes = enterpriseAttributes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EnterpriseAttributes {

        private String employeeNumber;
        private String costCenter;

        public String getEmployeeNumber() {
            return employeeNumber;
        }

        public void setEmployeeNumber(String employeeNumber) {
            this.employeeNumber = employeeNumber;
        }

        public String getCostCenter() {
            return costCenter;
        }

        public void setCostCenter(String costCenter) {
            this.costCenter = costCenter;
        }
    }
}
