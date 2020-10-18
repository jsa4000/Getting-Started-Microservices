# README

Cloud platforms provide a wealth of benefits for the organizations that use them. However, there’s no denying that adopting the cloud can put strains on DevOps teams. Developers must use microservices to architect for portability, meanwhile operators are managing extremely large hybrid and multi-cloud deployments. **Istio** lets you connect, secure, control, and observe services.

At a high level, **Istio** helps reduce the complexity of these deployments, and eases the strain on your development teams. It is a completely open source **service mesh** that layers transparently onto existing distributed applications. It is also a platform, including APIs that let it integrate into any logging platform, or telemetry or policy system. Istio’s diverse feature set lets you successfully, and efficiently, run a distributed microservice architecture, and provides a uniform way to secure, connect, and monitor microservices.

**Istio** makes it easy to create a network of deployed services with load balancing, service-to-service authentication, monitoring, and more, with few or no code changes in service code. You add Istio support to services by deploying a special sidecar proxy throughout your environment that intercepts all network communication between microservices, then configure and manage Istio using its control plane functionality, which includes:

- Automatic load balancing for HTTP, gRPC, WebSocket, and TCP traffic.
- Fine-grained control of traffic behavior with rich routing rules, retries, failovers, and fault injection.
- A pluggable policy layer and configuration API supporting access controls, rate limits and quotas.
- Automatic metrics, logs, and traces for all traffic within a cluster, including cluster ingress and egress.
- Secure service-to-service communication in a cluster with strong identity-based authentication and authorization.

**Istio** is designed for extensibility and meets diverse deployment needs. It does this by intercepting and configuring mesh traffic as shown in the following diagram:

![Istio](./images/arch.svg)

## Pre-Requisites

- Container Engine (Docker, Containerd, CRI-O, etc.) -> `Docker Engine 19.03.13`
  
  ```bash
  # Verify docker version installed
  docker version

  Client: Docker Engine - Community
  Cloud integration  0.1.18
  Version:           19.03.13

  Server: Docker Engine - Community
  Engine:
  Version:          19.03.13
  ```

- Kubernetes (Docker Desktop, Minikube, K3S, Kind) -> `Kubernetes v1.18.8`

  ```bash
  # Verify kubernetes version installed
  kubectl version

  Client Version: version.Info{Major:"1", Minor:"18", GitVersion:"v1.18.8", GitCommit:"9f2892aab98fe339f3bd70e3c470144299398ace", GitTreeState:"clean", BuildDate:"2020-08-13T16:12:48Z", GoVersion:"go1.13.15", Compiler:"gc", Platform:"darwin/amd64"}
  Server Version: version.Info{Major:"1", Minor:"18", GitVersion:"v1.18.8", GitCommit:"9f2892aab98fe339f3bd70e3c470144299398ace", GitTreeState:"clean", BuildDate:"2020-08-13T16:04:18Z", GoVersion:"go1.13.15", Compiler:"gc", Platform:"linux/amd64"}
  ```

