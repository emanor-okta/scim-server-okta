package com.oktaice.scim.controller.api.scim;


import com.oktaice.scim.model.ScimUserPatchOpV1;
import com.oktaice.scim.model.User;
import com.oktaice.scim.model.scim11.ScimOktaIceUser;
import com.oktaice.scim.model.scim11.ScimUser;
import com.oktaice.scim.model.ScimUserPatchOp;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.service.ScimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RestController
@ConditionalOnProperty(name = "scim.service", havingValue = "wip")
@RequestMapping("/scim/v1/Users")
public class ScimV1UserController extends ScimBaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(ScimV1UserController.class);

    UserRepository userRepository;
    ScimService scimService;

    public ScimV1UserController(UserRepository userRepository, ScimService scimService) {
        this.userRepository = userRepository;
        this.scimService = scimService;
        logger.info("Using ScimV1UserController...");
    }

    /**
     * the getUser method V1
     */
    @GetMapping("/{uuid}")
    public @ResponseBody
    ScimOktaIceUser getUser(@PathVariable String uuid, HttpServletResponse response) {
        return scimService.userToScim1OktaIceUser(findUserByUUID(uuid, userRepository));
    }

    /**
     * the createUser method
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ScimUser createUser(@RequestBody ScimUser scimUser) {
        //Get new user's information
        User newUser = scimService.scim1UserToUser(scimUser);
        //Save new user to DB
        userRepository.save(newUser);
        //Returns the user information and convert it to a SCIM User
        return scimService.userToScim1OktaIceUser(newUser);
    }

    /**
     * the replaceUser method
     */
    @PutMapping("/{uuid}")
    public @ResponseBody
    ScimOktaIceUser replaceUser(@RequestBody ScimUser scimUser, @PathVariable String uuid) {
        //Finds the Repository User by uuid
        User user = findUserByUUID(uuid, userRepository);
        //Convert the SCIM User to a Repository User format if an existing Repository User can be found.
        User userWithUpdates = scimService.scim1UserToUser(scimUser);
        //Copy attribute values from userWithUpdates to the existing Repository User
        copyUser(userWithUpdates, user);
        //Save the updated value to DB
        userRepository.save(user);
        //Return the updated user information and convert it to a SCIM User
        return scimService.userToScim1OktaIceUser(user);
    }


    /*
     * PATCH Request, either update status, or sync password are allowed operations.
     */
    @PatchMapping("/{uuid}")
    public @ResponseBody
    ScimOktaIceUser updateUser(
            @RequestBody ScimUserPatchOpV1 scimUserPatchOp, @PathVariable String uuid
    ) {
        //Confirm that the ScimUserPatchOp is valid.
        scimUserPatchOp.validatePatchOp();
        //Finds the Repository User by uuid
        User user = findUserByUUID(uuid, userRepository);

        if (scimUserPatchOp.updateUser(user))
            userRepository.save(user);

        //Return the updated user information and convert it to a SCIM User
        return scimService.userToScim1OktaIceUser(user);
    }
}
