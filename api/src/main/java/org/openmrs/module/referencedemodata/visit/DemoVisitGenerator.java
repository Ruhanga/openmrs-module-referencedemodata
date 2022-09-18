/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.referencedemodata.visit;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.referencedemodata.Randomizer;
import org.openmrs.module.referencedemodata.diagnosis.DemoDiagnosisGenerator;
import org.openmrs.module.referencedemodata.obs.DemoObsGenerator;
import org.openmrs.module.referencedemodata.orders.DemoOrderGenerator;
import org.openmrs.module.referencedemodata.providers.DemoProviderGenerator;

import static org.openmrs.module.referencedemodata.Randomizer.randomArrayEntry;
import static org.openmrs.module.referencedemodata.Randomizer.randomBetween;
import static org.openmrs.module.referencedemodata.Randomizer.randomSubArray;
import static org.openmrs.module.referencedemodata.Randomizer.shouldRandomEventOccur;
import static org.openmrs.module.referencedemodata.ReferenceDemoDataUtils.toDate;
import static org.openmrs.module.referencedemodata.ReferenceDemoDataUtils.toLocalDateTime;

@Slf4j
public class DemoVisitGenerator {
	
	private static final int ADMISSION_DAYS_MIN = 1;
	
	private static final int ADMISSION_DAYS_MAX = 5;
	
