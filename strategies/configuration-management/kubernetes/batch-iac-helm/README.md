# Helm chart for Example Batch Processes 

This is a chart to be used by the batch processes:

- To Provide confimaps and secrets to be pulled at runtime
- To Provide a self-registration method by using a Custom Resource Definition in kubernetes CRD

## Install CDR

1. Create a namespace `test`

    ```bash
    kubectl create ns test
    ```

2. Switch to that context `test`

    ```bash
    kubectl config set-context --current --namespace=test
    ```

3. Install custom CRD `batch-process-crd.yaml`  used for Batch Processes

    ```bash
    kubectl apply -f batch-process-crd.yaml
    ```

4. Verify the CDR has been installed successfully

    ```bash
    kubectl get crd
    ```

To delete the CDR use the following command

```bash
kubectl delete -f batch-process-crd.yaml
```

## Install Chart

- Install current chart

```bash
helm install -f values.yaml --name=batchprocess --tiller-namespace=test --namespace=test .

## Test from configenvironmentes+/spa/DEV
helm install --debug --dry-run -f values.yaml --name=batchprocess --tiller-namespace=test --namespace=test .

## Install the chart without the crd
helm install -f values.yaml --name=batchprocess --tiller-namespace=test --namespace=test --set batch.create=false .
```

- Remove the helm chart

  ```bash
  helm delete batchprocess --tiller-namespace=test --purge
  ```

- Access to the current pod via 

```bash
# Get the pod used to bind the secrets and configmap
kubectl get pods | grep 'springcloud' | awk '{print $1}'

# Port-forward to expose 8080
kubectl port-forward pods/$(kubectl get pods | grep 'springcloud' | awk '{print $1}') 8080:8080
```
