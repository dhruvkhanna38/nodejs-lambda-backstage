import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.Variable;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.EnvironmentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepositoryIdentifier;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.model.task.ScriptTaskProperties;
import com.atlassian.bamboo.specs.util.BambooServer;

@BambooSpec
public class PlanSpec {
    
    public Deployment rootObject() {
        final Deployment rootObject = new Deployment(new PlanIdentifier("{{cookiecutter.bamboo.project_key}}", "{{cookiecutter.bamboo.bamboo_plan_key}}")
                .oid(new BambooOid("")),
            "nodejs-lambda-deployment-project")
            .oid(new BambooOid(""))
            .description("description")
            .releaseNaming(new ReleaseNaming("release-3")
                    .autoIncrement(true))
            .environments(new Environment("NonProd")
                    .description("Production Deploy")
                    .tasks(new ScriptTask()
                            .description("Test Script")
                            .enabled(false)
                            .interpreter(ScriptTaskProperties.Interpreter.BINSH_OR_CMDEXE)
                            .inlineBody("#!/bin/bash\n\ndocker run --rm --name ${bamboo.containerName}  \\\n\t-v /var/run/docker.sock:/var/run/docker.sock \\\n\t-v ${bamboo.build.working.directory}:/buildDir -w /buildDir \\\n    ${bamboo.dockerRepo}/${bamboo.dockerImage}:${bamboo.tagVersion} \\\n    /bin/sh -c \"pwd && ls -ltr -a && sudo apt remove cmdtest -y && sudo apt-get update && sudo npm install --global yarn &&  yarn install &&  sls deploy -v -s preprod --aws-s3-accelerate\""),
                        new VcsCheckoutTask()
                            .description("Source Checkout for Serverless file Reference")
                            .checkoutItems(new CheckoutItem()
                                    .repository(new VcsRepositoryIdentifier()
                                            .name("{{cookiecutter.component_id}}"))),
                        new ScriptTask()
                            .description("Pull  the artifact from Nexus")
                            .inlineBody("#!/bin/bash\n#export versionToDeploy=${bamboo_deploy_release}.tar.gz\nPACKAGE_VERSION=$(cat package.json \\\n    | grep version \\\n    | head -1 \\\n    | awk -F: '{ print $2 }' \\\n    | sed 's/[\",]//g')\n    \nPACKAGE_NAME=$(cat package.json \\\n    | grep name \\\n    | head -1 \\\n    | awk -F: '{ print $2 }' \\\n    | sed 's/[\",]//g')\necho '========'    \necho $PACKAGE_VERSION \necho $PACKAGE_NAME\n\nNEXUS_LINK=\"https://repo1.ae.sda.corp.telstra.com/nexus/content/repositories/telstra-npm/\"\n\nNEXUS_DOWNLOAD_LINK=\"${NEXUS_LINK}${PACKAGE_NAME}/-/test-nodejs-lambda-\"\nNEXUS_DOWNLOAD_LINK_WITH_VERSION=\"${NEXUS_DOWNLOAD_LINK}${PACKAGE_VERSION}.tgz\"\nNEXUS_DOWNLOAD_LINK1=${NEXUS_DOWNLOAD_LINK_WITH_VERSION//[[:space:]]/}\necho $NEXUS_DOWNLOAD_LINK1\n\nexport mainPlan=${bamboo_planName}\necho $bamboo_build_working_directory\nexport rootFolder=`echo $mainPlan | cut -f3 -d' '`\nenv > env.txt\ndocker pull ${bamboo.dockerRepo}/${bamboo.nodeImage}:${bamboo.tagVersion}\necho \"\"\necho \"**Downloading Artifacts from Nexus**\"\ndocker run --rm --name ${bamboo.containerName} --env-file ./env.txt \\\n    -v \"${bamboo.build.working.directory}\":/buildDir \\\n    -u $(id -u ci):$(id -g ci) \\\n    -w /buildDir \\\n    ${bamboo.dockerRepo}/${bamboo.nodeImage}:${bamboo.tagVersion} \\\n    /bin/sh -c \"wget --user=${bamboo_nexus_deployment_user} --password=${bamboo_nexus_deployment_password} ${NEXUS_DOWNLOAD_LINK1}\"\n\n \n\n#chmod +x $versionToDeploy\n\n \n\n#if [ -f $versionToDeploy ]\n#    then\n        echo \"******* ${PACKAGE_VERSION} found in current directory. Will extract now ******************\"\n        tar xzf test-nodejs-lambda-0.0.8.tgz && zip test-nodejs-lambda-0.0.8.zip $(tar tf test-nodejs-lambda-0.0.8.tgz)      \n        sleep 10\n#    else\n#        echo \"******* ERR: $versionToDeploy not found in current directory. Please check ******************\"\n#        exit 2\n#fi\n\nls -ltr"),
                        new ScriptTask()
                            .description("Deploy to AWS")
                            .inlineBody("echo \"pulling the required image from dockerRepo\"\nenv > env.txt\ndocker pull ${bamboo.dockerRepo}/${bamboo.dockerImage}:${bamboo.tagVersion}\necho \"\"\n\necho \"deploying stack\"\necho \"deploying stack\"\n\t\nexport AWS_ACCESS_KEY_ID=${bamboo_AWS_ACCESS_KEY_ID_PASSWORD}\nexport AWS_SECRET_ACCESS_KEY=${bamboo_AWS_SECRET_ACCESS_KEY}\n\nstage=\"$Stage\"\n\necho \"creating serverless directory\" \necho $PWD\nmkdir -m 777 $PWD/.serverless \necho \"created directory\" \nls -ltr -a\ncp test-nodejs-lambda-0.0.8.zip $PWD/.serverless\necho \"copy  directory completed\" \nls -ltr -a $PWD/.serverless/\n\n\n#!/bin/bash\n\ndocker run --rm --name ${bamboo.containerName}  \\\n\t-v /var/run/docker.sock:/var/run/docker.sock \\\n\t-v ${bamboo.build.working.directory}:/buildDir -w /buildDir \\\n    ${bamboo.dockerRepo}/${bamboo.dockerImage}:${bamboo.tagVersion} \\\n    /bin/sh -c \"pwd && ls -ltr -a && sudo apt remove cmdtest -y && sudo apt-get update && sudo npm install --global yarn &&  yarn install &&  sls deploy -v -s preprod --aws-s3-accelerate\""))
                    .variables(new Variable("SLS_DEBUG",
                            "*"),
                        new Variable("containerName",
                            "aws-lambda-nodejs"),
                        new Variable("dockerRepo",
                            "docker-registry-v2.ae.sda.corp.telstra.com"),
                        new Variable("nodeImage",
                            "agb-node-cov-env"),
                        new Variable("tagVersion",
                            "latest")));
        return rootObject;
    }
    
