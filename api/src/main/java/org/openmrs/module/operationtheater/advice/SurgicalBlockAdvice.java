package org.openmrs.module.operationtheater.advice;

import org.ict4h.atomfeed.server.repository.AllEventRecordsQueue;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.joda.time.DateTime;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Method;
import java.net.URI;
import java.sql.SQLException;
import java.util.UUID;

public class SurgicalBlockAdvice implements AfterReturningAdvice {
    private static final String CATEGORY = "surgicalblock";
    private static final String TITLE = "Surgical Block";
    private static final String SAVE_PATIENT_SURGICAL_BLOCK_METHOD = "save";
    private static final String RAISE_PATIENT_SURGICAL_BLOCK_EVENT_GLOBAL_PROPERTY = "atomfeed.publish.eventsForSurgicalBlockChange";
    private static final String SURGICAL_BLOCK_EVENT_URL_PATTERN_GLOBAL_PROPERTY = "atomfeed.event.urlPatternForSurgicalBlock";
    private static final String DEFAULT_SURGICAL_BLOCK_URL_PATTERN = "/openmrs/ws/rest/v1/surgicalBlock/{uuid}?v=full";
    private AtomFeedSpringTransactionManager atomFeedSpringTransactionManager;
    private EventService eventService;

    public SurgicalBlockAdvice() throws SQLException {
        atomFeedSpringTransactionManager = new AtomFeedSpringTransactionManager(getSpringPlatformTransactionManager());
        AllEventRecordsQueue allEventRecordsQueue = new AllEventRecordsQueueJdbcImpl(atomFeedSpringTransactionManager);

        this.eventService = new EventServiceImpl(allEventRecordsQueue);
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] arguments, Object target) throws Throwable {
        if (shouldRaiseRelationshipEvent() && SAVE_PATIENT_SURGICAL_BLOCK_METHOD.equals(method.getName())) {
            String contents = getUrlPattern().replace("{uuid}", ((SurgicalBlock) returnValue).getUuid());
            final Event event = new Event(UUID.randomUUID().toString(), TITLE, DateTime.now(), (URI) null, contents, CATEGORY);

            atomFeedSpringTransactionManager.executeWithTransaction(
                    new AFTransactionWorkWithoutResult() {
                        @Override
                        protected void doInTransaction() {
                            eventService.notify(event);
                        }

                        @Override
                        public PropagationDefinition getTxPropagationDefinition() {
                            return PropagationDefinition.PROPAGATION_REQUIRED;
                        }
                    }
            );
        }
    }

    private boolean shouldRaiseRelationshipEvent() {
        String raiseEvent = Context.getAdministrationService().getGlobalProperty(RAISE_PATIENT_SURGICAL_BLOCK_EVENT_GLOBAL_PROPERTY);
        return Boolean.valueOf(raiseEvent);
    }

    private String getUrlPattern() {
        return Context.getAdministrationService().getGlobalProperty(SURGICAL_BLOCK_EVENT_URL_PATTERN_GLOBAL_PROPERTY, DEFAULT_SURGICAL_BLOCK_URL_PATTERN);
    }

    private PlatformTransactionManager getSpringPlatformTransactionManager() {
        return Context.getRegisteredComponents(PlatformTransactionManager.class).get(0);
    }

}
