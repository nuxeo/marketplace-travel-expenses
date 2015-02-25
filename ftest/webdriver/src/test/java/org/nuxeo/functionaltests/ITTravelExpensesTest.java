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

import org.junit.After;
import org.junit.Test;

import org.nuxeo.functionaltests.elements.TaskElement;
import org.nuxeo.functionaltests.elements.TaskListElement;
import org.nuxeo.functionaltests.elements.WorkflowListElement;
import org.nuxeo.functionaltests.elements.tasks.ValidateExpense;
import org.nuxeo.functionaltests.elements.tasks.CreateExpense;
import org.nuxeo.functionaltests.pages.DocumentBasePage.UserNotConnectedException;
import org.nuxeo.functionaltests.elements.tasks.AccountExpense;
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

    private TravelExpensesApp getApp() throws MalformedURLException {
        navToUrl(NUXEO_URL + "/travel-expenses");
        return asPage(TravelExpensesApp.class);
    }

    @Test
    public void testListingsAreAvailable() throws Exception {
        loginAsUser();

        TravelExpensesApp app = getApp();

        TaskListElement taskList = app.gotoTaskList();
        assertNotNull(taskList);

        WorkflowListElement workflowList = app.gotoWorkflowList();
        assertNotNull(workflowList);
    }

    @Test
    public void testExpenseWorkflow() throws Exception {

        // create
        loginAsUser();

        TravelExpensesApp app = getApp();

        TaskListElement taskList = app.gotoTaskList();
        taskList.createTask();

        CreateExpense create = TaskElement.of(CreateExpense.class);
        assertEquals(1, create.getActors().size());
        assertEquals("Administrator", create.getActors().get(0));
        assertEquals("Submit for validation", create.getDirective());

        create.label.sendKeys("A label");
        create.amount.sendKeys("500");
        create.description.sendKeys("A description");
        new Select(create.nature).selectByValue("transportation");
        create.action("submit");

        app.goBack();

        assertEquals(1, app.getTaskList().size());

        logout();

        // validate
        loginAsAccountant();
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
        assertEquals("transportation", validate.nature.getText());
        validate.action("validate");
        logout();

        // account

        loginAsManager();
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
        // TODO: assertEquals("500", account.amount.getText());
        assertEquals("transportation", account.nature.getText());

        account.customerCode.sendKeys("C101");

        account.action("done");

        app.goBack();

        assertEquals(0, app.getTaskList().size());
    }

    @Test
    public void testDeleteExpense() throws Exception {
        loginAsUser();

        TravelExpensesApp app = getApp();

        TaskListElement taskList = app.gotoTaskList();
        taskList.createTask();

        app.goBack();

        TaskListElement tasks = app.getTaskList();
        assertEquals(1, tasks.size());
        tasks.deleteTask(0);
        assertEquals(0, tasks.size());
    }

    @After
    public void tearDown() {
        logout();
    }

    // TODO: fix authentication with other users besides Administrator
    // see https://jira.nuxeo.com/browse/NXP-16601 / https://jira.nuxeo.com/browse/NXJS-19
    private void loginAsAccountant() throws UserNotConnectedException {
        login(); //login("accountant", "accountant");
    }

    private void loginAsManager() throws UserNotConnectedException {
        login(); //login("manager", "manager");
    }

    private void loginAsUser() throws UserNotConnectedException {
        login(); //loginAsTestUser();
    }
}
