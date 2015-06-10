package com.hadoop.bigmock.yarn;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hadoop.bigmock.yarn.utils.ConfigurationLoader;
import com.hadoop.bigmock.yarn.utils.ResourceUtils;
import com.hadoop.bigmock.yarn.utils.YarnUtils;

public class Client {

	private static final Logger log = LoggerFactory.getLogger(Client.class);

	private Configuration conf;
	private YarnClient yarnClient;
	private FileSystem fs;

	private String appName;
	private boolean debug;
	private String queue;

	private Properties clientProperties;

	public static void main(String[] args) throws Exception {
		boolean result = false;
		try {
			Client client = new Client();
			try {
				// run client
				result = client.run();
			} catch (IllegalArgumentException e) {
				System.err.println(e.getLocalizedMessage());
				System.exit(-1);
			}
		} catch (Throwable t) {
			log.error("error running client", t);
			System.exit(1);
		}
		// check result status
		if (result) {
			log.info("application completed successfully");
			System.exit(0);
		}
		log.error("application failed");
		System.exit(2);
	}

	public Client() throws IOException, ConfigurationException {
		conf = new YarnConfiguration();
		fs = FileSystem.get(conf);
		yarnClient = YarnClient.createYarnClient();
		yarnClient.init(conf);

		// load client properties
		clientProperties = ConfigurationLoader.loadProperties("client.properties");
		appName = clientProperties.getProperty("name");
		queue = clientProperties.getProperty("queue");
		debug = clientProperties.containsKey("debug");
	}

