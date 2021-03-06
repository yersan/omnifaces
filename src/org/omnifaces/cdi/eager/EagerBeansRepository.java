/*
 * Copyright 2014 OmniFaces.
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
package org.omnifaces.cdi.eager;

import static org.omnifaces.util.Utils.isAnyEmpty;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 * Bean repository via which various types of eager beans can be instantiated on demand.
 *
 * @since 1.8
 * @author Arjan Tijms
 *
 */
@ApplicationScoped
public class EagerBeansRepository implements BeansInstantiator {

	@Inject
	private BeanManager beanManager;

	private List<Bean<?>> applicationScopedBeans;
	private List<Bean<?>> sessionScopedBeans;
	private Map<String, List<Bean<?>>> requestScopedBeansViewId;
	private Map<String, List<Bean<?>>> requestScopedBeansRequestURI;

	@Override
	public void instantiateApplicationScoped() {
		if (isAnyEmpty(applicationScopedBeans, beanManager)) {
			return;
		}

		instantiateBeans(applicationScopedBeans);
	}

	@Override
	public void instantiateSessionScoped() {
		if (isAnyEmpty(sessionScopedBeans, beanManager)) {
			return;
		}

		instantiateBeans(sessionScopedBeans);
	}

	@Override
	public void instantiateByRequestURI(String relativeRequestURI) {
		instantiateRequestScopedBeans(requestScopedBeansRequestURI, relativeRequestURI);
	}

	@Override
	public void instantiateByViewID(String viewId) {
		instantiateRequestScopedBeans(requestScopedBeansViewId, viewId);
	}

	private void instantiateRequestScopedBeans(Map<String, List<Bean<?>>> beans, String key) {
		if (isAnyEmpty(beans, beanManager) || !beans.containsKey(key)) {
			return;
		}

		instantiateBeans(beans.get(key));
	}

	private void instantiateBeans(List<Bean<?>> beans) {
		for (Bean<?> bean : beans) {
			beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean)).toString();
		}
	}

	public void setApplicationScopedBeans(List<Bean<?>> applicationScopedBeans) {
		this.applicationScopedBeans = applicationScopedBeans;
	}

	public void setSessionScopedBeans(List<Bean<?>> sessionScopedBeans) {
		this.sessionScopedBeans = sessionScopedBeans;
	}

	public void setRequestScopedBeansViewId(Map<String, List<Bean<?>>> requestScopedBeansViewId) {
		this.requestScopedBeansViewId = requestScopedBeansViewId;
	}

	public void setRequestScopedBeansRequestURI(Map<String, List<Bean<?>>> requestScopedBeansRequestURI) {
		this.requestScopedBeansRequestURI = requestScopedBeansRequestURI;
	}

}