	private static final String[] NOTE_TEXT = {
			"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Vulputate enim nulla aliquet porttitor. Fermentum iaculis eu non diam phasellus vestibulum lorem sed. Orci ac auctor augue mauris augue neque. Fames ac turpis egestas sed tempus urna. Sit amet justo donec enim diam vulputate. Tortor aliquam nulla facilisi cras fermentum. Aliquet eget sit amet tellus. Elit ullamcorper dignissim cras tincidunt lobortis feugiat. Nisl tincidunt eget nullam non nisi est. Volutpat maecenas volutpat blandit aliquam etiam erat.",
			"Massa massa ultricies mi quis hendrerit. Consectetur libero id faucibus nisl tincidunt. Sed faucibus turpis in eu mi. Lobortis scelerisque fermentum dui faucibus in ornare. Scelerisque felis imperdiet proin fermentum. Faucibus vitae aliquet nec ullamcorper sit. Placerat in egestas erat imperdiet sed euismod nisi porta. Arcu felis bibendum ut tristique et egestas. Amet commodo nulla facilisi nullam vehicula ipsum a arcu. Viverra vitae congue eu consequat ac. Convallis a cras semper auctor neque vitae. Mauris a diam maecenas sed enim ut sem. Phasellus faucibus scelerisque eleifend donec. Quisque sagittis purus sit amet volutpat. Lorem dolor sed viverra ipsum nunc aliquet bibendum. Vitae congue eu consequat ac felis donec et. Purus sit amet volutpat consequat mauris nunc congue nisi vitae. Non quam lacus suspendisse faucibus. Tellus mauris a diam maecenas sed enim ut sem. Euismod in pellentesque massa placerat duis ultricies lacus.",
			"Maecenas pharetra convallis posuere morbi leo urna molestie at elementum. Ullamcorper velit sed ullamcorper morbi tincidunt ornare. Malesuada fames ac turpis egestas sed tempus urna et. Morbi leo urna molestie at elementum eu facilisis sed odio. Eu facilisis sed odio morbi quis commodo odio aenean. Faucibus a pellentesque sit amet porttitor eget dolor. Suscipit adipiscing bibendum est ultricies. Mattis ullamcorper velit sed ullamcorper morbi tincidunt ornare massa eget. Nulla posuere sollicitudin aliquam ultrices sagittis orci a scelerisque purus. Libero id faucibus nisl tincidunt eget nullam non nisi. Quis viverra nibh cras pulvinar mattis nunc sed blandit libero. Commodo quis imperdiet massa tincidunt nunc pulvinar sapien et ligula. Natoque penatibus et magnis dis parturient. Fames ac turpis egestas sed tempus urna et pharetra pharetra. Sem nulla pharetra diam sit amet nisl suscipit. Felis bibendum ut tristique et egestas quis.",
			"Eget aliquet nibh praesent tristique. Lectus arcu bibendum at varius vel. Eget duis at tellus at urna condimentum mattis pellentesque. Quisque egestas diam in arcu cursus euismod quis viverra. Tellus orci ac auctor augue mauris augue. Id nibh tortor id aliquet lectus proin. Et ultrices neque ornare aenean euismod elementum nisi quis eleifend. Morbi leo urna molestie at elementum eu. Mauris ultrices eros in cursus turpis massa tincidunt. Arcu risus quis varius quam quisque id diam vel.\n\nScelerisque eu ultrices vitae auctor eu augue. Sem integer vitae justo eget magna fermentum iaculis eu. Diam volutpat commodo sed egestas egestas. Sit amet facilisis magna etiam tempor orci eu lobortis. Nam aliquam sem et tortor consequat. Id cursus metus aliquam eleifend mi in nulla posuere sollicitudin. Tempus quam pellentesque nec nam aliquam sem. Odio eu feugiat pretium nibh. Elementum eu facilisis sed odio morbi quis commodo odio aenean. Sagittis orci a scelerisque purus semper eget. Duis ut diam quam nulla porttitor. Vitae auctor eu augue ut lectus. Tincidunt nunc pulvinar sapien et. Est pellentesque elit ullamcorper dignissim. Etiam non quam lacus suspendisse faucibus interdum posuere lorem ipsum. Tristique et egestas quis ipsum suspendisse. Egestas fringilla phasellus faucibus scelerisque eleifend. Sollicitudin aliquam ultrices sagittis orci a scelerisque. Semper risus in hendrerit gravida.",
			"Blandit massa enim nec dui nunc mattis enim. Faucibus turpis in eu mi bibendum neque egestas congue quisque. Elementum nisi quis eleifend quam adipiscing vitae proin sagittis nisl. Iaculis urna id volutpat lacus laoreet. Diam vulputate ut pharetra sit amet aliquam id. Non quam lacus suspendisse faucibus interdum posuere lorem. Facilisis leo vel fringilla est. Porta nibh venenatis cras sed. Auctor eu augue ut lectus arcu. Ipsum faucibus vitae aliquet nec. Scelerisque felis imperdiet proin fermentum leo.\n\nMollis aliquam ut porttitor leo a diam sollicitudin. Non pulvinar neque laoreet suspendisse interdum consectetur libero. Tellus at urna condimentum mattis pellentesque id nibh. Libero volutpat sed cras ornare arcu dui vivamus. Est placerat in egestas erat imperdiet. Donec ultrices tincidunt arcu non sodales neque sodales. Nibh mauris cursus mattis molestie a iaculis at. Donec adipiscing tristique risus nec feugiat in fermentum. Mollis aliquam ut porttitor leo a diam sollicitudin. Eget nullam non nisi est sit amet. Felis eget nunc lobortis mattis aliquam faucibus purus in massa. Volutpat est velit egestas dui id. Velit euismod in pellentesque massa placerat. Leo integer malesuada nunc vel risus. Auctor elit sed vulputate mi sit amet mauris commodo. Auctor urna nunc id cursus metus aliquam. Enim sit amet venenatis urna cursus eget nunc scelerisque viverra. Amet luctus venenatis lectus magna fringilla urna. Vestibulum lorem sed risus ultricies tristique nulla aliquet enim. Dolor sit amet consectetur adipiscing elit duis tristique sollicitudin.",
			"Gravida in fermentum et sollicitudin. Nibh ipsum consequat nisl vel pretium lectus quam.",
			"Faucibus et molestie ac feugiat sed lectus. Condimentum lacinia quis vel eros donec ac. Urna porttitor rhoncus dolor purus.",
			"Est sit amet facilisis magna etiam tempor. Sed vulputate odio ut enim blandit volutpat. Eget est lorem ipsum dolor sit amet. Arcu vitae elementum curabitur vitae nunc sed velit. Venenatis cras sed felis eget velit.",
			"Vel turpis nunc eget lorem dolor sed. Cras semper auctor neque vitae. Mauris in aliquam sem fringilla ut morbi tincidunt augue interdum. Lectus proin nibh nisl condimentum id venenatis a condimentum vitae. Justo donec enim diam vulputate ut pharetra. Et ultrices neque ornare aenean euismod. Lobortis feugiat vivamus at augue eget. Et malesuada fames ac turpis egestas maecenas. Massa enim nec dui nunc mattis enim ut. Sit amet purus gravida quis blandit. Pellentesque pulvinar pellentesque habitant morbi tristique senectus. Turpis egestas sed tempus urna et pharetra. Nibh tortor id aliquet lectus proin nibh. Sed adipiscing diam donec adipiscing. Tortor condimentum lacinia quis vel eros donec ac odio. Accumsan in nisl nisi scelerisque eu ultrices vitae auctor eu. Consectetur adipiscing elit duis tristique sollicitudin nibh sit amet commodo. Nibh cras pulvinar mattis nunc. Mattis vulputate enim nulla aliquet porttitor lacus luctus. Et ligula ullamcorper malesuada proin libero nunc consequat.",
			"Montes nascetur ridiculus mus mauris vitae. Proin sagittis nisl rhoncus mattis. Pulvinar pellentesque habitant morbi tristique senectus et netus et malesuada. Urna cursus eget nunc scelerisque viverra mauris in aliquam sem.",
			"Neque convallis a cras semper auctor neque vitae. Proin nibh nisl condimentum id venenatis a condimentum vitae. Viverra accumsan in nisl nisi scelerisque eu ultrices vitae. Ac tincidunt vitae semper quis lectus nulla. ",
			"Ut tortor pretium viverra suspendisse. Elit ut aliquam purus sit amet luctus. Feugiat in ante metus dictum at tempor. Cursus sit amet dictum sit amet justo donec enim. Sed libero enim sed faucibus turpis in eu. Tincidunt arcu non sodales neque sodales ut etiam sit amet. Molestie nunc non blandit massa enim nec dui nunc. Massa massa ultricies mi quis hendrerit dolor magna eget. In massa tempor nec feugiat nisl pretium fusce id. Mi tempus imperdiet nulla malesuada pellentesque elit eget. Sit amet risus nullam eget felis eget nunc lobortis mattis. Scelerisque fermentum dui faucibus in ornare quam viverra orci sagittis. In mollis nunc sed id semper risus in hendrerit gravida. Sed odio morbi quis commodo odio. Enim nulla aliquet porttitor lacus. Pulvinar neque laoreet suspendisse interdum consectetur libero. Consectetur libero id faucibus nisl tincidunt. Ipsum consequat nisl vel pretium lectus quam id leo in. Suspendisse potenti nullam ac tortor vitae purus faucibus ornare suspendisse. Vitae sapien pellentesque habitant morbi tristique.\n\nFermentum leo vel orci porta non. Aliquet porttitor lacus luctus accumsan tortor posuere ac. Diam volutpat commodo sed egestas egestas fringilla phasellus. Netus et malesuada fames ac. Tristique senectus et netus et malesuada fames ac turpis egestas. Mattis ullamcorper velit sed ullamcorper morbi tincidunt ornare massa eget. Tortor dignissim convallis aenean et. Interdum posuere lorem ipsum dolor sit amet consectetur adipiscing. Diam vulputate ut pharetra sit amet aliquam id. Lectus arcu bibendum at varius vel pharetra. Vitae ultricies leo integer malesuada nunc vel risus commodo viverra. Pellentesque sit amet porttitor eget dolor morbi non arcu.\n\nPosuere morbi leo urna molestie. In fermentum posuere urna nec tincidunt praesent semper. Condimentum id venenatis a condimentum vitae sapien pellentesque. Etiam erat velit scelerisque in dictum non consectetur a. Urna cursus eget nunc scelerisque viverra. Congue mauris rhoncus aenean vel elit scelerisque mauris. Justo eget magna fermentum iaculis eu non. Leo vel orci porta non. Nulla pellentesque dignissim enim sit amet venenatis. Fringilla phasellus faucibus scelerisque eleifend donec pretium vulputate. Vitae suscipit tellus mauris a. Ante metus dictum at tempor commodo ullamcorper. Sed arcu non odio euismod. Facilisi etiam dignissim diam quis enim lobortis scelerisque fermentum dui. Pellentesque habitant morbi tristique senectus et netus et. Pulvinar elementum integer enim neque volutpat ac. Diam sollicitudin tempor id eu nisl nunc mi ipsum."
	};
	
