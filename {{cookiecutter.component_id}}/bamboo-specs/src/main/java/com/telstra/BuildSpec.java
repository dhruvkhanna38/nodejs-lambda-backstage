import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.Variable;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.InjectVariablesTask;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;
import com.atlassian.bamboo.specs.model.task.InjectVariablesScope;
import com.atlassian.bamboo.specs.util.BambooServer;
import java.time.Duration;

@BambooSpec
public class PlanSpec {
    
    public Plan plan() {
        final Plan plan = new Plan(new Project()
                .oid(new BambooOid(""))
                .key(new BambooKey("{{cookiecutter.bamboo.project_key}}"))
                .name("{{cookiecutter.organization.team_name}}")
                .description("{{cookiecutter.description}}"),
            "{{cookiecutter.component_id}}",
            new BambooKey("{{cookiecutter.bamboo.bamboo_plan_key}}"))
            .oid(new BambooOid(""))
            .description("{{cookiecutter.description}}")
            .pluginConfigurations(new ConcurrentBuilds())
            .stages(new Stage("Build")
                    .jobs(new Job("Build Plan",
                            new BambooKey("BUILD"))
                            .description("Build Plan")
                            .artifacts(new Artifact()
                                    .name("release-info")
                                    .copyPattern("release-info.properties")
                                    .shared(true),
                                new Artifact()
                                    .name("maven-jar")
                                    .copyPattern("*.jar")
                                    .location("target")
                                    .shared(true))
                            .tasks(new VcsCheckoutTask()
                                    .description("SourceCodeCheckout")
                                    .checkoutItems(new CheckoutItem().defaultRepository()),
                                new ScriptTask()
                                    .description("Pull Git SHA id and Release info")
                                    .inlineBody("#!/bin/bash\ngit_short_sha=`git log --pretty=format:'%h' -n 1`\nprevious_version=`git describe --tags --abbrev=0 --match 'v*.*' | cut -c2-`\nimage_name=${bamboo.CONTAINER_REGISTRY}/${bamboo.ARTIFACT_NAME}:${git_short_sha}\n\necho git_short_sha=${git_short_sha} > release-info.properties\necho previous_version=${previous_version} >> release-info.properties\necho image_name=${image_name} >> release-info.properties"),
                                new InjectVariablesTask()
                                    .description("Inject Bamboo variables")
                                    .path("release-info.properties")
                                    .namespace("releaseInfo")
                                    .scope(InjectVariablesScope.RESULT),
                                new ScriptTask()
                                    .description("Build Application code and run tests")
                                    .inlineBody("#!/bin/bash\n\necho \"pulling the required image\"\ndocker pull ${bamboo.dockerRepo}/${bamboo.nodeImage}:${bamboo.tagVersion}\necho ${bamboo.build.working.directory}\necho \"running the build\"\ndocker run --rm --name ${bamboo.containerName} -v ${bamboo.build.working.directory}:/buildDir -w /buildDir \\\n    ${bamboo.dockerRepo}/${bamboo.nodeImage}:${bamboo.tagVersion} \\\n    bash -c \"echo Installing npm@latest && npm install -g npm@latest && npm install && npm run test && pwd && ls -R && chmod -R 777 . \""),
                                new ScriptTask()
                                    .description("JetPack SAAS Trigger")
                                    .inlineBody("#!/bin/bash\ncurl $bamboo_JETPACK_SAAS_INITIATE_TRIGGER | bash"),
                                new ScriptTask()
                                    .description("JetPack SAAS Breakbuild SonarQube")
                                    .inlineBody("#!/bin/bash\nexport SCANNER_TO_BREAK_BUILD=Sonarqube\ncurl $bamboo_JETPACK_SAAS_REPORT_BREAKBUILD | bash"),
                                new ScriptTask()
                                    .description("Publish to Nexus Repo")
                                    .inlineBody("#!/usr/bin/env bash\ndocker_v2=\"docker-registry-v2.ae.sda.corp.telstra.com\"\napplication_base_image=\"${docker_v2}/o2a/node-centos:10\"\necho ${bamboo_NEXUS_DEPLOYMENT_TOKEN_PASSWORD}\nnexus_token=\"${bamboo_NEXUS_DEPLOYMENT_TOKEN_PASSWORD}\"\n\ndocker run --rm -i \\\n-v $(pwd):/app \\\n-e nexus_token=${nexus_token} \\\n-w /app \\\n${application_base_image} sh -c \" \\\nmv .npmrc ~/.npmrc && \\\nnpm config set _auth ${nexus_token} && \\\nnpm publish --access=public\""))))
            .linkedRepositories("{{cookiecutter.component_id}}")
            
            .triggers(new RepositoryPollingTrigger()
                    .withPollingPeriod(Duration.ofSeconds(120)))
            .variables(new Variable("ARTIFACT_NAME",
                    "lambda-nodejs"),
                new Variable("AWS_ACCESS_KEY_ID_PASSWORD",
                    ""),
                new Variable("AWS_SECRET_ACCESS_KEY",
                    ""),
                new Variable("CONTAINER_REGISTRY",
                    "docker-registry-v2.ae.sda.corp.telstra.com"),
                new Variable("REBUILD_BUILDER",
                    "false"),
                new Variable("SLS_DEBUG",
                    "*"),
                new Variable("containerName",
                    "aws-lambda-nodejs"),
                new Variable("dockerImage",
                    "docker-client"),
                new Variable("dockerRepo",
                    "docker-registry-v2.ae.sda.corp.telstra.com"),
                new Variable("env",
                    "preprod"),
                new Variable("nodeImage",
                    "agb-node-cov-env"),
                new Variable("tagVersion",
                    "latest"))
            .planBranchManagement(new PlanBranchManagement()
                    .createForPullRequest()
                    .delete(new BranchCleanup()
                        .whenRemovedFromRepositoryAfterDays(7)
                        .whenInactiveInRepositoryAfterDays(30))
                    .notificationLikeParentPlan())
            .forceStopHungBuilds();
        return plan;
    }
    
