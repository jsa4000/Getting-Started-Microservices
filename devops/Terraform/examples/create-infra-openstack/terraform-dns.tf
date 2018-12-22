# Creaing DNS zone to store the redords delegated to this zone.
resource "openstack_dns_zone_v2" "web_zone" {
  name = "example.com."
  email = "email2@example.com"
  description = "DNS Zone for example.com subdomains"
  ttl = 6000
  type = "PRIMARY"
}

# Create dns record for web.example.com.
resource "openstack_dns_recordset_v2" "web_example_com" {
  zone_id = "${openstack_dns_zone_v2.web_zone.id}"
  name = "web.example.com."
  description = "Record to point to the web server load balancer"
  ttl = 3000
  type = "A"
  records = ["192.168.1.2"]
}