	private static final String[] COVID_SYMPTOM_CODES = {
			"30e2da8f-34ca-4c93-94c8-d429f22d381c",
			"1e2918fb-4e15-11e4-8a57-0800271c1b75",
			"2395de62-f5a6-49b6-ab0f-57a21a9029c1",
			"140238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
			"2c75944e-5fc5-4d9d-b389-fe878fde38e3",
			"21835173-4e15-11e4-8a57-0800271c1b75",
			"40829791-1535-4c02-9b6f-e97dd1a516f6",
			"c1fb49bd-0ca5-43ae-bf8d-df35e73c34ea",
			"87b3f6a1-6d79-4923-9485-200dfd937782",
			"64fb3f91-08d8-4a3b-92ba-16655e834b1c",
			"2134b7e3-4e15-11e4-8a57-0800271c1b75"
	};
	
	private static final String[] COVID_TESTS = {
			"efe34fae-de1e-4430-86ac-3198d0fb4a70",
			"7149a26d-c0a7-42c4-86ae-099616830831",
			"c1cc178c-390c-4194-b286-f9c57e0f313d"
	};
	
	private final DemoProviderGenerator providerGenerator;
	
	private final DemoObsGenerator obsGenerator;
	
	private final DemoOrderGenerator orderGenerator;
	
