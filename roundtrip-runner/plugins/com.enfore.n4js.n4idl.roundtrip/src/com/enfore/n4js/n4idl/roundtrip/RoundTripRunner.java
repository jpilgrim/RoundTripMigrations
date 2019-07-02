/*
 * Copyright (c) 2018 NumberFour AG, Luca Beurer-Kellner
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Luca Beurer-Kellner - Initial API and implementation
 */
package com.enfore.n4js.n4idl.roundtrip;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.n4js.generator.AbstractSubGenerator;
import org.eclipse.n4js.projectModel.IN4JSCore;
import org.eclipse.n4js.projectModel.IN4JSProject;
import org.eclipse.n4js.runner.IRunner;
import org.eclipse.n4js.runner.RunConfiguration;
import org.eclipse.n4js.runner.extension.IRunnerDescriptor;
import org.eclipse.n4js.runner.extension.RunnerDescriptorImpl;
import org.eclipse.n4js.runner.extension.RuntimeEnvironment;
import org.eclipse.n4js.runner.nodejs.NodeRunner;
import org.eclipse.n4js.ts.typeRefs.TypeRef;
import org.eclipse.n4js.ts.types.TClass;
import org.eclipse.n4js.ts.types.TInterface;
import org.eclipse.n4js.ts.types.TypesPackage;
import org.eclipse.n4js.ts.utils.TypeUtils;
import org.eclipse.n4js.typesystem.N4JSTypeSystem;
import org.eclipse.n4js.typesystem.utils.Result;
import org.eclipse.n4js.typesystem.utils.RuleEnvironment;
import org.eclipse.n4js.typesystem.utils.RuleEnvironmentExtensions;
import org.eclipse.n4js.utils.ResourceNameComputer;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.xbase.lib.Exceptions;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * A custom {@link IRunner} that executes classes that implement a round-trip
 * runner contract interface (cf.
 * {@link RoundTripRunnerConstants.RUNNER_CONTRACT_INTERFACE}) and allows to
 * visualize the round-trip execution of migrations.
 */
public class RoundTripRunner extends NodeRunner {
	/** Class Logger */
	private static final Logger LOGGER = Logger.getLogger(RoundTripRunner.class);

	/** ID of the RoundTrip runner as defined in the plugin.xml. */
	public static final String ID = "com.enfore.n4js.n4idl.roundtrip.ROUNDTRIP";

	/**
	 * Class for providing descriptors for the RoundTrip runner with the same information as defined in the plugin.xml
	 * (used only by N4jsc). Supports instance supplying with injection.
	 */
	public static final class RoundTripRunnerDescriptorProvider implements Provider<IRunnerDescriptor> {

		@Inject
		private Provider<RoundTripRunner> roundTripRunnerProvider;

		@Override
		public IRunnerDescriptor get() {
			return new RunnerDescriptorImpl(ID, "N4IDL RoundTrip Runner", RuntimeEnvironment.NODEJS, roundTripRunnerProvider.get());
		}

	}

	// round-trip runner specific environment variables
	private static final String ROUND_TRIP_MODULE_KEY = "ROUND_TRIP_MODULE";
	private static final String ROUND_TRIP_CLASS_NAME_KEY = "ROUND_TRIP_CLASS_NAME";
	
	private static final String ROUND_TRIP_RUNNER_PROJECT = "com.enfore.n4js.n4idl.roundtrip";
	
	/** Runtime path of the run.js module to execute round-trip migrations. */
	private static final String ROUND_TRIP_RUNNER_RT_PATH = "src-gen/com/enfore/n4js/n4idl/roundtrip/run";

	/** key in the $executionData dictionary that specifies the user selection. */
	private static final String EXECUTION_DATA_USER_SELECTION_KEY = "userSelection";

	@Inject
	private N4JSTypeSystem n4jsTypeSystem;

	@Inject
	private IN4JSCore n4jsCore;

	@Inject
	private ResourceNameComputer resourceNameComputer;

	/**
	 * Returns the {@link TInterface} instance of the N4JS contract interface in the given ResourceSet.
	 *
	 * Returns {@code null} if the interface can not be found.
	 */
	protected TInterface getContractInterface(ResourceSet resourceSet) {
		IResourceDescriptions index = n4jsCore.getXtextIndex(resourceSet);
		return firstDescriptionMatch(TInterface.class,
				index.getExportedObjects(TypesPackage.eINSTANCE.getTInterface(),
						RoundTripRunnerConstants.ROUND_TRIP_MIGRATION_FQN, false));
	}

	@Override
	public void prepareConfiguration(RunConfiguration config) {		
		// determine the exported name of the class in the module, that implements the contract interface
		final String roundTripClassName = findContractInterfaceClass(config.getUserSelection());

		if (null == roundTripClassName) {
			LOGGER.error("Failed to determine " + RoundTripRunnerConstants.RUNNER_CONTRACT_INTERFACE + 
					" implementing class name for module " + config.getUserSelection());
			return;
		}
		
		Map<String, Object> data = config.getExecutionData();
		final String originalUserSelection = getTargetFileName(config.getUserSelection());
		
		if (null == originalUserSelection) {
			LOGGER.error("Failed to extract userSelection from executionData.");
			return;
		}
		
		data.put(EXECUTION_DATA_USER_SELECTION_KEY, ROUND_TRIP_RUNNER_RT_PATH);

		
		IN4JSProject runnerProject = n4jsCore.findAllProjectMappings().get(ROUND_TRIP_RUNNER_PROJECT);
		if (null == runnerProject) {
			LOGGER.error("Failed to find round-trip runner project.");
			return;
		}

		// switch file-to-run for run.js file in runner project
		config.setFileToRun(new File(runnerProject.getLocationPath() + "/" + ROUND_TRIP_RUNNER_RT_PATH).toPath());
		// add runner script project to path
		config.addAdditionalPath(runnerProject.getLocationPath().toString());
		
		// pass round-trip runner specific parameters as environment variables
		Map<String, String> env = new HashMap<String, String>();
		env.putAll(config.getEnvironmentVariables());
		env.put(ROUND_TRIP_MODULE_KEY, originalUserSelection);
		env.put(ROUND_TRIP_CLASS_NAME_KEY, roundTripClassName);
		
		config.setEnvironmentVariables(env);
		
		config.setExecutionData(data);
	}
	
