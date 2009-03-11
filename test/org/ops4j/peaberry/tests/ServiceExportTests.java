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

package org.ops4j.peaberry.tests;

import static com.google.inject.name.Names.named;
import static java.util.Collections.singletonMap;
import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.Attributes.names;
import static org.ops4j.peaberry.util.Attributes.union;
import static org.ops4j.peaberry.util.Filters.ldap;
import static org.ops4j.peaberry.util.TypeLiterals.export;
import static org.ops4j.peaberry.util.TypeLiterals.iterable;
import static org.osgi.framework.Constants.SERVICE_RANKING;

import java.util.Map;

import org.ops4j.peaberry.AttributeFilter;
import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.ServiceRegistry;
import org.ops4j.peaberry.util.AbstractDecorator;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Named;

import examples.ids.Id;

/**
 * Test exporting of service implementations to the {@code ServiceRegistry}.
 * 
 * @author mcculls@gmail.com (Stuart McCulloch)
 */
@Test
public final class ServiceExportTests
    extends InjectableTestCase {

  @Inject
  Id importedId;

  @Inject
  Iterable<Id> importedIds;

  @Inject
  @Named("sample")
  Id sampleId;

  public static class ExportedIdImpl
      implements Id {

    @Override
    public String toString() {
      return "EXPORTED";
    }
  }

  @Override
  protected void configure() {
    bind(AttributeFilter.class).toInstance(ldap("(id=TEST)"));
    bind(Id.class).toProvider(service(Id.class).filter(ldap("(id=TEST)")).single());
    bind(iterable(Id.class)).toProvider(
        service(Id.class).filter(Key.get(AttributeFilter.class)).multiple());

    bind(export(Id.class)).toProvider(
        service(Key.get(ExportedIdImpl.class)).decoratedWith(new AbstractDecorator<Id>() {
          @Override
          protected Id decorate(final Id instance, Map<String, ?> attributes) {
            return new Id() {
              @Override
              public String toString() {
                return "**" + instance + "**";
              }
            };
          }
        }).out(Key.get(ServiceRegistry.class)).export());

    // exercise the filter code that uses sample attributes...
    bind(Id.class).annotatedWith(named("sample")).toProvider(
        service(Id.class).attributes(singletonMap("id", "TEST")).single());
  }

  @SuppressWarnings("unchecked")
  public void testServiceExports() {
    reset();

    missing(importedId);
    check(importedIds, "[]");

    register("TEST");
    check(importedId, "TEST");
    check(importedIds, "[TEST]");
    check(sampleId, "TEST");

    // now publish our service (will get a later service.id)
    final Export<Id> exportedId = (Export) getInstance(Key.get(export(Id.class)));

    // exported service won't be used, as it doesn't match the filter
    check(importedId, "TEST");
    check(importedIds, "[TEST]");
    check(sampleId, "TEST");

    // modify exported service so it matches the import filter (drop ranking)
    exportedId.attributes(union(names("id=TEST")));

    // exported service still won't be used, as ranking isn't higher
    check(importedId, "TEST");
    check(importedIds, "[TEST, **EXPORTED**]");
    check(sampleId, "TEST");

    // modify our exported service so it matches the import filter (add ranking)
    exportedId.attributes(union(names("id=TEST"), singletonMap(SERVICE_RANKING, 8)));

    // exported service should now be used, as it has a higher ranking
    check(importedId, "**EXPORTED**");
    check(importedIds, "[**EXPORTED**, TEST]");
    check(sampleId, "**EXPORTED**");

    exportedId.put(new Id() {
      @Override
      public String toString() {
        return "UPDATED";
      }
    });

    check(importedId, "**UPDATED**");
    check(importedIds, "[**UPDATED**, TEST]");
    check(sampleId, "**UPDATED**");

    exportedId.unput();

    check(importedId, "TEST");
    check(importedIds, "[TEST]");
    check(sampleId, "TEST");

    unregister("TEST");
    missing(importedId);
    check(importedIds, "[]");
    missing(sampleId);
  }
}
