# CI/CD With kubernetes

## Introduction

## Installation

### Nexus Repository Manager

Sonatype Nexus Repository Manager Comunity.

> Supported formats: Bower, Docker, git-lfs, Maven 2, npm, NuGet, PyPI, RubyGems, Site/Raw

In order to install Nexus into a kubernets cluster use the following command

> In the current chart-helm version there is an *error* with the **nexus-proxy** pods. In order to **solve** the issue is neccesary to force helm to get the nexus-proxy image **v2.2.0**.

    sudo helm upgrade --install sonatype-nexus stable/sonatype-nexus --namespace devops --set nexusProxy.imageTag=2.2.0,persistence.storageClass=nfs-slow,nexusBackup.persistence.enabled=false,ingress.enabled=true,ingress.annotations."kubernetes\.io/ingress\.class"=traefik,nexusProxy.env.nexusHttpHost=nexus.devops.com,ingress.tls.enabled=false

> The default login is admin/admin123

Add the DNS to the host file on your system

```txt
10.0.0.11   nexus.devops.com
```

Those are the URLs to access to Sonatype Nexus Repository.

- [Nexus Dashboard](http://nexus.devops.com:31971)

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

- Install helm. In this [link](https://gitlab.com/charts/gitlab/blob/master/doc/installation/command-line-options.md) it can be seen all que commands.

    sudo helm upgrade --install gitlab gitlab/gitlab --version=1.0.1 --namespace devops --set global.hosts.domain=devops.com,global.hosts.externalIP=10.0.0.11,certmanager-issuer.email=jsa4000@hotmail.com,registry.enabled=false,minio.persistence.storageClass=nfs-slow,gitlab.gitaly.enabled=false,gitlab.sidekiq.enabled=false,gitlab.unicorn.enabled=false,gitlab.migrations.enabled=false,gitlab-runner.install=false,prometheus.install=false,certmanager.install=false,certmanager.rbac.create=false,prometheus.rbac.create=false,gitlab-runner.rbac.create=false,redis.persistence.storageClass=nfs-slow

    sudo helm upgrade --install gitlab gitlab/gitlab --version=1.0.1 --namespace devops -f /vagrant/files/values-minikube-minimum.yaml

- Check the status of the resources

    sudo kubectl get pods,svc,pvc,pv,ingress -n devops

- Since there is no parameter to specify the StorageClassName, it is needed to manually change the configuration.

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
10.0.0.11   sonarqube.monitoring.com
```

- Login to [sonarqube](http://sonarqube.devops.com:31971)

> The default login is admin/admin.

- Get the token to later review the code. (This can the gotten again using the *help* option *Analyze a new project*)

    Token: 4511865a5cd74f05fd27c43cceeb8d8ebe48605c

    mvn sonar:sonar -Dsonar.host.url=http://sonarqube.devops.com:31971 -Dsonar.login=4511865a5cd74f05fd27c43cceeb8d8ebe48605c

## References