/**
 * Copyright (C) 2008 Stuart McCulloch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.peaberry.test.cases;

import static org.ops4j.peaberry.util.Attributes.names;
import static org.ops4j.peaberry.util.Attributes.properties;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.util.Map;
import java.util.Properties;

import org.testng.annotations.Test;

/**
 * Test attribute helper methods.
 * 
 * @author mcculls@gmail.com (Stuart McCulloch)
 */
@Test
public final class AttributeTests {

  public void testPropertyConverter() {
    final Properties properties = new Properties();

    properties.put(1, "badKey");
    properties.setProperty("string", "one");
    properties.put("integer", 1);

    final Map<?, ?> attributes = properties(properties);

    assertNull(attributes.get(1));
    assertEquals(attributes.get("string"), "one");
    assertEquals(attributes.get("integer"), 1);
    assertEquals(attributes.size(), 2);
  }

  public void testNameConverter() {
    try {
      names("cn=John Doe", "=", "dc=com");
      fail("Expected IllegalArgumentException");
    } catch (final IllegalArgumentException e) {}
  }
}