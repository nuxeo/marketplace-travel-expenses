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

import org.nuxeo.functionaltests.AbstractTest;
import org.nuxeo.functionaltests.AjaxRequestManager;
import org.nuxeo.functionaltests.fragment.WebFragmentImpl;
import org.nuxeo.functionaltests.PolymerHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Represents a nx-task element.
 *
 * @since 7.2
 */
public class TaskElement extends WebFragmentImpl {

    private final PolymerHelper polymer;

    @FindBy(css = ".field.actors")
    private WebElement actors;

    @FindBy(css = ".field.dueDate")
    private WebElement dueDate;

    @FindBy(css = ".field.directive")
    private WebElement directive;

    public static <T extends TaskElement> T of(Class<T> clazz) {
        return AbstractTest.getWebFragment(By.tagName("nx-task"), clazz);
    }

    public TaskElement(WebDriver driver, WebElement element) {
        super(driver, element);
        polymer = new PolymerHelper(driver);
    }

    public void action(String name) {
        WebElement toolbar = findElement(By.tagName("paper-toolbar"));
        WebElement button = toolbar.findElement(By.cssSelector("paper-button[data-action='" + name + "']"));
        polymer.tap(button);
        new AjaxRequestManager(driver).waitForJsClient();
    }

    public List<String> getActors() {
        return actors.findElements(By.tagName("li"))
            .stream()
            .map(WebElement::getText)
            .collect(toList());
    }

    public String getDirective() {
        return directive.findElement(By.tagName("span")).getText();
    }

}
