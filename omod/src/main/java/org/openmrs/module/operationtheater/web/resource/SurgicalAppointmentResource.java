package org.openmrs.module.operationtheater.web.resource;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.StringProperty;
import io.swagger.models.properties.UUIDProperty;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.validation.ValidateUtil;

import javax.xml.bind.SchemaOutputResolver;
import java.util.List;
import java.util.Set;

@Resource(name = RestConstants.VERSION_1
        + "/surgicalAppointment", supportedClass = SurgicalAppointment.class, supportedOpenmrsVersions = { "2.0.*",
                "2.1.*" })
public class SurgicalAppointmentResource extends DataDelegatingCrudResource<SurgicalAppointment> {
	
	@Override
	public SurgicalAppointment getByUniqueId(String surgicalAppointmentUuid) {
		return Context.getService(SurgicalAppointmentService.class).getSurgicalAppointmentByUuid(surgicalAppointmentUuid);
	}
	
	@Override
	protected void delete(SurgicalAppointment surgicalAppointment, String s, RequestContext requestContext)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Delete not supported on SurgicalAppointment resource");
	}
	
	@Override
	public SurgicalAppointment newDelegate() {
		return new SurgicalAppointment();
	}
	
	@Override
	public SurgicalAppointment save(SurgicalAppointment surgicalAppointment) {
		return Context.getService(SurgicalAppointmentService.class).save(surgicalAppointment);
	}
	
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		SurgicalAppointment surgicalAppointment = this.getByUniqueId(uuid);
		SurgicalAppointment cloneOfSurgicalAppointment = new SurgicalAppointment(surgicalAppointment);
		this.setConvertedProperties(cloneOfSurgicalAppointment, propertiesToUpdate, this.getUpdatableProperties(), false);
		Context.getService(SurgicalAppointmentService.class).validateSurgicalAppointment(cloneOfSurgicalAppointment);
		this.setConvertedProperties(surgicalAppointment, propertiesToUpdate, this.getUpdatableProperties(), false);
		ValidateUtil.validate(surgicalAppointment);
		surgicalAppointment = this.save(surgicalAppointment);
		return ConversionUtil.convertToRepresentation(surgicalAppointment, Representation.DEFAULT);
		
	}
	
	@Override
	public void purge(SurgicalAppointment surgicalAppointment, RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Purge not supported on SurgicalAppointment resource");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		if ((representation instanceof DefaultRepresentation) || (representation instanceof RefRepresentation)) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("id");
			description.addProperty("uuid");
			description.addProperty("patient", Representation.DEFAULT);
			description.addProperty("actualStartDatetime");
			description.addProperty("actualEndDatetime");
			description.addProperty("status");
			description.addProperty("notes");
			description.addProperty("sortWeight");
			description.addProperty("bedNumber");
			description.addProperty("bedLocation");
			description.addProperty("surgicalAppointmentAttributes");
			return description;
		}
		if ((representation instanceof FullRepresentation)) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("id");
			description.addProperty("uuid");
			description.addProperty("patient", Representation.FULL);
			description.addProperty("actualStartDatetime");
			description.addProperty("actualEndDatetime");
			description.addProperty("status");
			description.addProperty("notes");
			description.addProperty("sortWeight");
			description.addProperty("bedNumber");
			description.addProperty("bedLocation");
			description.addProperty("surgicalAppointmentAttributes");
			return description;
		}
		return null;
	}

	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl modelImpl = ((ModelImpl) super.getGETModel(rep));
		if ((rep instanceof DefaultRepresentation) || (rep instanceof RefRepresentation)) {
			modelImpl.property("id", new IntegerProperty()).property("uuid", new UUIDProperty())
					.property("patient", new StringProperty()).property("actualStartDateTime", new DateProperty())
					.property("actualEndDateTime", new DateProperty()).property("status", new StringProperty())
					.property("notes", new StringProperty()).property("sortWeight", new IntegerProperty())
					.property("bedNumber", new StringProperty()).property("bedLocation", new StringProperty())
					.property("surgicalAppointmentAttributes", new StringProperty());
		}
		if (rep instanceof FullRepresentation) {
			modelImpl.property("id", new IntegerProperty()).property("uuid", new UUIDProperty())
					.property("patient", new StringProperty()).property("actualStartDateTime", new DateProperty())
					.property("actualEndDateTime", new DateProperty()).property("status", new StringProperty())
					.property("notes", new StringProperty()).property("sortWeight", new IntegerProperty())
					.property("bedNumber", new StringProperty()).property("bedLocation", new StringProperty())
					.property("surgicalAppointmentAttributes", new StringProperty());
		}
		return modelImpl;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		delegatingResourceDescription.addProperty("id");
		delegatingResourceDescription.addProperty("uuid");
		delegatingResourceDescription.addRequiredProperty("patient");
		delegatingResourceDescription.addRequiredProperty("surgicalBlock");
		delegatingResourceDescription.addProperty("actualStartDatetime");
		delegatingResourceDescription.addProperty("actualEndDatetime");
		delegatingResourceDescription.addProperty("status");
		delegatingResourceDescription.addProperty("notes");
		delegatingResourceDescription.addProperty("sortWeight");
		delegatingResourceDescription.addProperty("surgicalAppointmentAttributes");
		return delegatingResourceDescription;
	}

	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl().property("id", new IntegerProperty()).property("uuid", new UUIDProperty())
				.property("patient", new StringProperty()).property("actualStartDateTime", new DateProperty())
				.property("actualEndDateTime", new DateProperty()).property("status", new StringProperty())
				.property("notes", new StringProperty()).property("sortWeight", new IntegerProperty())
				.property("surgicalBlock", new StringProperty())
				.property("surgicalAppointmentAttributes", new StringProperty());
	}
	
	@PropertyGetter("surgicalAppointmentAttributes")
	public static List<SurgicalAppointmentAttribute> getAttributes(SurgicalAppointment instance) {
		return instance.getActiveAttributes();
	}
	
	@PropertyGetter("bedNumber")
	public static String getBedNumber(SurgicalAppointment surgicalAppointment) {
		BedDetails bedDetails = Context.getService(BedManagementService.class)
		        .getBedAssignmentDetailsByPatient(surgicalAppointment.getPatient());
		if (bedDetails == null) {
			return null;
		}
		return bedDetails.getBedNumber();
	}
	
	@PropertyGetter("bedLocation")
	public static String getBedLocation(SurgicalAppointment surgicalAppointment) {
		Patient patient = surgicalAppointment.getPatient();
		BedDetails bedDetails = Context.getService(BedManagementService.class).getBedAssignmentDetailsByPatient(patient);
		if (bedDetails == null) {
			return null;
		}
		return bedDetails.getPhysicalLocation().getName();
	}
	
	@PropertySetter("surgicalAppointmentAttributes")
	public static void setAttributes(SurgicalAppointment surgicalAppointment, Set<SurgicalAppointmentAttribute> attrs) {
		for (SurgicalAppointmentAttribute attr : attrs) {
			attr.setSurgicalAppointment(surgicalAppointment);
		}
		surgicalAppointment.setSurgicalAppointmentAttributes(attrs);
	}
}
