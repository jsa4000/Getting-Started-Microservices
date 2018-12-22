# Creating object storage to store eb static content
resource "openstack_objectstorage_container_v1" "web_static_content" {
  region = "RegionOne"
  name   = "web_static_${var.environment}_content"

  metadata {
    static = "true"
  }

  #content_type = "application/json"

  versioning {
    type = "versions"
    location = "web_static_content_versions"
  }
}
