# Kubernetes Cluster Suite

This folder creates a full kubernetes environment.

## Loading Kubernetes Cluster

- Load the cluster

    vagrant up

- In order to see the servers and nodes loaded

    vagrant status

    ```txt
    Current machine states:

    k8s-master                running (virtualbox)
    k8s-node1                 running (virtualbox)
    k8s-node2                 running (virtualbox)

    This environment represents multiple VMs. The VMs are all listed
    above with their current state. For more information about a specific
    VM, run `vagrant status NAME`.
    ```

- Open ssh with one of the instances

    vagrant ssh k8s-master

