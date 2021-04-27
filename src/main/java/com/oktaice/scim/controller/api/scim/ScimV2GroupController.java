package com.oktaice.scim.controller.api.scim;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.scim20.ScimGroup;
import com.oktaice.scim.model.scim20.ScimGroupPatchOp;
import com.oktaice.scim.repository.GroupRepository;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.service.ScimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@ConditionalOnProperty(name = "scim.service", havingValue = "wip")
@RequestMapping("scim/v2/Groups")
public class ScimV2GroupController extends ScimBaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(ScimV2GroupController.class);

    GroupRepository groupRepository;
    UserRepository userRepository;
    ScimService scimService;

    public ScimV2GroupController(
            GroupRepository groupRepository, UserRepository userRepository, ScimService scimService
    ) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.scimService = scimService;
        logger.info("Using ScimV2GroupController...");
    }

    /**
     * the getGroup method
     */
    @GetMapping("/{uuid}")
    public ScimGroup getGroup(@PathVariable String uuid) {
        //Returns the Repository Group and convert it to a SCIM Group.
        return scimService.groupToScim2Group(findOneByUUID(uuid, groupRepository));
    }


    /**
     * the createGroup method
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ScimGroup createGroup(@RequestBody ScimGroup scimGroup) {
        //Get new group's information
        Group newGroup = scimService.scim2GroupToGroup(scimGroup);
        ///Save new group to DB
        groupRepository.save(newGroup);
        //Returns the group information and convert it to a SCIM Group
        return scimService.groupToScim2Group(newGroup);
    }

    /**
     * TODO: Implement the replaceGroup method
     */
    @PutMapping("/{uuid}")
    public @ResponseBody
    ScimGroup replaceGroup(@RequestBody ScimGroup scimGroup, @PathVariable String uuid) {
        //Finds the Repository Group by uuid
        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        //Convert the SCIM Group to a Repository Group format if an existing Repository Group can be found.
        Group groupWithUpdates = scimService.scim2GroupToGroup(scimGroup);
        //Copy attribute values from groupWithUpdates to the existing Repository Group
        copyGroup(groupWithUpdates, group);
        //Save the updated value to DB
        groupRepository.save(group);
        //Return the updated group information and convert it to a SCIM Group
        return scimService.groupToScim2Group(group);
    }

    /**
     * the updateGroup method
     */
    @PatchMapping("/{uuid}")
    public @ResponseBody
    ScimGroup updateGroup(
            @RequestBody ScimGroupPatchOp scimGroupPatchOp, @PathVariable String uuid
    ) {
        //Confirm that the ScimGroupPatchOp is valid.
        scimService.validateGroupPatchOp(scimGroupPatchOp);
        //Finds the Repository Group by uuid
        Group group = groupRepository.findOneByUuid(uuid);
        //If cannot find the group, returns "Resource not found" error message.
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        //Update group with PatchOp
        scimService.updateGroupByPatchOp(group, scimGroupPatchOp);
        //Save the updated value to DB
        groupRepository.save(group);
        // Return the updated group information and convert it to a SCIM Group
          return scimService.groupToScim2Group(group);
    }
}
