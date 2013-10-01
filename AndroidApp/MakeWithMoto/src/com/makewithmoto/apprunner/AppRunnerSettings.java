package com.makewithmoto.apprunner;

import com.makewithmoto.events.Project;

public class AppRunnerSettings {

	private static AppRunnerSettings instance;

	public static AppRunnerSettings get() {
		if (instance == null)
			instance = new AppRunnerSettings();

		return instance;
	}

	public Project project;

}
