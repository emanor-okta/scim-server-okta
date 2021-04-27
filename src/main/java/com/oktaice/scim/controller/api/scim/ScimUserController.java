package com.oktaice.scim.controller.api.scim;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.ScimListResponse;
import com.oktaice.scim.model.support.ScimPageFilter;
import com.oktaice.scim.model.User;
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

@RestController
@ConditionalOnProperty(name = "scim.service", havingValue = "wip")
@RequestMapping({"/scim/v1/Users", "/scim/v2/Users"})
public class ScimUserController extends ScimBaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(ScimUserController.class);

    UserRepository userRepository;
    ScimService scimService;

    public ScimUserController(UserRepository userRepository, ScimService scimService) {
        this.userRepository = userRepository;
        this.scimService = scimService;
        logger.info("Using ScimUserController...");
    }


    @GetMapping()
    public @ResponseBody ScimListResponse getUsers(@ModelAttribute ScimPageFilter scimPageFilter,
                                                   HttpServletRequest request) {
        boolean isV2 = request.getRequestURI().contains("v2");
        //GET STARTINDEX AND COUNT FOR PAGINATION
        logger.info("Filter: " + scimPageFilter.getFilter() + ", startIndex: " + scimPageFilter.getStartIndex() +
                ", count: " + scimPageFilter.getCount() + ", version: " + (isV2 ? "v2" : "v1"));
        PageRequest pageRequest =
            new PageRequest(scimPageFilter.getStartIndex() - 1, scimPageFilter.getCount());

        Page<User> users = null;

        //PARSE SEARCH FILTER
        Matcher match = scimPageFilter.parseFilter();
        if (match.find()) {
            String searchKeyName = match.group(1);
            String searchValue = match.group(2);
            //IF THERE'S A VALID FILTER, USE THE PROPER METHOD FOR USER SEARCH
            switch (searchKeyName) {
                case ScimPageFilter.USER_USERNAME:
                    users = userRepository.findByUsername(searchValue, pageRequest);
                    break;
                case ScimPageFilter.USER_ACTIVE:
                    users = userRepository.findByActive(Boolean.valueOf(searchValue), pageRequest);
                    break;
                case ScimPageFilter.USER_FIRST_NAME:
                    users = userRepository.findByFirstName(searchValue, pageRequest);
                    break;
                case ScimPageFilter.USER_LAST_NAME:
                    users = userRepository.findByLastName(searchValue, pageRequest);
                    break;
                default:
                    throw new HttpClientErrorException(HttpStatus.NOT_IMPLEMENTED, "Filter not implemented");
            }
        } else {
            //IF THERE'S NO FILTER, FIND ALL ENTRIES
            users = userRepository.findAll(pageRequest);
        }

        /**
         * TODO: Complete the getUsers method
         */
        //Get a list of Repository Users from search and convert to a SCIM List Response
        List<User> foundUsers = users.getContent();
        return scimService.usersToListResponse(
                foundUsers, scimPageFilter.getStartIndex(), scimPageFilter.getCount(), isV2
        );
    }


    /**
     * TODO: Review the deleteUser method
     */
    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String uuid) {
        User user = findUserByUUID(uuid, userRepository);

        //remove user from groups
        for (Group g : user.getGroups()) {
            g.getUsers().remove(user);
        }

        //delete user
        userRepository.delete(user);
    }
}

