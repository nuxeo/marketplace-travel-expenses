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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.util.concurrent.TimeUnit;

/**
 * Polymer helper
 * @since 7.2
 */
public class PolymerHelper {

    private final JavascriptExecutor js;

    public PolymerHelper(WebDriver driver) {
        js = (JavascriptExecutor) driver;
    }

    public void flush() {
        js.executeScript("Polymer.dom.flush();");
    }

    public void tap(WebElement target) {
        js.executeScript("arguments[0].fire('tap')", target);
    }

    public void watchTransitions() {
        js.executeScript(
                "window.polymerTransitionsDone = false;" +
                "window.addEventListener('neon-animation-finish'," +
                "  function(e) { " +
                "    window.polymerTransitionsDone = true;" +
                "  })");
    }

    public void waitForTransitions() {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(AbstractTest.driver).withTimeout(
            AbstractTest.LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS).pollingEvery(
            AbstractTest.POLLING_FREQUENCY_MILLISECONDS, TimeUnit.MILLISECONDS).ignoring(
            NoSuchElementException.class);
        wait.until(driver -> js.executeScript("return window.polymerTransitionsDone;"));
    }
}
