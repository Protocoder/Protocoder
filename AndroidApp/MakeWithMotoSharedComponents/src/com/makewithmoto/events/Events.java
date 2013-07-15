package com.makewithmoto.events;



public class Events {
	public static class ProjectEvent {
		private Project project;
		private String name;
		private String action;
		
		public ProjectEvent(Project aProject, String anAction) {
			project = aProject;
			action = anAction;
		}
		public ProjectEvent(Project aProject, String aName, String anAction) {
			project = aProject;
			action = anAction;
			name = aName;
		}
		public String getAction() { return action; }
		public void setAction(String newAction) { action = newAction; }
		public String getFile() {
			return project.getUrl();
		}
		public String getName() {
			return (name == null) ? project.getName() : name;
		}
		public Project getProject() {
			if (project == null) {
				project = Project.get(getName());
			}
			return project; 
		}
	}
	
	public static class ReloadAppViewEvent {
		private Project project;
		public ReloadAppViewEvent(ProjectEvent evt) {
			project = evt.getProject();
		}
		public Project getProject() { return project; }
	}
	
	public static class LogEvent {
		private String msg;
		private String tag;
		
		public LogEvent(final String aTag, final String aMsg) {
			msg = aMsg;
			tag = aTag;
		}
		public String getMessage() { return msg; }
		public String getTag() { return tag; }
	}
}