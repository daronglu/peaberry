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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import examples.ids.RegistryService;

/**
 * Register {@code IdService} implementations on demand.
 * 
 * @author mcculls@gmail.com (Stuart McCulloch)
 */
public class IdServiceActivator
    implements BundleActivator {

  public void start(final BundleContext ctx) {
    ctx.registerService(RegistryService.class.getName(), new RegistryServiceImpl(ctx), null);
  }

  public void stop(@SuppressWarnings("unused") final BundleContext ctx) {}
}