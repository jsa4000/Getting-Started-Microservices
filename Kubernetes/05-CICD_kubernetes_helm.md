# CI/CD With kubernetes

## Introduction

## Installation

### Nexus Repository Manager

Sonatype Nexus Repository Manager Comunity.

> Supported formats: Bower, Docker, git-lfs, Maven 2, npm, NuGet, PyPI, RubyGems, Site/Raw

In order to install Nexus into a kubernets cluster use the following command

> In the current chart-helm version there is an *error* with the **nexus-proxy** pods. In order to **solve** the issue is neccesary to force helm to get the nexus-proxy image **v2.2.0**.

    sudo helm upgrade --install sonatype-nexus stable/sonatype-nexus --namespace devops --set nexusProxy.imageTag=2.2.0,persistence.storageClass=nfs-slow,nexusBackup.persistence.enabled=false,ingress.enabled=true,ingress.annotations."kubernetes\.io/ingress\.class"=traefik,nexusProxy.env.nexusHttpHost=nexus.devops.com,nexusProxy.env.nexusDockerHost=docker-registry.devops.com,ingress.tls.enabled=false

> The default login is admin/admin123

Add the DNS to the host file on your system

```txt
10.0.0.11   nexus.devops.com
10.0.0.11   docker-registry.devops.com
```

> Remember to use **HTTP Port 5003** when creating **docker-registry**, son *nexus-proxy* redirect `docker-registry.devops.com` to that port.

Those are the URLs to access to Sonatype Nexus Repository.

