.PHONY: setup build prepare-vm configure-os deploy ssh edit-vault

version=$(shell git rev-parse --short HEAD)

setup:
	brew install ansible awscli jq
	ansible-galaxy role install geerlingguy.docker geerlingguy.pip

build:
	docker build . -t bookmarks:$(version)

prepare-vm:
	cd ansible && ansible-playbook infra.yml --connection=local

configure-os:
	cd ansible && ansible-playbook -i inventories/aws_ec2.yml --vault-password-file ~/.bookmarks_vault_password site.yml -u ubuntu -b

deploy:
	# deploy to a container host

ssh:
	ssh ubuntu@$(shell aws ec2 describe-instances --filters "Name=tag:App,Values=bookmarks" | jq '.Reservations[].Instances[0].PublicIpAddress' -r)

edit-vault:
	ansible-vault edit --vault-password-file ~/.bookmarks_vault_password ./ansible/inventories/group_vars/all/vault
