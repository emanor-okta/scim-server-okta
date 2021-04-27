package com.oktaice.scim.controller.api.scim;


import com.oktaice.scim.model.ScimUserPatchOpV2;
import com.oktaice.scim.model.User;
import com.oktaice.scim.model.scim20.ScimOktaIceUser;
import com.oktaice.scim.model.scim20.ScimUser;
import com.oktaice.scim.model.ScimUserPatchOp;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.service.ScimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@ConditionalOnProperty(name = "scim.service", havingValue = "wip")
@RequestMapping("/scim/v2/Users")
public class ScimV2UserController extends ScimBaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(ScimV2UserController.class);

    UserRepository userRepository;
    ScimService scimService;

    public ScimV2UserController(UserRepository userRepository, ScimService scimService) {
        this.userRepository = userRepository;
        this.scimService = scimService;
        logger.info("Using ScimV2UserController...");
    }


    /**
     * the getUser method V2
     */
    @GetMapping("/{uuid}")
    public @ResponseBody
    ScimOktaIceUser getUser(@PathVariable String uuid) {
        return scimService.userToScim2OktaIceUser(findUserByUUID(uuid, userRepository));
    }


    /**
     * the createUser method
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ScimUser createUser(@RequestBody ScimUser scimUser) {
        //Get new user's information
        User newUser = scimService.scim2UserToUser(scimUser);
        //Save new user to DB
        userRepository.save(newUser);
        //Returns the user information and convert it to a SCIM User
        return scimService.userToScim2OktaIceUser(newUser);
    }

    /**
     * the replaceUser method
     */
    @PutMapping("/{uuid}")
    public @ResponseBody ScimOktaIceUser replaceUser(@RequestBody ScimUser scimUser, @PathVariable String uuid) {
        //Finds the Repository User by uuid
        User user = findUserByUUID(uuid, userRepository);
        //Convert the SCIM User to a Repository User format if an existing Repository User can be found.
        User userWithUpdates = scimService.scim2UserToUser(scimUser);
        //Copy attribute values from userWithUpdates to the existing Repository User
        copyUser(userWithUpdates, user);
        //Save the updated value to DB
        userRepository.save(user);
        //Return the updated user information and convert it to a SCIM User
        return scimService.userToScim2OktaIceUser(user);
    }

    /**
     * the updateUser method
     */
    @SuppressWarnings("unchecked")
    @PatchMapping("/{uuid}")
    public @ResponseBody ScimOktaIceUser updateUser(
            @RequestBody ScimUserPatchOpV2 scimUserPatchOp, @PathVariable String uuid
    ) {
        //Confirm that the ScimUserPatchOp is valid.
        scimUserPatchOp.validatePatchOp();
        //Finds the Repository User by uuid
        User user = findUserByUUID(uuid, userRepository);

        if (scimUserPatchOp.updateUser(user))
            userRepository.save(user);

        //Return the updated user information and convert it to a SCIM User
        return scimService.userToScim2OktaIceUser(user);
    }

}
