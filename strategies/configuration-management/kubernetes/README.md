# Kubernetes Configuration Management

Kubernetes is inspired in all the good practices that applies to distributed systems.
In this case, tools such as Consul, etcd, etc are very well integrated with programing frameworks such as Spring, .NET, etc.. that provides a way to retrieve configuration and to be in-sync with the latest changes. Other tools that are available to manage confidential or sensible information such as passwords, secrets, etc.. is Vault. This tools also is very well integrated with previous frameworks mentioned.


 However kubernetes has it own default wat to manage all this configuration, by using ConfigMap and Secrets.
