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

package ch.acanda.eclipse.pmd.wizard;

import static ch.acanda.eclipse.pmd.ui.util.ValidationUtil.errorIfBlank;

import java.nio.file.Paths;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import ch.acanda.eclipse.pmd.ui.model.ValidationProblem;
import ch.acanda.eclipse.pmd.ui.model.ValidationProblem.Severity;
import ch.acanda.eclipse.pmd.ui.model.ValidationResult;
import ch.acanda.eclipse.pmd.ui.model.ViewModel;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * View model for the wizard page to add a new file system rule set configuration.
 * 
 * @author Philip Graf
 */
class AddFileSystemRuleSetConfigurationWizardPageModel extends ViewModel {
    
    private String name;
    private String location;
    /**
     * This property is derived from {@link #location}. If {@link #location} is valid this list contains the rules of
     * the selected rule set, otherwise it is empty. It is never {@code null}.
     */
    private ImmutableList<Rule> rules = ImmutableList.of();
    
    @Override
    protected boolean updateDirty() {
        return !(Strings.isNullOrEmpty(name) && Strings.isNullOrEmpty(location));
    }
    
    @Override
    protected void reset() {
        setName(null);
        setLocation(null);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        setProperty("name", this.name, this.name = name);
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(final String location) {
        setProperty("location", this.location, this.location = location);
    }
    
    public ImmutableList<Rule> getRules() {
        return rules;
    }
    
    private void setRules(final ImmutableList<Rule> rules) {
        assert rules != null;
        setProperty("rules", this.rules, this.rules = rules);
    }
    
    @Override
    protected ImmutableSet<String> createValidatedPropertiesSet() {
        return ImmutableSet.of("location", "name");
    }
    
    @Override
    protected void validate(final String propertyName, final ValidationResult validationResult) {
        validateName(validationResult);
        validateLocation(propertyName, validationResult);
    }
    
    /**
     * Validates the location of the rule set configuration and sets or resets the property {@link #rules} depending on
     * whether {@link #location} contains a valid rule set configuration location or not.
     */
    private void validateLocation(final String propertyName, final ValidationResult result) {
        final Builder<Rule> rules = ImmutableList.builder();
        if (!errorIfBlank("location", location, "Please enter the location of the rule set configuration", result)) {
            RuleSet ruleSet = null;
            try {
                if (Paths.get(location).toFile().exists()) {
                    ruleSet = new RuleSetFactory().createRuleSet(location);
                    rules.addAll(ruleSet.getRules());
                }
            } catch (final RuleSetNotFoundException e) {
                // the rule set location is invalid - the validation problem will be added below
            }
            if (ruleSet == null || ruleSet.getRules().isEmpty()) {
                result.add(new ValidationProblem("location", Severity.ERROR, "The rule set configuration at the given location is invalid"));
            }
        }
        if ("location".equals(propertyName)) {
            setRules(rules.build());
        }
    }
    
    private void validateName(final ValidationResult result) {
        errorIfBlank("name", name, "Please enter a name for this rule set configuration", result);
    }
    
}
