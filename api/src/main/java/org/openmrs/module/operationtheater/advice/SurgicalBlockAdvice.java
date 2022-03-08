package org.openmrs.module.operationtheater.advice;

import org.ict4h.atomfeed.server.service.Event;
import java.time.LocalDateTime;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

public class SurgicalBlockAdvice extends BaseAdvice {
	
	private static final String CATEGORY = "surgicalblock";
	
	private static final String TITLE = "Surgical Block";
	
	private static final String SAVE_PATIENT_SURGICAL_BLOCK_METHOD = "save";
	
	private static final String RAISE_PATIENT_SURGICAL_BLOCK_EVENT_GLOBAL_PROPERTY = "atomfeed.publish.eventsForSurgicalBlockChange";
	
	private static final String SURGICAL_BLOCK_EVENT_URL_PATTERN_GLOBAL_PROPERTY = "atomfeed.event.urlPatternForSurgicalBlock";
	
	private static final String DEFAULT_SURGICAL_BLOCK_URL_PATTERN = "/openmrs/ws/rest/v1/surgicalBlock/{uuid}?v=full";
	
	private final SurgicalAppointmentAdvice surgicalAppointmentAdvice;
	
	public SurgicalBlockAdvice() {
		super();
		surgicalAppointmentAdvice = new SurgicalAppointmentAdvice();
	}
	
	@Override
	public void afterReturning(Object returnValue, Method method, Object[] arguments, Object target) throws Throwable {
		if (shouldRaiseRelationshipEvent() && SAVE_PATIENT_SURGICAL_BLOCK_METHOD.equals(method.getName())) {
			String contents = getUrlPattern().replace("{uuid}", ((SurgicalBlock) returnValue).getUuid());
			notifyEvent(new Event(UUID.randomUUID().toString(), TITLE, LocalDateTime.now(), (URI) null, contents, CATEGORY));
			createEventsForAppointments(((SurgicalBlock) returnValue).getSurgicalAppointments(), method);
		}
	}
	
	private void createEventsForAppointments(Set<SurgicalAppointment> surgicalAppointments, Method saveMethod)
	        throws Throwable {
		// Hacky fix to create event for Surgical Appointment as we don't make different
		// call to create a new surgical appointment
		for (SurgicalAppointment surgicalAppointment : surgicalAppointments) {
			surgicalAppointmentAdvice.afterReturning(surgicalAppointment, saveMethod, null, null);
		}
	}
	
	private boolean shouldRaiseRelationshipEvent() {
		return shouldRaiseEvent(RAISE_PATIENT_SURGICAL_BLOCK_EVENT_GLOBAL_PROPERTY);
	}
	
	private String getUrlPattern() {
		return getUrlPattern(SURGICAL_BLOCK_EVENT_URL_PATTERN_GLOBAL_PROPERTY, DEFAULT_SURGICAL_BLOCK_URL_PATTERN);
	}
}
