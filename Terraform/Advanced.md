# Terraform Advanced

## Introduction

This is a document to explain more advanced topics for the creation of a Cloud infraestructure using Terrafom.

Cloud infraestructures requires core components such as: VPC, Subnets, NET Gateways, DNS, Routing, Security Groups, VPN, KMS.. Also, components in upper layers such as ECS, ELB, EVS, IMS... become important during the creation of the PaaS.

## Strucuture

[Introduction to OpenStack](https://docs.openstack.org/security-guide/introduction/introduction-to-openstack.html)

This is the basic structure of a Cloud Infraestrcuture (*Native Open Stack*)

- **Compute**: OpenStack Compute service (nova) provides services to support the management of virtual machine instances at scale, instances that host multi-tiered applications, dev or test environments, “Big Data” crunching Hadoop clusters, or high-performance computing.
- **Object Storage**: The OpenStack Object Storage service (swift) provides support for storing and retrieving arbitrary data in the cloud. The Object Storage service provides both a native API and an Amazon Web Services S3-compatible API. It is important to understand that object storage differs from traditional file system storage. Object storage is best used for static data such as media files (MP3s, images, or videos), virtual machine images, and backup files.
- **Block Storage**: The OpenStack Block Storage service (cinder) provides persistent block storage for compute instances. The Block Storage service is responsible for managing the life-cycle of block devices, from the creation and attachment of volumes to instances, to their release.
- **Shared File Systems**: The Shared File Systems service (manila) provides a set of services for managing shared file systems in a multi-tenant cloud environment, similar to how OpenStack provides for block-based storage management through the OpenStack Block Storage service project. With the Shared File Systems service, you can create a remote file system, mount the file system on your instances, and then read and write data from your instances to and from your file system.
- **Networking**: The OpenStack Networking service provides various networking services to cloud users (tenants) such as IP address management, DNS, DHCP, load balancing, and security groups (network access rules, like firewall policies). This service provides a framework for software defined networking (SDN) that allows for pluggable integration with various networking solutions.
- **Dashboard**: The OpenStack Dashboard (horizon) provides a web-based interface for both cloud administrators and cloud tenants. Using this interface, administrators and tenants can provision, manage, and monitor cloud resources.
- **Identity service**: The OpenStack Identity service (keystone) is a shared service that provides authentication and authorization services throughout the entire cloud infrastructure. The Identity service has pluggable support for multiple forms of authentication. Oftenly calles IAM or *Identity And Access Management*.
- **Image service**: The OpenStack Image service (glance) provides disk-image management services, including image discovery, registration, and delivery services to the Compute service, as needed.
- **Data processing service**: The Data Processing service (sahara) provides a platform for the provisioning, management, and usage of clusters running popular processing frameworks.
- **Other supporting technology**: Messaging is used for internal communication between several OpenStack services. By default, OpenStack uses message queues based on the AMQP. Like most OpenStack services, AMQP supports pluggable components. Today the implementation back end could be RabbitMQ, Qpid, or ZeroMQ.

## Variables



## Modules



## Infraestructure

## Example
