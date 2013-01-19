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

package ch.acanda.eclipse.pmd.wizard;

import java.nio.file.Paths;

import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.domain.FileSystemRuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.ProjectRuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.WorkspaceRuleSetConfiguration;
import ch.acanda.eclipse.pmd.preferences.PMDWorkspaceSettings;
import ch.acanda.eclipse.pmd.ui.dialog.FileSelectionDialog;

/**
 * Controller for the wizard to add a new rule set configuration.
 * 
 * @author Philip Graf
 */
final class AddRuleSetConfigurationController {
    
    private final AddRuleSetConfigurationModel model;
    private final IProject project;
    
    public AddRuleSetConfigurationController(final IProject project) {
        model = new AddRuleSetConfigurationModel(project);
        this.project = project;
    }
    
    public AddRuleSetConfigurationModel getModel() {
        return model;
    }
    
    public void browse(final Shell shell) {
        if (model.isFileSystemTypeSelected()) {
            final FileDialog fileDialog = new FileDialog(shell);
            final String file = fileDialog.open();
            if (file != null) {
                model.setLocation(file);
            }
            
        } else if (model.isProjectTypeSelected()) {
            browseContainer(shell, project);
            
        } else if (model.isWorkspaceTypeSelected()) {
            browseContainer(shell, project.getWorkspace().getRoot());
            
        } else {
            throw new IllegalStateException();
        }
    }
    
    private void browseContainer(final Shell shell, final IContainer container) {
        final FileSelectionDialog dialog = new FileSelectionDialog(shell);
        dialog.setMessage("Choose a PMD rule set configuration:");
        dialog.setValidator(new ISelectionStatusValidator() {
            @Override
            public IStatus validate(final Object[] selection) {
                IStatus result = new Status(IStatus.OK, PMDPlugin.ID, "");
                if (selection.length == 1 && !(selection[0] instanceof IContainer)) {
                    final IResource resource = (IResource) selection[0];
                    final String configuration = resource.getLocation().toOSString();
                    try {
                        new RuleSetFactory().createRuleSet(configuration);
                    } catch (final RuleSetNotFoundException | IllegalArgumentException e) {
                        // the rule set location is invalid
                        result = new Status(IStatus.WARNING, PMDPlugin.ID, resource.getName()
                                + " is not a valid PMD rule set configuration");
                    }
                } else {
                    result = new Status(IStatus.WARNING, PMDPlugin.ID, "");
                }
                return result;
            }
        });
        dialog.setInput(container);
        if (dialog.open() == Window.OK) {
            model.setLocation(getRelativePath(container, (IResource) dialog.getFirstResult()));
        }
    }
    
    private String getRelativePath(final IContainer container, final IResource resource) {
        return Paths.get(container.getLocationURI()).relativize(Paths.get(resource.getLocationURI())).toString();
    }
    
    public RuleSetConfiguration createRuleSetConfiguration() {
        if (model.isValid()) {
            final PMDWorkspaceSettings workspaceSettings = new PMDWorkspaceSettings(PMDPlugin.getDefault().getPreferenceStore());
            final int id = workspaceSettings.getNextRuleSetConfigurationId();
            final RuleSetConfiguration config;
            if (model.isWorkspaceTypeSelected()) {
                config = new WorkspaceRuleSetConfiguration(id, model.getName(), Paths.get(model.getLocation()));
            } else if (model.isProjectTypeSelected()) {
                config = new ProjectRuleSetConfiguration(id, model.getName(), Paths.get(model.getLocation()));
            } else if (model.isFileSystemTypeSelected()) {
                config = new FileSystemRuleSetConfiguration(id, model.getName(), Paths.get(model.getLocation()));
            } else {
                throw new IllegalStateException("Unknown configuration type");
            }
            return config;
        }
        throw new IllegalStateException("Cannot create RuleSetConfiguration as the model is not valid");
    }
    
}
