// =====================================================================
//
// Copyright (C) 2012, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.marker.resolution.java.emptycode;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import ch.acanda.eclipse.pmd.marker.resolution.ASTQuickFixTestCase;

/**
 * Unit plug-in test for {@link EmptyStatementBlockQuickFix}.
 * 
 * @author Philip Graf
 */
public class EmptyStatementBlockQuickFixTest extends ASTQuickFixTestCase<EmptyStatementBlockQuickFix> {
    
    public EmptyStatementBlockQuickFixTest(final TestParameters parameters) {
        super(parameters);
    }
    
    @Parameters
    public static Collection<Object[]> getTestData() {
        return createTestData(EmptyStatementBlockQuickFixTest.class.getResourceAsStream("EmptyStatementBlock.xml"));
    }
    
}
