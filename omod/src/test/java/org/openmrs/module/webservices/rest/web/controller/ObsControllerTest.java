/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Assert;
import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

public class ObsControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private WebRequest emptyRequest() {
		return new ServletWebRequest(new MockHttpServletRequest());
	}
	
	private void log(String label, Object object) {
		String toPrint;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.getSerializationConfig().set(SerializationConfig.Feature.INDENT_OUTPUT, true);
			toPrint = mapper.writeValueAsString(object);
		}
		catch (Exception ex) {
			toPrint = "" + object;
		}
		if (label != null)
			toPrint = label + ": " + toPrint;
		System.out.println(toPrint);
	}
	
	/**
	 * @see ObsController#getObs(String,WebRequest)
	 * @verifies get a default representation of a obs
	 */
	
	@Test
	public void getObs_shouldGetADefaultRepresentationOfAObs() throws Exception {
		Object result = new ObsController().retrieve("39fb7f47-e80a-4056-9285-bd798be13c63", emptyRequest());
		Assert.assertNotNull(result);
		log("Obs fetched (default)", result);
		Assert.assertEquals("39fb7f47-e80a-4056-9285-bd798be13c63", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "uri"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "concept"));
		
	}
	
	/**
	 * @see ObsController#getObs(String,WebRequest)
	 * @verifies get a full representation of a obs
	 */
	@Test
	public void getObs_shouldGetAFullRepresentationOfAObs() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		Object result = new ObsController().retrieve("39fb7f47-e80a-4056-9285-bd798be13c63", new ServletWebRequest(req));
		Assert.assertNotNull(result);
		log("Obs fetched (default)", result);
		Assert.assertEquals("39fb7f47-e80a-4056-9285-bd798be13c63", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "uri"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "concept"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
		
	}
	
	/**
	 * @see ObsController#getObsByPatientId(String,WebRequest)
	 * @verifies get a default representation of all obs
	 */
	@Test
	public void getObsByPatientId_shouldGetADefaultRepresentationOfAllObs() throws Exception {
		List<Object> results = new ObsController().search("6TS-4", emptyRequest(), new MockHttpServletResponse());
		Assert.assertNotNull(results);
		Object result = results.get(8);
		Assert.assertEquals(9, results.size());
		Assert.assertNotNull(PropertyUtils.getProperty(result, "uri"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
		
	}
	
	/**
	 * @see ObsController#createObs(SimpleObject,WebRequest,HttpServletResponse)
	 * @verifies create a new obs with numeric concept
	 */
	@Test
	public void createObs_shouldCreateANewObsWithNumericConcept() throws Exception {
		List<Obs> observationsByPerson = Context.getObsService().getObservationsByPerson(
		    (Context.getPatientService().getPatient(7)));
		int before = observationsByPerson.size();
		String json = "{\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\",\"concept\":\"a09ab2c5-878e-4905-b25d-5784167d0216\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"obsDatetime\":\"2011-05-18\",\"value\":\"150.0\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		new ObsController().create(post, emptyRequest(), new MockHttpServletResponse());
		List<Obs> observationsByPersonAfterSave = Context.getObsService().getObservationsByPerson(
		    (Context.getPatientService().getPatient(7)));
		Assert.assertEquals(before + 1, observationsByPersonAfterSave.size());
		Obs newObs = observationsByPersonAfterSave.get(0);
		Assert.assertEquals(150.0, newObs.getValueNumeric());
	}
	
	/**
	 * @see ObsController#createObs(SimpleObject,WebRequest,HttpServletResponse)
	 * @verifies create a new obs with text concept
	 */
	@Test
	public void createObs_shouldCreateANewObsWithTextConcept() throws Exception {
		List<Obs> observationsByPerson = Context.getObsService().getObservationsByPerson(
		    (Context.getPatientService().getPatient(7)));
		int before = observationsByPerson.size();
		String json = "{\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\",\"concept\":\"96408258-000b-424e-af1a-403919332938\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"obsDatetime\":\"2011-05-18\",\"value\":\"high\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		new ObsController().create(post, emptyRequest(), new MockHttpServletResponse());
		List<Obs> observationsByPersonAfterSave = Context.getObsService().getObservationsByPerson(
		    (Context.getPatientService().getPatient(7)));
		Assert.assertEquals(before + 1, observationsByPersonAfterSave.size());
		Obs newObs = observationsByPersonAfterSave.get(0);
		Assert.assertEquals("high", newObs.getValueText());
	}
	
	/**
	 * @see ObsController#voidObs(String,String,WebRequest,HttpServletResponse)
	 * @verifies void a obs
	 */
	@Test
	public void voidObs_shouldVoidAObs() throws Exception {
		Obs obs = Context.getObsService().getObs(9);
		Assert.assertFalse(obs.isVoided());
		new ObsController().delete("be48cdcb-6a76-47e3-9f2e-2635032f3a9a", "unit test", emptyRequest(),
		    new MockHttpServletResponse());
		obs = Context.getObsService().getObs(9);
		Assert.assertTrue(obs.isVoided());
		Assert.assertEquals("unit test", obs.getVoidReason());
	}
	
	/**
	 * @see ObsController#updatePatient(String,SimpleObject,WebRequest,HttpServletResponse)
	 * @verifies change a property on an obs
	 */
	@Test
	public void updateObs_shouldChangeAPropertyOnAnObs() throws Exception {
		
		SimpleObject post = new ObjectMapper().readValue("{\"valueNumeric\": 35.0}", SimpleObject.class);
		Object editedObs = new ObsController().update("39fb7f47-e80a-4056-9285-bd798be13c63", post, emptyRequest(),
		    new MockHttpServletResponse());
		List<Obs> obsList = Context.getObsService().getObservationsByPerson(Context.getPersonService().getPerson(7));
		Obs newObs = obsList.get(obsList.size() - 1);
		Obs oldObs = Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63");
		Assert.assertTrue(Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63").isVoided());
		Assert.assertFalse(oldObs.getValueNumeric().equals(new Double("35.0")));
		Assert.assertTrue(newObs.getValueNumeric().equals(new Double("35.0")));
	}
	
	/**
	 * @see ObsController#updatePatient(String,SimpleObject,WebRequest,HttpServletResponse)
	 * @verifies change a complex property on an obs
	 */
	@Test
	public void updateObs_shouldChangeAComplexPropertyOnAnObs() throws Exception {
		
		String json = "{\"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object editedObs = new ObsController().update("39fb7f47-e80a-4056-9285-bd798be13c63", post, emptyRequest(),
		    new MockHttpServletResponse());
		Obs oldObs = Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63");
		List<Obs> obsList = Context.getObsService().getObservationsByPerson(Context.getPersonService().getPerson(7));
		Obs newObs = obsList.get(obsList.size() - 1);
		Assert.assertTrue(Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63").isVoided());
		Assert.assertFalse(new Integer(2).equals(oldObs.getLocation().getId()));
		Assert.assertTrue(new Integer(2).equals(newObs.getLocation().getId()));
		
	}
	
	/**
	 * @see ObsController#purgeObs(String,WebRequest,HttpServletResponse)
	 * @verifies fail to purge an obs with dependent data
	 */
	@Test
	@ExpectedException(APIException.class)
	public void purgeObs_shouldFailToPurgeAnObsWithDependentData() throws Exception {
		executeDataSet("org/openmrs/api/include/ObsServiceTest-complex.xml");
		new ObsController().purge("9b6639b2-5785-4603-a364-075c2d61cd51", emptyRequest(), new MockHttpServletResponse());
		
	}
	
	/**
	 * @see ObsController#purgeObs(String,WebRequest,HttpServletResponse)
	 * @verifies purge a simple obs
	 */
	@Test
	public void purgeObs_shouldPurgeASimpleObs() throws Exception {
		Assert.assertNotNull(Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63"));
		new ObsController().purge("39fb7f47-e80a-4056-9285-bd798be13c63", emptyRequest(), new MockHttpServletResponse());
		Assert.assertNull(Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63"));
		
	}
	
}
