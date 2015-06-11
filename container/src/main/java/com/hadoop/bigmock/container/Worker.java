package com.hadoop.bigmock.container;

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

import com.hadoop.bigmock.container.utils.Constants;

@Component
public class Worker {

	private static final Logger log = LoggerFactory.getLogger(Worker.class);

	private final CuratorFramework client;

	private String workerId;

	private PathChildrenCache assignments;

	@Autowired
	public Worker(CuratorFramework client) {
		this.client = client;
		try {
			workerId = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.warn("unable to retrieve hostname", e);
			workerId = UUID.randomUUID().toString();
		}
	}

	public void register() throws Exception {
		log.info("registering new worker " + workerId);

		// ensure worker path
		EnsurePath path = client.newNamespaceAwareEnsurePath(Constants.ZK_WORKERS_PATH);
		path.ensure(client.getZookeeperClient());

		// @formatter:off
		// add to worker node
		client.create()
			.withMode(CreateMode.EPHEMERAL)
			.inBackground()
			.forPath(Constants.ZK_WORKERS_PATH + "/" + workerId, new byte[0]);
		//add to assignments node
		client.create()
			.withMode(CreateMode.EPHEMERAL)
			.inBackground()
			.forPath(Constants.ZK_ASSIGNED_PATH + "/" + workerId, new byte[0]);
		// @formatter:on

		// initialie assignments cache
		assignments = new PathChildrenCache(client, Constants.ZK_ASSIGNED_PATH + "/" + workerId, true);
		assignments.start();

		// register a listener
		assignments.getListenable().addListener(assignmentsListener);
	}

	PathChildrenCacheListener assignmentsListener = new PathChildrenCacheListener() {
		@Override
		public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
			switch (event.getType()) {
			case CHILD_ADDED:
				log.info("child added " + ZKPaths.getNodeFromPath(event.getData().getPath()));
				break;
			default:
				log.info("worker received event " + event);
				break;
			}
		}
	};

}
