/*
 * Copyright (C) 2023 jkertz
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

import java.util.Map;
import org.eclipse.jgit.api.Git;

/**
 *
 * @author jkertz
 */
public class JGSConfigInfo extends JGScachedResult {

    private Map<String, Map<String, Map<String, String>>> configInfoMap;

    public JGSConfigInfo(Git git) {
        super(git);
    }

    public Map<String, Map<String, Map<String, String>>> getConfigInfo() throws Exception {
        if (!isValid) {
            configInfoMap = wrapper.getConfigInfo(git);
            //TODO: reenable after finding correct invalidation
//           isValid = true;
        }
        return configInfoMap;
    }

    public void setConfigInfo(Map<String, Map<String, Map<String, String>>> configInfo) throws Exception {
        this.configInfoMap = configInfo;
        wrapper.saveConfigInfo(git, configInfoMap);
    }
}