    public PlanPermissions planPermission() {
        final PlanPermissions planPermission = new PlanPermissions(new PlanIdentifier("{{cookiecutter.bamboo.project_key}}", "{{cookiecutter.bamboo.bamboo_plan_key}}"))
            .permissions(new Permissions()
                    .userPermissions("BAMBOO_BUILDPLAN_DID_ADMIN", PermissionType.EDIT, PermissionType.BUILD, PermissionType.CLONE, PermissionType.VIEW, PermissionType.ADMIN)
                    .userPermissions("BAMBOO_BUILDPLAN_DID_NON_ADMIN", PermissionType.EDIT, PermissionType.VIEW, PermissionType.CLONE)
                    .userPermissions("BAMBOO_BUILDPLAN_DEPLOYMENT_USER", PermissionType.BUILD, PermissionType.EDIT, PermissionType.VIEW, PermissionType.ADMIN, PermissionType.CLONE)
                    .groupPermissions("{{cookiecutter.organization.team_name}}-readonly", PermissionType.EDIT, PermissionType.VIEW, PermissionType.CLONE, PermissionType.BUILD, PermissionType.ADMIN)
                    .groupPermissions("{{cookiecutter.organization.team_name}}-admin", PermissionType.EDIT, PermissionType.VIEW, PermissionType.CLONE, PermissionType.BUILD, PermissionType.ADMIN)
                    .groupPermissions("{{cookiecutter.organization.team_name}}-users", PermissionType.EDIT, PermissionType.VIEW, PermissionType.CLONE, PermissionType.BUILD, PermissionType.ADMIN)
                    .loggedInUserPermissions(PermissionType.VIEW)
                    .anonymousUserPermissionView());
        return planPermission;
    }
    
    public static void main(String... argv) {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("https://bamboo.tools.telstra.com");
        final PlanSpec planSpec = new PlanSpec();
        
        final Plan plan = planSpec.plan();
        bambooServer.publish(plan);
        
        final PlanPermissions planPermission = planSpec.planPermission();
        bambooServer.publish(planPermission);
    }
}