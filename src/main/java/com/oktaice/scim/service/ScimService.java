package com.oktaice.scim.service;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.scim20.ScimGroup;
import com.oktaice.scim.model.scim20.ScimGroupPatchOp;
import com.oktaice.scim.model.ScimListResponse;
import com.oktaice.scim.model.scim20.ScimOktaIceUser;
import com.oktaice.scim.model.scim20.ScimUser;
import com.oktaice.scim.model.ScimUserPatchOp;
import com.oktaice.scim.model.User;

import java.util.List;

public interface ScimService {

    String USERS_LOCATION_BASE = "/scim";
    String USERS_LOCATION_V2 = "/scim/v2/Users";
    String USERS_LOCATION_V1 = "/scim/v1/Users";

    String GROUPS_LOCATION_BASE = "/scim";
    String GROUPS_LOCATION_BASE_1 = "/scim/v1/Groups";
    String GROUPS_LOCATION_BASE_2 = "/scim/v2/Groups";

    /**
     * These two methods make sure ScimUserPatchOp and ScimGroupPatchOp are properly formatted.
     */
//    void validateUserPatchOp(ScimUserPatchOp scimUserPatchOp, boolean isV2);
    void validateGroupPatchOp(ScimGroupPatchOp scimGroupPatchOp);

    /**
     * These three methods focus on the User objects transformations.
     * For example, scimUserToUser (ScimUser scimUser) method takes in a ScimUser and return a traditional API user.
     * The User object is suitable for storing in the repository.
     * The userToScimOktaIceUser (User user) method serves the other way around.
     * It takes in an API user and returns a ScimOktaIceUser.
     */
    User scim2UserToUser(ScimUser scimUser);
    User scim1UserToUser(com.oktaice.scim.model.scim11.ScimUser scimUser);
    ScimOktaIceUser userToScim2OktaIceUser(User user);
    com.oktaice.scim.model.scim11.ScimOktaIceUser userToScim1OktaIceUser(User user);
    ScimListResponse usersToListResponse(List<User> users, Integer startIndex, Integer pageCount, boolean isV2);

    /**
     * These four methods focus on the Group objects transformations.
     * For example, scimGroupToGroup (ScimGroup scimGroup) method takes in a ScimGroup
     * and return a traditional API group.
     * The groupToScimGroup (Group group) method serves the other way around.
     * It takes in an API group and returns a ScimGroup.
     */
    Group scim2GroupToGroup(ScimGroup scimGroup);
    Group scim1GroupToGroup(com.oktaice.scim.model.scim11.ScimGroup scimGroup);
    ScimGroup groupToScim2Group(Group group);
    com.oktaice.scim.model.scim11.ScimGroup groupToScim1Group(Group group);
    //The updateGroupByPatchOp method update the Repository Group with the patch information
    void updateGroupByPatchOp(Group group, ScimGroupPatchOp scimGroupPatchOp);
    ScimListResponse groupsToListResponse(List<Group> groups, Integer startIndex, Integer pageCount, boolean isV2);
}
