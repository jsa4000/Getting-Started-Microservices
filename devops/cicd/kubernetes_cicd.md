# Kubernetes CI/CC

## Tools

- Source Code: Github
- CI/CD: Jenkins/Spinnaker
- Artifacts : Nexus
- Images : Nexus
- Quality: SonarQube
- Testing: TBD

## Installation

### Pre-requisites

Following are the pre-requisites needed

- Kubernetes cluster
- kubectl binary
- Helm v3

Create new namespace `cicd-tools` to deploy the CI/CD tools

  ```bash
  # Create a namespace called cicd
  kubectl create ns cicd-tools

  # Retrieve the namespaces
  kubectl get ns

  # Switch to previous namespace created
  kubectl config set-context --current --namespace=cicd-tools
  ```

### Traefik

1. Add `traaefik` repository to helm

    ```bash
    # Add traefik chart repository
    helm3 repo add traefik https://helm.traefik.io/traefik
    # Update repo
    helm3 repo update
    ```

2. Install `traefik` using official helm chart 

    ```bash
    # Install traefik with default values
    helm3 install traefik traefik/traefik -n cicd-tools
    ```

3. Verify the installation

    ```bash
    # Verify traefik is currently running and a load balancer has been created.
    kubectl get -n cicd-tools svc,pods
    ```

### Jenkins (Operator)

