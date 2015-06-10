package com.hadoop.bigmock.container.utils;

public final class Constants {

	public static final String ZK_NS_PATH = "datalake";
	public static final String ZK_APP_PATH = "/drs";
	public static final String ZK_LEADER_PATH = ZK_APP_PATH + "/leader";
	public static final String ZK_WORKERS_PATH = ZK_APP_PATH + "/workers";
	public static final String ZK_ASSIGNED_PATH = ZK_APP_PATH + "/assigned";
	public static final String ZK_TASKS_PATH = ZK_APP_PATH + "/tasks";

	private Constants() {
	}
}
