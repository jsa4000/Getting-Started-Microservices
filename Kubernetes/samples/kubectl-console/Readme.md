# Node.js Sample

## Build Docker image

In order to build the docker image:

- Locate the Dockerfile with the definition of the file

- Build the dockerfile

    sudo docker build -t nodejs-sample:v1 .

- Test the docker image (run the image)

    sudo docker run -d -p 8080:8080 nodejs-sample:v1

- Test the service invoking the URL

    curl http://localhost:8080/

## Publish image to a Docker repository

In order to create Docker images and publish them into a repository (public or private)

### Github

- Sign up for an account on [https://hub.docker.com/](https://hub.docker.com/). After verifying your email you are ready to go and upload your first docker image.

- Log in on https://hub.docker.com/

- Click on Create Repository. Choose a name (e.g. samples) and a description for your repository and click Create.

- Log into the Docker Hub from the command line (Enter your password when prompted)

   sudo docker login --username=jsa4000

- Check the image ID using

    sudo docker images

    ```txt
    REPOSITORY                                          TAG                 IMAGE ID            CREATED             SIZE
    nodejs-sample                                       v1                  58a8d4f0d19a        About an hour ago   655MB
    kubernetesdashboarddev/kubernetes-dashboard-amd64   head                7a05e890e6f3        5 days ago          122MB
    k8s.gcr.io/kube-proxy-amd64                         v1.11.1             d5c25579d0ff        6 days ago          97.8MB

    k8s.gcr.io/pause                                    3.1                 da86e6ba6ca1        7 months ago        742kB
    node                                                6.9.2               faaadb4aaf9b        19 months ago       655MB
    ```

- Create a **tag** for your image (use IMAGE ID)

    sudo docker tag 58a8d4f0d19a jsa4000/nodejs-sample:initial

- Push your image to the repository you created

    sudo docker push jsa4000/nodejs-sample

    ```txt
    The push refers to repository [docker.io/jajsa4000viersantos/nodejs-sample]
    c7ec1e07708b: Pushed 
    381c97ba7dc3: Mounted from library/node
    604c78617f34: Mounted from library/node
    fa18e5ffd316: Mounted from library/node
    0a5e2b2ddeaa: Mounted from library/node
    53c779688d06: Mounted from library/node
    60a0858edcd5: Mounted from library/node
    b6ca02dfe5e6: Mounted from library/node
    initial: digest: sha256:0f948adca6e58d1c062e9b4d053299cfdaf139ac834d9492ca6c0c3c7b211636 size: 2002
    ```

- Verify the image has been succesfully published

    sudo docker pull jsa4000/nodejs-sample:initial

### Private Repository

To add a new (docker) respository into Kubernetes env use the following command

    kubectl create secret docker-registry my-secret --docker-server=123.456.789.0:9595 --docker-username=admin --docker-password=XXXX --docker-email=test@xyz.com

## Create the Deployment

In orde to create the deployment, no declarative way, use the following command:

    kubectl run nodejs-sample --image=jsa4000/nodejs-sample:initial --port=8080

- Check the current status for *deployments* and *pods*

    kubectl get deployments
    kubectl get pods
    kubectl get events

- To see the configuration for Kubernetes

    kubectl config view

> This image must be create in all nodes or pulled from a respository. The error in this case is *ErrImageNeverPull*

- To delete the deployments execute the following command

     kubectl delete deployments nodejs-sample

## Create the Service

In order to expose the pods or deployments to other pods or externally, it is neccesary to create services on kubernetes

- To expose a Pod/deployment to the public internet using the kubectl expose command:

    kubectl expose deployment nodejs-sample --type=NodePort

- Display information about the Service:

    kubectl get services nodejs-sample
    kubectl describe services nodejs-sample

    ```txt
    Name:                     nodejs-sample
    Namespace:                default
    Labels:                   run=nodejs-sample
    Annotations:              <none>
    Selector:                 run=nodejs-sample
    Type:                     NodePort
    IP:                       10.99.106.48
    Port:                     <unset>  8080/TCP
    TargetPort:               8080/TCP
    NodePort:                 <unset>  32132/TCP
    Endpoints:                10.100.1.5:8080
    Session Affinity:         None
    External Traffic Policy:  Cluster
    Events:                   <none>
    ```

- Verify the service just locating the k8s-node used for the deplyment (if no replica) and the port assigned

    curl http://10.0.0.12:32132/

- Create 3 replicas so the deployment/service will be replicated over the nodes (if admin is not configured as replica node)

    kubectl scale deployments/nodejs-sample --replicas=3

    curl http://10.0.0.12:32132/
    curl http://10.0.0.13:32132/

- To delete a service already created

    kubectl delete services nodejs-sample

    ```txt
    Hello World!
    ```

## YAML Files

Previous commands can be described as **DSL** files in order to create or update **operators** (deployment, service, deamonset, ..) in kubernetes

Create a file with the deplyment *nodejs-sample-deployment.yaml*

```yml
apiVersion: apps/v1 # for versions before 1.9.0 use apps/v1beta2
kind: Deployment
metadata:
  name: nodejs-deployment
spec:
  selector:
    matchLabels:
      name: nodejs
  replicas: 2 # tells deployment to run 2 pods matching the template
  template:
    metadata:
      labels:
        name: nodejs
    spec:
      containers:
      - name: nodejs-app
        image: jsa4000/nodejs-sample:initial
        ports:
        - containerPort: 8080
```

Create a file with the service (NodePort type) to expose *nodejs-sample-service.yaml*

```yml
apiVersion: v1
kind: Service
metadata:
  name: nodejs-service
  labels:
    name: nodejs-service
spec:
  type: NodePort
  ports:
    - port: 8080
      nodePort: 30080
      name: http
      protocol: TCP
  selector:
    name: nodejs
```

**Create** or **Apply*** (update) both definitions using yml files.

    kubectl create -f nodejs-sample-deployment.yaml
    kubectl create -f nodejs-sample-service.yaml

To scale the deployment use following command or update yml files replica parameter and use **apply** to update.

    kubectl scale -f nodejs-sample-deployment.yaml --replicas=3

To remove previous deployment and service

    kubectl delete -f nodejs-sample-deployment.yaml
    kubectl delete -f nodejs-sample-service.yaml
