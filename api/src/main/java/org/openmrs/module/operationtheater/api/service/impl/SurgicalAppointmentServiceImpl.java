package org.openmrs.module.operationtheater.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.operationtheater.api.dao.SurgicalAppointmentDao;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public class SurgicalAppointmentServiceImpl extends BaseOpenmrsService implements SurgicalAppointmentService {

	private static final Log log = LogFactory.getLog(SurgicalAppointmentServiceImpl.class);

	static final String SURGERY_ORDER_TYPE_NAME = "Surgery Order";

	static final String SURGICAL_ORDER_CONCEPT_NAME = "General Surgical Procedure";

	private SurgicalAppointmentDao surgicalAppointmentDao;

	private SurgicalBlockDAO surgicalBlockDao;

	private OrderService orderService;

	private ConceptService conceptService;

	public void setSurgicalAppointmentDao(SurgicalAppointmentDao surgicalAppointmentDao) {
		this.surgicalAppointmentDao = surgicalAppointmentDao;
	}

	public void setSurgicalBlockDao(SurgicalBlockDAO surgicalBlockDao) {
		this.surgicalBlockDao = surgicalBlockDao;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Override
	@Transactional
	public SurgicalAppointment save(SurgicalAppointment surgicalAppointment) {
		validateSurgicalAppointment(surgicalAppointment);
		validatePatientForConflictingSurgicalAppointment(surgicalAppointment);
		if (surgicalAppointment.getId() == null) {
			Order surgeryOrder = createSurgeryOrder(surgicalAppointment);
			if (surgeryOrder != null) {
				surgicalAppointment.setOrder(surgeryOrder);
			}
		}
		return surgicalAppointmentDao.save(surgicalAppointment);
	}
	
	@Override
	public void validateSurgicalAppointment(SurgicalAppointment surgicalAppointment) {
		List<SurgicalAppointment> overlappingSurgicalAppointments = surgicalAppointmentDao
		        .getOverlappingActualTimeEntriesForAppointment(surgicalAppointment);
		if (overlappingSurgicalAppointments.size() > 0) {
			throw new ValidationException(
			        "Surgical Appointment has conflicting actual time with existing appointments in this OT");
		}
	}
	
	@Override
	@Transactional
	public SurgicalAppointment getSurgicalAppointmentByUuid(String uuid) {
		return surgicalAppointmentDao.getSurgicalAppointmentByUuid(uuid);
	}
	
	@Override
	@Transactional
	public SurgicalAppointmentAttribute getSurgicalAppointmentAttributeByUuid(String uuid) {
		return surgicalAppointmentDao.getSurgicalAppointmentAttributeByUuid(uuid);
	}
	
	private void validatePatientForConflictingSurgicalAppointment(SurgicalAppointment surgicalAppointment) {
		List<SurgicalAppointment> overlappingSurgicalAppointmentsForPatient = surgicalBlockDao
		        .getOverlappingSurgicalAppointmentsForPatient(surgicalAppointment.getSurgicalBlock().getStartDatetime(),
		            surgicalAppointment.getSurgicalBlock().getEndDatetime(), surgicalAppointment.getPatient(),
		            surgicalAppointment.getSurgicalBlock().getId());
		if (overlappingSurgicalAppointmentsForPatient.size() > 0) {
			SurgicalAppointment conflictingSurgicalAppointment = overlappingSurgicalAppointmentsForPatient.get(0);
			SurgicalBlock conflictingSurgicalBlock = conflictingSurgicalAppointment.getSurgicalBlock();
			throw new ValidationException(conflictingSurgicalAppointment.getPatient().getGivenName() + " "
			        + conflictingSurgicalAppointment.getPatient().getFamilyName() + " has conflicting appointment at "
			        + conflictingSurgicalBlock.getLocation().getDisplayString() + " with "
			        + conflictingSurgicalBlock.getProvider().getName());
		}
	}

	private Order createSurgeryOrder(SurgicalAppointment surgicalAppointment) {
		try {
			OrderType orderType = orderService.getOrderTypeByName(SURGERY_ORDER_TYPE_NAME);
			if (orderType == null) {
				log.warn("Surgery Order order type not found; skipping order creation for surgical appointment");
				return null;
			}
			Concept concept = conceptService.getConceptByName(SURGICAL_ORDER_CONCEPT_NAME);
			if (concept == null) {
				log.warn("Surgical Order concept not found; skipping order creation for surgical appointment");
				return null;
			}
			CareSetting careSetting = orderService.getCareSettingByName(CareSetting.CareSettingType.OUTPATIENT.toString());
			if (careSetting == null) {
				log.warn("OUTPATIENT care setting not found; skipping order creation for surgical appointment");
				return null;
			}
			SurgicalBlock block = surgicalAppointment.getSurgicalBlock();
			if (block == null || block.getProvider() == null) {
				log.warn("Surgical block or provider is null; skipping order creation for surgical appointment");
				return null;
			}
			Order order = new Order();
			order.setPatient(surgicalAppointment.getPatient());
			order.setOrderType(orderType);
			order.setConcept(concept);
			order.setCareSetting(careSetting);
			order.setOrderer(block.getProvider());
			order.setDateActivated(new Date());
			return orderService.saveOrder(order, null);
		}
		catch (Exception e) {
			log.error("Failed to create Surgery Order for surgical appointment; appointment will be saved without an order", e);
			return null;
		}
	}
}
