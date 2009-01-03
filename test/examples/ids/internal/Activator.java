/**
 * Copyright (C) 2008 Stuart McCulloch
 *
 * Licensed under the Apache License, Version 2.0 (the "License"){}
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

package examples.ids.internal;

import static com.google.inject.Guice.createInjector;
import static org.ops4j.peaberry.Peaberry.osgiModule;

import org.ops4j.peaberry.ServiceRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import examples.ids.IdManager;

/**
 * Register {@link IdManager} implementation with OSGi service registry.
 * 
 * @author mcculls@gmail.com (Stuart McCulloch)
 */
public final class Activator
    implements BundleActivator {

  public void start(final BundleContext bundleContext) {
    bundleContext.registerService(IdManager.class.getName(), new IdManagerImpl(createInjector(
        osgiModule(bundleContext)).getInstance(ServiceRegistry.class)), null);
  }

  public void stop(final BundleContext bundleContext) {}
}