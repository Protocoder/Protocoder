/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.protocoderrunner.services.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;

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
        // get mContext Calendar object with current time
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

        // add to mContext global alarm thingie
        tasks.add(new Task(id, p, cal, delay, repeating, wakeUpScreen));

        String message = "";

        Intent intent = new Intent(c, AlarmReceiver.class);
        intent.putExtra(ALARM_INTENT, message);

        if (wakeUpScreen) {
            intent.putExtra(Project.SETTINGS_SCREEN_WAKEUP, true);
        }

        // Project p = ProjectManager.getInstance().getCurrentProject();
        intent.putExtra(Project.NAME, p.getName());
        intent.putExtra(Project.FOLDER, p.getFolder());

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
