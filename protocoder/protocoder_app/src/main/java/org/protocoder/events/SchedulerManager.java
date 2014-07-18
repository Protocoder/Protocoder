/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoder.events;

import java.util.ArrayList;
import java.util.Calendar;

import org.protocoder.services.AlarmReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class SchedulerManager {

	Context c;
	private static SchedulerManager INSTANCE;
	ArrayList<Task> tasks = new ArrayList<SchedulerManager.Task>();

	public SchedulerManager(Context c) {
		this.c = c;
	}

	public static SchedulerManager getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new SchedulerManager(c);
		}

		return INSTANCE;
	}

	public void removeTask(Task t) {
		tasks.remove(t);
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public void loadTasks() {

	}

	public void saveTasks() {

	}

	private static final String ALARM_INTENT = "protocoder_alarm_message";

	public void setAlarmDelayed(Project p, int delay, boolean alarmRepeat, boolean wakeUpScreen) {
		// get a Calendar object with current time
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, delay);

		setAlarm(p, cal, delay, alarmRepeat, wakeUpScreen);
	}

	/*
	 * public void setAlarm(Project p, int month, int day, int hourOfDay, int
	 * minute, int second, boolean wakeUpScren) { Calendar cal =
	 * Calendar.getInstance(); cal.set(cal.get(Calendar.YEAR), month, day,
	 * hourOfDay, minute, second);
	 * 
	 * setAlarm(p, cal, false, wakeUpScren, -1); }
	 */

	public void setAlarm(Project p, int hourOfDay, int minute, int second, boolean wakeUpScren) {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), hourOfDay, minute,
				second);

		setAlarm(p, cal, 0, false, wakeUpScren);
	}

	public void setAlarm(Project p, Calendar cal, int delay, boolean repeating, boolean wakeUpScreen) {
		int id = (int) Math.round((999999999 * Math.random()));

		// add to a global alarm thingie
		tasks.add(new Task(id, p, cal, delay, repeating, wakeUpScreen));

		String message = "";

		Intent intent = new Intent(c, AlarmReceiver.class);
		intent.putExtra(ALARM_INTENT, message);

		if (wakeUpScreen) {
			intent.putExtra("wakeUpScreen", true);
		}

		// Project p = ProjectManager.getInstance().getCurrentProject();
		intent.putExtra(Project.NAME, p.getName());
		intent.putExtra(Project.TYPE, p.getType());

		PendingIntent sender = PendingIntent.getBroadcast(c, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) (c).getSystemService(Context.ALARM_SERVICE);

		if (repeating) {
			am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), delay, sender);

		} else {
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

		}

	}

	class Task {
		final int TYPE_ALARM = 0;
		final int TYPE_BOOT = 1;
		final int TYPE_SMS = 1;

		int id;
		int type;
		Project p;
		Calendar time;
		int delay;
		boolean wakeUpScreen;

		public Task(int id, Project p, Calendar time, int delay, boolean repeating, boolean wakeUpScreen) {
			this.id = id;
			this.p = p;
			this.time = time;
			this.delay = delay;
			this.wakeUpScreen = wakeUpScreen;
		}

		public String getTimeString() {
			return time.toString();
		}
	}

}