- Helm 3 -> `Helm v3.3.4`
  
  > [Download link](https://github.com/helm/helm/releases)

  ```bash
  # Change the permissions and copy to user's bin folder
  chmod +x helm
  sudo cp helm /usr/local/bin/helm3

  # Check the helm version
  helm3 version

  version.BuildInfo{Version:"v3.3.4", GitCommit:"a61ce5633af99708171414353ed49547cf05013d", GitTreeState:"clean", GoVersion:"go1.14.9"}
  ```

## Install Istio

Following process is highly detailed in the official [Istio website](https://istio.io/latest/docs/setup/getting-started/)

1. Go to the Istio release [page](https://github.com/istio/istio/releases/) to download the installation file for your OS, or download and extract the latest release automatically (Linux or macOS).

  > It is a good practice to **always** specify the version to be installed.

  ```bash
  curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.7.3 TARGET_ARCH=x86_64 sh -
  ```

2. Move to the Istio package directory and add the istioctl client to your path (Linux or macOS) or copy it to global scope.

  ```bash
  # Export variable
  cd istio-1.7.3
  export PATH=$PWD/bin:$PATH

  # Global Scope. Change the permissions and copy to user's bin folder
  cd istio-1.7.3/bin
  chmod +x istioctl
  sudo cp istioctl /usr/local/bin/istioctl
  ```

3. Check current version

  ```bash
  istioctl version

  no running Istio pods in "istio-system"
  1.7.3
  ```

4. Install Istio.

    There are several default [profiles](https://istio.io/latest/docs/setup/additional-setup/config-profiles/) to install istio, depending on the requirements and tools to be used. It is useful to start from one of those profiles already created and perform the necessary modifications and tweaks.

    - **default**: enables components according to the default settings of the IstioOperator API. This profile is recommended for production deployments and for primary clusters in a multicluster mesh. You can display the default setting by running the command istioctl profile dump.
    - **demo**: configuration designed to showcase Istio functionality with modest resource requirements. It is suitable to run the Bookinfo application and associated tasks. This is the configuration that is installed with the quick start instructions.
    This profile enables high levels of tracing and access logging so it is not suitable for performance tests.
    - **minimal**: the minimal set of components necessary to use Istio’s traffic management features.
    - **remote**: used for configuring remote clusters of a multicluster mesh.
    empty: deploys nothing. This can be useful as a base profile for custom configuration.
    - **preview**: the preview profile contains features that are experimental. This is intended to explore new features coming to Istio. Stability, security, and performance are not guaranteed - use at your own risk.

    We use the `demo` configuration profile. It’s selected to have a good set of defaults for testing, but there are other profiles for production or performance testing.

    > Following operation will take several minutes depending on the internet connection and resources.

  ```bash
  # Check the profiles configuration values that will be used for istio operator
  code manifests/profiles/
  # Check the helm charts used by the operator ([operator-sdk](https://github.com/operator-framework/operator-sdk))
  code manifests/charts/

  # Install istio with demo profile
  istioctl install --set profile=demo

  ✔ Istio core installed
  ✔ Istiod installed
  ✔ Egress gateways installed
  ✔ Ingress gateways installed
  ✔ Installation complete
  ```

5. Verify the installation.

  > Istio has created a new namespace `istio-system`

  ```bash
  # Check istio-system namespace has been created
  kubectl get ns

  NAME              STATUS   AGE
  default           Active   50m
  istio-system      Active   97s
  kube-node-lease   Active   50m
  kube-public       Active   50m
  kube-system       Active   50m

  # Check for the new CRDs created
  kubectl get crd -n istio-system

  NAME                                       CREATED AT
  adapters.config.istio.io                   2020-10-14T09:15:39Z
  attributemanifests.config.istio.io         2020-10-14T09:15:39Z
  authorizationpolicies.security.istio.io    2020-10-14T09:15:39Z
  destinationrules.networking.istio.io       2020-10-14T09:15:39Z
  envoyfilters.networking.istio.io           2020-10-14T09:15:39Z
  gateways.networking.istio.io               2020-10-14T09:15:39Z
  ...

  # Check all pods installed are current running:  istiod, istio-egressgateway and istio-ingressgateway.
  kubectl get pods -n istio-system

  NAME                                    READY   STATUS    RESTARTS   AGE
  istio-egressgateway-66f8f6d69c-qwlpl    1/1     Running   0          5m24s
  istio-ingressgateway-758d8b79bd-blw74   1/1     Running   0          5m25s
  istiod-7556f7fddf-v4sl4                 1/1     Running   0          5m45s
  ```

6. Install the **addons+*

  > This will install the addons included with istio: `Kiali`, `Prometheus`, `Grafana`, `Jaeger`, etc..

  ```bash
  # Install istio addons
  kubectl apply -f samples/addons

  serviceaccount/grafana created
  configmap/grafana created
  service/grafana created
  deployment.apps/grafana created
  configmap/istio-grafana-dashboards created
  configmap/istio-services-grafana-dashboards created
  deployment.apps/jaeger created
  service/tracing created
  service/zipkin created
  customresourcedefinition.apiextensions.k8s.io/monitoringdashboards.monitoring.kiali.io created
  serviceaccount/kiali created
  configmap/kiali created
  clusterrole.rbac.authorization.k8s.io/kiali-viewer created
  clusterrole.rbac.authorization.k8s.io/kiali created
  clusterrolebinding.rbac.authorization.k8s.io/kiali created
  service/kiali created
  deployment.apps/kiali created
  serviceaccount/prometheus created
  configmap/prometheus created
  clusterrole.rbac.authorization.k8s.io/prometheus created
  clusterrolebinding.rbac.authorization.k8s.io/prometheus created
  service/prometheus created
  deployment.apps/prometheus created

  # Optional: Zipkin can be also installed
  kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.7/samples/addons/extras/zipkin.yaml

  deployment.apps/zipkin created
  service/tracing configured
  service/zipkin configured
  ```

7.  Verify the addons has been **installed** and **running**
  
  ```bash
  # Check all pods installed are current running:  grafana, prometheus, kiali, jaeger, zipkin
  kubectl get pods -n istio-system

  NAME                                    READY   STATUS    RESTARTS   AGE
  grafana-75b5cddb4d-xdcqp                1/1     Running   0          5m8s
  istio-egressgateway-66f8f6d69c-zjg44    1/1     Running   0          8m31s
  istio-ingressgateway-758d8b79bd-sdrdp   1/1     Running   0          8m32s
  istiod-7556f7fddf-bj76n                 1/1     Running   0          8m38s
  jaeger-5795c4cf99-x884f                 1/1     Running   0          5m8s
  kiali-6c49c7d566-xgzvk                  1/1     Running   0          5m8s
  prometheus-9d5676d95-p795n              2/2     Running   0          5m7s
  zipkin-556c4d54f5-9ntnx                 1/1     Running   0          76s

  # Check all services installed are current running:  grafana, prometheus, kiali, jaeger, zipkin
  kubectl get svc -n istio-system
  NAME                   TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                                                                      AGE
  grafana                ClusterIP      10.98.120.248   <none>        3000/TCP                                                                     6m3s
  istio-egressgateway    ClusterIP      10.99.110.55    <none>        80/TCP,443/TCP,15443/TCP                                                     9m27s
  istio-ingressgateway   LoadBalancer   10.102.89.11    localhost     15021:32749/TCP,80:32607/TCP,443:31412/TCP,31400:31448/TCP,15443:32618/TCP   9m27s
  istiod                 ClusterIP      10.111.234.39   <none>        15010/TCP,15012/TCP,443/TCP,15014/TCP,853/TCP                                9m33s
  kiali                  ClusterIP      10.98.97.159    <none>        20001/TCP,9090/TCP                                                           6m3s
  prometheus             ClusterIP      10.107.5.242    <none>        9090/TCP                                                                     6m2s
  tracing                ClusterIP      10.99.145.194   <none>        80/TCP                                                                       6m3s
  zipkin                 ClusterIP      10.99.136.43    <none>        9411/TCP                                                                     6m3s
  ```

8. Create a namespace to deploy the microservices

   > In the following example we will create a new namespace `microservices` to be used with istio.

  ```bash
  # Create the namespace where microservices will be deployed
  kubectl create ns microservices

  namespace/microservices created

  # Show all the namespaces
  kubectl get ns

  NAME              STATUS   AGE
  default           Active   4d6h
  istio-system      Active   11m
  kube-node-lease   Active   4d6h
  kube-public       Active   4d6h
  kube-system       Active   4d6h
  microservices     Active   36s
  ```

9. Switch to current namespace
  
  ```bash
  # Switch to current context from ddefault
  kubectl config set-context --current --namespace=microservices

  Context "docker-desktop" modified.

  # Get the current context info and namespace
  kubectl config get-contexts

  CURRENT          NAME             CLUSTER          AUTHINFO         NAMESPACE
  *                docker-desktop   docker-desktop   docker-desktop   microservices
  ```

10.  Deploy the **sample** application based on kubernetes native resources  

  ```bash
  # Check the resources to be created
  code src/01_bookinfo_deployment/

  # Create the  book info application
  kubectl apply -f src/01_bookinfo_deployment/

  service/details created
  serviceaccount/bookinfo-details created
  deployment.apps/details-v1 created
  service/ratings created
  serviceaccount/bookinfo-ratings created
  deployment.apps/ratings-v1 created
  service/reviews created
  serviceaccount/bookinfo-reviews created
  deployment.apps/reviews-v1 created
  deployment.apps/reviews-v2 created
  deployment.apps/reviews-v3 created
  service/productpage created
  serviceaccount/bookinfo-productpage created
  ```

11. Wait until all the pods are currently`running`.

  > Actually istio is **NOT** currently working. Pods have only one pod running 1/1,  so the SideCar is not active yet.

  ```bash
  # Create the  book info application
  kubectl get pods -w

  NAME                              READY   STATUS    RESTARTS   AGE
  details-v1-558b8b4b76-6snwg       1/1     Running   0          85s
  productpage-v1-6987489c74-ft6pf   1/1     Running   0          84s
  ratings-v1-7dc98c7588-sqqnx       1/1     Running   0          84s
  reviews-v1-7f99cc4496-vgj4t       1/1     Running   0          84s
  reviews-v2-7d79d5bd5d-qqfhn       1/1     Running   0          84s
  reviews-v3-7dbcdcbc56-tn6nw       1/1     Running   0          84s

  # Get the services
  kubectl get svc
  NAME          TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
  details       ClusterIP   10.99.0.151     <none>        9080/TCP   3m33s
  productpage   ClusterIP   10.96.164.128   <none>        9080/TCP   3m32s
  ratings       ClusterIP   10.99.104.182   <none>        9080/TCP   3m33s
  reviews       ClusterIP   10.99.131.90    <none>        9080/TCP   3m32s
  ```

12. Verify the deployment using `port-forward` and accessing to the `prodcutpage`


  ```bash
  # Create the  book info application
  kubectl port-forward svc/productpage 9080:9080

  # Verify using a webbrowser.
  http://localhost:9080

  ```

12. Open `grafana` and `Kiali` application using `istioctl` tool.

  > Once `grafana` and `Kiali` are opened there is nothing to show yet by Istio in the graph
 
  ```bash
  # Open kiali dashboard -> Graph -> Empty Graph
  istioctl dashboard kiali

  # Open grafana dashboard -> Go to istio-mesh-dashboard at http://localhost:3000/d/G8wLrJIZk/istio-mesh-dashboard?orgId=1&refresh=5s
  istioctl dashboard grafana
  ```

13. Create Istio `Gateway` and `VirtualService` for the `productpage` service to open the connection to the outside

  ```bash
  # Check the resources to be created
  code src/02_bookinfo_gateway/

  # Create the  book info application
  kubectl apply -f src/02_bookinfo_gateway/
  gateway.networking.istio.io/bookinfo-gateway created
  virtualservice.networking.istio.io/bookinfo created
  ```

14. Verify the connection using the load balancer (`ServiceType -> LoadBalancer`) created by istio ingress controller

  > It is working as a normal ingress-controller such as `nginx` or `traefik`

  ```bash
  # Get the EXTERNAL-IP and Ports available. 80/443
  kubectl get svc -n istio-system
  kubectl get svc -n istio-system | grep istio-ingressgateway

  NAME                   TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                                                                      AGE
  istio-ingressgateway   LoadBalancer   10.102.89.11    localhost     15021:32749/TCP,80:32607/TCP,443:31412/TCP,31400:31448/TCP,15443:32618/TCP   32m

  # Use the EXTERNAL-IP and Port s 80:32607/TCP,443:31412
  http://localhost:80/productpage
  ```

15. Open `grafana` and `Kiali` application using `istioctl` tool.

  > Once `grafana` and `Kiali` are opened there is **still** nothing to show yet by Istio in the graph
 
  ```bash
  # Open kiali dashboard -> Graph -> Empty Graph
  istioctl dashboard kiali

  # Check in IstioConfig the VirtualServie and Gateway habe been created and both are ok.
  http://localhost:20001/kiali/console/istio?namespaces=microservices

  # Open grafana dashboard -> Go to istio-mesh-dashboard at http://localhost:3000/d/G8wLrJIZk/istio-mesh-dashboard?orgId=1&refresh=5s
  istioctl dashboard grafana
  ```

16. Create a **label** into the namespace created to automatically inject **Envoy Sidecar Proxies** into Pods.

  ```bash
  # Set the auto injection label so istio knows the namespace to monitor
  kubectl label namespace microservices istio-injection=enabled

  namespace/default labeled

  # Verify the label has been created
  kubectl get ns --show-labels  

  NAME              STATUS   AGE    LABELS
  default           Active   4d7h   <none>
  istio-system      Active   42m    istio-injection=disabled
  kube-node-lease   Active   4d7h   <none>
  kube-public       Active   4d7h   <none>
  kube-system       Active   4d7h   <none>
  microservices     Active   31m    istio-injection=enabled
  ```

17. Verify the Pods currently running.

  > There is no Sidecar Proxy running yet `1/1 Running`

  ```bash
  # Get the pods in microservice namespace
  kubectl get pods

  NAME                              READY   STATUS    RESTARTS   AGE
  details-v1-558b8b4b76-6snwg       1/1     Running   0          28m
  productpage-v1-6987489c74-ft6pf   1/1     Running   0          28m
  ratings-v1-7dc98c7588-sqqnx       1/1     Running   0          28m
  reviews-v1-7f99cc4496-vgj4t       1/1     Running   0          28m
  reviews-v2-7d79d5bd5d-qqfhn       1/1     Running   0          28m

  # There is no Sidecar yet configured
  ```

17. Force to stop all the pods and verify again the status

  > There is no Sidecar Proxy running yet `1/1 Running`

  ```bash
  # Delete all the pods
  kubectl get pods | awk '{print $1}' | xargs kubectl delete pod
  kubectl get pods -o=name | kubectl delete

  # Get the pods in microservice namespace
  kubectl get pods

  NAME                              READY   STATUS    RESTARTS   AGE
  details-v1-558b8b4b76-64pbq       2/2     Running   0          57s
  productpage-v1-6987489c74-nh7f6   2/2     Running   0          57s
  ratings-v1-7dc98c7588-qs64j       2/2     Running   0          57s
  reviews-v1-7f99cc4496-p85kw       2/2     Running   0          56s
  reviews-v2-7d79d5bd5d-lrqnd       2/2     Running   0          56s
  reviews-v3-7dbcdcbc56-qprm2       2/2     Running   0          56s
  ```


17. Create new namespace
18. Install demo application and default kubernetes ingress controller (Show the yaml files content)
19. Remove ingress  controller (nginx)
20. Install istio and show the pods created into istio-system (Show the diferent profiles)
21. Install sample addons
22. Create gateway and virual service (Show the yaml files content)
23. Test the aplication and show grafana and kiali (empty)
24. Set the auto-injection label into workspace
25. Remove7stop all the pods and install again (force the sidecar to be injected)
26. Test again
27. Proceed with the next examples using Virtual Services, DestinationRules, etc..