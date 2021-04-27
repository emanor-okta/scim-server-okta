package com.oktaice.scim.controller.api.scim;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Handles errors and the SCIM configuration endpoints.
 * The SCIM Configuration endpoints presents static JSON configuration files (under resources/scim-json) as output.
 */
@RestController
@RequestMapping(value = {"/scim/v2", "/scim/v1"})
public class ScimBaseController extends ScimBaseHelper {

    /**
     * Return the features supported by the ICE Research SCIM API.
     * i.e.: The ICE Research API supports patchop, but does not support bulk operations.
     */
    @GetMapping({"/ServiceProviderConfig", "/ServiceProviderConfig"})
    public ResponseEntity<InputStreamResource> getServiceProviderConfig() throws IOException {
        return getResourceJsonFile("/scim-json/ServiceProviderConfig.json");
    }//getServiceProviderConfig

    /**
     * Return the Resource Types supported by ICE Research (User and Group)
     */
    @GetMapping({"/ResourceTypes", "/ResourceTypes"})
    public ResponseEntity<InputStreamResource> getResourceTypes() throws IOException {
        return getResourceJsonFile("/scim-json/ResourceTypes.json");
    }

    /**
     * Return the SCIM schemas supported by ICE Research
     */
    @GetMapping({"/Schemas", "/Schemas"})
    public ResponseEntity<InputStreamResource> getSchemas() throws IOException {
        return getResourceJsonFile("/scim-json/Schemas.json");
    }

    /**
     * Helper method that read JSON files for the configuration endpoints
     */
    private ResponseEntity<InputStreamResource> getResourceJsonFile(String fileName) throws IOException {
        ClassPathResource jsonFile = new ClassPathResource(fileName);

        return ResponseEntity
                .ok()
                .contentLength(jsonFile.contentLength())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new InputStreamResource(jsonFile.getInputStream()));
    }
}

