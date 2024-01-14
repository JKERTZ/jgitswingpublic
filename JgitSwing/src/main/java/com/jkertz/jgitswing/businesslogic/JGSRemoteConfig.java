/*
 * Copyright (C) 2024 JKERTZ
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jkertz.jgitswing.businesslogic;

import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.RemoteConfig;

/**
 *
 * @author jkertz
 */
public class JGSRemoteConfig extends JGScachedResult {

    private List<RemoteConfig> remoteList;

    public JGSRemoteConfig(Git git) {
        super(git);
    }

    public List<RemoteConfig> getRemoteList() throws Exception {
        if (!isValid) {
            remoteList = wrapper.remoteList(git);
            //TODO: reenable after finding correct invalidation
//           isValid = true;
        }
        return remoteList;
    }
}
