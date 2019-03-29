output "public_ip" {
  value = "${aws_instance.bastion.public_ip}"
}

output "key_name" {
  value = "${aws_key_pair.bastion_key.key_name}"
}