# Kubernetes Advanced

## ConfigMaps

[Configure Pods ConfigMaps](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/)

**ConfigMaps** allows you to decouple *configuration* artifacts from image content to keep containerized applications portable.

ConfigMap types have some **limitations**:

- The limit for **configMap** and **secrets** is 1MB.
- Do not put information such as **end-points**, **networking** or **security-keys**, since **k8s** manage this information in other ways using **secrets**, **namespaces**, **service-discovery**, etc..

Following a **configmap** yaml file that defines key-value pair attributes for a *game-config*.

``game-config.yaml``

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: game-config
  namespace: default
data:
  # Define single key-value pairs
  color: purple
  mode: hard
  textmode: "true"
  # Define a key-value pair with a configuration properties (from-file)
  game.properties: |
    enemies=aliens
    lives=3
    enemies.cheat=true
    enemies.cheat.level=noGoodRotten
    secret.code.passphrase=UUDDLRLRBABAS
    secret.code.allowed=true
    secret.code.lives=30
```

Use the following command to apply this configmap into k8s cluster

    sudo kubectl apply -f game-config.yaml

### Kubectl Creation

Previous configMap files can be also **generated** via *kubectl*.

Following command **creates** a ConfigMap into K8s cluster with all files contents.

> ``--from-file`` does not take into account the single key-value pairs only the files inside the folder.

    kubectl create configmap game-config --from-file=/configmap/game/

To show the content, run the following commands

    sudo kubectl describe configmap/game-config
    sudo kubectl get configmap/game-config -o yaml

In order to get a concrete key from configmap, it can be used the following command

    sudo kubectl get configmap/game-config -o jsonpath="{.data.ui\.properties}"

> Note the ``\.`` that is used for the key. The ``.`` instead is used to separate the hierarchy of the yaml file.

### ConfigMap Basic Example

For the example, the image being used is the **busybox** image.

The idea is to **print** the **environmental** variables we are setting from the *configmaps* previously created.

Following a *deployment* that get some keys from the configmap as **environment** variables and other as **volume** to create the file needed to start or update the settings for app.

``game-pod.yaml``

```yaml
apiVersion: v1
kind: Pod
metadata:
    name: game-pod
spec:
    containers:
    - name: game-container
      image: k8s.gcr.io/busybox
      # Get all the env variables configured. No 'args' tag required
      #command: [ "/bin/sh", "-c", "env" ]
      # Show the content of the file. No 'args' tag required
      #command: [ "/bin/sh", "-c", "cat /etc/config/game.properties" ]
      command: ["/bin/sh","-c"]
      args: ["env; cat /etc/config/game.properties"]
      env:
        # Define a default environment variable
        - name: GAME_ROOT_USER
          value: "default"
        # Define the environment variable using configmap
        - name: GAME_COLOR_KEY
          valueFrom:
            configMapKeyRef:
              # The ConfigMap containing the value you want to assign to GAME_COLOR_KEY
              name: game-config
              # Specify the key associated with the value
              key: color
      # Mount the volume with a path to store the configmap files inside the container
      volumeMounts:
      - name: game-properties-volume
        # Define the path inside the container for the files store in configmap
        mountPath: /etc/config
    volumes:
    # Create the valume previously referenced
    - name: game-properties-volume
      configMap:
        # Reference the config map that is going to be used
        name: game-config
        items:
        - key: game.properties
          path: game.properties
    restartPolicy: Never
```

Create the Pod

    sudo kubectl apply -f game-pod.yaml

Get the pods to see if previous pod has been created.

    sudo kubectl get pods

Verify all the variables in the configmap configured have been set correctly.

    sudo kubectl logs game-pod

The **output** from the logs should display the *enviroment variables* previously configured and the content of the ``game.properties`` file mounted.

```txt
KUBERNETES_PORT=tcp://10.96.0.1:443
KUBERNETES_SERVICE_PORT=443

GAME_COLOR_KEY=purple                    # From game-config ConfigMap

KUBERNETES_PORT_443_TCP_PROTO=tcp
PROMETHEUS_SERVICE_PORT_WEB=9090

GAME_ROOT_USER=default                   # From game-config ConfigMap

