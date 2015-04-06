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

package org.protocoder.network;

import java.util.ArrayList;

public class ConnectedUser {

    private static ConnectedUser instance;
    ArrayList<String> users;

    public static ConnectedUser getInstance() {

        if (instance == null) {
            instance = new ConnectedUser();
        }

        return instance;
    }


    public ConnectedUser() {
        users = new ArrayList<>();
    }

    public void addUserIp(String ip) {
        users.add(ip);
    }

    public boolean isIpRegistered(String ip) {
//        for (String user : users) {
//            if (user.equals(ip)) {
//                return true;
//            }
//        }
        return users.contains(ip);

        //return false;
    }

}
