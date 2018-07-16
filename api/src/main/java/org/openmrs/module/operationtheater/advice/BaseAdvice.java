package org.openmrs.module.operationtheater.advice;

import org.ict4h.atomfeed.server.repository.AllEventRecordsQueue;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class BaseAdvice implements AfterReturningAdvice {
	
	private final AtomFeedSpringTransactionManager atomFeedSpringTransactionManager;
	
	private final EventService eventService;
	
	public BaseAdvice() {
		atomFeedSpringTransactionManager = new AtomFeedSpringTransactionManager(getSpringPlatformTransactionManager());
		AllEventRecordsQueue allEventRecordsQueue = new AllEventRecordsQueueJdbcImpl(atomFeedSpringTransactionManager);
		
		this.eventService = new EventServiceImpl(allEventRecordsQueue);
	}
	
	private PlatformTransactionManager getSpringPlatformTransactionManager() {
		return Context.getRegisteredComponents(PlatformTransactionManager.class).get(0);
	}
	
	protected boolean shouldRaiseEvent(String globalPropertyName) {
		return Boolean.valueOf(Context.getAdministrationService().getGlobalProperty(globalPropertyName));
	}
	
	protected String getUrlPattern(String urlPatternGlobalPropertyName, String defaultUrlPattern) {
		return Context.getAdministrationService().getGlobalProperty(urlPatternGlobalPropertyName, defaultUrlPattern);
	}
	
	protected void notifyEvent(Event event) {
		atomFeedSpringTransactionManager.executeWithTransaction(new AFTransactionWorkWithoutResult() {
			
			@Override
			protected void doInTransaction() {
				eventService.notify(event);
			}
			
			@Override
			public PropagationDefinition getTxPropagationDefinition() {
				return PropagationDefinition.PROPAGATION_REQUIRED;
			}
		});
	}
}
