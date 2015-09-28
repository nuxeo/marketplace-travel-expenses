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

public class AccountExpense extends TaskElement {

    @FindBy(css = "[item]:nth-of-type(1) > span")
    public WebElement label;

    @FindBy(css = "[item]:nth-of-type(2) > span")
    public WebElement amount;

    @FindBy(css = "[item]:nth-of-type(3) > span")
    public WebElement description;

    @FindBy(css = "[item]:nth-of-type(4) > span")
    public WebElement department;

    @FindBy(css = "[item]:nth-of-type(5) > span")
    public WebElement nature;

    @FindBy(css = "[item]:nth-of-type(6) > input")
    public WebElement customerCode;

    public AccountExpense(WebDriver driver, WebElement element) {
        super(driver, element);
    }
}
