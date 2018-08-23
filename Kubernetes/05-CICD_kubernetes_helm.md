# CI/CD With kubernetes

## Introduction

## Installation

### Nexus Repository Manager

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

    sudo helm upgrade --install gitlab gitlab/gitlab --version=1.0.1 --set global.hosts.domain=gitlab.devops.com,global.hosts.externalIP=10.0.0.11,certmanager-issuer.email=jsa4000@hotmail.com,redis.enabled=false,registry.enabled=false,global.minio.enabled=false,gitlab.gitaly.enabled=false,gitlab.sidekiq.enabled=false,gitlab.unicorn.enabled=false,gitlab.migrations.enabled=false,gitlab-runner.enabled=false

    sudo helm upgrade --install gitlab gitlab/gitlab --version=1.0.1 --set global.hosts.domain=gitlab.devops.com,global.hosts.externalIP=10.0.0.11,certmanager-issuer.email=jsa4000@hotmail.com,redis.enabled=false,registry.enabled=false,global.minio.persistence.storageClass=nfs-slow,gitlab.gitaly.enabled=false,gitlab.sidekiq.enabled=false,gitlab.unicorn.enabled=false,gitlab.migrations.enabled=false,gitlab-runner.enabled=false

    

- Check the status of the resources

    sudo kubectl get pods,svc,pvc,pv,ingress --all-namespaces


### Sonarkube

## References