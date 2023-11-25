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

import java.util.List;
import com.jkertz.jgitswing.model.JGStag;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;

/**
 *
 * @author jkertz
 */
public class JGStags extends JGScachedResult {

    private List<JGStag> jgStags;

    public JGStags(Git git) {
        super(git);
    }

    public List<JGStag> getJGStags(int amount) throws Exception {
        if (!isValid) {
            jgStags = wrapper.getJGStags(git, amount);
            //TODO: reenable after finding correct invalidation
//            isValid = true;
        }
        return jgStags;
    }

    public Ref tagCommit(String tagName, String tagMessage, String taggerName, String taggerEmail, String commit) throws Exception {
        Ref tagRef = wrapper.tag(git, tagName, tagMessage, taggerName, taggerEmail, commit);
        return tagRef;
    }
}
