package org.openmrs.module.operationtheater.advice;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.UUID;

public class SurgicalAppointmentAdvice extends BaseAdvice {
	
	private static final String CATEGORY = "surgicalappointment";
	
	private static final String TITLE = "Surgical Appointment";
	
	private static final String SAVE_PATIENT_SURGICAL_APPOINTMENT_METHOD = "save";
	
	private static final String RAISE_PATIENT_SURGICAL_APPOINTMENT_EVENT_GLOBAL_PROPERTY = "atomfeed.publish.eventsForSurgicalAppointmentChange";
	
	private static final String SURGICAL_APPOINTMENT_EVENT_URL_PATTERN_GLOBAL_PROPERTY = "atomfeed.event.urlPatternForSurgicalAppointment";
	
	private static final String DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN = "/openmrs/ws/rest/v1/surgicalAppointment/{uuid}?v=full";
	
	@Override
	public void afterReturning(Object returnValue, Method method, Object[] arguments, Object target) throws Throwable {
		if (shouldRaiseEvent() && SAVE_PATIENT_SURGICAL_APPOINTMENT_METHOD.equals(method.getName())) {
			String contents = getUrlPattern().replace("{uuid}", ((SurgicalAppointment) returnValue).getUuid());
			notifyEvent(new Event(UUID.randomUUID().toString(), TITLE, DateTime.now(), (URI) null, contents, CATEGORY));
		}
	}
	
	private boolean shouldRaiseEvent() {
		return shouldRaiseEvent(RAISE_PATIENT_SURGICAL_APPOINTMENT_EVENT_GLOBAL_PROPERTY);
	}
	
	private String getUrlPattern() {
		return getUrlPattern(SURGICAL_APPOINTMENT_EVENT_URL_PATTERN_GLOBAL_PROPERTY,
		    DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
	}
}
