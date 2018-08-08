package org.openmrs.module.operationtheater.advice;

import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.net.URI;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, SurgicalAppointmentAdvice.class })
public class SurgicalAppointmentAdviceTest {
	
	private static final String URL_PATTERN = "atomfeed.event.urlPatternForSurgicalAppointment";
	
	private static final String EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE = "atomfeed.publish.eventsForSurgicalAppointmentChange";
	
	private static final String UUID = "5631b434-78aa-102b-91a0-001e378eb17e";
	
	private static final String DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN = "/openmrs/ws/rest/v1/surgicalAppointment/{uuid}?v=full";
	
	@Mock
	private SurgicalAppointment surgicalAppointment;
	
	@Mock
	private PlatformTransactionManager transactionManager;
	
	@Mock
	private AdministrationService administrationService;
	
	@Mock
	private AllEventRecordsQueueJdbcImpl allEventRecordsQueue;
	
	@Mock
	private Event event;
	
	@Mock
	private EventServiceImpl eventService;
	
	private SurgicalAppointmentAdvice surgicalAppointmentAdvice;
	
	private AtomFeedSpringTransactionManager atomFeedSpringTransactionManager;
	
	@Before
	public void setUp() throws Exception {
		mockStatic(Context.class);
		when(Context.getRegisteredComponents(any())).thenReturn(Collections.singletonList(transactionManager));
		when(Context.getAdministrationService()).thenReturn(administrationService);
		
		atomFeedSpringTransactionManager = spy(new AtomFeedSpringTransactionManager(transactionManager));
		
		whenNew(AtomFeedSpringTransactionManager.class).withAnyArguments().thenReturn(atomFeedSpringTransactionManager);
		
		when(surgicalAppointment.getUuid()).thenReturn(UUID);
		whenNew(AllEventRecordsQueueJdbcImpl.class).withArguments(this.atomFeedSpringTransactionManager)
		        .thenReturn(allEventRecordsQueue);
		whenNew(EventServiceImpl.class).withArguments(allEventRecordsQueue).thenReturn(eventService);
		whenNew(Event.class).withAnyArguments().thenReturn(event);
		when(administrationService.getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE)).thenReturn("true");
		when(administrationService.getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN))
		        .thenReturn(DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
		
		doNothing().when(eventService).notify(any());
		surgicalAppointmentAdvice = new SurgicalAppointmentAdvice();
	}
	
	@Test
	public void shouldRaiseSurgicalAppointmentChangeEventToEventRecordsTable() throws Throwable {
		
		surgicalAppointmentAdvice.afterReturning(surgicalAppointment, this.getClass().getMethod("save"), null, null);
		
		verify(atomFeedSpringTransactionManager, times(1)).executeWithTransaction(any(AFTransactionWorkWithoutResult.class));
		verify(administrationService, times(1)).getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE);
		verify(administrationService, times(1)).getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
		verify(eventService, times(1)).notify(any());
		verifyNew(Event.class, times(1)).withArguments(anyString(), eq("Surgical Appointment"), any(Date.class),
		    any(URI.class), eq(String.format("/openmrs/ws/rest/v1/surgicalAppointment/%s?v=full", UUID)),
		    eq("surgicalappointment"));
	}
	
	@Test
	public void shouldRaiseSurgicalAppointmentChangeEventToEventRecordsTableWithCustomUrlPattern() throws Throwable {
		when(administrationService.getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN))
		        .thenReturn("/openmrs/ws/{uuid}");
		
		surgicalAppointmentAdvice.afterReturning(surgicalAppointment, this.getClass().getMethod("save"), null, null);
		
		verify(atomFeedSpringTransactionManager, times(1)).executeWithTransaction(any(AFTransactionWorkWithoutResult.class));
		verify(administrationService, times(1)).getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE);
		verify(administrationService, times(1)).getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
		verify(eventService, times(1)).notify(any());
		verifyNew(Event.class, times(1)).withArguments(anyString(), eq("Surgical Appointment"), any(Date.class),
		    any(URI.class), eq(String.format("/openmrs/ws/%s", UUID)), eq("surgicalappointment"));
	}
	
	@Test
	public void shouldNotRaiseEventToEventRecordTableIfGlobalPropertyIsDisabled() throws Throwable {
		when(administrationService.getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE)).thenReturn("false");
		
		surgicalAppointmentAdvice.afterReturning(surgicalAppointment, this.getClass().getMethod("save"), null, null);
		
		verify(atomFeedSpringTransactionManager, never()).executeWithTransaction(any());
		verify(administrationService, times(1)).getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE);
		verify(administrationService, never()).getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
	}
	
	@Test
	public void shouldNotRaiseEventToEventRecordTableIfMethodNameIsNotSave() throws Throwable {
		surgicalAppointmentAdvice.afterReturning(surgicalAppointment, this.getClass().getMethod("temp"), null, null);
		
		verify(atomFeedSpringTransactionManager, never()).executeWithTransaction(any());
		verify(administrationService, times(1)).getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE);
		verify(administrationService, never()).getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
	}
	
	// As Mockito can't mock reflection methods, we need these 2 empty method
	public void save() {
	}
	
	public void temp() {
	}
}
