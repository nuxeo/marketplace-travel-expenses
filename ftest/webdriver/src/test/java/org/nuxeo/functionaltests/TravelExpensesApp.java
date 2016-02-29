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

import org.nuxeo.functionaltests.elements.TaskListElement;
import org.nuxeo.functionaltests.elements.WorkflowListElement;
import org.nuxeo.functionaltests.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TravelExpensesApp extends AbstractPage {

    private final PolymerHelper polymer;

    private final AjaxRequestManager ajax;

    @FindBy(tagName = "paper-tab")
    List<WebElement> tabs;

    @FindBy(tagName = "paper-icon-button")
    WebElement backButton;

    public TravelExpensesApp(WebDriver driver) {
        super(driver);
        polymer = new PolymerHelper(driver);
        ajax = new AjaxRequestManager(driver);
    }

    public TaskListElement getTaskList() {
        return getWebFragment(By.tagName("nx-task-list"), TaskListElement.class);
    }

    public TaskListElement gotoTaskList() {
        tabs.get(0).click();
        return getTaskList();
    }

    public WorkflowListElement getWorkflowList() {
        return getWebFragment(By.tagName("nx-workflow-list"), WorkflowListElement.class);
    }

    public WorkflowListElement gotoWorkflowList() {
        tabs.get(1).click();
        return getWorkflowList();
    }

    public void goBack() {
        polymer.watchTransitions();
        polymer.tap(backButton);
        polymer.flush();
        ajax.waitForJQueryRequests();
    }
}
