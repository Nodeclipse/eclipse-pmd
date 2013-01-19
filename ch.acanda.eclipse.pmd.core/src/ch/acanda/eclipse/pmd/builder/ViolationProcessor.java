// =====================================================================
//
// Copyright (C) 2012 - 2013, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.builder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

import net.sourceforge.pmd.RuleViolation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import ch.acanda.eclipse.pmd.marker.MarkerUtil;

/**
 * Processes the rule violations found by a PMD analysis.
 * 
 * @author Philip Graf
 */
public class ViolationProcessor {
    
    public void annotate(final IFile file, final Iterator<RuleViolation> violations) throws CoreException, IOException {
        MarkerUtil.removeAllMarkers(file);
        final String content = new String(Files.readAllBytes(file.getRawLocation().toFile().toPath()), file.getCharset());
        while (violations.hasNext()) {
            final RuleViolation violation = violations.next();
            MarkerUtil.addMarker(file, content, violation);
        }
    }
    
}
