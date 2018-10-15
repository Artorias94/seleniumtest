
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
import org.entando.selenium.utils.pageParts.Kebab;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;
import static java.lang.Thread.sleep;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "PageSearchProvider", port = "8080")
public class PageSearchConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTPageTreePage dTPageTreePage;

    @BeforeAll
    public void setupSessionAndNavigateToUserManagement (){
        PactDslWithProvider builder = ConsumerPactBuilder.consumer("LoginConsumer").hasPactWith("LoginProvider");
        PactDslResponse accessTokenResponse = buildGetAccessToken(builder);
        PactDslResponse getUsersResponse = PactUtil.buildGetUsers(accessTokenResponse,1,1);
        getUsersResponse = PactUtil.buildGetUsers(getUsersResponse,1,10);
        PactDslResponse getPagesResponse = buildGetPages(getUsersResponse);
        PactDslResponse getPageStatusResponse = buildGetPageStatus(getPagesResponse);
        PactDslResponse getWidgetsDashResponse = PactUtil.buildGetWidgets(getPageStatusResponse);
        PactDslResponse getGroupsResponse = buildGetGroups(getWidgetsDashResponse);
        PactDslResponse getPageModelsResponse = buildGetPageModels(getGroupsResponse);
        PactDslResponse getLanguagesResponse = buildGetLanguages(getPageModelsResponse);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getLanguagesResponse);

        PactDslResponse getHomePage = buildGetHomePage(getProfileTypesResponse, "draft");
        PactDslResponse getParentCodeResponse = PactUtil.buildGetPagesParentCode(getHomePage, "homepage");

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getParentCodeResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLinkWithSleep("Page Designer", "Page Tree");
        });
    }

    @Pact(provider = "PageSearchProvider", consumer = "PageSearchConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse searchPageResponse = buildSearchPage(builder,"code", "ASC", "pagina4",1,100);
        return searchPageResponse.toPact();
    }

    private PactDslResponse buildGetHomePage(PactDslResponse builder,String status) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The home page OPTIONS Interaction")
                .path("/entando/api/pages/homepage")
                .method("OPTIONS")
                .matchQuery("status", "\\w+", "" + status)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The home page GET Interaction")
                .path("/entando/api/pages/homepage")
                .method("GET")
                .matchQuery("status", "\\w+", "" + status);
        return standardResponse(request, "{\"payload\":{\"code\":\"homepage\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Home\",\"it\":\"Home\"},\"fullTitles\":{\"en\":\"Home\",\"it\":\"Home\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"service\",\"page_se\",\"pagina4\",\"node1\"],\"position\":-1,\"numWidget\":4,\"lastModified\":\"2018-10-02 11:28:11\",\"fullPath\":\"homepage\",\"token\":\"FskPvpj07Z++YLIYLHlofw==\",\"references\":{\"jacmsContentManager\":false}},\"errors\":[],\"metaData\":{\"status\":\"draft\"}}");
    }

    private PactDslResponse buildSearchPage(PactDslWithProvider builder,String sort, String direction, String pageCodeToken, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The search OPTIONS Interaction")
                .path("/entando/api/pages/search")
                .method("OPTIONS")
                .matchQuery("sort", "\\w+", "" + sort)
                .matchQuery("direction", "\\w+", "" + direction)
                .matchQuery("pageCodeToken", "\\w+", "" + pageCodeToken)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", "" + page)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The search GET Interaction")
                .path("/entando/api/pages/search")
                        .matchQuery("sort", "\\w+", "" + sort)
                        .matchQuery("direction", "\\w+", "" + direction)
                        .matchQuery("pageCodeToken", "\\w+", "" + pageCodeToken)
                        .matchQuery("page", "\\d+", "" + page)
                        .matchQuery("pageSize", "\\d+", "" + page)
                .method("GET");
        return standardResponse(request, "{\"payload\":[{\"code\":\"pagina4\",\"status\":\"unpublished\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Pagina4\",\"it\":\"Pagina4\"},\"fullTitles\":{\"en\":\"Home / Pagina4\",\"it\":\"Home / Pagina4\"},\"ownerGroup\":\"administrators\",\"joinGroups\":[],\"children\":[],\"position\":3,\"numWidget\":0,\"lastModified\":\"2018-10-02 11:09:30\",\"fullPath\":\"homepage/pagina4\",\"token\":null}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":100,\"lastPage\":1,\"totalItems\":1,\"sort\":\"code\",\"direction\":\"ASC\",\"additionalParams\":{},\"pageCodeToken\":\"pagina4\"}}");
    }


    @Test
    public void runTest() throws InterruptedException {

        dTPageTreePage.setSearchField("pagina4");
        dTPageTreePage.getSearchButton().click();
    }
}
