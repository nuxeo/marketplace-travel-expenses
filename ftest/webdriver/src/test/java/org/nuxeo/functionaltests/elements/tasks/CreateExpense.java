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
package org.nuxeo.functionaltests.elements.tasks;

import org.nuxeo.functionaltests.elements.TaskElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CreateExpense extends TaskElement {

    @FindBy(css = "core-field:nth-child(1) > input")
    public WebElement label;

    @FindBy(css = "core-field:nth-child(2) > input")
    public WebElement amount;

    @FindBy(css = "core-field:nth-child(3) > input")
    public WebElement description;

    @FindBy(css = "core-field:nth-child(4) > select")
    public WebElement nature;

    public CreateExpense(WebDriver driver, WebElement element) {
        super(driver, element);
    }
}