For the installation of Jenkins on kubernetes the Jenkins Operator is going to be used. For further information about the process or detailed information check the [Official Repository](https://github.com/jenkinsci/kubernetes-operator) or [Official WebSite](https://jenkinsci.github.io/kubernetes-operator/docs/installation/)

1. Install Jenkins Custom Resource Definition into `cicd-tools`

    ```bash
    # Create the custom resources definitions used by Jenkins operator
    kubectl apply -f https://raw.githubusercontent.com/jenkinsci/kubernetes-operator/master/deploy/crds/jenkins_v1alpha2_jenkins_crd.yaml
    ```

2. Install Jenkins Operator via Helm v3.

    ```bash
    # Add Jenkins repositories to Helm
    helm3 repo add jenkins https://raw.githubusercontent.com/jenkinsci/kubernetes-operator/master/chart

    # Retrieve the helm repo list configured
    helm3 repo list

    # Update the helm repo
    helm3 repo update

    # Get the default values used to modify if necessary
    helm3 inspect values jenkins/jenkins-operator > jenkins-operator-values.yaml

    # Check the namespace set in the values, since it must be equal to the current namespace "cicd-tools"
    # Install jenkins operator
    helm3 install jenkins-operator jenkins/jenkins-operator -n cicd-tools -f jenkins-operator-values.yaml
    # Install jenkins operator
    helm3 install jenkins-operator jenkins/jenkins-operator -n cicd-tools --set jenkins.backup.enabled=false,jenkins.enabled=false
    # Or Install jenkins operator with values
    helm3 install jenkins-operator jenkins/jenkins-operator -n cicd-tools --set jenkins.backup.enabled=false,jenkins.namespace=cicd-tools

    # To add custom values using the install command, use the --set parameter and a list with the key/values separated by commas
    #helm3 install jenkins-operator jenkins/jenkins-operator -n cicd-tools --set jenkins.labels.LabelKey=LabelValue,jenkins.annotations.AnnotationKey=AnnotationValue
    ```

    Output:

    ```bash
    NOTES:
    1. Watch Jenkins instance being created:
    $ kubectl --namespace cicd-tools get pods -w

    1. Get Jenkins credentials:
    $ kubectl --namespace cicd-tools get secret jenkins-operator-credentials-jenkins -o 'jsonpath={.data.user}' | base64 -d
    $ kubectl --namespace cicd-tools get secret jenkins-operator-credentials-jenkins -o 'jsonpath={.data.password}' | base64 -d

    1. Connect to Jenkins (actual Kubernetes cluster):
    $ kubectl --namespace cicd-tools port-forward jenkins-jenkins 8080:8080

    Now open the browser and enter http://localhost:8080
    ```

3. Delete Jenkins Operator

    ```bash
    # Delete helm operator
    helm3 delete jenkins-operator
    ```

4. Create Jenkins Operator using the CRD

    Create a file with the documentation `jenkins-operator-instance.yaml`

    ```yaml
    apiVersion: jenkins.io/v1alpha2
    kind: Jenkins
    metadata:
      name: jenkins-instance
    spec:
      master:
        containers:
        - name: jenkins-master
          image: jenkins/jenkins:lts
          imagePullPolicy: Always
          livenessProbe:
            failureThreshold: 30
            httpGet:
              path: /login
              port: http
              scheme: HTTP
            initialDelaySeconds: 2048
            periodSeconds: 10
            successThreshold: 1
            timeoutSeconds: 5
          readinessProbe:
            failureThreshold: 30
            httpGet:
              path: /login
              port: http
              scheme: HTTP
            initialDelaySeconds: 2048
            periodSeconds: 10
            successThreshold: 1
            timeoutSeconds: 1
          resources:
            limits:
              cpu: 1500m
              memory: 3Gi
            requests:
              cpu: "1"
              memory: 500Mi
    ```

    Create the jenkins resources created previously.

    ```bash
    # Create Jenkins instance
    kubectl apply -f jenkins-operator-instance.yaml

    # Get the pods running
    kubectl get pods -w
    ```

### Jenkins (Chart)

The chart and the installation process can be found in the official [Github repository](https://github.com/jenkinsci/helm-charts)

1. Add `jenkins` helm repository

    ```bash
    # Add repo
    helm3 repo add jenkinscharts https://charts.jenkins.io
    # Update repo
    helm3 repo update
    ```

2. Install Jenkins with the default values

    ```bash
    # Install Jenkins
    helm3 install jenkins jenkinscharts/jenkins -n cicd-tools

    # Install overriding default values
    helm3 install jenkins jenkinscharts/jenkins -n cicd-tools --set master.JCasC.enabled=false,master.JCasC.defaultConfig=false,master.sidecars.configAutoReload.enabled=false

    # To change the default values manually use the following command
    helm3 inspect values jenkinscharts/jenkins > jenkins-chart-values.yaml
    # Verify the values before install it
    helm3 template jenkins jenkinscharts/jenkins -n cicd-tools -f jenkins-chart-values.yaml
    # Install jenkins with the override values
    helm3 install jenkins jenkinscharts/jenkins -n cicd-tools -f jenkins-chart-values.yaml

    # Wait until Jenkins pods is running
    kubectl get pods -n cicd-tools -w
    ```

3. Verify Jenkins installation

    ```bash
    # Use URL (host mapped) using ingress controller
     http://jenkins.cicd.com

    # Use prot-forward to connecto to Jenkins
    kubectl port-forward svc/jenkins -n cicd-tools 8080:8080

    # Check logs to get the key to start Jenkins, if not automatic configuration has been set
    kubectl logs -n cicd-tools $(kubectl get pods -n cicd-tools -o name | awk -F "/" '{print $2}' | grep jenkins) -f
    # *************************************************************
    # *************************************************************
    # *************************************************************
    # 
    # Jenkins initial setup is required. An admin user has been created and a password generated.
    # Please use the following password to proceed to installation:
    # 
    # efcccba1fa584b1fb3ecbc52d703b98f  -> Copy this key on the logs
    # 
    # This may also be found at: /var/jenkins_home/secrets/initialAdminPassword
    # 
    # *************************************************************
    # *************************************************************
    # *************************************************************

    # in order to get the Admin password (if already initialized)
    printf $(kubectl get secret --namespace cicd-tools jenkins -o jsonpath="{.data.jenkins-admin-password}" | base64 --decode);echo
    ```

4. **Update/Correct** jenkins Plugins

    After the installation is **complete**, Jenkins may complains because some plugins could not be correctly installed.
    This is some issue related with the plugins server that cannot be solved automatically.

    In this case, after the installation it is needed to update/install missing plugins and **retry** the necessary times until all plugins have been downloaded.

    Ej. Sometimes the plugin A depends on B and B depends on C. In this case, search for the C Plugin and install it. Later, try to install or restart the server again. Repeat this process until all the dependencies has been fixes and no alarms or notifications are prompted once the server has restarted.

5. Deploy ingress controller (if **NOT** configured during the installation of the chart)

    ```yaml
    apiVersion: extensions/v1beta1
    kind: Ingress
    metadata:
      name: jenkins
      annotations:
        kubernetes.io/ingress.class: traefik
        traefik.ingress.kubernetes.io/rewrite-target: /
        traefik.ingress.kubernetes.io/rule-type: "PathPrefixStrip"
    spec:
      rules:
      - host: jenkins.cicd.com
        http:
          paths:
          - path: /
            backend:
              serviceName: jenkins
              servicePort: 8080
    ```

    ```bash
    # Apply current specification.
    k apply -f jenkins-ingress.yaml

    ## Add following entry into hosts (/etc/hosts) file
    127.0.0.1   jenkins.cicd.com

    # Test ingress controller
    http://jenkins.cicd.com
    ```

6. Configure Kubernetes cluster in jenkins (kubernetes config, Agent, Pod Template, etc..)

    Initially the chart configures current Kubernetes cluster to be used by `Jenknis`. However, in is possible to create additional configuration using another cluster, Agents, Pod templates, etc..

    The Steps are defined bellow:

     - Configure Kubernetes cluster
        - http://jenkins.cicd.com/configureClouds/ 
        - Credentials -> Add -> Secret File -> Select the `.kube/config` file. 
        - Finally select previous configuration from the combobox and test connection.
     - Name: Kubernetes
     - Kubernetes Namespace: cicd-tools
     - Jenkins URL: http://jenkins.cicd-tools.svc.cluster.local:8080
     - Jenkins Tunnel: jenkins-agent.cicd-tools.svc.cluster.local:50000
     - Labels:  jenkins=slave
     - Create Pod Template.
       - Name: kube
       - Namespace: cicd-tools
       - Labels: kubepods
       - Container Template:
         - Name: jnlp
         - Image: jenkins/jnlp-slave:4.3-9-alpine
         - Delete default arguments and commands in container template
         - Environment variable: JENKINS_URL=http://jenkins.cicd-tools.svc.cluster.local:8080
       - Pod Retention: Never
     - Save

7. Create Pipelines

    In order top create the first pipeline:

    - Select `New Item` from the left menu
    - Input the name of the pipeline to build and select `Pipeline`, then `Ok`.
    - Set a `Description`and under `Pipeline` section select `Pipeline Script` -> `Maven (kubernetes)`
    - Press `Save` and force to execute (schedule) the pipeline
    - Check pods are create using: `kubectl get pods -A -w`

8. Changing the Jenkins Theme UI

    - Search and install the Plugin `Simple Theme`
    - Go to `Manage Jenkins` -> `Configure System` and search for the section `Theme`. 
    - Add `CSS URL` using a custom css file, then save.
    - There are several available themes in https://github.com/afonsof/jenkins-material-theme
    - ie. https://cdn.rawgit.com/afonsof/jenkins-material-theme/gh-pages/dist/material-indigo.css

9. SonarQube Scanner tool

    Go to Sonarqube:

    - Generate a token: go `User` > `My Account` > `Security`. 
    - Your existing tokens are listed here, each with a Revoke button.

    Go to Jenkins:

    - Install the SonarScanner for Jenkins via the `Jenkins Update Center`. Restart
    - Configure your **SonarQube Server**:
      - Log into Jenkins as an administrator and go to `Manage Jenkins` > `Configure System`.
      - Scroll down to the `SonarQube` configuration section, click `Add SonarQube`, and add the values you're prompted for.
        - Enable injection of SonarQube server configuration as build environment variables (`SONAR_CONFIG_NAME`, `SONAR_HOST_URL`, `SONAR_AUTH_TOKEN` )
        - Name: sonarqube
        - URL: http://sonarqube-sonarqube.cicd-tools.svc.cluster.local:9000
      - The server authentication token should be created as a `Secret Text` credential.
    - Configure your **Sonarqube Tool**:
      - Go to `Manage Jenkins` > `Global Tool Configuration`
      - Scroll down to the SonarScanner configuration section and click on `Add SonarScanner`.
      - Select the `Name` for the tool
      - Check `Install Automatically`or use `Add Installer`

10. Nexus Uploader tool

    Go to Nexus:

    - Generate an user: go `Server Administrator And Configuration` > `Security` > `User` > `Create Local User`
    - Use the ID and password to identify the user (`jenkins/jenkins`)
    - Select `Active` and assign the roles (ie. admin)
    - Select `Save`
    - Log-out and log-in to test the user created

    Go to Jenkins:

    - Install `Nexus Platform` Plugin for Jenkins via the `Jenkins Update Center`. Restart
    - Go to a project and navigate through the pipeline section. Select `Pielione Syntax` to access to the `Snippet Generator`
    - In steps select `nexusArtifactUploader: Nexus Artifact Uploader`:
      - Version: `NEXUS3`
      - Protocol: HTTP
      - Nexus URL: sonatype-nexus-service.cicd-tools.svc.cluster.local:8081
      - Credentials: Create User/Name credentials using previous user created in Nexus (`jenkins/jenkins`)
      - GroupId/Version: This must be extracted from pom.xml or project file. `com.example/1.0.0`
      - Repository: maven-releases    # check snapshots repositories to match the version name.
      - Artifacts:
        - ArtifactId: my-app
        - Type: jar
        - File: target/my-app-1.0-SNAPSHOT.jar
    - The press `Generate Pipeline Script`. This creates the script to be used in JenkinsFile

11. Blue Ocean Plugin

    - Install `Blue Ocean` Plugin. (This will install almost all Blue Ocean Plugins available)
      - **Note:** Retry several times if fails installing some dependencies. `Manage Jenkins` -> `Plugin Manager` -> `Available` -> `Blue Ocean`.
    - After the installation restart Jenkins -> http://jenkins.cicd.com/restart
    - In order to enable Blue Ocean Dashboard, click on `Open Blue Ocean` at the left side

12. Restart Jenkins Server

    To restart Jenkins manually, you can use either of the following commands (by entering their URL in a browser):

    - `jenkins_url`/safeRestart : Allows all running jobs to complete. New jobs will remain in the queue to run after the restart is complete.
    - `jenkins_url`/restart : Forces a restart without waiting for builds to complete.


13. Delete helm chart

    ```bash
    # Install Jenkins
    helm3 delete -n cicd-tools jenkins
    ```

### Nexus (Operator)

- [Create Repositories](https://levelup.gitconnected.com/deploying-private-npm-packages-to-nexus-a16722cc8166)

1. Install Operator Lifecycle Manager (OLM), a tool to help manage the Operators running on the cluster.

    ```bash
    # Download and install OLM to manage operators
    curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/0.16.1/install.sh | bash -s 0.16.1
    # verify OLM installation
    kubectl get ns
    kubectl get pods -n olm
    kubectl get pods -n operators
    ```

2. Install the operator by running the following command:

    >  This Operator will be installed in the "operators" namespace and will be usable from all namespaces in the cluster.

    ```bash
    # Install the operator in the default operators namespace
    kubectl create -n cicd-tools -f https://operatorhub.io/install/nexus-operator-m88i.yaml

    # Download specific operator version (i,e VERSION=v0.3.0)
    export VERSION=v0.3.0
    kubectl apply -n cicd-tools -f "https://github.com/m88i/nexus-operator/releases/download/${VERSION}/nexus-operator.yaml"
    ```

3. After install, watch your operator come up using next command.

    ```bash
    # Get the resources created
    kubectl get csv -n cicd-tools

    NAME                    DISPLAY          VERSION   REPLACES                PHASE
    nexus-operator.v0.3.0   Nexus Operator   0.3.0     nexus-operator.v0.2.1   Succeeded
    ```

4. Create a file using the CRD to create a Nexus instance

   Create file `nexus-operator-instance.yaml` with following content.

    ```yaml
    apiVersion: apps.m88i.io/v1alpha1
    kind: Nexus
    metadata:
      name: nexus3
    spec:
      networking:
        expose: false
      persistence:
        persistent: false
      replicas: 1
      resources:
        limits:
          cpu: '2'
          memory: 2Gi
        requests:
          cpu: '1'
          memory: 2Gi
      useRedHatImage: false
    ```

     Create the nexus resources created previously.

    ```bash
    # Create nexus instance
    kubectl apply -f nexus-operator-instance.yaml

    # Get the pods running
    kubectl get pods -w
    ```

5. Verify Nexus installation

    ```bash
    # Connect to Nexus via 8081 port (admin/admin123)
    kubectl port-forward svc/nexus3 8081:8081
    ```

6. Delete Nexus Operator

    ```bash
    # Delete Nexus operator
    helm3 delete -f https://operatorhub.io/install/nexus-operator-m88i.yaml
    ```

### Nexus (Chart)

Nexus chart can be found on [helm chart](https://github.com/oteemo/charts)

1. Add `oteemo` helm repository

    ```bash
    # Add repo
    helm3 repo add oteemocharts https://oteemo.github.io/charts
    # Update repo
    helm3 repo update
    ```

2. Install Nexus with the default values

    ```bash
    # Install Nexus
    helm3 install sonatype-nexus oteemocharts/sonatype-nexus -n cicd-tools

    # Install Nexus Manually
    helm3 inspect values oteemocharts/sonatype-nexus > nexus-chart-values.yaml
    helm3 install sonatype-nexus oteemocharts/sonatype-nexus -n cicd-tools -f nexus-chart-values.yaml

    # Wait until Nexus pods is running
    kubectl get pods -w
    ```

3. Install Ingress for Nexus (Without using `NexusProxy`)

    ```bash
    # install nexus without proxy
    helm3 install sonatype-nexus oteemocharts/sonatype-nexus -n cicd-tools -f nexus-chart-values-no-proxy.yaml

    # Install ingress
    kubectl apply -n cicd-tools -f nexus-ingress.yaml

    # Wait until Nexus pods is running
    kubectl get -n cicd-tools pods -w
    ```

4. Verify Nexus installation

    ```bash
    # Use the URL created
    http://nexus.cicd.com/

    # Use prot-forward to connect to Nexus (The default login is admin/admin123)
    kubectl port-forward -n cicd-tools svc/sonatype-nexus 8081:8080
    ```

5. Delete helm chart

    ```bash
    # Install nexus
    helm3 delete -n cicd-tools sonatype-nexus

### Sonarqube

Sonarqube has no operator. So the [helm chart](https://github.com/oteemo/charts) is used instead

1. Add `oteemo` helm repository

    ```bash
    # Add repo
    helm3 repo add oteemocharts https://oteemo.github.io/charts
    # Update repo
    helm3 repo update
    ```

2. Install Sonarqube with the default values

    ```bash
    # Install sonarqube
    helm3 install sonarqube oteemocharts/sonarqube -n cicd-tools

     # Install sonarqube Manually
    helm3 inspect values oteemocharts/sonarqube > sonarqube-chart-values.yaml
    helm3 install sonarqube oteemocharts/sonarqube -n cicd-tools -f sonarqube-chart-values.yaml

    # Wait until sonarqube pods is running
    kubectl get pods -w
    ```

3. Verify Sonarqube installation

    ```bash
     # Use the URL created
    http://sonar.cicd.com/

    # Use prot-forward to connecto to sonarqube (The default login is admin/admin.)
    kubectl port-forward -n cicd-tools svc/sonarqube-sonarqube 9000:9000
    ```

4. Delete helm chart

    ```bash
    # Install sonarqube
    helm3 delete sonarqube -n cicd-tools
    ```

## CD/CD

- [Jenkins Examples](https://www.jenkins.io/solutions/pipeline/)
- [Jenkins Kubernetes](https://plugins.jenkins.io/kubernetes/)
- [Jenkins Pipeline](https://www.magalix.com/blog/create-a-ci/cd-pipeline-with-kubernetes-and-jenkins)
- [JenkinsFile example](https://github.com/jcorioland/kubernetes-jenkins/blob/master/Jenkinsfile)
- [Jenkins in Kubernetes cluster](https://gist.github.com/jonico/ced8555104834b934044d80699e404a6)
- [Sonaqube integration](https://github.com/jatinngupta/Jenkins-SonarQube-Pipeline/blob/master/Jenkinsfile)

### Create the Github Repository

Create a Personal Access token in Github

Go to Github:

- Login with the account
- Got to `Settings`-> `Developer settings` -> `Personal access token` -> New
- Add all `scopes` related to 'Repo' and press `Generate Token`
- Copy the token and save it for later use.

`Personal access token` can be used as a **password**. It is needed to create new credentials to the `Credential Manager`.

Go to Jenkins 

- Go to `Credentials` > `System` > `Global credentials` > Add credentials a page will open.
- In Kind drop-down select `Username and password`.
- In User put a non-existing `Username` like `github-user`.
- Add `Personal Access Token` in the `Password` field

### Create a Pipeline project in Jenkins

1. Go to `Jenkins` at http://jenkins.cicd.com/ and select `New Item`
2. In the Enter an item name field, specify the **name** for your new Pipeline project (e.g. `template-library-java-build`). Scroll down and click `Pipeline`, then click `OK` at the end of the page.
3. ( Optional ) On the next page, specify a brief description for your Pipeline in the Description field (*e.g. Template project to launch a basic pipeline using Maven.*)
4. Click the `Pipeline` tab at the top of the page to scroll down to the `Pipeline` section.
5. From the `Definition` field, choose the `Pipeline script from SCM` option. This option instructs Jenkins to obtain your Pipeline from Source Control Management (SCM), which will be your locally cloned Git repository.
6. From the `SCM` field, choose `Git`.
7. In the Repository URL field, specify the URI. (ie https://github.com/jsantosa-minsait/template-library-java.git). Verify Jenkins can connect with the Git repository. Add new credentials if needed.
8. Select the branches to build. i.e. `*/master` or `*/main`
9. Click `Save` to save your new Pipeline project. You’re now ready to begin creating your `Jenkinsfile`, which you’ll be checking into your locally cloned Git repository.

### Create a JenkinsFile for the Pipeline

Typical Pipeline steps：

1. Checkout SCM: Checkout source code from GitHub repository.
2. Unit test: It will continue to execute next stage after unit test passed.
3. SonarQube analysis：Process sonarQube code quality analysis.
4. Build & push snapshot image: Build the image based on selected branches in the behavioral strategy. Push the tag of SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER to DockerHub, among which, the $BUILD_NUMBER is the operation serial number in the pipeline's activity list.
5. Push the latest image: Tag the master branch as latest and push it to DockerHub.
6. Deploy to dev: Deploy master branch to Dev environment. verification is needed for this stage.
7. Push with tag: Generate tag and released to GitHub. Then push the tag to DockerHub.
8. Deploy to production: Deploy the released tag to the Production environment.

Jenkinsfile (Groovy)

```groovy
podTemplate(containers: [
    containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'golang', image: 'golang:1.8.0', ttyEnabled: true, command: 'cat')
  ]) {

    node(POD_LABEL) {
        // checkout sources
        checkout scm

        stage('Build') {
            container('maven') {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Test') {
            container('maven') {
                sh 'mvn test'
            }
        }
        stage('Deliver') {
            container('maven') {
                sh './jenkins/scripts/deliver.sh'
            }
        }
    }
}
```

Jenkinsfile (DSL)

```groovy
pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: some-label-value
spec:
  containers:
  - name: maven
    image: maven:3.3.9-jdk-8-alpine
    command:
    - cat
    tty: true
  - name: busybox
    image: busybox
    command:
    - cat
    tty: true
"""
        }
    }
    stages {
        stage('Build') {
            steps {
                container('maven') {
                    sh 'mvn -B -DskipTests clean package'
                }
            }
        }
        stage('Test') {
            steps {
                container('maven') {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    container('maven') {
                        junit 'target/surefire-reports/*.xml'
                    }
                }
            }
        }
        stage('Deliver') {
            steps {
                container('maven') {
                    sh './jenkins/scripts/deliver.sh'
                }
            }
        }
    }
}

```

### Create MultiBranch Pipeline

> In this example `Blue Ocean` Plugins are not being used.

- Select `New Item`from the left side.
- Select a name for the project (i.e `template-service-nodejs`) and `Multibranch Pipeline`, then press`OK`.
- Specify a Name/Description etc. for the project
- Within the `Branch Sources` section select `Github` source, then select the github `Credentials` created with aa valid token
- Set the Github Repository URL, delete all the pre-configured behaviours and select `Discover Branches` -> `All branches` and `Filter by Name (with regular expression`) then use the following regex `^(?:.*develop|.*main|.*release/\d+\.\d+\.\d+(?!.))$` or simply `(main|develop|release-.*|feature-.*|hotfix-.*)`
- In build configuration select Mode `by Jenkinsfile` and set the script path. i.e. `jenkins/Jenkinsfile`
- Set `Scan Multibranch Pipeline Triggers` to trigger periodically events to check any changes in branches. Select 1 or 2 minutes interval.
- Then `Save`.
- Jenkins now will check for the branches to generate the indexes.

### Create JobDSL - SEED

In order to create Jobs with JOBDSL is a better common practice to create Jobs using a seed project. This project will bootstrap all the jobs needed.

- Install `jobdsl` plugin in Jenkins
- Create `FreeStyle` project by selecting the option and press save it.
- In `Source Code Management` select Git, fill RepositoryURL (`https://github.com/*/*.git`), select the credentials and branches to build: `*/main`
- In `Build` section select `Process Jobs DSL` and select the path for the Jobs to be created `src/jobs/**/*.groovy`
- Choose the options when Jobs are removed, modified, views, etc.. Select the `Additional classpath` if a groovy library is used `src/main/groovy`

```groovy
String basePath = 'example1'
String repo = 'https://github.com/jsantosa-minsait/template-library-java.git'

folder(basePath) {
    description 'This example shows basic folder/job creation.'
}

job("$basePath/gradle-example-build") {
    scm {
        github repo
    }
    triggers {
        scm 'H/5 * * * *'
    }
    steps {
        gradle 'assemble'
    }
}

job("$basePath/gradle-example-deploy") {
    parameters {
        stringParam 'host'
    }
    steps {
        shell 'scp war file; restart...'
    }
}
```

It is possible to create Jobs by using a pipeline and `JenkinsFile`. For that it is necessary to trigger jobs using organizations folder or creating pipelines manually in Jenkins.

`JenkinsFile`

```groovy
node {
    checkout scm
    jobDsl targets: 'src/jobs/**/*.groovy',
           removedJobAction: 'DELETE',
           removedViewAction: 'DELETE',
           lookupStrategy: 'SEED_JOB',
           additionalClasspath: 'src/main/groovy'
}
```