	private String getTargetFileName(URI uri) {
		final String userSelection_targetFileName = resourceNameComputer.generateFileDescriptor(uri, null);
		IN4JSProject project = resolveProject(uri);
		String base = AbstractSubGenerator.calculateProjectBasedOutputDirectory(project, true);
		return base + "/" + userSelection_targetFileName;
	}

	/**
	 * Resolves project from provided URI.
	 */
	private IN4JSProject resolveProject(URI n4jsSourceURI) {
		final Optional<? extends IN4JSProject> optionalProject = n4jsCore.findProject(n4jsSourceURI);
		if (!optionalProject.isPresent()) {
			throw new RuntimeException(
					"Cannot handle resource without containing project. Resource URI was: " + n4jsSourceURI + ".");
		}
		return optionalProject.get();
	}

	/**
	 * Attempts to determine the exported name of the class in the module, that
	 * implements the contract interface.
	 *
	 * For now, if there are multiple such classes in the module, one will be selected arbitrarily (cf. {@link Stream#findAny()}).
	 */
	private String findContractInterfaceClass(URI userSelection) {
		Optional<? extends IN4JSProject> project = n4jsCore.findProject(userSelection);

		if (!project.isPresent()) {
			Exceptions.sneakyThrow(new CoreException(new Status(IStatus.ERROR,
						RoundTripRunnerConstants.MAIN_PLUGIN_ID,
						"Failed to determine the N4JS project of the selected file.")));
		}

		ResourceSet resourceSet = n4jsCore.createResourceSet(Optional.of(project.get()));
		IResourceDescriptions descriptions = n4jsCore.getXtextIndex(resourceSet);

		final IResourceDescription resourceDescription = descriptions.getResourceDescription(userSelection);

		if (null == resourceDescription) {
			LOGGER.error(String.format("Failed to find resource with URI %s", userSelection));
			return null;
		}

		// create a valid type ref for round trip migration interface
		TInterface contractInterface = getContractInterface(resourceSet);

		// in case the round-trip runtime libraries are missing
		if (null == contractInterface) {
			Exceptions.sneakyThrow(new CoreException(new Status(IStatus.ERROR,
							RoundTripRunnerConstants.MAIN_PLUGIN_ID,
							"Could not find the round-trip runtime libraries. " +
							"Please make sure they are declared as a project dependency.")));
		}

		// double-check that the instance is resolved
		if (contractInterface.eIsProxy()) {
			contractInterface = (TInterface) EcoreUtil.resolve(contractInterface, resourceSet);
		}

		final TypeRef contractInterfaceTypeRef = TypeUtils.createTypeRef(contractInterface);

		// setup stream of all TClasses that are contained in the module
		Stream<TClass> resourceClasses = descriptionMatches(TClass.class,
				resourceDescription.getExportedObjectsByType(TypesPackage.eINSTANCE.getTClass()));

		// check for each class, whether it implements the required interface
		TClass roundTripClass = resourceClasses
			// resolve all classes using the resource set
			.map(c -> c.eIsProxy() ? (TClass) EcoreUtil.resolve(c, resourceSet) : c)
			.filter(c -> {

			// check whether the class implements the required interface
			final RuleEnvironment env = RuleEnvironmentExtensions.newRuleEnvironment(c.eResource());

			Result isSubtype = n4jsTypeSystem.subtype(env,
					TypeUtils.createTypeRef(c), contractInterfaceTypeRef);

			return !isSubtype.isFailure() && isSubtype.isSuccess();
		}).findAny().orElse(null);

		if (null == roundTripClass) {
			Exceptions.sneakyThrow(new CoreException(new Status(IStatus.ERROR,
						RoundTripRunnerConstants.MAIN_PLUGIN_ID,
						"The selected file does not contain a round-trip executable class. " +
						String.format("For an instance to be round-trip executable, it must implement interface '%s'.",
								RoundTripRunnerConstants.RUNNER_CONTRACT_INTERFACE))));
		}

		return roundTripClass.getExportedName();
	}

	/**
	 * Transform the given iterable of {@link IEObjectDescription}s to a stream of
	 * (possibly unresolved) {@link EObject}s of the given type.
	 */
	private <T> Stream<T> descriptionMatches(Class<T> clazz, Iterable<IEObjectDescription> iterable) {
		return stream(iterable)
			.map(d -> d.getEObjectOrProxy())
			.filter(clazz::isInstance)
			.map(clazz::cast);
	}

	/**
	 * Returns the first match of an {@link EObject} of the given type, in the
	 * given iterable of {@link IEObjectDescription}s.
	 */
	private <T> T firstDescriptionMatch(Class<T> clazz, Iterable<IEObjectDescription> iterable) {
		return descriptionMatches(clazz, iterable)
				.findAny()
				.orElse(null);
	}

	/**
	 * Transforms an {@link Iterable} to a {@link Stream}.
	 */
	private static <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}
}