    public DeploymentPermissions deploymentPermission() {
        final DeploymentPermissions deploymentPermission = new DeploymentPermissions("nodejs-lambda-deployment-project")
            .permissions(new Permissions()
                    .userPermissions("BAMBOO_BUILDPLAN_DID_ADMIN", PermissionType.VIEW, PermissionType.EDIT)
                    .userPermissions("BAMBOO_BUILDPLAN_DID_NON_ADMIN", PermissionType.VIEW, PermissionType.EDIT)
                    .groupPermissions("{{cookiecutter.organization.team_name}}-admin", PermissionType.VIEW, PermissionType.EDIT)
                    .groupPermissions("{{cookiecutter.organization.team_name}}-readonly", PermissionType.EDIT, PermissionType.VIEW)
                    .groupPermissions("{{cookiecutter.organization.team_name}}-users", PermissionType.EDIT, PermissionType.VIEW)
                    .loggedInUserPermissions(PermissionType.VIEW));
        return deploymentPermission;
    }
    
    public EnvironmentPermissions environmentPermission1() {
        final EnvironmentPermissions environmentPermission1 = new EnvironmentPermissions("nodejs-lambda-deployment-project")
            .environmentName("NonProd")
            .permissions(new Permissions()
                    .userPermissions("BAMBOO_BUILDPLAN_DID_ADMIN", PermissionType.BUILD, PermissionType.EDIT, PermissionType.VIEW)
                    .userPermissions("BAMBOO_BUILDPLAN_DID_NON_ADMIN", PermissionType.BUILD, PermissionType.EDIT, PermissionType.VIEW)
                    .groupPermissions("{{cookiecutter.organization.team_name}}-admin", PermissionType.EDIT, PermissionType.VIEW)
                    .groupPermissions("{{cookiecutter.organization.team_name}}-readonly", PermissionType.VIEW, PermissionType.EDIT)
                    .groupPermissions("{{cookiecutter.organization.team_name}}-users", PermissionType.VIEW, PermissionType.EDIT)
                    .loggedInUserPermissions(PermissionType.VIEW));
        return environmentPermission1;
    }
    
    public static void main(String... argv) {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("https://bamboo.tools.telstra.com");
        final PlanSpec planSpec = new PlanSpec();
        
        final Deployment rootObject = planSpec.rootObject();
        bambooServer.publish(rootObject);
        
        final DeploymentPermissions deploymentPermission = planSpec.deploymentPermission();
        bambooServer.publish(deploymentPermission);
        
        final EnvironmentPermissions environmentPermission1 = planSpec.environmentPermission1();
        bambooServer.publish(environmentPermission1);
    }
}