KUBERNETES_SERVICE_HOST=10.96.0.1
```

```txt
enemies=aliens
lives=3
enemies.cheat=true
enemies.cheat.level=noGoodRotten
secret.code.passphrase=UUDDLRLRBABAS
secret.code.allowed=true
secret.code.lives=30
```

> It can be used **configmaps** to set all the environment variables at once into a pod. This is by using the key word ``envFrom:configMapRef:name`` instead ``env:[]``

## Secrets

[Sercrets on Kubernetes](https://kubernetes.io/docs/concepts/configuration/secret/)

Objects of type **secret** are intended to hold sensitive information, such as *passwords*, *OAuth tokens*, and *ssh keys*.

Putting this information in a **secret** is safer and more flexible than putting it verbatim in a **pod** definition or in a docker image

The values need to be encoded in **base64**

``game-secret.yaml``

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: game-secret
type: Opaque
data:
  dbname: dXJsX3Nob3J0ZW5lcl9kYg==
  username: dXNlcg==
password: bXlzZWNyZXRwYXNzd29yZA==
```

Secrets can be automatically generated by kubectl.

> The information will be automatilcaly converted into *base64*.

- By **key-value-pair** using ``--from-literal``.

        sudo kubectl create secret generic game-secret --from-literal=username='admin' --from-literal=password='admin123'

- By **file** using ``--from-file``.

        sudo kubectl create secret generic game-secret --from-file=./username.txt --from-file=./password.txt

In order to get the secret use the following command

    sudo kubectl get secret game-secret -o yaml

In order to get a key from the secret and *decode* it from *base64*.

    sudo kubectl get secret game-secret -o jsonpath="{.data.username}" | base64 --decode; echo
    sudo kubectl get secret game-secret -o jsonpath="{.data.password}" | base64 --decode; echo

## Secrets Example

For the example, the image being used is the **busybox** image.

The idea is to **print** the **environmental** variables we are setting from the *configmaps* previously created.

Following a *deployment* that get some keys from the configmap as **environment** variables and other as **volume** to create the file needed to start or update the settings for app.

``game-pod.yaml``

```yaml
apiVersion: v1
kind: Pod
metadata:
    name: game-pod
spec:
    containers:
    - name: game-container
      image: k8s.gcr.io/busybox
      # Get all the env variables configured. No 'args' tag required
      #command: [ "/bin/sh", "-c", "env" ]
      # Show the content of the file. No 'args' tag required
      #command: [ "/bin/sh", "-c", "cat /etc/foo/game-username" ]
      command: ["/bin/sh","-c"]
      args: ["env; cat /etc/foo/game-username; cat /etc/foo/game-password"]
      env:
        # Define a default environment variable
        - name: GAME_ROOT_USER
          value: "default"
        # Define the environment variable using secrets
        - name: GAME_USER_NAME
          valueFrom:
            secretKeyRef:
              # The secrets containing the value you want to assign to GAME_COLOR_KEY
              name: game-secret
              # Specify the key associated with the value
              key: username
         # Define the environment variable using secrets
        - name: GAME_USER_PASSWORD
          valueFrom:
            secretKeyRef:
              # The secrets containing the value you want to assign to GAME_COLOR_KEY
              name: game-secret
              # Specify the key associated with the value
              key: password
      # Mount the volume with a path to store the secrets files inside the container
      volumeMounts:
      - name: game-secrets-volume
        # Define the path inside the container for the files store in secrets
        mountPath: /etc/foo
    volumes:
    # Create the valume previously referenced
    - name: game-secrets-volume
      secret:
        # Reference the config map that is going to be used
        secretName: game-secret
        items:
        - key: username
          path: game-username
          mode: 256
        - key: password
          path: game-password
          mode: 256
    restartPolicy: Never
```

> Mode **511** is equal to permission value of **0777**. Owing to JSON limitations, it must be specified the **mode** in **decimal** notation.

Create the Pod

    sudo kubectl apply -f game-pod.yaml

Get the pods to see if previous pod has been created.

    sudo kubectl get pods

Verify all the variables in the configmap configured have been set correctly.

    sudo kubectl logs game-pod

```txt
KUBERNETES_PORT=tcp://10.96.0.1:443
KUBERNETES_SERVICE_PORT=443
HOSTNAME=game-pod

GAME_USER_PASSWORD=admin123             # From game-secrets file

PATH=/usr/local/sbin:/usr/local/bin:
PROMETHEUS_PORT_9090_TCP_PROTO=tcp

GAME_USER_NAME=admin                    # From game-secrets file

KUBERNETES_SERVICE_HOST=10.96.0.1
```

```txt
adminadmin123
```

## Volumes

## Stateful Deployments

## Horizontal Auto-scaling

## Roles and Rules

## Affinity vs NodeSelector

## Loggin

[Logging](https://kubernetes.io/docs/concepts/cluster-administration/logging/)