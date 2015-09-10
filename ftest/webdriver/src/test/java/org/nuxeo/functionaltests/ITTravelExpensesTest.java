/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nelson Silva <nsilva@nuxeo.com>
 */
package org.nuxeo.functionaltests;

import org.junit.Before;
import org.junit.Test;

import org.nuxeo.functionaltests.elements.TaskElement;
import org.nuxeo.functionaltests.elements.TaskListElement;
import org.nuxeo.functionaltests.elements.WorkflowListElement;
import org.nuxeo.functionaltests.elements.tasks.ValidateExpense;
import org.nuxeo.functionaltests.elements.tasks.CreateExpense;
import org.nuxeo.functionaltests.pages.DocumentBasePage.UserNotConnectedException;
import org.nuxeo.functionaltests.elements.tasks.AccountExpense;
import org.nuxeo.functionaltests.pages.admincenter.AdminCenterBasePage;
import org.nuxeo.functionaltests.pages.admincenter.usermanagement.GroupsTabSubPage;
import org.nuxeo.functionaltests.pages.admincenter.usermanagement.UsersGroupsBasePage;
import org.nuxeo.functionaltests.pages.admincenter.usermanagement.UsersTabSubPage;
import org.openqa.selenium.support.ui.Select;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Nuxeo Travel Expenses Tests
 *
 * @since 7.2
 */
public class ITTravelExpensesTest extends AbstractTest {

    public static final String MEMBERS_GROUP = "members";
    public static final String MANAGERS_GROUP = "managers";
    public static final String ACCOUNTANTS_GROUP = "accountancy";

    public static final String MANAGER_USER = "manager";
    public static final String ACCOUNTANT_USER = "accountant";

    private static boolean isSetupDone = false;

    private TravelExpensesApp getApp() throws MalformedURLException {
        navToUrl(NUXEO_URL + "/travel-expenses");
        return asPage(TravelExpensesApp.class);
    }

    @Test
    public void testListingsAreAvailable() throws Exception {
        loginAsTestUser();

        TravelExpensesApp app = getApp();

        TaskListElement taskList = app.gotoTaskList();
        assertNotNull(taskList);

        WorkflowListElement workflowList = app.gotoWorkflowList();
        assertNotNull(workflowList);
    }

    @Test
    public void testExpenseWorkflow() throws Exception {

        // create
        loginAsTestUser();

        TravelExpensesApp app = getApp();

        TaskListElement taskList = app.gotoTaskList();
        taskList.createTask();

        app.goBack();
        assertEquals(1, app.getTaskList().size());
        taskList.selectTask(0);

        CreateExpense create = TaskElement.of(CreateExpense.class);
        assertEquals(1, create.getActors().size());
        assertEquals(TEST_USERNAME, create.getActors().get(0));
        assertEquals("Submit for validation", create.getDirective());

        create.label.sendKeys("A label");
        create.amount.sendKeys("500");
        create.description.sendKeys("A description");
        new Select(create.department).selectByValue("it");
        new Select(create.nature).selectByValue("transportation");
        create.action("submit");

        app.goBack();

        // task is no longer assigned to initiator
        assertEquals(0, app.getTaskList().size());

        logout();

        // validate
        loginAsManager();
        app = getApp();

        taskList = app.gotoTaskList();
        assertEquals(1, app.getTaskList().size());
        taskList.selectTask(0);

        ValidateExpense validate = TaskElement.of(ValidateExpense.class);
        assertEquals(1, validate.getActors().size());
        assertEquals("group:managers", validate.getActors().get(0));
        assertEquals("Validation", validate.getDirective());

        assertEquals("A label", validate.label.getText());
        assertEquals("500", validate.amount.getText());
        assertEquals("A description", validate.description.getText());
        assertEquals("it", validate.department.getText());
        assertEquals("transportation", validate.nature.getText());
        validate.action("validate");

        app.goBack();

        // task is no longer assigned to manager
        assertEquals(0, app.getTaskList().size());

        logout();

        // account

        loginAsAccountant();
        app = getApp();

        taskList = app.gotoTaskList();
        assertEquals(1, app.getTaskList().size());
        taskList.selectTask(0);

        AccountExpense account = TaskElement.of(AccountExpense.class);
        assertEquals(1, account.getActors().size());
        assertEquals("group:accountancy", account.getActors().get(0));
        assertEquals("Set customer code", account.getDirective());

        assertEquals("A label", account.label.getText());
        assertEquals("A description", account.description.getText());
        assertEquals("500", account.amount.getText());
        assertEquals("it", validate.department.getText());
        assertEquals("transportation", account.nature.getText());

        account.customerCode.sendKeys("C101");

        account.action("done");

        app.goBack();

        assertEquals(0, app.getTaskList().size());
    }

