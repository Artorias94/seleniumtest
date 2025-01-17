/*
Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
This library is free software; you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation; either version 2.1 of the License, or (at your option)
any later version.
This library is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
details.
 */
package org.entando.selenium.contracttests;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.dsl.PactDslRequestWithPath;
import au.com.dius.pact.consumer.dsl.PactDslResponse;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import org.entando.selenium.pages.*;
import org.entando.selenium.utils.UsersTestBase;
import org.entando.selenium.utils.Utils;
import org.entando.selenium.utils.pageParts.Kebab;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;
import static java.lang.Thread.sleep;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserRoleEditProvider", port = "8080")
public class UserRoleEditConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTUserRolesPage dTUserRolesPage;

    @Autowired
    public DTUserRoleAddPage dTUserRoleAddPage;

    @Autowired
    public DTUserRoleEditPage dTUserRoleEditPage;


    @BeforeAll
    public void setupSessionAndNavigateToUserManagement (){
        PactDslWithProvider builder = ConsumerPactBuilder.consumer("LoginConsumer").hasPactWith("LoginProvider");
        PactDslResponse accessTokenResponse = buildGetAccessToken(builder);
        PactDslResponse getUsersResponse = PactUtil.buildGetUsers(accessTokenResponse,1,1);
        getUsersResponse = PactUtil.buildGetUsers(getUsersResponse,1,10);
        PactDslResponse getPagesResponse = buildGetPages(getUsersResponse);
        PactDslResponse getPageStatusResponse = buildGetPageStatus(getPagesResponse);
        PactDslResponse getWidgetsResponse = buildGetWidgets(getPageStatusResponse);
        PactDslResponse getGroupsResponse = buildGetGroups(getWidgetsResponse);
        PactDslResponse getPageModelsResponse = buildGetPageModels(getGroupsResponse);
        PactDslResponse getLanguagesResponse = buildGetLanguages(getPageModelsResponse);
        PactDslResponse getRoles = buildGetRoles(getLanguagesResponse, 1, 10);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getRoles);
        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getProfileTypesResponse.toPact(), config, mockServer -> {
            login();
            //Navigation to the page
            dTDashboardPage.SelectSecondOrderLinkWithSleep("User Management", "Roles");
        });
    }

    @Pact(provider = "UserRoleEditProvider", consumer = "UserRoleEditConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getPermissionResponse = buildPermission(builder,1,0);
        PactDslResponse getRoleResponse = buildGetRole(getPermissionResponse,1,10);
        PactDslResponse getRoleToPutResponse = buildGetRoleToPut(getRoleResponse);
        PactDslResponse putRoleResponse = buildPutRole(getRoleToPutResponse);
        return putRoleResponse.toPact();
    }

    private PactDslResponse buildGetRoles(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/roles")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize);
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/roles")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"unimportant\",\"name\":\"UNIMPORTANT\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":2,\"totalItems\":11,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildGetRole(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath request = builder.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/roles")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"unimportant\",\"name\":\"UNIMPORTANT\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":2,\"totalItems\":11,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildPermission(PactDslWithProvider builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/permissions")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize);
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/permissions")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"unimportant\",\"name\":\"UNIMPORTANT\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":2,\"totalItems\":11,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildGetRoleToPut(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The User Add OPTIONS Interaction")
                .path("/entando/api/roles/unimportant")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The User Add POST Interaction")
                //TODO add the expectation for the incoming data
                .path("/entando/api/roles/unimportant")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"code\":\"unimportant\"},\"errors\":[],\"metaData\":{}}");
    }

    private PactDslResponse buildPutRole(PactDslResponse builder) {
        PactDslRequestWithPath request = builder
                .uponReceiving("The User Add POST Interaction")
                //TODO add the expectation for the incoming data
                .path("/entando/api/roles/unimportant")
                .method("PUT");
        return standardResponse(request, "{\"payload\":{\"code\":\"unimportant\"},\"errors\":[],\"metaData\":{}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        String changedRoleName = "UNIMPORTANT200";
        Kebab kebab = dTUserRolesPage.getTable().getKebabOnTable("UNIMPORTANT", groupsTableHeaderTitles.get(0), groupsTableHeaderTitles.get(2));
        kebab.getClickable().click();
        Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
        kebab.getAction("Edit").click();
        dTUserRoleEditPage.setNameField(changedRoleName);
        dTUserRoleEditPage.getSaveButton().click();
    }
}
