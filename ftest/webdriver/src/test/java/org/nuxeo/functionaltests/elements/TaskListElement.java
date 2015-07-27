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
package org.nuxeo.functionaltests.elements;

import org.nuxeo.functionaltests.AjaxRequestManager;
import org.nuxeo.functionaltests.fragment.WebFragmentImpl;
import org.nuxeo.functionaltests.PolymerHelper;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents a nx-task-list element.
 *
 * @since 7.2
 */
public class TaskListElement extends WebFragmentImpl {

    private final PolymerHelper polymer;

    private final AjaxRequestManager ajax;

    @FindBy(id = "list")
    private WebElement list;

    @FindBy(tagName = "paper-fab")
    private WebElement createButton;


    public TaskListElement(WebDriver driver, WebElement element) {
        super(driver, element);
        ajax = new AjaxRequestManager(driver);
        polymer = new PolymerHelper(driver);
    }

    public void createTask() {
        polymer.tap(createButton);
        ajax.waitForJQueryRequests();
    }

    public void deleteTask(int idx) {
        WebElement deleteBtn = getItem(idx).findElement(By.cssSelector("paper-icon-button[icon='delete']"));
        polymer.tap(deleteBtn);
        Alert alert = driver.switchTo().alert();
        alert.accept();
        ajax.waitForJQueryRequests();
    }

    public void selectTask(int idx) {
        //polymer.watchTransitions();
        polymer.tap(getItem(idx));
        polymer.flush();
        //polymer.waitForTransitions();
        ajax.waitForJQueryRequests();
    }

    public int size() {
        return list.findElements(By.cssSelector("paper-item:not([hidden])")).size();
    }

    private WebElement getItem(int idx) {
        return list.findElement(By.cssSelector("paper-item:nth-of-type(" + (idx + 1) + ")"));
    }
}