- [Nexus Dashboard](http://nexus.devops.com:31971)
- [Docker Registry API](http://docker-registry.devops.com:31971)

#### Nexus Issues

There are some settings needed to create a **docker-repository** from Nexus UI.

- Configure *HTTP Port 5003* when docker repository is created
- Use *self-hosted* docker repositories. However it can be created a group so *proxy* and the *hosted* repositories share images.
- Allow incoming requests and Docker v1 API

In order to reproduce this error try to connect to [docker-registry](http://docker-registry.devops.com:31971)

    sudo kubectl logs -n devops sonatype-nexus-6458bc949f-pbmw6 nexus-proxy

```txt
2018-08-25 16:25:34,768 [ERROR] [vert.x-eventloop-thread-0] [io.vertx.core.http.impl.HttpClientRequestImpl] io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: localhost/127.0.0.1:5003
2018-08-25 16:25:52,922 [ERROR] [vert.x-eventloop-thread-0] [io.vertx.core.net.impl.ConnectionBase] java.io.IOException: Connection reset by peer
vagrant@k8s-master:~$
```

#### Publish docker image to repository

- AS an example, start, clone a repo with a `DockerFile` to build

        git clone https://github.com/crccheck/docker-hello-world.git

- Build the image and use a local tag

        sudo docker build -t docker-hello-world .

- Add the `nexus.devops.com` -> `10.0.0.11` to the host file

        sudo vi /etc/hosts

- Since Nexus has been deplyed using http, it is needed to set up the following configuration

        sudo vi /etc/docker/daemon.json

        {"insecure-registries": ["docker-registry.devops.com:31971"]}

- Restart docker service to apply the new configuration

        sudo service docker restart

- Login to the current repository

        sudo docker login docker-registry.devops.com:31971

- Get the id of the tag created

        sudo docker image list

- Tag the docker image usinf the host previously configured

> This is using the official *nexus documentation*

        sudo docker tag 95042a53b601 docker-registry.devops.com:31971/hello-world:latest

- Finally **publish** the docker image to the registry

        sudo docker push docker-registry.devops.com:31971/hello-world:latest

> Sometimes if it is not working inside k8s cluster (because *ingress*, *roles*, *namespaces*, etc..), it is recommended to use first a simple docker image to get in working to save some time.

    # To rin Nexus3 container exposing the Ports for the UI and Docker regitry use the following command,
    sudo docker run -d -p 8081:8081 -p 8123:8123 --name nexus sonatype/nexus3

#### Publish maven package to repository

- First access to Nexus3 UI, login and create a new **hosted-reposritoy** for **maven2** packages.

> Be care about the *Maven2/Version Policy*, this will filter the packages you publish to match with that particular tag: *snapshot*, *release*, etc..

- Secondly, it must be added the following code into the *pom.xml* on the proyect

  - Add a **distributionManagement** tag with `repository` (releases), `snapshotRepository` (snapshost) , etc..

  ```xml
  <distributionManagement>
    <repository>
       <id>nexus-releases</id>
       <url>http://nexus.devops.com:31971/repository/maven-releases/</url>
    </repository>
     <snapshotRepository>
        <id>nexus-snapshots</id>
        <name>Internal Snapshots</name>
        <url>http://nexus.devops.com:31971/repository/maven-snapshots/</url>
    </snapshotRepository>
  </distributionManagement>
  ```

  - Disable the defautlt ``maven-deploy-plugin``

  ```xml
  <plugin>
     <groupId>org.apache.maven.plugins</groupId>
     <artifactId>maven-deploy-plugin</artifactId>
     <version>${maven-deploy-plugin.version}</version>
     <configuration>
        <skip>true</skip>
     </configuration>
  </plugin>
  ```

  - Add the `nexus-staging-maven-plugin` to create a new goal that publish the packages into Nexus repository.

  ```xml
  <plugin>
    <groupId>org.sonatype.plugins</groupId>
    <artifactId>nexus-staging-maven-plugin</artifactId>
    <version>1.5.1</version>
    <executions>
       <execution>
          <id>default-deploy</id>
          <phase>deploy</phase>
          <goals>
             <goal>deploy</goal>
          </goals>
       </execution>
    </executions>
    <configuration>
       <serverId>nexus</serverId>
       <nexusUrl>http://nexus.devops.com:31971/nexus</nexusUrl>
       <skipStaging>true</skipStaging>
    </configuration>
  </plugin>
  ```

- Add the credentials needed to publish into Nexus3 reposository.This configuration is glolbally configured into maven in ``settings.xml``

  ```xml
  <servers>
     <server>
        <id>nexus-snapshots</id>
        <username>admin</username>
        <password>admin123</password>
     </server>
  </servers>
  <servers>
     <server>
        <id>nexus-releases</id>
        <username>admin</username>
        <password>admin123</password>
     </server>
  </servers>
  ```

- Finally, compile the proyect using ``mnv``

        mvn clean deploy

#### Nexus References

- [Create a private docker registry using Nexus3 Docker image](https://www.ivankrizsan.se/2016/06/09/create-a-private-docker-registry/)
- [Publish a Maven package on Nexus 3](https://www.baeldung.com/maven-deploy-nexus)

### GitLab (Repository + CICD)

> The chart on the official [helm repository](https://github.com/helm/charts/tree/master/stable/gitlab-ce) is **deprecated**.

The gitlab chart is the best way to operate GitLab on Kubernetes. This chart contains all the required components to get started, and can scale to large deployments.

THe offical helm chart is hosted in their own [repository](https://charts.gitlab.io/).

The default deployment includes:

- Core GitLab components: Unicorn, Shell, Workhorse, Registry, Sidekiq, and Gitaly
- Optional dependencies: **Postgres**, Redis, Minio
- An auto-scaling, unprivileged GitLab Runner using the **Kubernetes executor**
- Automatically provisioned SSL via Let's Encrypt

Following are the steps:

- Add the GitLab repository to helm

        sudo helm repo add gitlab https://charts.gitlab.io/

- Forze to update all the repos and packages

        sudo helm repo update

- Install helm

  > In this [link](https://gitlab.com/charts/gitlab/blob/master/doc/installation/command-line-options.md) it can be seen all que commands.

        # Install Gitlab community Edition

        sudo helm upgrade --install gitlab gitlab/gitlab --version=1.0.1 --namespace devops --set global.hosts.domain=devops.com,certmanager-issuer.email=jsa4000@hotmail.com,registry.enabled=false,minio.persistence.storageClass=nfs-slow,gitlab-runner.install=false,prometheus.install=false,certmanager.install=false,certmanager.rbac.create=false,prometheus.rbac.create=false,gitlab-runner.rbac.create=false,redis.persistence.storageClass=nfs-slow,postgresql.persistence.storageClass=nfs-slow,nginx-ingress.controller.service.type=NodePort,nginx-ingress.controller.stats.enabled=false,nginx-ingress.controller.metrics.enabled=false,nginx-ingress.defaultBackend.replicaCount=1,nginx-ingress.controller.replicaCount=1,gitlab.gitlab-shell.minReplicas=1,gitlab.gitlab-shell.maxReplicas=1,nginx-ingress.controller.service.externalTrafficPolicy=Cluster,gitlab.gitaly.persistence.storageClass=nfs-slow,gitlab.gitaly.persistence.size=10Gi,gitlab.unicorn.minReplicas=1,gitlab.unicorn.maxReplicas=1,gitlab.unicorn.workerProcesses=1,gitlab.migrations.image.repository=registry.gitlab.com/gitlab-org/build/cng/gitlab-rails-ce,gitlab.unicorn.workhorse.image=registry.gitlab.com/gitlab-org/build/cng/gitlab-workhorse-ce,gitlab.unicorn.image.repository=registry.gitlab.com/gitlab-org/build/cng/gitlab-unicorn-ce,gitlab.sidekiq.image.repository=registry.gitlab.com/gitlab-org/build/cng/gitlab-sidekiq-ce,global.hosts.https=false

        # Install gitlab-omnibus version (deprecated)

        sudo helm upgrade --install gitlab gitlab/gitlab-omnibus --namespace devops --set baseDomain=devops.com,legoEmail=jsa4000@gmail.com,postgresStorageSize=10Gi,gitlabDataStorageSize=10Gi,gitlabRegistryStorageSize=10Gi,gitlabConfigStorageClass=nfs-slow,gitlabDataStorageClass=nfs-slow,gitlabRegistryStorageClass=nfs-slow,postgresStorageClass=nfs-slow,redisStorageClass=nfs-slow,gitlab-runner.gitlabUrl=gitlab-gitlab.devops.svc.cluster.local:8005,baseIP=10.0.0.11,pagesExternalScheme=http,pagesExternalDomain=pages-devops.com

        # Install minikube version

        # Minimal setup to init minikube:
        #       minikube start --disk-size 80g --cpus 3 --memory 8192
        # https://gitlab.com/charts/gitlab/blob/master/doc/minikube/README.md

        helm upgrade --install gitlab gitlab/gitlab --version=1.0.1 --namespace devops -f https://gitlab.com/charts/gitlab/raw/master/examples/values-minikube-minimum.yaml --set gitlab.migrations.image.repository=registry.gitlab.com/gitlab-org/build/cng/gitlab-rails-ce,gitlab.sidekiq.image.repository=registry.gitlab.com/gitlab-org/build/cng/gitlab-sidekiq-ce,gitlab.unicorn.image.repository=registry.gitlab.com/gitlab-org/build/cng/gitlab-unicorn-ce,gitlab.unicorn.workhorse.image=registry.gitlab.com/gitlab-org/build/cng/gitlab-workhorse-ce

        helm upgrade --install gitlab gitlab/gitlab --version=1.0.1 --namespace devops -f https://gitlab.com/charts/gitlab/raw/master/examples/values-minikube-minimum.yaml.yaml

  > Wildcard DNS for any IP address [nip.io](http://nip.io/) or [xip.io](http://xip.io/)
  > Note that the parameter **postgresql.persistence.storageClass** is not included in the offcial documentation inside ``values.yaml``. **Helm** automatically takes the *sub-charts* and propage the variables to the rest of the sub-charts, usign a *folder-type* structure to go deeper into the *hierarchy*.
  > Previous configuration is inteneded to be the minimal as possible to run in a local environment, no replicas no high-availability for pods.
  > There is no ``externalIP`` and ``externalTrafficPolicy`` ingress has been changed from *Local* to **Cluster**.

- Check the status of the resources

        # Check the status of all resources
        sudo kubectl get pods,svc,pvc,pv,ingress -n devops

        # To check where ingress-controller exporse the Ports to the outside
        sudo kubectl get svc,ingress -n devops

- Installing certificates

        # To get the pem certificate from the secret
        kubectl get secret -n devops gitlab-wildcard-tls-ca -ojsonpath={.data.cfssl_ca} | base64 --decode > gitlab.192.168.99.100.nip.io.ca.pem

        # Install certificate on computers OS.
        certutil –addstore -enterprise –f "Root" gitlab.192.168.99.100.nip.io.ca.pem

- Initial login

        sudo kubectl get secret -n devops gitlab-gitlab-initial-root-password -ojsonpath={.data.password} | base64 --decode ; echo

- Check if it can be access to some of the resources:

  > First get where *gitlab-nginx-ingress-controller* NodePort service is configured for acceptinh Http outside connections.

  - [Minio Dashboard](http://minio.devops.com:30560)
  - [GitLab Dashboard](http://gitlab.devops.com:30560/)

#### GitLab Issues

##### Gitlab-runner

In order to create a runner, the parameters needed depends on the cluster created and the info gitlab provide in the ui.

- gitlabUrl : http://gitlab-unicorn.devops.svc.cluster.local:8080 (unicorn)
- runnerRegistrationToken: XczB7FPqhkTazACzQV6Mg74dGfQ8jA6NxlB1Et4eWUL5muStWQanR5EWwMaLfhLd
- rbac.create: true
- runners.cloneUrl: http://gitlab-unicorn.devops.svc.cluster.local:8181 (workhorse)

Finally, run the following command to install the runner chart

        sudo helm upgrade --install gitlab-runner gitlab/gitlab-runner --namespace devops --set runnerRegistrationToken=XczB7FPqhkTazACzQV6Mg74dGfQ8jA6NxlB1Et4eWUL5muStWQanR5EWwMaLfhLd,rbac.create=true,gitlabUrl=http://gitlab-unicorn.devops.svc.cluster.local:8080,runners.cloneUrl=http://gitlab-unicorn.devops.svc.cluster.local:8181

Verify the pod is currently running

##### Error 404, 500, etc

These errors are basically related with certificates and ssl connections through secure protocols.

To fix this issue use **http** protocol instead.

##### Fix: WARNING: This version of GitLab depends on gitlab-shell 8.1.1, but you're running Unknown. Please update gitlab-shell

> Simply install gitlab-unicorn + other dependendcies required

Get the full yaml definition from the ``gitlab-unicorn`` deployment

        sudo kubectl get deploy -n devops gitlab-unicorn -o yaml > /vagrant/files/gitlab-unicorn-deploy.yaml

Delete previous deployment since it is neccesary to remove some chackings

        sudo kubectl delete -n devops deploy/gitlab-unicorn

Add the ``BYPASS_SCHEMA_VERSION`` env variable to skip the ``wait-for-deps``

```yaml
  - args:
        - /scripts/wait-for-deps
        env:
        - name: BYPASS_SCHEMA_VERSION
          value: "1"
        - name: GITALY_FEATURE_DEFAULT_ON
          value: "1"
```

Finallt, create again the deplyment using the mod.

        sudo kubectl apply -f /vagrant/files/gitlab-unicorn-deploy.yaml

##### Minio Access key

In order to get minio *access-key* and *secret-key* is neccesary to access to the **secrets** and convert each key from Base64

    sudo kubectl get secret -n devops gitlab-minio-secret -o yaml

To get the ``accesskey`` and ``secretkey`` just parse the records from *json* format.

    # Get The Access Key
    sudo kubectl get secret -n devops gitlab-minio-secret -o jsonpath="{.data.accesskey}" | base64 --decode ; echo

    #Get the Secret Key
    sudo kubectl get secret -n devops gitlab-minio-secret -o jsonpath="{.data.secretkey}" | base64 --decode ; echo

##### Persistence Volume in **Pending** state

This *state* is caused because it has not been specified the StorageClassName. In this case, it is needed to manually change the configuration.

    sudo kubectl get -n devops pvc/gitlab-postgresql -o yaml > /vagrant/files/gitlab-postgresql-pvc.yaml

``` yaml
spec:
  storageClassName: nfs-slow   # line to add with the Storage class available
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
```

- Delete and apply following changes.

        sudo kubectl delete -n devops pvc/gitlab-postgresql
        sudo kubectl apply -f /vagrant/files/gitlab-postgresql-pvc.yaml

### SonarQube

SonarQube is an open sourced code quality scanning tool. The documentation of the helm-char can be found on this [link](https://github.com/helm/charts/tree/master/stable/sonarqube)

- Install the sonarqube helm-chart using the following command.

        sudo helm upgrade --install sonarqube stable/sonarqube --namespace devops --set ingress.enabled=true,ingress.hosts={sonarqube.devops.com},ingress.annotations."kubernetes\.io/ingress\.class"=traefik,service.type=NodePort,postgresql.persistence.storageClass=nfs-slow

- Add the new dns to the host file

        ```txt
        10.0.0.11   sonarqube.devops.com
        ```

- Login to [sonarqube](http://sonarqube.devops.com:31971)

    > The default login is admin/admin.

- Get the token to later review the code. (This can the gotten again using the *help* option *Analyze a new project*)

        Token: 4511865a5cd74f05fd27c43cceeb8d8ebe48605c

        mvn sonar:sonar -Dsonar.host.url=http://sonarqube.devops.com:31971 -Dsonar.login=4511865a5cd74f05fd27c43cceeb8d8ebe48605c

## References