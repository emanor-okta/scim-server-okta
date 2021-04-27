package com.oktaice.scim.controller.api.scim;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.scim11.ScimGroup;
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
@RequestMapping("/scim/v1/Groups")
public class ScimV1GroupController extends ScimBaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(ScimV1GroupController.class);

    GroupRepository groupRepository;
    UserRepository userRepository;
    ScimService scimService;

    public ScimV1GroupController(
            GroupRepository groupRepository, UserRepository userRepository, ScimService scimService
    ) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.scimService = scimService;
        logger.info("Using ScimV1GroupController...");
    }


    /**
     * the getGroup method
     */
    @GetMapping("/{uuid}")
    public ScimGroup getGroup(@PathVariable String uuid) {
        //Returns the Repository Group and convert it to a SCIM Group.
        return scimService.groupToScim1Group(findOneByUUID(uuid, groupRepository));
    }


    /**
     * the createGroup method
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ScimGroup createGroup(@RequestBody ScimGroup scimGroup) {
        //Get new group's information
        Group newGroup = scimService.scim1GroupToGroup(scimGroup);
        ///Save new group to DB
        groupRepository.save(newGroup);
        //Returns the group information and convert it to a SCIM Group
        return scimService.groupToScim1Group(newGroup);
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
        Group groupWithUpdates = scimService.scim1GroupToGroup(scimGroup);
        //Copy attribute values from groupWithUpdates to the existing Repository Group
        copyGroup(groupWithUpdates, group);
        //Save the updated value to DB
        groupRepository.save(group);
        //Return the updated group information and convert it to a SCIM Group
        return scimService.groupToScim1Group(group);
    }



}
