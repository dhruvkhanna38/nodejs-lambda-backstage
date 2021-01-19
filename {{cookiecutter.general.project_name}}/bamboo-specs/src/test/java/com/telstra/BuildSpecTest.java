package com.telstra;

import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.exceptions.PropertiesValidationException;
import com.atlassian.bamboo.specs.api.util.EntityPropertiesBuilders;
import com.telstra.BuildSpec;

import org.junit.Test;

public class BuildSpecTest {
    @Test
    public void checkYourPlanOffline() throws PropertiesValidationException {
        Plan plan = new BuildSpec().createPlan();

        EntityPropertiesBuilders.build(plan);
    }
    
    @Test
    public void checkYourDeployPlanOffline() throws PropertiesValidationException {
    	Deployment deployPlan = DeploymentSpec.deploymentPlan();

        EntityPropertiesBuilders.build(deployPlan);
    }

}
