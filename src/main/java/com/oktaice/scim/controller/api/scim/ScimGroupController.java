package com.oktaice.scim.controller.api.scim;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.scim.ScimGroup;
import com.oktaice.scim.model.scim.ScimGroupPatchOp;
import com.oktaice.scim.model.scim.ScimListResponse;
import com.oktaice.scim.model.scim.support.ScimPageFilter;
import com.oktaice.scim.repository.GroupRepository;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.service.ScimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.regex.Matcher;

import static com.oktaice.scim.service.ScimService.GROUPS_LOCATION_BASE;

/**
 * SCIM API for group management
 */
@RestController
@ConditionalOnProperty(name = "scim.service", havingValue = "wip")
@RequestMapping(GROUPS_LOCATION_BASE)
public class ScimGroupController extends ScimBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ScimGroupController.class);

    GroupRepository groupRepository;
    UserRepository userRepository;
    ScimService scimService;

    public ScimGroupController(
            GroupRepository groupRepository, UserRepository userRepository, ScimService scimService
    ) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.scimService = scimService;
        logger.info("Using ScimGroupController...");
    }

    /**
     * TODO: Implement the getGroup method
     */
    @GetMapping("/{uuid}")
    public ScimGroup getGroup(@PathVariable String uuid) {
        //This is the line to delete
//        return new ScimGroup();
        //Searches a Repository Group by its uuid
        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        //Returns the Repository Group and convert it to a SCIM Group.
        return scimService.groupToScimGroup(group);
    }

    @GetMapping
    public @ResponseBody
    ScimListResponse getGroups(@ModelAttribute ScimPageFilter scimPageFilter) {
        //GET STARTINDEX AND COUNT FOR PAGINATION
        PageRequest pageRequest = new PageRequest(scimPageFilter.getStartIndex() - 1, scimPageFilter.getCount());

        Page<Group> groups = null;

        //PARSE SEARCH FILTER
        Matcher match = scimPageFilter.parseFilter();
        if (match.find()) {
            String searchKeyName = match.group(1);
            String searchValue = match.group(2);
            //IF THERE'S A VALID FILTER, USE THE PROPER METHOD FOR GROUP SEARCH
            switch (searchKeyName) {
                case ScimPageFilter.GROUP_NAME:
                    groups = groupRepository.findByName(searchValue, pageRequest);
                    break;
                case ScimPageFilter.GROUP_UUID:
                    groups = groupRepository.findByUuid(searchValue, pageRequest);
                    break;
                default:
                    throw new HttpClientErrorException(HttpStatus.NOT_IMPLEMENTED, "Filter not implemented");
            }
        } else {
            //IF THERE'S NO FILTER, FIND ALL ENTRIES
            groups = groupRepository.findAll(pageRequest);
        }

        /**
         * TODO: Complete the getGroups method
         */
        //Get a list of Repository Groups from search and convert to a SCIM List Response
        List<Group> groupsFound = groups.getContent();
        return scimService.groupsToListResponse(
                groupsFound, scimPageFilter.getStartIndex(), scimPageFilter.getCount()
        );
    }

    /**
     * TODO: Implement the createGroup method
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ScimGroup createGroup(@RequestBody ScimGroup scimGroup) {
        //Get new group's information
        Group newGroup = scimService.scimGroupToGroup(scimGroup);
        ///Save new group to DB
        groupRepository.save(newGroup);

//        try {
//            Thread.sleep(60 * 1000 * 6);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //Returns the group information and convert it to a SCIM Group
        return scimService.groupToScimGroup(newGroup);
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
        Group groupWithUpdates = scimService.scimGroupToGroup(scimGroup);
        //Copy attribute values from groupWithUpdates to the existing Repository Group
        copyGroup(groupWithUpdates, group);
        //Save the updated value to DB
        groupRepository.save(group);
        //Return the updated group information and convert it to a SCIM Group
        return scimService.groupToScimGroup(group);
    }

    /**
     * The copyGroup method takes in two Repository Groups.
     * It copy information from the first Repository Group (from) to the second Repository Group (to)
     */
    private void copyGroup(Group from, Group to) {
        Assert.notNull(from, "From Group cannot be null");
        Assert.notNull(to, "To Group cannot be null");

        to.setDisplayName(from.getDisplayName());
        to.setUsers(from.getUsers());
    }

    /**
     * TODO: Implement the updateGroup method
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
        //
        // Return the updated group information and convert it to a SCIM Group


//        try {
//            Thread.sleep(1000 * 90);
//        } catch (InterruptedException ie) {ie.printStackTrace();}

        return scimService.groupToScimGroup(group);

    }

    /**
     * TODO: Review the deleteGroup method
     */
    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable String uuid) {
        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        groupRepository.delete(group);
    }
}
