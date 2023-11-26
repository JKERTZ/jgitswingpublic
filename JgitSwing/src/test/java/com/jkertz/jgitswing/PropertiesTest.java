/*
 * Copyright (C) 2023 jkertz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jkertz.jgitswing;

import com.jkertz.jgitswing.settings.JGSsettings;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author jkertz
 */
public class PropertiesTest {

    private final String propertiesFileName = "test.properties";
    private final String xmlFileName = "test.xml";

    private void hello() {
        System.out.println("hello");
    }

    private void saveToProperties() throws Exception {
        System.out.println("saveToProperties");
        Properties prop = new Properties();
        prop.setProperty("propKey1", "propValue1");
        prop.setProperty("propKey2", "propValue2");
        FileOutputStream outStream = new FileOutputStream(propertiesFileName);
        prop.store(outStream, "saveToProperties comment");
        outStream.close();
    }

    private void loadFromProperties() throws Exception {
        System.out.println("loadFromProperties");
        FileInputStream inStream = new FileInputStream(propertiesFileName);
        Properties prop = new Properties();
        prop.load(inStream);
        Set<String> stringPropertyNames = prop.stringPropertyNames();
        for (String stringPropertyName : stringPropertyNames) {
            String property = prop.getProperty(stringPropertyName);
            System.out.println("stringPropertyName: " + stringPropertyName + " property:" + property);
        }
    }

    private void saveToPropertiesXml() throws Exception {
        System.out.println("saveToPropertiesXml");
        Properties prop = new Properties();
        prop.setProperty("propKey1", "propValue1");
        prop.setProperty("propKey2", "propValue2");
        FileOutputStream outStream = new FileOutputStream(xmlFileName);

        prop.storeToXML(outStream, "saveToPropertiesXml comment");
        outStream.close();
    }

    private void loadFromPropertiesXml() throws Exception {
        System.out.println("loadFromPropertiesXml");
        FileInputStream inStream = new FileInputStream(xmlFileName);
        Properties prop = new Properties();
        prop.loadFromXML(inStream);
        Set<String> stringPropertyNames = prop.stringPropertyNames();
        for (String stringPropertyName : stringPropertyNames) {
            String property = prop.getProperty(stringPropertyName);
            System.out.println("stringPropertyName: " + stringPropertyName + " property:" + property);
        }
    }

    private void getUserDirTest() {
        String currentUsersHomeDir = System.getProperty("user.home");
        String userDir = System.getProperty("user.dir");
        System.out.println("currentUsersHomeDir" + currentUsersHomeDir);
        System.out.println("userDir" + userDir);
    }

    private void jGSsettingsTest() {
        JGSsettings jGSsettings = JGSsettings.getINSTANCE();
    }

    private void freeObjectTest() {
        // Create some objects
        System.out.println("Create some objects");
        MyClass obj1 = new MyClass();
        Object obj2 = new Object();

        // Set the references to null to make the objects eligible for garbage collection
        System.out.println("Set the references to null");
        obj1 = null;
        obj2 = null;

        // Suggest the JVM to run the garbage collector
        System.out.println("Suggest the JVM to run the garbage collector");
        System.gc();
    }

    private static class MyClass {

        public MyClass() {
            System.out.println("MyClass constructor");
        }

        @Override
        protected void finalize() throws Throwable {
            try {
                // Cleanup operations
                System.out.println("MyClass finalize");

            } finally {
                super.finalize();
            }
        }
    }

}