    @Test
    public void testDeleteExpense() throws Exception {
        loginAsTestUser();

        TravelExpensesApp app = getApp();

        TaskListElement taskList = app.gotoTaskList();
        taskList.createTask();

        app.goBack();

        TaskListElement tasks = app.getTaskList();
        assertEquals(1, tasks.size());
        tasks.deleteTask(0);
        assertEquals(0, tasks.size());
    }

    @Before
    public void setUp() throws UserNotConnectedException {
        if (isSetupDone) {
            return;
        }
        AjaxRequestManager ajax = new AjaxRequestManager(driver);
        AdminCenterBasePage admin = login().getAdminCenter();

        // create users
        UsersTabSubPage usersTab = admin.getUsersGroupsHomePage().getUsersTab().searchUser(TEST_USERNAME);
        if (!usersTab.isUserFound(TEST_USERNAME)) {
            ajax.watchAjaxRequests();
            usersTab.getUserCreatePage().createUser(TEST_USERNAME, TEST_USERNAME, "", "", "email1", TEST_PASSWORD, MEMBERS_GROUP);
            ajax.waitForAjaxRequests();
        }
        usersTab = admin.getUsersGroupsHomePage().getUsersTab(true).searchUser(MANAGER_USER);
        if (!usersTab.isUserFound(MANAGER_USER)) {
            ajax.watchAjaxRequests();
            usersTab.getUserCreatePage().createUser(MANAGER_USER, MANAGER_USER, "", "", "email2", TEST_PASSWORD, MEMBERS_GROUP);
            ajax.waitForAjaxRequests();
        }
        usersTab = admin.getUsersGroupsHomePage().getUsersTab().searchUser(ACCOUNTANT_USER);
        if (!usersTab.isUserFound(ACCOUNTANT_USER)) {
            ajax.watchAjaxRequests();
            usersTab.getUserCreatePage().createUser(ACCOUNTANT_USER, ACCOUNTANT_USER, "", "", "email3", TEST_PASSWORD, MEMBERS_GROUP);
            ajax.waitForAjaxRequests();
        }

        // create groups
        GroupsTabSubPage groupsTab = admin.getUsersGroupsHomePage().getGroupsTab().searchGroup(MANAGERS_GROUP);
        if (!groupsTab.isGroupFound(MANAGERS_GROUP)) {
            ajax.watchAjaxRequests();
            groupsTab.getGroupCreatePage().createGroup(MANAGERS_GROUP, MANAGERS_GROUP, new String[] { MANAGER_USER }, null);
            ajax.waitForAjaxRequests();
        }
        groupsTab = admin.getUsersGroupsHomePage().getGroupsTab(true).searchGroup(ACCOUNTANTS_GROUP);
        if (!groupsTab.isGroupFound(ACCOUNTANTS_GROUP)) {
            ajax.watchAjaxRequests();
            groupsTab.getGroupCreatePage().createGroup(ACCOUNTANTS_GROUP, ACCOUNTANTS_GROUP, new String[]{ ACCOUNTANT_USER }, null);
            ajax.waitForAjaxRequests();
        }

        logout();
        isSetupDone = true;
    }

    // TODO: remove test users and groups
    public void tearDown() throws UserNotConnectedException {
        UsersGroupsBasePage usersGroupsPage = login().getAdminCenter().getUsersGroupsHomePage();
        UsersTabSubPage usersTab = usersGroupsPage.getUsersTab();
        usersTab = usersTab.searchUser(TEST_USERNAME);
        if (usersTab.isUserFound(TEST_USERNAME)) {
            usersTab.viewUser(TEST_USERNAME).deleteUser();
        }
        usersTab = usersGroupsPage.getUsersTab();
        usersTab = usersTab.searchUser(MANAGER_USER);
        if (usersTab.isUserFound(MANAGER_USER)) {
            usersTab.viewUser(MANAGER_USER).deleteUser();
        }
        usersTab = usersGroupsPage.getUsersTab();
        usersTab = usersTab.searchUser(ACCOUNTANT_USER);
        if (usersTab.isUserFound(ACCOUNTANT_USER)) {
            usersTab.viewUser(ACCOUNTANT_USER).deleteUser();
        }
        logout();
    }

    private void loginAsAccountant() throws UserNotConnectedException {
        login(ACCOUNTANT_USER, TEST_PASSWORD);
    }

    private void loginAsManager() throws UserNotConnectedException {
        login(MANAGER_USER, TEST_PASSWORD);
    }

}
