.PHONY: setup build make-vm config-os deploy ssh vault

bookmarks_vm_ip=$(shell aws ec2 describe-instances --filters "Name=tag:App,Values=bookmarks" | jq '.Reservations[].Instances[0].PublicIpAddress' -r)

setup:
	brew install ansible awscli jq
	ansible-galaxy role install geerlingguy.docker geerlingguy.pip

build:
	docker build . -t bookmarks:latest

make-vm:
	cd ansible && ansible-playbook 1_make_vm.yml --connection=local

config-os:
	cd ansible && ansible-playbook -i inventories/aws_ec2.yml --vault-password-file ~/.bookmarks_vault_password 2_config_os.yml -u ubuntu -b

deploy:
	cd ansible && ansible-playbook -i inventories/aws_ec2.yml --vault-password-file ~/.bookmarks_vault_password 3_deploy.yml -u ubuntu -b

ssh:
	ssh ubuntu@$(bookmarks_vm_ip)

edit-vault:
	ansible-vault edit --vault-password-file ~/.bookmarks_vault_password ./ansible/inventories/group_vars/all/vault
