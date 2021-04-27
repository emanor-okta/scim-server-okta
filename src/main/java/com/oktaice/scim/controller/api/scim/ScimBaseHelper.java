package com.oktaice.scim.controller.api.scim;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.User;
import com.oktaice.scim.model.scim20.ScimExceptionResponse;
import com.oktaice.scim.repository.GroupRepository;
import com.oktaice.scim.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletResponse;

public class ScimBaseHelper {

    /**
     * TODO: Review the handleException method
     * Handle exceptions in SCIM format
     */
    @ExceptionHandler(Exception.class)
    public ScimExceptionResponse handleException(Exception e, HttpServletResponse response) {
        HttpStatus responseStatus = HttpStatus.CONFLICT;
        if (e instanceof HttpStatusCodeException) {
            responseStatus = ((HttpStatusCodeException) e).getStatusCode();
        }
        response.setStatus(responseStatus.value());
        return new ScimExceptionResponse(e.getMessage(), responseStatus.toString());
    }


    protected Group findOneByUUID(String uuid, GroupRepository groupRepository) {
        //Searches a Repository Group by its uuid
        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return group;
    }

    protected void copyGroup(Group from, Group to) {
        Assert.notNull(from, "From Group cannot be null");
        Assert.notNull(to, "To Group cannot be null");

        to.setDisplayName(from.getDisplayName());
        to.setUsers(from.getUsers());
    }

    protected User findUserByUUID(String uuid, UserRepository userRepository) {
        //Searches a Repository User by its uuid
        User user = userRepository.findOneByUuid(uuid);

        //Returns the Repository User and convert it to a SCIM User.
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        return user;
    }

    /**
     * The copyUser method takes in two Repository Users.
     * It copy information from the first Repository User (from) to the second Repository User (to)
     */
    protected void copyUser(User from, User to) {
        Assert.notNull(from, "From User cannot be null");
        Assert.notNull(to, "To User cannot be null");

        to.setActive(from.getActive());
        to.setUserName(from.getUserName());

        to.setEmail(from.getEmail());

        to.setLastName(from.getLastName());
        to.setMiddleName(from.getMiddleName());
        to.setFirstName(from.getFirstName());

        to.setCostCenter(from.getCostCenter());
        to.setEmployeeNumber(from.getEmployeeNumber());

        to.setFavoriteIceCream(from.getFavoriteIceCream());
    }
}