	public boolean run() throws Exception {
		log.info("running client");
		yarnClient.start();
		if (debug) {
			YarnUtils.printClusterStats(yarnClient, queue);
		}

		// create new yarn application
		YarnClientApplication app = yarnClient.createApplication();
		GetNewApplicationResponse appResponse = app.getNewApplicationResponse();

		int amMem = Integer.parseInt(clientProperties.getProperty("amMem", "128"));
		int maxMem = appResponse.getMaximumResourceCapability().getMemory();
		log.info("maximum memory allowed => " + maxMem);
		if (amMem > maxMem) {
			log.info("request application master memory " + amMem + " is more than max memory " + maxMem
					+ ", adjusting");
			amMem = maxMem;
		}

		int amVCores = Integer.parseInt(clientProperties.getProperty("amCores", "1"));
		int maxVCores = appResponse.getMaximumResourceCapability().getVirtualCores();
		log.info("maximum virtual cores allowed => " + maxVCores);
		if (amVCores > maxVCores) {
			log.info("requested virtual cores " + amVCores + " is more than max virtual cores " + maxVCores
					+ ", adjusting");
			amVCores = maxVCores;
		}

		// create application context
		ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
		ApplicationId appId = appContext.getApplicationId();

		// set application general attributes
		appContext.setApplicationName(appName);
		appContext.setKeepContainersAcrossApplicationAttempts(clientProperties.containsKey("keepContainers"));

		// create application master container
		ContainerLaunchContext appContainer = Records.newRecord(ContainerLaunchContext.class);

		// set local resource for application master
		Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();

		// add application master jar to resource
		ResourceUtils.addLocalResource(fs, clientProperties.getProperty("amJar"), "am.jar", appName, appId.toString(),
				localResources);
		// add am properties file if provided at runtime
		if (new File("am.properties").exists()) {
			ResourceUtils.addLocalResource(fs, "am.properties", "am.properties", appName, appId.toString(),
					localResources);
		}

		// add container jar
		String containerJar = clientProperties.getProperty("containerJar");
		String suffix = appName + Path.SEPARATOR + appId + Path.SEPARATOR + FilenameUtils.getName(containerJar);
		Path dest = new Path(fs.getHomeDirectory(), suffix);
		log.info("uploading container jar [" + containerJar + "] to hdfs [" + dest.toString() + "]");
		fs.copyFromLocalFile(new Path(containerJar), dest);

		// specify local resource on container
		appContainer.setLocalResources(localResources);

		// create environment
		Map<String, String> env = new HashMap<String, String>();

		// set classpath
		StringBuilder classPathEnv = new StringBuilder(Environment.CLASSPATH.$$()).append(
				ApplicationConstants.CLASS_PATH_SEPARATOR).append("./*");
		for (String c : conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH,
				YarnConfiguration.DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH)) {
			classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR);
			classPathEnv.append(c.trim());
		}
		env.put("CLASSPATH", classPathEnv.toString());
		env.put("CONTAINER_JAR_PATH", dest.toString());

		// specify environment on container
		appContainer.setEnvironment(env);
		if (debug) {
			log.info("Environment set as => " + env.toString());
		}

		// create a the command to execute application master
		Vector<CharSequence> vargs = new Vector<CharSequence>();
		vargs.add(Environment.JAVA_HOME.$$() + "/bin/java");
		vargs.add("-Xmx" + amMem + "m");
		// set classname
		vargs.add("com.hadoop.bigmock.yarn.ApplicationMaster");
		// logs
		vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AppMaster.stdout");
		vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AppMaster.stderr");

		// set command on container to run application master
		appContainer.setCommands(YarnUtils.createCommand(vargs));

		// specify container requirements
		Resource resource = Records.newRecord(Resource.class);
		resource.setMemory(amMem);
		resource.setVirtualCores(amVCores);
		appContext.setResource(resource);
		log.info("container capability set as " + amMem + "m memory and and " + amVCores + " virtual cores");

		// Setup security tokens
		if (UserGroupInformation.isSecurityEnabled()) {
			Credentials credentials = new Credentials();
			String tokenRenewer = conf.get(YarnConfiguration.RM_PRINCIPAL);
			if (tokenRenewer == null || tokenRenewer.length() == 0) {
				throw new IOException("Can't get Master Kerberos principal for the RM to use as renewer");
			}

			// For now, only getting tokens for the default file-system.
			final Token<?> tokens[] = fs.addDelegationTokens(tokenRenewer, credentials);
			if (tokens != null) {
				for (Token<?> token : tokens) {
					log.info("Got dt for " + fs.getUri() + "; " + token);
				}
			}
			DataOutputBuffer dob = new DataOutputBuffer();
			credentials.writeTokenStorageToStream(dob);
			ByteBuffer fsTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
			appContainer.setTokens(fsTokens);
		}

		// container setup complete, set on context
		appContext.setAMContainerSpec(appContainer);

		// set priroity
		Priority pri = Records.newRecord(Priority.class);
		pri.setPriority(Integer.parseInt(clientProperties.getProperty("priority", "0")));
		appContext.setPriority(pri);
		log.info("using priority " + pri);

		// set queue
		appContext.setQueue(queue);
		log.info("using queue " + queue);

		// submit application
		yarnClient.submitApplication(appContext);

		// continue to monitor application
		return monitor(appId);
	}

	private boolean monitor(ApplicationId appId) throws YarnException, IOException {
		while (true) {
			try {
				// check status every second
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			// get application status report
			ApplicationReport report = yarnClient.getApplicationReport(appId);
			if (debug) {
				log.info("Got application report from ASM for" + ", appId=" + appId.getId() + ", clientToAMToken="
						+ report.getClientToAMToken() + ", appDiagnostics=" + report.getDiagnostics()
						+ ", appMasterHost=" + report.getHost() + ", appQueue=" + report.getQueue()
						+ ", appMasterRpcPort=" + report.getRpcPort() + ", appStartTime=" + report.getStartTime()
						+ ", yarnAppState=" + report.getYarnApplicationState().toString() + ", distributedFinalState="
						+ report.getFinalApplicationStatus().toString() + ", appTrackingUrl=" + report.getTrackingUrl()
						+ ", appUser=" + report.getUser());
			}

			// get application statuses
			YarnApplicationState yarnStatus = report.getYarnApplicationState();
			FinalApplicationStatus finalStatus = report.getFinalApplicationStatus();
			if (FinalApplicationStatus.SUCCEEDED == finalStatus) {
				if (YarnApplicationState.FINISHED == yarnStatus) {
					log.info("application completed successfully, stopping monitoring");
					return true;
				} else {
					log.info("application did not completed successfully. yarn status = " + yarnStatus.toString()
							+ ", final status = " + finalStatus.toString());
					return false;
				}
			} else if (FinalApplicationStatus.KILLED == finalStatus || FinalApplicationStatus.FAILED == finalStatus) {
				log.info("application ended prematurely. yarn status = " + yarnStatus.toString() + ", final status = "
						+ finalStatus.toString());
				return false;
			}
		}
	}
}