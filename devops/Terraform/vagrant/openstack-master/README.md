# All-In-One Single Machine

[Official guide installing All-In-One Single Machine](https://docs.openstack.org/devstack/latest/guides/single-machine.html)

## Minimal Install

You need to have a system with a fresh install of Linux. You can download the Minimal CD for Ubuntu releases since DevStack will download & install all the additional dependencies. The netinstall ISO is available for Fedora and CentOS/RHEL. You may be tempted to use a desktop distro on a laptop, it will probably work but you may need to tell Network Manager to keep its fingers off the interface(s) that OpenStack uses for bridging.
Network Configuration.

Determine the network configuration on the interface used to integrate your OpenStack cloud with your existing network. For example, if the IPs given out on your network by DHCP are 192.168.1.X - where X is between 100 and 200 you will be able to use IPs 201-254 for **floating ips**.

To make things easier later change your host to use a static IP instead of DHCP (i.e. 192.168.1.201).
Installation shake and bake.

## Create OpenStack User

We need to add a user to install DevStack. (if you created a user during install you can skip this step and just give the user sudo privileges below)

    sudo su
    useradd -s /bin/bash -d /opt/stack -m stack

Since this user will be making many changes to your system, it will need to have sudo privileges:

    echo "stack ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

From here on you should use the user you created. Logout and login as that user.

    su -l stack  

    # if not sudo user
    sudo su -l stack

## Download DevStack

We’ll grab the latest version of DevStack via https:

    sudo apt-get install git -y
    git clone https://git.openstack.org/openstack-dev/devstack
    cd devstack

## Run DevStack

[Configuring DevStack for Horizon](https://docs.openstack.org/horizon/latest/contributor/ref/local_conf.html)

Now to configure ``stack.sh``. DevStack includes a sample in ``devstack/samples/local.conf``.

> This file must be in ``LF`` format (End Of Line mode).

Create ``local.conf`` as shown below to do the following:

- Set ``FLOATING_RANGE`` (CIDR) to a range not used on the local network, i.e. 10.0.0.224/27. This configures IP addresses ending in 225-254 to be used as **floating IP**s.
- Set ``FIXED_RANGE`` and ``FIXED_NETWORK_SIZE`` to configure the internal address space used by the instances.
- Set ``FLAT_INTERFACE`` to the Ethernet interface that connects the host to your local network. This is the interface that should be configured with the static IP address mentioned above.
- Set the administrative password. This password is used for the admin and demo accounts set up as OpenStack users.
- Set the ``DATABASE_PASSWORD`` administrative password. The default here is a random hex string which is inconvenient if you need to look at the database directly for anything.
- Set the ``RABBIT_PASSWORD`` password.
- Set the ``SERVICE_PASSWORD`` password. This is used by the OpenStack services (Nova, Glance, etc) to authenticate with Keystone.
- Set the ``HOST_IP`` with the IP Address of current host.

``local.conf`` should look something like this:

```bash
[[local|localrc]]
ADMIN_PASSWORD=password
DATABASE_PASSWORD=$ADMIN_PASSWORD
RABBIT_PASSWORD=$ADMIN_PASSWORD
SERVICE_PASSWORD=$ADMIN_PASSWORD
HOST_IP=10.0.0.10
FLOATING_RANGE=10.0.0.224/27
LOGFILE=$DEST/logs/stack.sh.log
LOGDAYS=2
SWIFT_HASH=66a3d6b56c1f479c8b4e70ab5c2000f5
SWIFT_REPLICAS=1
SWIFT_DATA_DIR=$DEST/data
```

> It can be used the pre-made configuration inside ``/vagrant/files/`` folder.  i.e ``cp /vagrant/files/local.lbaas.conf local.conf``

Run DevStack:

    ./stack.sh

A seemingly endless stream of activity ensues. When complete you will see a summary of stack.sh’s work, including the relevant URLs, accounts and passwords to poke at your shiny new OpenStack.

```txt
This is your host IP address: 10.0.0.10
This is your host IPv6 address: ::1
Horizon is now available at http://10.0.0.10/dashboard
Keystone is serving at http://10.0.0.10/identity/
The default users are: admin and demo
The password: password

WARNING:
Using lib/neutron-legacy is deprecated, and it will be removed in the future

Services are running under systemd unit files.
For more information see:
https://docs.openstack.org/devstack/latest/systemd.html

DevStack Version: stein
Change: 86011b700a89dc4e7e156eb662f435271934d5f1 Merge "Update cirros version" 2018-12-15 10:24:47 +0000
OS Version: Ubuntu 16.04 xenial
```

Create an snapshot

     vagrant snapshot save openstack-master initial-openstack

## Using OpenStack

At this point you should be able to access the dashboard from other computers on the local network. In this example that would be http://192.168.1.201/ for the dashboard (aka Horizon). Launch VMs and if you give them floating IPs and security group access those VMs will be accessible from other machines on your network.

## Network

By default , previous installation creates new interfaces on host machine. The most important interface is ``br-ex``, that allows internal networks created on open-stack to connect to external interfaces such as internet.

Following command shows the interface ``br-ex`` starting from the range configured in the settings. ``FLOATING_RANGE=10.0.0.224/27``

```bash
vagrant@openstack-master:~$ ifconfig br-ex
br-ex     Link encap:Ethernet  HWaddr 8e:78:34:db:a3:4e
          inet addr:10.0.0.225  Bcast:0.0.0.0  Mask:255.255.255.224
          inet6 addr: 2001:db8::2/64 Scope:Global
          inet6 addr: fe80::8c78:34ff:fedb:a34e/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:32 errors:0 dropped:0 overruns:0 frame:0
          TX packets:13 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1
          RX bytes:1844 (1.8 KB)  TX bytes:1326 (1.3 KB)
 ```

Perform some sanity checks (get current networks created)

    . ./openrc
    openstack network list  # should show public and private networks