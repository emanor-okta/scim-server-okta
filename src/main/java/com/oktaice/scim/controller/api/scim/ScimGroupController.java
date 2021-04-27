package com.oktaice.scim.controller.api.scim;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.ScimListResponse;
import com.oktaice.scim.model.support.ScimPageFilter;
import com.oktaice.scim.repository.GroupRepository;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.service.ScimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;

/**
 * SCIM API for group management
 */
@RestController
@ConditionalOnProperty(name = "scim.service", havingValue = "wip")
@RequestMapping({"/scim/v1/Groups", "/scim/v2/Groups"})
public class ScimGroupController extends ScimBaseHelper {

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


    @GetMapping
    public @ResponseBody
    ScimListResponse getGroups(@ModelAttribute ScimPageFilter scimPageFilter, HttpServletRequest request) {
        //GET STARTINDEX AND COUNT FOR PAGINATION
        PageRequest pageRequest = new PageRequest(scimPageFilter.getStartIndex() - 1, scimPageFilter.getCount());

        Page<Group> groups = null;
        boolean isV2 = request.getRequestURI().contains("v2");

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
                groupsFound, scimPageFilter.getStartIndex(), scimPageFilter.getCount(), isV2
        );
    }


    /**
     * the deleteGroup method
     */
    @DeleteMapping
    @RequestMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable String uuid) {
        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        groupRepository.delete(group);
    }
}
