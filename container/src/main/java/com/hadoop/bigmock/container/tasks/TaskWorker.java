package com.hadoop.bigmock.container.tasks;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.EnsurePath;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadoop.bigmock.container.exceptions.TaskCreateException;
import com.hadoop.bigmock.container.utils.Constants;
import com.hadoop.bigmock.core.processors.TaskProcessor;
import com.hadoop.bigmock.core.tasks.MockTask;

@Component
public class TaskWorker<T extends MockTask> implements TaskListener<T> {

	private static final Logger log = LoggerFactory.getLogger(TaskWorker.class);

	private final CuratorFramework client;
	private final ObjectMapper mapper;
	private final TaskProcessor<T> processor;

	private String workerId;

	private PathChildrenCache assignments;

	@Autowired
	public TaskWorker(CuratorFramework client, ObjectMapper mapper, TaskProcessor<T> processor) {
		this.client = client;
		this.mapper = mapper;
		this.processor = processor;

		try {
			workerId = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.warn("unable to retrieve hostname", e);
			workerId = UUID.randomUUID().toString();
		}
	}

	public void register() throws Exception {
		log.debug("registering new worker " + workerId);

		// ensure worker path
		EnsurePath path = client.newNamespaceAwareEnsurePath(Constants.ZK_WORKERS_PATH);
		path.ensure(client.getZookeeperClient());

		// @formatter:off
		// add to worker node
		client.create()
			.withMode(CreateMode.EPHEMERAL)
			.inBackground()
			.forPath(Constants.ZK_WORKERS_PATH + "/" + workerId, new byte[0]);
		// add to assignments node
		client.create()
			.withMode(CreateMode.EPHEMERAL)
			.inBackground()
			.forPath(Constants.ZK_ASSIGNED_PATH + "/" + workerId, new byte[0]);
		// @formatter:on

		// initialize assignments cache
		assignments = new PathChildrenCache(client, Constants.ZK_ASSIGNED_PATH + "/" + workerId, true);
		assignments.start();

		// register a listener
		assignments.getListenable().addListener(assignmentsListener);
	}

	@Override
	public void newTask(T task) throws TaskCreateException {
		log.debug("new task received " + task.getName());
		processor.process(task);
	}

	PathChildrenCacheListener assignmentsListener = new PathChildrenCacheListener() {
		@Override
		public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
			switch (event.getType()) {
			case CHILD_ADDED:
				String taskName = ZKPaths.getNodeFromPath(event.getData().getPath());
				byte[] taskData = event.getData().getData();

				// create mock task
				T task = mapper.readValue(new String(taskData), new TypeReference<T>() {
				});
				task.setName(taskName);

				// received new task
				newTask(task);

				break;
			default:
				log.debug("worker received event " + event);
				break;
			}
		}
	};

}