package com.oktaice.scim.service;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.scim20.ScimEnterpriseUser;
import com.oktaice.scim.model.scim20.ScimGroup;
import com.oktaice.scim.model.scim20.ScimGroupPatchOp;
import com.oktaice.scim.model.ScimListResponse;
import com.oktaice.scim.model.scim20.ScimOktaIceUser;
import com.oktaice.scim.model.ScimPatchOp;
import com.oktaice.scim.model.ScimResource;
import com.oktaice.scim.model.scim20.ScimUser;
import com.oktaice.scim.model.ScimUserPatchOp;
import com.oktaice.scim.model.User;
import com.oktaice.scim.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.oktaice.scim.model.scim20.ScimEnterpriseUser.SCHEMA_USER_ENTERPRISE;
import static com.oktaice.scim.model.scim20.ScimOktaIceUser.SCHEMA_USER_OKTA_ICE;
import static com.oktaice.scim.model.ScimPatchOp.SCHEMA_PATCH_OP_V1;
import static com.oktaice.scim.model.ScimUserPatchOp.SCHEMA_PATCH_OP;

@Service
public class ScimServiceImpl implements ScimService {

    private UserRepository userRepository;

    public ScimServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    public void validateUserPatchOp(ScimUserPatchOp scimUserPatchOp, boolean isV2) {
//        validatePatchSchemaAndOperations(scimUserPatchOp.getSchemas().get(0), scimUserPatchOp.getOperations().size(), isV2);
//
//        // only replace is supported
//        if (!ScimPatchOp.OPERATION_REPLACE.equals(scimUserPatchOp.getOperations().get(0).getOp())) {
//            throw new RuntimeException("Only 'replace' operation supported for PatchOp.");
//        }
//    }

    @Override
    public void validateGroupPatchOp(ScimGroupPatchOp scimGroupPatchOp) {
        validatePatchSchemaAndOperations(scimGroupPatchOp.getSchemas().get(0), scimGroupPatchOp.getOperations().size(), true);

        // only replace and add are supported
        if (
            !ScimPatchOp.OPERATION_REPLACE.equals(scimGroupPatchOp.getOperations().get(0).getOp()) &&
            !ScimPatchOp.OPERATION_ADD.equals(scimGroupPatchOp.getOperations().get(0).getOp())
        ) {
            throw new RuntimeException("Only 'replace' and 'add' operations supported for Group PatchOp.");
        }
    }

    private void validatePatchSchemaAndOperations(String schema, int numOperations, boolean isV2) {
        if (!(SCHEMA_PATCH_OP.equals(schema) && isV2) && !(SCHEMA_PATCH_OP_V1.equals(schema) && !isV2)) {
            throw new RuntimeException("PatchOp must contain correct schema attribute.");
        }


        if (numOperations == 0) {
            throw new RuntimeException("PatchOp must contain operations.");
        }
    }

    @Override
    public User scim2UserToUser(ScimUser scimUser) {
        User user = new User();

        // flat attributes
        user.setActive(scimUser.isActive());
        user.setUserName(scimUser.getUserName());

        // name attributes
        if (scimUser.getName() != null) {
            user.setFirstName(scimUser.getName().getGivenName());
            user.setMiddleName(scimUser.getName().getMiddleName());
            user.setLastName(scimUser.getName().getFamilyName());
        }

        // email attributes
        for (ScimUser.Email email : scimUser.getEmails()) {
            if ("work".equals(email.getType())) {
                user.setEmail(email.getValue());
            }
        }

        // enterprise attributes
        if (
            scimUser instanceof ScimEnterpriseUser && ((ScimEnterpriseUser) scimUser).getEnterpriseAttributes() != null
        ) {
            ScimEnterpriseUser scimEnterpriseUser = (ScimEnterpriseUser) scimUser;
            user.setCostCenter(scimEnterpriseUser.getEnterpriseAttributes().getCostCenter());
            user.setEmployeeNumber(scimEnterpriseUser.getEnterpriseAttributes().getEmployeeNumber());
        }

        // okta ice attributes
        if (
            scimUser instanceof ScimOktaIceUser && ((ScimOktaIceUser) scimUser).getOktaIceAttributes() != null
        ) {
            ScimOktaIceUser scimOktaIceUser = (ScimOktaIceUser) scimUser;
            user.setFavoriteIceCream(scimOktaIceUser.getOktaIceAttributes().getIceCream());
        }

        return user;
    }

    @Override
    public User scim1UserToUser(com.oktaice.scim.model.scim11.ScimUser scimUser) {
        User user = new User();

        // flat attributes
        user.setActive(scimUser.isActive());
        user.setUserName(scimUser.getUserName());

        // name attributes
        if (scimUser.getName() != null) {
            user.setFirstName(scimUser.getName().getGivenName());
            user.setMiddleName(scimUser.getName().getMiddleName());
            user.setLastName(scimUser.getName().getFamilyName());
        }

        // email attributes
        for (com.oktaice.scim.model.scim11.ScimUser.Email email : scimUser.getEmails()) {
            if ("work".equals(email.getType())) {
                user.setEmail(email.getValue());
            }
        }

        // enterprise attributes
        if (
                scimUser instanceof com.oktaice.scim.model.scim11.ScimEnterpriseUser &&
                        ((com.oktaice.scim.model.scim11.ScimEnterpriseUser) scimUser).getEnterpriseAttributes() != null
        ) {
            com.oktaice.scim.model.scim11.ScimEnterpriseUser scimEnterpriseUser =
                    (com.oktaice.scim.model.scim11.ScimEnterpriseUser) scimUser;
            user.setCostCenter(scimEnterpriseUser.getEnterpriseAttributes().getCostCenter());
            user.setEmployeeNumber(scimEnterpriseUser.getEnterpriseAttributes().getEmployeeNumber());
        }

        // okta ice attributes
        if (
                scimUser instanceof com.oktaice.scim.model.scim11.ScimOktaIceUser &&
                        ((com.oktaice.scim.model.scim11.ScimOktaIceUser) scimUser).getOktaIceAttributes() != null
        ) {
            com.oktaice.scim.model.scim11.ScimOktaIceUser scimOktaIceUser = (com.oktaice.scim.model.scim11.ScimOktaIceUser) scimUser;
            user.setFavoriteIceCream(scimOktaIceUser.getOktaIceAttributes().getIceCream());
        }

        return user;
    }

    @Override
    public ScimOktaIceUser userToScim2OktaIceUser(User user) {
        Assert.notNull(user, "User must not be null");

        // automatically sets schemas
        ScimOktaIceUser scimOktaIceUser = new ScimOktaIceUser();

        // flat attributes
        scimOktaIceUser.setId(user.getUuid());
        scimOktaIceUser.setUserName(user.getUserName());
        scimOktaIceUser.setActive(user.getActive());

        // name attribute
        ScimUser.Name name = new ScimUser.Name();
        name.setGivenName(user.getFirstName());
        name.setMiddleName(user.getMiddleName());
        name.setFamilyName(user.getLastName());
        scimOktaIceUser.setName(name);

        // email(s) attribute
        ScimUser.Email email = new ScimUser.Email();
        email.setPrimary(true);
        email.setType("work");
        email.setValue(user.getEmail());
        List<ScimUser.Email> emails = new ArrayList<>();
        emails.add(email);
        scimOktaIceUser.setEmails(emails);

        // group(s) attribute
        if (user.getGroups() != null) {
            for (Group group : user.getGroups()) {
                ScimUser.Group scimUserGroup = new ScimUser.Group();
                scimUserGroup.setDisplay(group.getDisplayName());
                scimUserGroup.setValue(group.getUuid());
                scimOktaIceUser.getGroups().add(scimUserGroup);
            }
        }

        // enterprise attributes
        if (user.getCostCenter() != null || user.getEmployeeNumber() != null) {
            ScimEnterpriseUser.EnterpriseAttributes enterpriseAttributes = new ScimEnterpriseUser.EnterpriseAttributes();
            enterpriseAttributes.setEmployeeNumber(user.getEmployeeNumber());
            enterpriseAttributes.setCostCenter(user.getCostCenter());
            scimOktaIceUser.setEnterpriseAttributes(enterpriseAttributes);
        } else {
            scimOktaIceUser.getSchemas().remove(SCHEMA_USER_ENTERPRISE);
        }

        // okta ice attributes
        if (user.getFavoriteIceCream() != null) {
            ScimOktaIceUser.OktaIceAttributes oktaIceAttributes = new ScimOktaIceUser.OktaIceAttributes();
            oktaIceAttributes.setIceCream(user.getFavoriteIceCream());
            scimOktaIceUser.setOktaIceAttributes(oktaIceAttributes);
        } else {
            scimOktaIceUser.getSchemas().remove(SCHEMA_USER_OKTA_ICE);
        }

        // meta attributes
        ScimResource.Meta meta = new ScimResource.Meta();
        meta.setResourceType(ScimResource.Meta.RESOURCE_TYPE_USER);
        meta.setLocation(USERS_LOCATION_BASE + "/" + user.getUuid());
        scimOktaIceUser.setMeta(meta);

        return scimOktaIceUser;
    }

    @Override
    public com.oktaice.scim.model.scim11.ScimOktaIceUser userToScim1OktaIceUser(User user) {
        Assert.notNull(user, "User must not be null");

        // automatically sets schemas
        com.oktaice.scim.model.scim11.ScimOktaIceUser scimOktaIceUser =
                new com.oktaice.scim.model.scim11.ScimOktaIceUser();

        // flat attributes
        scimOktaIceUser.setId(user.getUuid());
        scimOktaIceUser.setUserName(user.getUserName());
        scimOktaIceUser.setActive(user.getActive());

        // name attribute
        com.oktaice.scim.model.scim11.ScimUser.Name name =
                new com.oktaice.scim.model.scim11.ScimUser.Name();
        name.setGivenName(user.getFirstName());
        name.setMiddleName(user.getMiddleName());
        name.setFamilyName(user.getLastName());
        scimOktaIceUser.setName(name);

        // email(s) attribute
        com.oktaice.scim.model.scim11.ScimUser.Email email =
                new com.oktaice.scim.model.scim11.ScimUser.Email();
        email.setPrimary(true);
        email.setType("work");
        email.setValue(user.getEmail());
        List<com.oktaice.scim.model.scim11.ScimUser.Email> emails = new ArrayList<>();
        emails.add(email);
        scimOktaIceUser.setEmails(emails);

        // group(s) attribute
        if (user.getGroups() != null) {
            for (Group group : user.getGroups()) {
                com.oktaice.scim.model.scim11.ScimUser.Group scimUserGroup =
                        new com.oktaice.scim.model.scim11.ScimUser.Group();
                scimUserGroup.setDisplay(group.getDisplayName());
                scimUserGroup.setValue(group.getUuid());
                scimOktaIceUser.getGroups().add(scimUserGroup);
            }
        }

        // enterprise attributes
        if (user.getCostCenter() != null || user.getEmployeeNumber() != null) {
            com.oktaice.scim.model.scim11.ScimEnterpriseUser.EnterpriseAttributes enterpriseAttributes =
                    new com.oktaice.scim.model.scim11.ScimEnterpriseUser.EnterpriseAttributes();
            enterpriseAttributes.setEmployeeNumber(user.getEmployeeNumber());
            enterpriseAttributes.setCostCenter(user.getCostCenter());
            scimOktaIceUser.setEnterpriseAttributes(enterpriseAttributes);
        } else {
            scimOktaIceUser.getSchemas().remove(com.oktaice.scim.model.scim11.ScimEnterpriseUser.SCHEMA_USER_ENTERPRISE);
        }

        // okta ice attributes
        if (user.getFavoriteIceCream() != null) {
            com.oktaice.scim.model.scim11.ScimOktaIceUser.OktaIceAttributes oktaIceAttributes =
                    new com.oktaice.scim.model.scim11.ScimOktaIceUser.OktaIceAttributes();
            oktaIceAttributes.setIceCream(user.getFavoriteIceCream());
            scimOktaIceUser.setOktaIceAttributes(oktaIceAttributes);
        } else {
            scimOktaIceUser.getSchemas().remove(com.oktaice.scim.model.scim11.ScimOktaIceUser.SCHEMA_USER_OKTA_ICE);
        }

        // meta attributes
        ScimResource.Meta meta = new ScimResource.Meta();
//        meta.setResourceType(ScimResource.Meta.RESOURCE_TYPE_USER);
        meta.setLocation(USERS_LOCATION_BASE + "/" + user.getUuid());
        scimOktaIceUser.setMeta(meta);

        return scimOktaIceUser;
    }

    @Override
    public ScimListResponse usersToListResponse(List<User> users, Integer startIndex, Integer pageCount, boolean isV2) {
        ScimListResponse scimListResponse = new ScimListResponse(isV2);

        scimListResponse.setStartIndex(startIndex);
        scimListResponse.setItemsPerPage(pageCount);
        scimListResponse.setTotalResults(users.size());

        for (User user : users) {
            if (isV2)
                scimListResponse.getResources().add(userToScim2OktaIceUser(user)); //, isV2));
            else
                scimListResponse.getResources().add(userToScim1OktaIceUser(user));
        }

        return scimListResponse;
    }

    @Override
    public Group scim2GroupToGroup(ScimGroup scimGroup) {
        Group group = new Group();

        group.setDisplayName(scimGroup.getDisplayName());

        for (ScimGroup.Member member : scimGroup.getMembers()) {
            User user = userRepository.findOneByUuid(member.getValue());
            if (user != null) {
                group.getUsers().add(user);
            }
        }

        return group;
    }

    @Override
    public Group scim1GroupToGroup(com.oktaice.scim.model.scim11.ScimGroup scimGroup) {
        Group group = new Group();

        group.setDisplayName(scimGroup.getDisplayName());

        for (com.oktaice.scim.model.scim11.ScimGroup.Member member : scimGroup.getMembers()) {
            User user = userRepository.findOneByUuid(member.getValue());
            if (user != null) {
                group.getUsers().add(user);
            }
        }

        return group;
    }

    @Override
    public ScimGroup groupToScim2Group(Group group) {
        Assert.notNull(group, "Group must not be null");

        // automatically sets schemas
        ScimGroup scimGroup = new ScimGroup();

        // flat attributes
        scimGroup.setId(group.getUuid());
        scimGroup.setDisplayName(group.getDisplayName());

        // member attributes
        for (User user : group.getUsers()) {
            ScimGroup.Member member = new ScimGroup.Member();
            member.setValue(user.getUuid());
            member.setDisplay(user.getUserName());
            scimGroup.getMembers().add(member);
        }

        // meta attributes
        ScimResource.Meta meta = new ScimResource.Meta();
        meta.setResourceType(ScimResource.Meta.RESOURCE_TYPE_GROUP);
        meta.setLocation(GROUPS_LOCATION_BASE + "/" + group.getUuid());
        scimGroup.setMeta(meta);

        return scimGroup;
    }

    @Override
    public com.oktaice.scim.model.scim11.ScimGroup groupToScim1Group(Group group) {
        Assert.notNull(group, "Group must not be null");

        // automatically sets schemas
        com.oktaice.scim.model.scim11.ScimGroup scimGroup = new com.oktaice.scim.model.scim11.ScimGroup();

        // flat attributes
        scimGroup.setId(group.getUuid());
        scimGroup.setDisplayName(group.getDisplayName());

        // member attributes
        for (User user : group.getUsers()) {
            com.oktaice.scim.model.scim11.ScimGroup.Member member =
                    new com.oktaice.scim.model.scim11.ScimGroup.Member();
            member.setValue(user.getUuid());
            member.setDisplay(user.getUserName());
            scimGroup.getMembers().add(member);
        }

        // meta attributes - only set Location, Ignore all other SCIM 1.1 meta attributes for now. Not sure if Okta uses them
        ScimResource.Meta meta = new ScimResource.Meta();
//        meta.setResourceType(ScimResource.Meta.RESOURCE_TYPE_GROUP);
        meta.setLocation(GROUPS_LOCATION_BASE + "/" + group.getUuid());
        scimGroup.setMeta(meta);

        return scimGroup;
    }

    @Override
    public void updateGroupByPatchOp(Group group, ScimGroupPatchOp scimGroupPatchOp) {
        Assert.notNull(group, "Group cannot be null");
        Assert.notNull(scimGroupPatchOp, "ScimGroupPatchOp cannot be null");

        ScimGroupPatchOp.Operation operation = scimGroupPatchOp.getOperations().get(0);
        String opType = operation.getOp();
        switch (opType) {
            case ScimPatchOp.OPERATION_ADD:
                doUpdateGroup(group, operation);
                break;
            case ScimPatchOp.OPERATION_REPLACE:
                if ("members".equals(operation.getPath())) {
                    doUpdateGroup(group, operation);
                } else {
                    group.setDisplayName(operation.getGroupValue().getDisplayName());
                }
                break;
            default:
                throw new RuntimeException("Patch operation not supported: " + operation);
        }
    }

    private void doUpdateGroup(Group group, ScimGroupPatchOp.Operation operation) {
        List<String> groupMemberUuids = group.getUsers().stream().map(User::getUuid).collect(Collectors.toList());
        if (ScimPatchOp.OPERATION_REPLACE.equals(operation.getOp())) {
            group.setUsers(new ArrayList<>());
            groupMemberUuids = new ArrayList<>();
        }
        for (ScimGroupPatchOp.Operation.MemberValue memberValue : operation.getMemberValues()) {
            User user = userRepository.findOneByUuid(memberValue.getValue());
            if (user != null && !groupMemberUuids.contains(user.getUuid())) {
                group.getUsers().add(user);
            }
        }
    }

    @Override
    public ScimListResponse groupsToListResponse(List<Group> groups, Integer startIndex, Integer pageCount, boolean isV2) {
        ScimListResponse scimListResponse = new ScimListResponse(isV2);

        scimListResponse.setStartIndex(startIndex);
        scimListResponse.setItemsPerPage(pageCount);
        scimListResponse.setTotalResults(groups.size());

        for (Group group : groups) {
            if (isV2)
                scimListResponse.getResources().add(groupToScim2Group(group));
            else
                scimListResponse.getResources().add(groupToScim1Group(group));
        }

        return scimListResponse;
    }
}
