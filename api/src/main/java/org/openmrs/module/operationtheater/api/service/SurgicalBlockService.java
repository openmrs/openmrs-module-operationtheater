package org.openmrs.module.operationtheater.api.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public interface SurgicalBlockService extends OpenmrsService {
	
	@Authorized({ "Manage OT Schedules" })
	SurgicalBlock save(SurgicalBlock surgicalBlock);
	
	@Authorized({ "View OT Schedules" })
	SurgicalBlock getSurgicalBlockWithAppointments(String surgicalBlockUuid);
	
	@Authorized({ "View OT Schedules" })
	List<SurgicalBlock> getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(Date startDatetime, Date endDatetime,
	        Boolean includeVoided);
	
	void validateSurgicalBlock(SurgicalBlock surgicalBlock);
}