	private final DemoDiagnosisGenerator diagnosisGenerator;
	
	private FormService fs = null;
	
	private EncounterService es = null;
	
	private VisitService vs = null;
	
	private EncounterRole clinicianRole = null;
	
	private Form visitNoteForm = null;
	
	private Form covidForm = null;
	
	public DemoVisitGenerator(DemoProviderGenerator providerGenerator, DemoObsGenerator obsGenerator,
			DemoOrderGenerator orderGenerator, DemoDiagnosisGenerator diagnosisGenerator) {
		this.providerGenerator = providerGenerator;
		this.obsGenerator = obsGenerator;
		this.orderGenerator = orderGenerator;
		this.diagnosisGenerator = diagnosisGenerator;
	}
	
	public Visit createDemoVisit(Patient patient, List<VisitType> visitTypes, Location location, boolean shortVisit,
			Visit lastVisit, int remainingVisits) {
		int patientAge = Optional.ofNullable(patient.getAge()).orElse(2);
		
		LocalDateTime visitStart;
		if (lastVisit == null || lastVisit.getStopDatetime() == null) {
			int maxYears = patientAge <= 5 ? Math.max(patientAge - 1, 1) : Math.max(5, remainingVisits);
			visitStart = LocalDateTime.now().minusDays(randomBetween(0, 365 * maxYears - 1))
					.minusMinutes(randomBetween(0, 24 * 60 - 1));
			
			if (remainingVisits > 0) {
				LocalDateTime minimumStartDate = LocalDateTime.now().minusYears(remainingVisits);
				
				if (visitStart.isAfter(minimumStartDate)) {
					visitStart = minimumStartDate.plusMinutes(randomBetween(-(24 * 60 - 1), 24 * 60 - 1));
				}
			} else {
				// if this is the only patient's only visit and the visit falls on or after today, ensure that the
				// visit is done today
				LocalDate visitStartDate = visitStart.toLocalDate();
				LocalDate today = LocalDate.now();
				if (visitStartDate.isEqual(today) || visitStartDate.isAfter(today)) {
					visitStart = LocalDateTime.now().minusMinutes(randomBetween(5 * 60, 24 * 60));
				}
			}
		} else {
			visitStart = toLocalDateTime(lastVisit.getStopDatetime())
					.plusDays(randomBetween(0, 365 - 1))
					.plusMinutes(randomBetween(-(24 * 60 - 1), 24 * 60 - 1));
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (visitStart.isAfter(LocalDateTime.now())) {
			visitStart = now.minusMinutes(randomBetween(5 * 60, 24 * 60));
		}
		
		if (patientAge <= 5) {
			shortVisit = true;
		}
		
		// just in case the start is today, back it up a few days.
		if (!shortVisit && LocalDate.now().minusDays(ADMISSION_DAYS_MAX + 1).isBefore(visitStart.toLocalDate())) {
			visitStart = visitStart.minusDays(ADMISSION_DAYS_MAX + 1);
		}
		
		Visit visit = new Visit(patient, Randomizer.randomListEntry(visitTypes), toDate(visitStart));
		visit.setLocation(location);
		
		Provider visitProvider = providerGenerator.getRandomClinician();
		
		LocalDateTime lastEncounterTime;
		
		int vitalsStartMinutes = randomBetween(0, 10);
		lastEncounterTime = visitStart.plusMinutes(vitalsStartMinutes);
		visit.addEncounter(createDemoVitalsEncounter(patient, toDate(lastEncounterTime), visitProvider));
		
		lastEncounterTime = lastEncounterTime.plusMinutes(randomBetween(1, 20));
		visit.addEncounter(createVisitNote(patient, toDate(lastEncounterTime), location, visitProvider));
		
		if (shortVisit) {
			int largestTimeDelta = 0;
			// roughly 2/3rds of "short" visits will have lab results
			if (shouldRandomEventOccur(.67)) {
				int labStartMinutes = randomBetween(1, 20);
				largestTimeDelta = labStartMinutes;
				
				Encounter labEncounter = createDemoLabsEncounter(patient,
						toDate(lastEncounterTime.plusMinutes(labStartMinutes)), location);
				visit.addEncounter(labEncounter);
				visit.addEncounter(
						createDemoLabOrdersEncounter(labEncounter, Duration.ofMinutes(Math.floorDiv(labStartMinutes, 2)),
								visitProvider));
			}
			
			// roughly 2/3rds of "short" visits will also have a COVID form
			if (shouldRandomEventOccur(.67)) {
				int formStartMinutes = randomBetween(1, 20);
				if (formStartMinutes > largestTimeDelta) {
					largestTimeDelta = formStartMinutes;
				}
				
				visit.addEncounter(
						createCovidForm(patient, toDate(lastEncounterTime.plusMinutes(formStartMinutes)), location,
								visitProvider));
			}
			
			lastEncounterTime = lastEncounterTime.plusMinutes(largestTimeDelta);
			
			LocalDateTime visitEndTime = lastEncounterTime.plusMinutes(randomBetween(0, 20));
			visit.setStopDatetime(toDate(visitEndTime));
		} else {
			// admit now and discharge a few days later
			Location admitLocation = Context.getLocationService().getLocation("Inpatient Ward");
			LocalDateTime admitTime = lastEncounterTime;
			{
				Encounter encounter = createEncounter("Admission", patient, toDate(admitTime), admitLocation, visitProvider);
				getEncounterService().saveEncounter(encounter);
				visit.addEncounter(encounter);
			}
			
			LocalDateTime dischargeTime = admitTime
					.plusDays(randomBetween(ADMISSION_DAYS_MIN, ADMISSION_DAYS_MAX))
					.plusMinutes(randomBetween(-12 * 30, 12 * 60));
			
			for (int i = 0; i < Duration.between(admitTime, dischargeTime).toDays(); i++) {
				lastEncounterTime = lastEncounterTime.plusDays(1)
						.plusMinutes(randomBetween(-12 * 60, 12 * 60));
				
				if (lastEncounterTime.isAfter(dischargeTime.minusHours(4))) {
					lastEncounterTime = dischargeTime.minusHours(4).minusMinutes(randomBetween(5, 60 * 12));
				}
				
				visit.addEncounter(
						createDemoVitalsEncounter(patient, toDate(lastEncounterTime), admitLocation, visitProvider));
				
				lastEncounterTime = lastEncounterTime.plusMinutes(randomBetween(0, 120));
				visit.addEncounter(createVisitNote(patient, toDate(lastEncounterTime), admitLocation, visitProvider));
				
				int labStartMinutes = randomBetween(0, 120);
				lastEncounterTime = lastEncounterTime.plusMinutes(labStartMinutes);
				
				Encounter labEncounter = createDemoLabsEncounter(patient, toDate(lastEncounterTime), location);
				visit.addEncounter(labEncounter);
				visit.addEncounter(
						createDemoLabOrdersEncounter(labEncounter, Duration.ofMinutes(Math.floorDiv(labStartMinutes, 2)),
								visitProvider));
			}
			
			{
				Encounter encounter = createEncounter("Discharge", patient, toDate(dischargeTime), admitLocation,
						visitProvider);
				getEncounterService().saveEncounter(encounter);
				visit.addEncounter(encounter);
			}
			visit.setStopDatetime(toDate(dischargeTime));
		}
		
		visit = getVisitService().saveVisit(visit);
		
		log.info("Created a visit with {} encounters for patient {} starting on {} and ending on {}",
				new Object[] { visit.getNonVoidedEncounters().size(), patient.getPatientIdentifier(), visit.getStartDatetime(), visit.getStopDatetime() });
		
		return visit;
	}
	
	protected Encounter createVisitNote(Patient patient, Date encounterTime, Location location, Provider provider) {
		Encounter visitNoteEncounter = createEncounter("Visit Note", patient, encounterTime, location, provider);
		visitNoteEncounter.setForm(getVisitNoteForm());
		getEncounterService().saveEncounter(visitNoteEncounter);
		
		obsGenerator.createTextObs("CIEL:162169", randomArrayEntry(NOTE_TEXT), patient, visitNoteEncounter, encounterTime,
				location);
		
		diagnosisGenerator.createDiagnosis(true, patient, visitNoteEncounter);
		
		// We generate up to 7 additional diagnosis, with exponentially decreasing likelihood adding another new diagnosis
		for (int i = 0; i < randomBetween(1, 7); i++) {
			if (shouldRandomEventOccur(Math.pow(.5, Math.pow(i + 1, 1d / (i + 1))))) {
				diagnosisGenerator.createDiagnosis(false, patient, visitNoteEncounter);
			}
		}
		
		return visitNoteEncounter;
	}
	
	protected Encounter createCovidForm(Patient patient, Date encounterTime, Location location, Provider provider) {
		Encounter covidFormEncounter = createEncounter("Consultation", patient, encounterTime, location, provider);
		covidFormEncounter.setForm(getCovidForm());
		getEncounterService().saveEncounter(covidFormEncounter);
		
		// symptoms
		// idea is at least one, probably two, lower chances of fewer
		// TODO This can probably be reduced to a simple calculation without the loop
		int numberOfCovidSymptoms = 1;
		for (int i = 0; i < randomBetween(1, COVID_SYMPTOM_CODES.length + 1); i++) {
			if (shouldRandomEventOccur(Math.pow(.8, Math.pow(i + 1, 1d / (i + 1))))) {
				numberOfCovidSymptoms++;
			}
		}
		String[] covidSymptoms = randomSubArray(COVID_SYMPTOM_CODES, numberOfCovidSymptoms);
		if (covidSymptoms != null && covidSymptoms.length > 0) {
			List<Obs> symptomObs = new ArrayList<>(covidSymptoms.length);
			for (String symptom : covidSymptoms) {
				symptomObs.add(obsGenerator.createCodedObs("0eaa21e0-5f69-41a9-9dea-942796590bbb", symptom, patient,
						covidFormEncounter, encounterTime, location));
			}
			
			covidFormEncounter.addObs(
					obsGenerator.createObsGroup("464d8752-45f8-4a25-8972-772cbc52efbe", patient, covidFormEncounter,
							encounterTime, location, symptomObs));
		}
		
		// tests
		List<Obs> covidTestObs = new ArrayList<>(3);
		// 80% of patients with symptoms are tested
		if (numberOfCovidSymptoms > 0 && shouldRandomEventOccur(.8)) {
			// test done indicator
			covidTestObs.add(obsGenerator.createCodedObs("89c5bc03-8ce2-40d8-a77d-20b5a62a1ca1",
					"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", patient, covidFormEncounter, encounterTime, location));
			
			// kind of test done
			covidTestObs.add(
					obsGenerator.createCodedObs("611190b4-2b83-42c5-bbb1-8d99bfaec9ed", randomArrayEntry(COVID_TESTS),
							patient, covidFormEncounter, encounterTime, location));
			
			// test result
			// 50% positive rate
			String testResult = shouldRandomEventOccur(.5) ?
					"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" :
					"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
			covidTestObs.add(obsGenerator.createCodedObs("192f67ac-247c-4e1d-966a-4c223e33d38b", testResult, patient,
					covidFormEncounter, encounterTime, location));
		} else {
			// otherwise, the patient wasn't tested
			covidTestObs.add(obsGenerator.createCodedObs("89c5bc03-8ce2-40d8-a77d-20b5a62a1ca1",
					"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", patient, covidFormEncounter, encounterTime, location));
		}
		
		covidFormEncounter.addObs(
				obsGenerator.createObsGroup("4c2a4b12-becc-4429-a94d-b78e06699d0f", patient, covidFormEncounter,
						encounterTime, location, covidTestObs));
		
		return getEncounterService().saveEncounter(covidFormEncounter);
	}
	
	protected Encounter createDemoVitalsEncounter(Patient patient, Date encounterTime, Provider provider) {
		Location location = Context.getLocationService().getLocation("Outpatient Clinic");
		return createDemoVitalsEncounter(patient, encounterTime, location, provider);
	}
	
	protected Encounter createDemoVitalsEncounter(Patient patient, Date encounterTime, Location location,
			Provider provider) {
		Encounter encounter = createEncounter("Vitals", patient, encounterTime, location, provider);
		getEncounterService().saveEncounter(encounter);
		obsGenerator.createDemoVitalsObs(patient, encounter, location);
		return encounter;
	}
	
	protected Encounter createDemoLabsEncounter(Patient patient, Date encounterTime, Location location) {
		Encounter encounter = createEncounter("Lab Results", patient, encounterTime, location,
				providerGenerator.getRandomLabTech());
		getEncounterService().saveEncounter(encounter);
		obsGenerator.createDemoLabObs(patient, encounter, location);
		return encounter;
	}
	
	protected Encounter createDemoLabOrdersEncounter(Encounter demoLabsEncounter, TemporalAmount orderBeforeResultDelta,
			Provider orderingProvider) {
		Encounter encounter = createEncounter("Order", demoLabsEncounter.getPatient(),
				toDate(toLocalDateTime(demoLabsEncounter.getEncounterDatetime()).minus(orderBeforeResultDelta)),
				demoLabsEncounter.getLocation(), orderingProvider);
		getEncounterService().saveEncounter(encounter);
		
		for (Obs obs : demoLabsEncounter.getAllObs()) {
			if (obs.getConcept() != null && obs.getConcept().getConceptClass().getName().equalsIgnoreCase("Test")) {
				orderGenerator.createDemoTestOrder(encounter, obs.getConcept());
			}
		}
		
		return encounter;
	}
	
	protected Encounter createEncounter(String encounterType, Patient patient, Date encounterTime, Location location,
			Provider provider) {
		Encounter encounter = new Encounter();
		encounter.setEncounterDatetime(encounterTime);
		encounter.setEncounterType(getEncounterService().getEncounterType(encounterType));
		encounter.setPatient(patient);
		encounter.setLocation(location);
		encounter.addProvider(getClinicianRole(), provider);
		return encounter;
	}
	
	protected EncounterRole getClinicianRole() {
		if (clinicianRole == null) {
			clinicianRole = getEncounterService().getEncounterRoleByName("Clinician");
		}
		
		return clinicianRole;
	}
	
	protected Form getVisitNoteForm() {
		if (visitNoteForm == null) {
			visitNoteForm = getFormService().getForm("Visit Note");
		}
		
		return visitNoteForm;
	}
	
	protected Form getCovidForm() {
		if (covidForm == null) {
			covidForm = getFormService().getForm("Covid 19");
		}
		
		return covidForm;
	}
	
	protected FormService getFormService() {
		if (fs == null) {
			fs = Context.getFormService();
		}
		
		return fs;
	}
	
	protected EncounterService getEncounterService() {
		if (es == null) {
			es = Context.getEncounterService();
		}
		
		return es;
	}
	
	protected VisitService getVisitService() {
		if (vs == null) {
			vs = Context.getVisitService();
		}
		return vs;
	}
}
